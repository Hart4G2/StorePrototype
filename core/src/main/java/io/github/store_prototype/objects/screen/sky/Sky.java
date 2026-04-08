package io.github.store_prototype.objects.screen.sky;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.size.ObjectSize;
import io.github.store_prototype.utils.time.WorldTime;

public class Sky {

    private static final String ANIMATION_TEXTURE = "gamescene/sky/sky_orange.png", ANIMATION_JSON = "gamescene/sky/sky_orange.json";
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
        Texture texture = new Texture(Gdx.files.internal(ANIMATION_TEXTURE));
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal(ANIMATION_JSON));

        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> animation = getTextureRegionAnimation(tag, data, texture);

            switch(tag.name) {
                case "Day":
                    dayAnimation = animation;
                    break;
                case "Morning":
                    morningAnimation = animation;
                    break;
                case "Evening":
                    eveningAnimation = animation;
                    break;
                case "Night":
                    nightAnimation = animation;
                    break;
                default:
                    Gdx.app.log("Sky", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
        }
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(0.23f, regions, Animation.PlayMode.NORMAL);
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
