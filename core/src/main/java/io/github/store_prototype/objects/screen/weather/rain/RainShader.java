package io.github.store_prototype.objects.screen.weather.rain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class RainShader implements Disposable {

    private ShaderProgram rainShader;
    private Mesh mesh;
    private float time;
    private float intensity = 0.0f;
    private float speed = 20f;
    private float angle = -.15f;

    public RainShader() {
        ShaderProgram.pedantic = false;

        String vertexShader =
            "attribute vec4 a_position;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main() {\n" +
                "    v_texCoords = a_texCoord0;\n" +
                "    gl_Position = a_position;\n" +
                "}\n";

        String rainFragmentShader =
            "precision mediump float;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform float u_time;\n" +
                "uniform float u_rainIntensity;\n" +
                "uniform float u_angle;\n" +
                "uniform float u_width;\n" +
                "uniform float u_speed;\n" +
                "\n" +
                "// Функция случайного шума\n" +
                "float rand(vec2 co) {\n" +
                "    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    // Масштабируем координаты для задания плотности капель\n" +
                "    vec2 uv = v_texCoords * vec2(30.0, 60.0);\n" +
                "\n" +
                "    // Вычисляем вектор направления падения капель на основе угла\n" +
                "    // При u_angle == 0 капли двигаются вертикально (вектор (0,1))\n" +
                "    vec2 fallDir = vec2(sin(u_angle), cos(u_angle));\n" +
                "\n" +
                "    // Анимируем падающие капли, смещая uv вдоль fallDir\n" +
                "    uv += fallDir * u_time * u_speed;\n" +
                "\n" +
                "    // Разбиваем uv на ячейки и получаем локальные координаты внутри ячейки\n" +
                "    vec2 cell = floor(uv);\n" +
                "    vec2 f = fract(uv);\n" +
                "\n" +
                "    // Поворачиваем локальные координаты, чтобы ось Y была ориентирована вдоль направления падения\n" +
                "    float s = sin(u_angle);\n" +
                "    float c = cos(u_angle);\n" +
                "    vec2 fRot = vec2(f.x * c - f.y * s, f.x * s + f.y * c);\n" +
                "\n" +
                "    // Для каждой ячейки задаём случайное смещение по горизонтали (из локальной оси X) и длину капли (локальная ось Y)\n" +
                "    float dropOffset = rand(cell);\n" +
                "    float dropLength = mix(0.3, 0.9, rand(cell + vec2(2.3, 4.7)));\n" +
                "\n" +
                "    // Горизонтальная маска: капля отрисовывается, если координата fRot.x близка к dropOffset\n" +
                "    float dx = abs(fRot.x - dropOffset);\n" +
                "    float maskWidth = 1.0 - smoothstep(u_width/2.0 - 0.05, u_width/2.0, dx);\n" +
                "\n" +
                "    // Вертикальная маска: капля отрисовывается, если fRot.y меньше dropLength\n" +
                "    float maskLength = 1.0 - smoothstep(dropLength - 0.05, dropLength, fRot.y);\n" +
                "\n" +
                "    // Итоговая маска капли\n" +
                "    float dropMask = maskWidth * maskLength * u_rainIntensity;\n" +
                "\n" +
                "    // Цвет капли: почти белый с легким голубым оттенком\n" +
                "    vec3 rainColor = vec3(0, 0.5, 1.0);\n" +
                "    gl_FragColor = vec4(rainColor * dropMask, dropMask);\n" +
                "}\n";

        rainShader = new ShaderProgram(vertexShader, rainFragmentShader);
        if (!rainShader.isCompiled()) {
            throw new GdxRuntimeException("Ошибка компиляции rainShader: " + rainShader.getLog());
        }

        mesh = createFullScreenQuad();
    }

    private Mesh createFullScreenQuad() {
        float[] vertices = {
            -1f, -1f, 0, 0, 0,
            1f, -1f, 0, 1, 0,
            1f,  1f, 0, 1, 1,
            -1f,  1f, 0, 0, 1
        };
        short[] indices = { 0, 1, 2, 2, 3, 0 };

        Mesh mesh = new Mesh(Mesh.VertexDataType.VertexBufferObject, true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
        mesh.setVertices(vertices);
        mesh.setIndices(indices);
        return mesh;
    }

    public void render(float delta) {
        time += delta;

        if (intensity > 0.0f) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            rainShader.bind();
            rainShader.setUniformf("u_time", time);
            rainShader.setUniformf("u_rainIntensity", intensity);
            rainShader.setUniformf("u_angle", angle);
            rainShader.setUniformf("u_width", .08f);
            rainShader.setUniformf("u_speed", speed);
            mesh.render(rainShader, GL20.GL_TRIANGLES);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        this.speed = 60 * intensity;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void dispose(){
        mesh.dispose();
    }
}
