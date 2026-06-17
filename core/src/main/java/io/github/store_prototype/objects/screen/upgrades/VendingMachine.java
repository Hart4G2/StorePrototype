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

public class VendingMachine extends Actor implements SimpleEventListener {

    private Animation<TextureRegion> lightAnimation;
    private Animation<TextureRegion> darkAnimation;
    private Animation<TextureRegion> lightBuyingAnimation;
    private Animation<TextureRegion> darkBuyingAnimation;
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
        Texture texture = new Texture("gamescene/upgrades/vending_machine.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/upgrades/vending_machine.json"));

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
                    lightBuyingAnimation.setFrameDuration(.1f);
                    break;
                case "dark_buying":
                    darkBuyingAnimation = animation;
                    darkBuyingAnimation.setFrameDuration(.1f);
                    break;
                default:
                    Gdx.app.log("VendingMachine", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
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
