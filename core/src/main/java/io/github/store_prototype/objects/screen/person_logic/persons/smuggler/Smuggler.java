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
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.objects.screen.person_logic.persons.StoreQueue;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.Utils;
import io.github.store_prototype.utils.size.StorePositionHelper;

public class Smuggler extends QueuePerson {

    private Animation<TextureRegion> walkRight, walkLeft;
    private Texture stayingTexture;
    private float stateTime;
    private Bag bag;

    private boolean isSold = false;

    public Smuggler(float refX, float refY, Person.PersonState state) {
        setAssets();
        this.state = state;
        size.setRefPosition(refX, refY);

        bag = new Bag();
        bag.setVisible(false);

        updateFromReference();
    }

    private void setAssets(){
        Assets assets = Assets.getAssets();

        walkRight = assets.getAnimation("gamescene/person/smuggler/person_smuggler_walking", "Right");
        walkRight.setPlayMode(Animation.PlayMode.LOOP);

        walkLeft = assets.getAnimation("gamescene/person/smuggler/person_smuggler_walking", "Left");
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);

        if (size == null) {
            TextureRegion firstFrame = walkRight.getKeyFrame(0);
            float refW = firstFrame.getRegionWidth() * 1.8f;
            float refH = firstFrame.getRegionHeight() * 1.8f;
            size = new PersonSize(refW, refH);
        }

        stayingTexture = assets.getTexture("gamescene/person/smuggler/person_smuggler_staying.png");
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if (!isSold) {
            if (state == PersonState.BUYING) {
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
        setState(PersonState.BUYING);
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
                animate(batch, walkRight);
                size.setX(size.getX() + delta * speed);
                updateReferenceFromActual();
                break;
            case LEFT:
                animate(batch, walkLeft);
                size.setX(size.getX() - delta * speed);
                updateReferenceFromActual();
                break;
            case STAYING:
            case BUYING:
                animate(batch, stayingTexture);
                break;
        }
    }

    private void animate(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.getPlayMode() == Animation.PlayMode.LOOP && animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    private void animate(Batch batch, Texture texture) {
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    @Override
    public boolean isEnded() {
        if(state == PersonState.BUYING || state == PersonState.STAYING) {
            return false;
        }
        return state == PersonState.RIGHT ? size.getX() >= Gdx.graphics.getWidth() + 200 : size.getX() <= -200;
    }

    @Override
    public void resize(float width, float height) {
        super.resize(width, height);
        bag.resize();
        bag.setOpen(bag.isOpen());
    }
}
