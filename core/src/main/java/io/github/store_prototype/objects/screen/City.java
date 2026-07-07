package io.github.store_prototype.objects.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.assets.Assets;

public class City {

    private Animation<TextureRegion> lightAnimation, darkAnimation, darkAnimation1;
    private Texture leninTexture, buildingFarLayer;
    private float x, y, width, height;
    private float leninX, buildingFarLayerX;
    private CityState state;

    public enum CityState {
        LIGHT, DARK, DARK1
    }

    public City() {
        x = 0;
        leninX = 0;
        buildingFarLayerX = 0;

        leninTexture = Assets.getAssets().getTexture("gamescene/city/lenin.png");
        buildingFarLayer = Assets.getAssets().getTexture("gamescene/city/buildingFarLayer.png");

        darkAnimation = Assets.getAssets().getAnimation("gamescene/city/city", "With_light1");
        darkAnimation.setFrameDuration(0.25f);
        darkAnimation1 = Assets.getAssets().getAnimation("gamescene/city/city", "With_light2");
        darkAnimation1.setFrameDuration(0.25f);
        lightAnimation = Assets.getAssets().getAnimation("gamescene/city/city", "No_light");
        lightAnimation.setFrameDuration(0.25f);

        setState(CityState.DARK);
    }

    public void render(float delta, Batch batch){
        batch.draw(buildingFarLayer, buildingFarLayerX, Gdx.graphics.getHeight() / 2f, width, height);

        switch (state){
            case DARK: {
                renderAnimation(batch, darkAnimation);
                break;
            }
            case DARK1: {
                renderAnimation(batch, darkAnimation1);
                break;
            }
            case LIGHT: {
                renderAnimation(batch, lightAnimation);
                break;
            }
        }

        batch.draw(leninTexture, leninX, Gdx.graphics.getHeight() / 2f, width, height);
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(0), x, y, width, height);
    }

    public void setState(CityState state) {
        this.state = state;
    }

    public void resize(float width, float height){
        y = height / 2;
        this.width = width;
        this.height = height / 2f;
    }
}


