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
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.utils.size.ObjectSize;

public class EventSign extends Actor {

    private static final String ANIMATION_TEXTURE = "gamescene/events/event.png", ANIMATION_JSON = "gamescene/events/event.json";
    private static float REF_SIZE = 100, REF_X, REF_Y;
    private Animation<TextureRegion> animation;
    private TextureRegion texture;
    private TextureRegion textureOutlined;
    private float stateTime;
    private ObjectSize size;
    private boolean showOutline = false;

    private EventSignState state;

    private float time;
    private String eventName;

    public enum EventSignState {
        ANIMATING, STAYING
    }

    public EventSign(String eventName, float x, float y) {
        this.eventName = eventName;
        REF_X = x;
        REF_Y = y;
        size = new ObjectSize(REF_X, REF_Y, REF_SIZE, REF_SIZE);
        setVisible(false);
        setAssets();
        setListeners();
        setState(EventSignState.STAYING);
        Main.getInstance().getGameScreen().getStage().addActor(this);
        setBounds(size.getX(), size.getY(), size.getWidth(), size.getWidth());
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
        Texture texture = new Texture(ANIMATION_TEXTURE);
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal(ANIMATION_JSON));

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
                PersonScene.getPersonScene().removeEventSign(EventSign.this);
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
            batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getWidth());
        }
    }

    private void render(Batch batch){
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getWidth());
    }

    private void renderOutline(Batch batch){
        batch.draw(textureOutlined, size.getX(), size.getY(), size.getWidth(), size.getWidth());
    }

    public void resize(){
        size.updateFromReference();
        setBounds(size.getX(), size.getY(), size.getWidth(), size.getWidth());

    }

    public void setState(EventSignState state) {
        this.state = state;
        stateTime = 0;
    }
}
