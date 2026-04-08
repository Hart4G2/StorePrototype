package io.github.store_prototype.utils.size;

public class ObjectSize extends Size {

    public ObjectSize(float refWidth, float refHeight) {
        super(refWidth, refHeight);
    }

    public ObjectSize(float refX, float refY, float refWidth, float refHeight) {
        super(refX, refY, refWidth, refHeight);
    }

    @Override
    public void updateFromReference() {
        float scale = Math.min(ScreenScaler.getScaleX(), ScreenScaler.getScaleY());
        x = ScreenScaler.scaleX(refX);
        y = ScreenScaler.scaleY(refY);
        width = refWidth * scale;
        height = refHeight * scale;
    }

    @Override
    public void resize(float screenWidth, float screenHeight) {
        updateFromReference();
    }
}
