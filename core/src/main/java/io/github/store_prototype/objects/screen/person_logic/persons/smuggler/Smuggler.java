package io.github.store_prototype.objects.screen.person_logic.persons.smuggler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.QueuePerson;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.objects.screen.person_logic.persons.StoreQueue;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.Utils;
import io.github.store_prototype.utils.size.StorePositionHelper;

public class Smuggler extends QueuePerson {

    private Animation<TextureRegion> walkingRightAnimation, walkingLeftAnimation, sellingRightAnimation, sellingLeftAnimation;
    private Texture stayingTexture;
    private float stateTime;
    private Bag bag;

    private boolean isSold = false;

    public Smuggler(float refX, float refY, PersonState state) {
        setAnimations();
        this.state = state;
        size.setRefPosition(refX, refY);
        size.updateFromReference();

        stayingTexture = new Texture("gamescene/person/smuggler/person_smuggler_staying.png");

        bag = new Bag();
        bag.setVisible(false);

        updateFromReference();
    }

    private void setAnimations() {
        Map<String, Animation<TextureRegion>> walking = loadAnimations(
            "gamescene/person/smuggler/person_smuggler_walking.png",
            "gamescene/person/smuggler/person_smuggler_walking.json",
            0.2f,
            Animation.PlayMode.LOOP
        );
        walkingLeftAnimation  = walking.get("Left");
        walkingRightAnimation = walking.get("Right");

        Map<String, Animation<TextureRegion>> selling = loadAnimations(
            "gamescene/person/smuggler/person_smuggler_selling.png",
            "gamescene/person/smuggler/person_smuggler_selling.json",
            0.4f,
            Animation.PlayMode.NORMAL
        );
        sellingLeftAnimation  = selling.get("Left");
        sellingRightAnimation = selling.get("Right");
    }

    private Map<String, Animation<TextureRegion>> loadAnimations(String texturePath, String jsonPath, float frameDuration, Animation.PlayMode playMode) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal(jsonPath));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        Map<String, Animation<TextureRegion>> result = new HashMap<>();
        for (FrameTag tag : data.meta.frameTags) {
            result.put(tag.name, getTextureRegionAnimation(tag, data, texture, frameDuration, playMode));
        }
        return result;
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> regions = new Array<>();
        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }
        return new Animation<>(frameDuration, regions, playMode);
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if (!isSold) {
            if (state == PersonState.SELLING_LEFT || state == PersonState.SELLING_RIGHT) {
                sell();
            } else {
                getToStore(delta);
            }
        }

        animate(delta, batch);
    }

    private void sell() {
        if (!bag.isOpen() && !bag.isVisible()) {
            onLeaveQueue();
        } else if(!bag.isOpen() && bag.isVisible()) {
            bag.remove();
            bag.setOpen(true);
            bag.setVisible(true);
            Main.getInstance().getGameScreen().getStage().addActor(bag);
        }
    }

    @Override
    protected void onReachCounter() {
        setState(state == PersonState.RIGHT ? PersonState.SELLING_RIGHT : PersonState.SELLING_LEFT);
        bag.setOpen(true);
        bag.setVisible(true);
        Main.getInstance().getGameScreen().getStage().addActor(bag);
    }

    @Override
    protected void onLeaveQueue() {
        if (Utils.randomInt(1, 3) == 1) {
            setState(PersonState.LEFT);
        } else {
            setState(PersonState.RIGHT);
        }
        bag.remove();
        isSold = true;
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
    }

    @Override
    protected void onQueueWaiting() {

    }

    private void animate(float delta, Batch batch) {
        switch (state) {
            case RIGHT:
                renderAnimation(batch, walkingRightAnimation);
                size.setX(size.getX() + delta * speed);
                updateReferenceFromActual();
                break;
            case LEFT:
                renderAnimation(batch, walkingLeftAnimation);
                size.setX(size.getX() - delta * speed);
                updateReferenceFromActual();
                break;
            case STAYING:
                renderAnimation(batch, stayingTexture);
                break;
            case SELLING_LEFT:
                renderAnimation(batch, sellingLeftAnimation);
                break;
            case SELLING_RIGHT:
                renderAnimation(batch, sellingRightAnimation);
                break;
        }
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.getPlayMode() == Animation.PlayMode.LOOP && animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    private void renderAnimation(Batch batch, Texture texture) {
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    @Override
    public boolean isEnded() {
        if(state == PersonState.SELLING_RIGHT || state == PersonState.SELLING_LEFT || state == PersonState.STAYING) {
            return false;
        }
        return state == PersonState.RIGHT ? size.getX() >= Gdx.graphics.getWidth() + 200 : size.getX() <= -200;
    }

    @Override
    public void resize(float width, float height) {
        super.resize(width, height);
        bag.resize();
        bag.setOpen(false);
    }
}
