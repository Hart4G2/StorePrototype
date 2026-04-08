//package io.github.store_prototype.objects.screen.weather.thunder;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Mesh;
//import com.badlogic.gdx.graphics.VertexAttribute;
//import com.badlogic.gdx.graphics.VertexAttributes;
//import com.badlogic.gdx.graphics.glutils.ShaderProgram;
//import com.badlogic.gdx.utils.GdxRuntimeException;
//
//import io.github.store_prototype.utils.Utils;
//
//public class ThunderShader {
//    private ShaderProgram lightningShader;
//    private Mesh mesh;
//    private float boltPath;
//
//    public ThunderShader() {
//        ShaderProgram.pedantic = false;
//
//        String vertexShader =
//            "attribute vec4 a_position;\n" +
//                "attribute vec2 a_texCoord0;\n" +
//                "varying vec2 v_texCoords;\n" +
//                "void main() {\n" +
//                    " v_texCoords = a_texCoord0;\n" +
//                    " gl_Position = a_position;\n" +
//                "}\n";
//
//        String lightningFragmentShader =
//            "precision mediump float;\n" +
//                "varying vec2 v_texCoords;\n" +
//                "uniform float u_thunderIntensity;\n" +
//                "uniform float u_active;\n" +
//                "uniform float u_boltPath;\n" +
//                "\n" +
//                "// Функция для вычисления горизонтального положения молнии по координате y.\n" +
//                "float lightningBolt(float y) {" +
//                "\n" +
//                " // Первая синусоида придает общий извилистый изгиб\n" +
//                " float wave1 = sin(y * 25.0) * 0.03;" +
//                "\n" +
//                "// Вторая синусоида добавляет резкости локальным изгибам\n" +
//                " float wave2 = sin(y * 50.0) * 0.015;\n" +
//                " return u_boltPath + wave1 + wave2;\n" +
//                "}\n" +
//                "void main() {" +
//                "\n" +
//                "// Вычисляем горизонтальное положение молнии для данного y\n" +
//                "float x_bolt = lightningBolt(v_texCoords.y);\n" +
//                "\n" +
//                " // Вычисляем расстояние от текущего пикселя до линии молнии\n" +
//                " float dist = abs(v_texCoords.x - x_bolt);\n" +
//                "\n" + " // Определяем толщину линии (в системе координат текстуры, 0..1)\n" +
//                " float thickness = 0.007;\n" +
//                "\n" +
//                " // С помощью smoothstep создаем резкое условие: при dist меньше толщины пиксель становится ярким\n" +
//                " float alpha = smoothstep(thickness, thickness * 0.5, dist);\n" +
//                " alpha = 1.0 - alpha;\n" +
//                "\n" + " // Масштабируем итоговую прозрачность по интенсивности и флагу активности\n" +
//                " alpha *= u_thunderIntensity * u_active;\n" +
//                "\n" +
//                " // Выбираем белый цвет для молнии\n" +
//                " vec3 color = vec3(1.0);\n" +
//                " gl_FragColor = vec4(color, alpha);\n" +
//                "}\n";
//        lightningShader = new ShaderProgram(vertexShader, lightningFragmentShader);
//        if (!lightningShader.isCompiled()) {
//            throw new GdxRuntimeException("Ошибка компиляции lightningShader: " + lightningShader.getLog());
//        }
//        mesh = createFullScreenQuad();
//    }
//
//    private Mesh createFullScreenQuad() {
//        float[] vertices = {-1f, -1f, 0, 0, 0, 1f, -1f, 0, 1, 0, 1f, 1f, 0, 1, 1, -1f, 1f, 0, 0, 1};
//        short[] indices = {0, 1, 2, 2, 3, 0};
//        Mesh mesh = new Mesh(Mesh.VertexDataType.VertexBufferObject, true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"), new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
//        mesh.setVertices(vertices);
//        mesh.setIndices(indices);
//        return mesh;
//    }
//
//    public void render() {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        if (Utils.randomInt(1, 1001) > 999) {
//            boltPath = Utils.randomFloat(0, 1);
//            lightningShader.bind();
//            lightningShader.setUniformf("u_thunderIntensity", .8f);
//            lightningShader.setUniformf("u_active", 1.0f);
//            lightningShader.setUniformf("u_boltPath", boltPath);
//            mesh.render(lightningShader, GL20.GL_TRIANGLES);
//        }
//        Gdx.gl.glDisable(GL20.GL_BLEND);
//    }
//
//    public void dispose() {
//        mesh.dispose();
//        lightningShader.dispose();
//    }
//}
