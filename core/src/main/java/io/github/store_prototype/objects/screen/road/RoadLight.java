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

public class RoadLight {

    private Animation<TextureRegion> lightAnimation;
    private float stateTime;
    private float width, height;
    private LightState state;

    public enum LightState {
        ON, OFF
    }

    public RoadLight() {
        Texture texture = new Texture("gamescene/road/road_light.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/road/road_light.json"));

        for (FrameTag tag : data.meta.frameTags) {
            lightAnimation = getTextureRegionAnimation(tag, data, texture);
        }

        setState(LightState.ON);
    }

    private Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(.25f, regions, Animation.PlayMode.LOOP);
    }

    public void render(float delta, Batch batch){
        stateTime += delta;

        if(state == LightState.ON){
            batch.draw(lightAnimation.getKeyFrame(stateTime), 0, 0, width, height);
        }
    }

    public void resize(float width, float height){
        this.width = width;
        this.height = height;
    }

    public void setState(LightState state) {
        this.state = state;
    }

    public LightState getState() {
        return state;
    }
}

