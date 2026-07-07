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
import io.github.store_prototype.utils.assets.Assets;

public class Sewerage extends Actor implements SimpleEventListener {
    private Animation<TextureRegion> opening, openingOutline;
    private TextureRegion closedTexture, closedOutlineTexture;
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
        opening = Assets.getAssets().getAnimation("gamescene/road/sewerage", "opening");
        opening.setFrameDuration(.1f);
        openingOutline = Assets.getAssets().getAnimation("gamescene/road/sewerage", "opening_outline");
        openingOutline.setFrameDuration(.1f);

        closedTexture = Assets.getAssets().getAnimation("gamescene/road/sewerage", "closed").getKeyFrame(0f);
        closedOutlineTexture = Assets.getAssets().getAnimation("gamescene/road/sewerage", "closed_outline").getKeyFrame(0f);
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
        if(opening.isAnimationFinished(stateTime)){
            setState(SewerageState.CLOSE);
            renderClosed(batch);
        } else {
            if(showOutline) {
                batch.draw(openingOutline.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(opening.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
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
