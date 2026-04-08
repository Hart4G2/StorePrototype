package io.github.store_prototype.utils.size;

import com.badlogic.gdx.Gdx;

public class ScreenScaler {

    public static final float REF_WIDTH = 1600f;
    public static final float REF_HEIGHT = 900f;

    public static float getScaleX() {
        return Gdx.graphics.getWidth() / REF_WIDTH;
    }

    public static float getScaleY() {
        return Gdx.graphics.getHeight() / REF_HEIGHT;
    }

    public static float scaleX(float refValue) {
        return refValue * getScaleX();
    }

    public static float scaleY(float refValue) {
        return refValue * getScaleY();
    }

    public static float scaleUniform(float refValue) {
        return refValue * Math.min(getScaleX(), getScaleY());
    }

    public static float[] transform(float refX, float refY) {
        return new float[]{scaleX(refX), scaleY(refY)};
    }

    public static float[] inverseTransform(float x, float y) {
        return new float[]{x / getScaleX(), y / getScaleY()};
    }

    public static float scaleThreshold(float refThreshold) {
        return refThreshold * getScaleX();
    }
}
