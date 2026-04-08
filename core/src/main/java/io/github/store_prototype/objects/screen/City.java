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

public class City {

    private Animation<TextureRegion> lightAnimation;
    private Animation<TextureRegion> darkAnimation;
    private Animation<TextureRegion> darkAnimation1;
    private Texture leninTexture;
    private Texture buildingFarLayer;
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

        leninTexture = new Texture("gamescene/city/lenin.png");
        buildingFarLayer = new Texture("gamescene/city/buildingFarLayer.png");

        Texture texture = new Texture("gamescene/city/city.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/city/city.json"));

        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> animation = getTextureRegionAnimation(tag, data, texture);

            switch(tag.name) {
                case "With_light1":
                    darkAnimation = animation;
                    break;
                case "With_light2":
                    darkAnimation1 = animation;
                    break;
                case "No_light":
                    lightAnimation = animation;
                    break;
                default:
                    Gdx.app.log("Store", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
        }

        setState(CityState.DARK);
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(0.25f, regions, Animation.PlayMode.NORMAL);
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

        if(Main.getInstance().getGameScreen().isCameraReturning()){
            moveToCenter(delta);
        }

        moveLayer(Layer.LENIN, delta);
        moveLayer(Layer.CITY, delta);
        moveLayer(Layer.FAR_LAYER, delta);
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

    private enum Layer {
        LENIN, CITY, FAR_LAYER
    }

    private void moveLayer(Layer layer, float delta){
        float screenWidth = Gdx.graphics.getWidth();

        float mouseX = Gdx.input.getX();
        float screenWidthPart = screenWidth * .2f;

        switch (layer){
            case LENIN:{
                float edgeWidthZone = screenWidth * .005f;
                float maxMoveSpeed = 20f * delta;

                if (mouseX < screenWidthPart && leninX > -edgeWidthZone) {
                    leninX -= maxMoveSpeed - (mouseX / screenWidthPart * maxMoveSpeed);
                } else if (mouseX > screenWidth - screenWidthPart && leninX < edgeWidthZone) {
                    leninX += (mouseX - (screenWidth - screenWidthPart)) / screenWidthPart * maxMoveSpeed;
                }
                break;
            }
            case CITY: {
                float edgeWidthZone = screenWidth * .007f;
                float maxMoveSpeed = 30f * delta;

                if (mouseX < screenWidthPart && x > -edgeWidthZone) {
                    x -= maxMoveSpeed - (mouseX / screenWidthPart * maxMoveSpeed);
                } else if (mouseX > screenWidth - screenWidthPart && x < edgeWidthZone) {
                    x += (mouseX - (screenWidth - screenWidthPart)) / screenWidthPart * maxMoveSpeed;
                }
                break;
            }
            case FAR_LAYER:{
                float edgeWidthZone = screenWidth * .01f;
                float maxMoveSpeed = 40f * delta;

                if (mouseX < screenWidthPart && buildingFarLayerX > -edgeWidthZone) {
                    buildingFarLayerX -= maxMoveSpeed - (mouseX / screenWidthPart * maxMoveSpeed);
                } else if (mouseX > screenWidth - screenWidthPart && buildingFarLayerX < edgeWidthZone) {
                    buildingFarLayerX += (mouseX - (screenWidth - screenWidthPart)) / screenWidthPart * maxMoveSpeed;
                }
                break;
            }
        }
    }

    private void moveToCenter(float delta){
        float moveSpeed = 10f * delta;

        if(leninX > 0){
            leninX -= moveSpeed;
        } else {
            leninX += moveSpeed;
        }

        if(x > 0){
            x -= moveSpeed;
        } else{
            x += moveSpeed;
        }

        if(buildingFarLayerX > 0){
            buildingFarLayerX -= moveSpeed;
        } else {
            buildingFarLayerX += moveSpeed;
        }
    }
}


