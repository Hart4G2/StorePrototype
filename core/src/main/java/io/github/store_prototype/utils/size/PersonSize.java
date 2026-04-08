package io.github.store_prototype.utils.size;

public class PersonSize extends Size {

    public PersonSize(float refWidth, float refHeight) {
        super(refWidth, refHeight);
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
