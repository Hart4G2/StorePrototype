package io.github.store_prototype.objects.screen.GUI;

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
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckNoticedEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.screens.GameScreen;

public class EventSign extends Actor {
    private Animation<TextureRegion> animation;
    private TextureRegion texture;
    private TextureRegion textureOutlined;
    private float stateTime;
    private boolean showOutline = false;

    private EventSignState state;

    private float time;
    private String eventName;

    public enum EventSignState {
        ANIMATING, STAYING
    }

    public EventSign(String eventName) {
        this.eventName = eventName;

        setVisible(false);

        setAssets();
        setListeners();

        setState(EventSignState.STAYING);

        Main.getInstance().getGameScreen().getStage().addActor(this);
        setBounds(Gdx.graphics.getWidth() / 1.473f, Gdx.graphics.getHeight() / 4f, Gdx.graphics.getWidth()  / 15f, Gdx.graphics.getHeight() / 8.4f);
        setTouchable(Touchable.enabled);

        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if(visible){
            time = 0;
        }
    }

    private void setAssets(){
        Texture texture = new Texture("gamescene/events/event.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/events/event.json"));

        this.texture = getTextureRegion(data.meta.frameTags.get(0), data, texture);
        this.textureOutlined = getTextureRegion(data.meta.frameTags.get(1), data, texture);

        for (FrameTag tag : data.meta.frameTags) {
            if(tag.name.equals("Without")) {
                animation = getTextureRegionAnimation(tag, data, texture);
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

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setState(EventSignState.ANIMATING);
                if(eventName.equals("DuckNoticedEvent")){
                    SimplePublisher.getPublisher().publish(new DuckNoticedEvent());
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;
        time += delta;

        if(time > 10){
            setVisible(false);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if(state == EventSignState.ANIMATING){
            renderAnimation(batch);
        } else {
            if(showOutline){
                renderOutline(batch);
            } else {
                render(batch);
            }
        }
    }

    private void renderAnimation(Batch batch){
        if(animation.isAnimationFinished(stateTime)){
            setVisible(false);
        } else {
            batch.draw(animation.getKeyFrame(stateTime), getX(), getY(), getWidth(), getHeight());
        }
    }

    private void render(Batch batch){
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    private void renderOutline(Batch batch){
        batch.draw(textureOutlined, getX(), getY(), getWidth(), getHeight());
    }

    public void resize(float width, float height){
        setBounds(width * 2 / 3, height / 8, width / 40f, height / 18f);
    }

    public void setState(EventSignState state) {
        this.state = state;
        stateTime = 0;
    }
}
