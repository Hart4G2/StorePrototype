package io.github.store_prototype.utils.size;

public abstract class Size {
    protected float x, y, width, height;
    protected float refX, refY, refWidth, refHeight;

    public Size(float refX, float refY, float refWidth, float refHeight) {
        this.refX = refX;
        this.refY = refY;
        this.refWidth = refWidth;
        this.refHeight = refHeight;
        updateFromReference();
    }

    public Size(float refWidth, float refHeight) {
        this(0, 0, refWidth, refHeight);
    }

    public void updateFromReference() {
        x = ScreenScaler.scaleX(refX);
        y = ScreenScaler.scaleY(refY);
        width = ScreenScaler.scaleX(refWidth);
        height = ScreenScaler.scaleY(refHeight);
    }

    public void updateReferenceFromActual() {
        float[] ref = ScreenScaler.inverseTransform(x, y);
        refX = ref[0];
        refY = ref[1];
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public void setRefPosition(float refX, float refY) {
        this.refX = refX;
        this.refY = refY;
        updateFromReference();
    }

    public abstract void resize(float width, float height);
}
