package io.github.store_prototype.objects.screen.road;

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
import io.github.store_prototype.utils.Utils;

public class Road {

    private Animation<TextureRegion> windAnimation;
    private TextureRegion idleTexture;
    private float stateTime;
    private float width, height;
    private RoadState state;
    private RoadLight roadLight;

    public enum RoadState {
        WIND, IDLE
    }

    public Road() {
        Texture texture = new Texture("gamescene/road/road.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/road/road.json"));

        for (FrameTag tag : data.meta.frameTags) {
            windAnimation = getTextureRegionAnimation(tag, data, texture);
        }

        roadLight = new RoadLight();

        initIdleTexture(data, texture);

        setState(RoadState.WIND, RoadLight.LightState.ON);
    }

    private Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(.1f, regions, Animation.PlayMode.NORMAL);
    }

    private void initIdleTexture(AsepriteData data, Texture texture){
        AsepriteFrame frameData = data.frames.get(0);
        Frame f = frameData.frame;
        idleTexture = new TextureRegion(texture, f.x, f.y, f.w, f.h);
    }

    public void render(float delta, Batch batch){
        stateTime += delta;

        if(state == RoadState.WIND){
            renderAnimation(batch);
        } else {
            renderIdle(batch);
        }
    }

    private void renderAnimation(Batch batch){
        if(windAnimation.isAnimationFinished(stateTime)){
            setState(RoadState.IDLE, roadLight.getState());
            renderIdle(batch);
            stateTime = 0;
        } else {
            batch.draw(windAnimation.getKeyFrame(stateTime), 0, 0, width, height);
        }
    }

    private void renderIdle(Batch batch){
        batch.draw(idleTexture, 0, 0, width, height);

        int r = Utils.randomInt(1, 1001);
        if(r > 999){
            setState(RoadState.WIND, roadLight.getState());
        }
    }

    public void renderLight(float delta, Batch batch){
        roadLight.render(delta, batch);
    }

    public void resize(float width, float height){
        this.width = width;
        this.height = height;
        roadLight.resize(width, height);
    }

    public RoadState getState() {
        return state;
    }

    public void setState(RoadState state, RoadLight.LightState lightState) {
        this.state = state;
        roadLight.setState(lightState);
    }
}
