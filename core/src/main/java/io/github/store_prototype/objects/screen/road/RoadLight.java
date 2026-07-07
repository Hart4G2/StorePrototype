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
import io.github.store_prototype.utils.assets.Assets;

public class RoadLight {

    private Animation<TextureRegion> lightAnimation;
    private float stateTime;
    private float width, height;
    private LightState state;

    public enum LightState {
        ON, OFF
    }

    public RoadLight() {
        lightAnimation = Assets.getAssets().getAnimation("gamescene/road/road_light", "Night");

        setState(LightState.ON);
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

