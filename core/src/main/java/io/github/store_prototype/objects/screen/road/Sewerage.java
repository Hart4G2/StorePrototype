package io.github.store_prototype.objects.screen.road;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckFallingEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.screens.GameScreen;

public class Sewerage extends Actor implements SimpleEventListener {
    private Animation<TextureRegion> openingAnimation;
    private Animation<TextureRegion> openingOtlineAnimation;
    private TextureRegion closedTexture;
    private TextureRegion closedOutlineTexture;
    private float stateTime;
    private boolean showOutline = false;

    private SewerageState state;

    public enum SewerageState {
        CLOSE, OPENING
    }

    public Sewerage() {
        SimplePublisher.getPublisher().addListener(this);

        setAssets();
        setListeners();

        setState(SewerageState.CLOSE);

        Main.getInstance().getGameScreen().getStage().addActor(this);
        setBounds(Gdx.graphics.getWidth() * 2 / 3f, Gdx.graphics.getHeight() / 10f, Gdx.graphics.getWidth() / 10f, Gdx.graphics.getHeight() / 8f);
        setTouchable(Touchable.enabled);
    }

    private void setAssets(){
        Texture texture = new Texture("gamescene/road/sewerage.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/road/sewerage.json"));

        for (FrameTag tag : data.meta.frameTags) {
            switch (tag.name){
                case "opening":{
                    openingAnimation = getTextureRegionAnimation(tag, data, texture);
                    break;
                }
                case "opening_outline":{
                    openingOtlineAnimation = getTextureRegionAnimation(tag, data, texture);
                    break;
                }
                case "closed":{
                    closedTexture = getTextureRegion(tag, data, texture);
                    break;
                }
                case "closed_outline":{
                    closedOutlineTexture = getTextureRegion(tag, data, texture);
                    break;
                }
            }
        }
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

    private TextureRegion getTextureRegion(FrameTag tag, AsepriteData data, Texture texture){
        AsepriteFrame frameData = data.frames.get(tag.from);
        Frame f = frameData.frame;
        return new TextureRegion(texture, f.x, f.y, f.w, f.h);
    }

    private void setListeners() {
        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                showOutline = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                showOutline = false;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(state == SewerageState.OPENING){
            renderAnimation(batch);
        } else {
            renderClosed(batch);
        }
    }

    private void renderAnimation(Batch batch){
        if(openingAnimation.isAnimationFinished(stateTime)){
            setState(SewerageState.CLOSE);
            renderClosed(batch);
        } else {
            if(showOutline) {
                batch.draw(openingOtlineAnimation.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(openingAnimation.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
            }
        }
    }

    private void renderClosed(Batch batch){
        if(showOutline) {
            batch.draw(closedOutlineTexture, getX(), getY(), getWidth(), getHeight());
        } else {
            batch.draw(closedTexture, getX(), getY(), getWidth(), getHeight());
        }
    }

    public void resize(float width, float height){
        setBounds(width * 2 / 3, height / 10, width / 10f, height / 8f);
    }

    public void setState(SewerageState state) {
        this.state = state;
        stateTime = 0;
    }

    @Override
    public void handleChange(Event event) {
        try{
            Event e = (DuckFallingEvent) event;
            setState(SewerageState.OPENING);
        } catch (ClassCastException ignore){}
    }
}
