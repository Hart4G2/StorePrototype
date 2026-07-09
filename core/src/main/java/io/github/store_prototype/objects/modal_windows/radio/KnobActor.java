package io.github.store_prototype.objects.modal_windows.radio;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class KnobActor extends Actor {
    private TextureRegion region;
    private float angleDeg = 0f;
    private float minAngle = -135f;
    private float maxAngle = 135f;

    public KnobActor(TextureRegion region) {
        this.region = region;
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(getColor());
        batch.draw(region,
            getX(), getY(),
            getOriginX(), getOriginY(),
            getWidth(), getHeight(),
            getScaleX(), getScaleY(),
            angleDeg);
    }

    public void setAngle(float angle) {
        angleDeg = MathUtils.clamp(angle, minAngle, maxAngle);
    }

    public float getAngle() {
        return angleDeg;
    }

    public float getFrequency() {
        return MathUtils.map(maxAngle, minAngle, 20f, 120f, angleDeg);
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        setOrigin();
    }

    private void setOrigin(){
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }

    public float getMinAngle() { return minAngle; }
    public float getMaxAngle() { return maxAngle; }
}
