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
import io.github.store_prototype.utils.time.WorldTime;

public class Sky {

    private Animation<TextureRegion> dayAnimation;
    private Animation<TextureRegion> morningAnimation;
    private Animation<TextureRegion> nightAnimation;
    private Animation<TextureRegion> eveningAnimation;
    private float stateTime;
    private float y, width, height;
    private SkyState state;

    public enum SkyState {
        MORNING, DAY, EVENING, NIGHT
    }

    public Sky() {
        Texture texture = new Texture(Gdx.files.internal("gamescene/sky/sky_orange.png"));
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/sky/sky_orange.json"));

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
        setState(SkyState.NIGHT);
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

    public void render(float delta, Batch batch){
        stateTime += delta;

        switch (state){
            case DAY: {
                renderAnimation(batch, dayAnimation, SkyState.EVENING);
                break;
            }
            case NIGHT: {
                renderAnimation(batch, nightAnimation, SkyState.MORNING);
                break;
            }
            case MORNING: {
                renderAnimation(batch, morningAnimation, SkyState.DAY);
                break;
            }
            case EVENING: {
                renderAnimation(batch, eveningAnimation, SkyState.NIGHT);
                break;
            }
        }
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation, SkyState nextState){
        batch.draw(animation.getKeyFrame(stateTime), 0, y, width, height);
        if(animation.isAnimationFinished(stateTime)){
            setState(nextState);
            System.out.println("Setting next sky state to " + nextState);
            stateTime = 0;

            if(nextState.equals(SkyState.MORNING)){
                WorldTime.getInstance().nextDay();
            }
        }
    }

    public void resize(float width, float height){
        y = height / 2;
        this.width = width;
        this.height = height / 2f;
    }

    public void setState(SkyState state) {
        this.state = state;
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
}
