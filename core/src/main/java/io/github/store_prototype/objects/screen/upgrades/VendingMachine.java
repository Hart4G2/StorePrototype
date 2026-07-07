package io.github.store_prototype.objects.screen.upgrades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.upgrades.VendingBuyingEvent;
import io.github.store_prototype.objects.screen.Store;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.assets.Assets;

public class VendingMachine extends Actor implements SimpleEventListener {

    private Animation<TextureRegion> lightAnimation, darkAnimation, lightBuyingAnimation, darkBuyingAnimation;
    private float x, y, width, height;
    private float stateTime;
    private Store.StoreState state;
    private boolean isBuying;

    public VendingMachine() {
        initAnimations();
        setState(Store.StoreState.LIGHT);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        SimplePublisher.getPublisher().addListener(this);
    }

    private void initAnimations(){
        lightAnimation = Assets.getAssets().getAnimation("gamescene/upgrades/vending_machine", "light");
        lightAnimation.setFrameDuration(1f);
        lightAnimation.setPlayMode(Animation.PlayMode.LOOP);
        darkAnimation = Assets.getAssets().getAnimation("gamescene/upgrades/vending_machine", "dark");
        darkAnimation.setFrameDuration(1f);
        darkAnimation.setPlayMode(Animation.PlayMode.LOOP);
        lightBuyingAnimation = Assets.getAssets().getAnimation("gamescene/upgrades/vending_machine", "light_buying");
        lightBuyingAnimation.setFrameDuration(.1f);
        lightBuyingAnimation.setPlayMode(Animation.PlayMode.LOOP);
        darkBuyingAnimation = Assets.getAssets().getAnimation("gamescene/upgrades/vending_machine", "dark_buying");
        darkBuyingAnimation.setFrameDuration(.1f);
        darkBuyingAnimation.setPlayMode(Animation.PlayMode.LOOP);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(stateTime), x, y, width, height);
    }

    public void resize(float worldWidth, float worldHeight){
        this.height = worldHeight  * .27f;
        this.width = worldWidth * .075f;
        this.x = worldWidth / 1.62f;
        this.y = worldHeight / 2.3f;

        setBounds(x, y, this.width, this.height);
    }

    public void setState(Store.StoreState state) {
        this.state = state;
    }

    public void setBuying(boolean buying) {
        isBuying = buying;
        stateTime = 0f;
    }

    @Override
    public void handleChange(Event event) {
        try{
            VendingBuyingEvent e = (VendingBuyingEvent) event;
            setBuying(true);
            new VendingPriceSign(this.getStage(), "+10$", x, y + height + 20, width);
        } catch (Exception ignore){}
    }
}
