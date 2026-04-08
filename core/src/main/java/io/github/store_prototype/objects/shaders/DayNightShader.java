package io.github.store_prototype.objects.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DayNightShader {
    private ShaderProgram shader;

    public DayNightShader() {
        ShaderProgram.pedantic = false;

        String vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/fragment.glsl").readString();

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Ошибка компиляции шейдера: " + shader.getLog());
        }
    }

    public void updateUniform(float stateTime) {
        float tintValue = 0.5f + 0.5f * stateTime;

        shader.bind();
        shader.setUniformf("u_tint", tintValue, tintValue, tintValue, 1f);
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void dispose() {
        shader.dispose();
    }
}

