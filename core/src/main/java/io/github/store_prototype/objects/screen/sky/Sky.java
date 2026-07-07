package io.github.store_prototype.objects.screen.sky;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.ObjectSize;

public class Sky {

    private static final float REF_WIDTH = 1600f, REF_HEIGHT = 450f, REF_Y = 450f;

    private Animation<TextureRegion> dayAnimation, morningAnimation, nightAnimation, eveningAnimation;
    private float stateTime;
    private ObjectSize size;
    private SkyState state;

    public enum SkyState {
        MORNING, DAY, EVENING, NIGHT
    }

    public Sky() {
        size = new ObjectSize(0, REF_Y, REF_WIDTH, REF_HEIGHT);
        setAssets();
        setState(SkyState.MORNING);
    }

    private void setAssets() {
        dayAnimation = Assets.getAssets().getAnimation("gamescene/sky/sky_orange", "Day");
        morningAnimation = Assets.getAssets().getAnimation("gamescene/sky/sky_orange", "Morning");
        nightAnimation = Assets.getAssets().getAnimation("gamescene/sky/sky_orange", "Night");
        eveningAnimation = Assets.getAssets().getAnimation("gamescene/sky/sky_orange", "Evening");

        dayAnimation.setFrameDuration(0.23f);
        morningAnimation.setFrameDuration(0.23f);
        nightAnimation.setFrameDuration(0.23f);
        eveningAnimation.setFrameDuration(0.23f);
    }

    public void render(float delta, Batch batch) {
        stateTime += delta;
        TextureRegion frame = getCurrentAnimation().getKeyFrame(stateTime);
        batch.draw(frame, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    private Animation<TextureRegion> getCurrentAnimation() {
        switch (state) {
            case DAY: return dayAnimation;
            case NIGHT: return nightAnimation;
            case MORNING: return morningAnimation;
            case EVENING: return eveningAnimation;
            default: return dayAnimation;
        }
    }

    public void setState(SkyState newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0;
        }
    }

    public SkyState getState() {
        return state;
    }

    public int getKeyFrameIndex(){
        switch (state){
            case DAY: return dayAnimation.getKeyFrameIndex(stateTime);
            case NIGHT: return nightAnimation.getKeyFrameIndex(stateTime);
            case EVENING: return eveningAnimation.getKeyFrameIndex(stateTime);
            case MORNING: return morningAnimation.getKeyFrameIndex(stateTime);
        }
        return -1;
    }

    public void resize(){
        size.updateFromReference();
    }
}
