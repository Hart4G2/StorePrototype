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
import io.github.store_prototype.utils.assets.Assets;

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
        windAnimation = Assets.getAssets().getAnimation("gamescene/road/road", "Day");
        windAnimation.setFrameDuration(.1f);
        idleTexture = windAnimation.getKeyFrame(0);

        roadLight = new RoadLight();
        setState(RoadState.WIND, RoadLight.LightState.ON);
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

    public void setLightState(RoadLight.LightState lightState) {
        roadLight.setState(lightState);
    }
}
