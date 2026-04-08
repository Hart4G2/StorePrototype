package io.github.store_prototype.objects.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.gui.BuyingEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.screens.GameScreen;

public class Store extends Actor implements SimpleEventListener {

    private Animation<TextureRegion> lightAnimation;
    private Animation<TextureRegion> darkAnimation;
    private Animation<TextureRegion> lightBuyingAnimation;
    private Animation<TextureRegion> darkBuyingAnimation;
    private float stateTime;
    private float x, y, width, height;
    private StoreState state;
    private StoreLight storeLight;
    private boolean isBuying;
    private float textureAspect;

    public enum StoreState {
        LIGHT, DARK
    }

    public Store() {
        storeLight = new StoreLight();
        initAnimations();
        setState(StoreState.DARK);
        setTouchable(Touchable.enabled);
    }

    private void initAnimations(){
        Texture texture = new Texture("gamescene/store/store.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/store/store.json"));

        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> animation = getTextureRegionAnimation(tag, data, texture);

            switch(tag.name) {
                case "light":
                    lightAnimation = animation;
                    break;
                case "dark":
                    darkAnimation = animation;
                    break;
                case "light_buying":
                    lightBuyingAnimation = animation;
                    lightBuyingAnimation.setFrameDuration(.15f);
                    break;
                case "dark_buying":
                    darkBuyingAnimation = animation;
                    darkBuyingAnimation.setFrameDuration(.15f);
                    break;
                default:
                    Gdx.app.log("Store", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
        }

        if (lightAnimation != null) {
            TextureRegion firstFrame = lightAnimation.getKeyFrame(0);
            textureAspect = (float) firstFrame.getRegionWidth() / firstFrame.getRegionHeight();
        } else {
            textureAspect = 1f;
        }

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(1f, regions, Animation.PlayMode.LOOP);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(Color.WHITE);
        switch (state){
            case DARK: {
                if(isBuying){
                    renderAnimation(batch, darkBuyingAnimation);
                    isBuying = !darkBuyingAnimation.isAnimationFinished(stateTime);
                } else {
                    renderAnimation(batch, darkAnimation);
                }

                break;
            }
            case LIGHT: {
                if(isBuying){
                    renderAnimation(batch, lightBuyingAnimation);
                    isBuying = !lightBuyingAnimation.isAnimationFinished(stateTime);
                } else {
                    renderAnimation(batch, lightAnimation);
                }
                break;
            }
        }
    }

    public void drawLight(Batch batch) {
        if(state == StoreState.LIGHT) {
            storeLight.render(batch);
        }
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(stateTime), x, y, width, height);
    }

    public void resize(float worldWidth, float worldHeight){
        this.height = worldHeight  / 2.3f;
        this.width = this.height * textureAspect;
        this.x = worldWidth / 2f - this.width / 2f;
        this.y = worldHeight / 2.3f;

        setBounds(x, y, this.width, this.height);

        storeLight.resize(worldWidth, worldHeight);
    }

    public void setState(StoreState state) {
        this.state = state;
    }

    @Override
    public void handleChange(Event event) {
        try{
            BuyingEvent e = (BuyingEvent) event;
            isBuying = true;
            stateTime = 0;
        } catch (Exception ignore){}
    }
}
