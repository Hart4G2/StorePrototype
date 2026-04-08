package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.BuyingPanel;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.SmileyObject;
import io.github.store_prototype.objects.values.Value;
import io.github.store_prototype.objects.values.ValueNames;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.Utils;

public class Buyer extends QueuePerson {

    private Animation<TextureRegion> walkingRightAnimation, walkingLeftAnimation;
    private Texture standingTexture, buyingTexture;
    private float stateTime;

    private BuyingPanel buyingPanel;
    private SmileyObject smileyObject;

    public Buyer(float x, float y, PersonState state, int personNum) {
        setAssets(personNum);
        size.setRefPosition(x, y);
        this.state = state;

        Value valueToBuy = new Value(ValueNames.COKE, 1);
        int cost = 10;

        buyingPanel = new BuyingPanel(valueToBuy, cost);

        updateFromReference();
    }

    public Buyer(float x, float y, PersonState state, int personNum, boolean ble) {
        setAssets(personNum);
        size.setRefPosition(x, y);
        this.state = state;
        if(ble) { refYSpeed = 5f; }

        Value valueToBuy = new Value(ValueNames.COKE, 1);
        int cost = 10;

        buyingPanel = new BuyingPanel(valueToBuy, cost);

        updateFromReference();
    }

    private void setAssets(int personNum){
        Texture texture = new Texture("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking.json"));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> animation = getTextureRegionAnimation(tag, data, texture);

            switch(tag.name) {
                case "Left":
                    walkingLeftAnimation = animation;
                    break;
                case "Right":
                    walkingRightAnimation = animation;
                    break;
                default:
                    Gdx.app.log("Buyer", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
        }

        buyingTexture = new Texture("gamescene/person/person_" + personNum + "/person_" + personNum + "_buying.png");

        standingTexture = new Texture("gamescene/person/person_" + personNum + "/person_" + personNum + "_staying.png");
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();

        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }

        return new Animation<>(0.2f, regions, Animation.PlayMode.NORMAL);
    }

    float time = 0;
    float stopBuying = 0;
    boolean isBought = false;

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;
        time += delta;

        if(!isBought) {
            if(state == PersonState.BUYING){
                if(time > stopBuying || buyingPanel.isBought()) {
                    onLeaveQueue();
                }
            } else {
                getToStore(delta);
            }
        }

        if(smileyObject != null) {
            smileyObject.render(delta, batch, size.getX() + size.getWidth() / 2f, size.getY() + size.getHeight() + 50);
        }
        animate(delta, batch);
    }

    @Override
    protected void onReachCounter() {
        stopBuying = time + 10;
        setState(PersonState.BUYING);
        buyingPanel.setVisible(true);
        Main.getInstance().getGameScreen().getStage().addActor(buyingPanel);
    }

    @Override
    protected void onLeaveQueue() {
        showSmiley(buyingPanel.isBought());
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
        isBought = true;
        if (Utils.randomInt(1, 3) == 1) {
            setState(PersonState.LEFT);
        } else {
            setState(PersonState.RIGHT);
        }
        buyingPanel.remove();
    }

    private void showSmiley(boolean positive){
        smileyObject = new SmileyObject(positive);
        smileyObject.resize();
        smileyObject.play();
    }

    @Override
    protected void onQueueWaiting() {

    }

    private void animate(float delta, Batch batch){
        switch (state){
            case RIGHT: {
                renderAnimation(batch, walkingRightAnimation);
                size.setX(size.getX() + delta * speed);
                updateReferenceFromActual();
                break;
            }
            case LEFT: {
                renderAnimation(batch, walkingLeftAnimation);
                size.setX(size.getX() - delta * speed);
                updateReferenceFromActual();
                break;
            }
            case STAYING: {
                renderAnimation(batch, standingTexture);
                break;
            }
            case BUYING: {
                renderAnimation(batch, buyingTexture);
                break;
            }
        }
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if(animation.isAnimationFinished(stateTime)){
            stateTime = 0;
        }
    }

    private void renderAnimation(Batch batch, Texture texture){
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
        buyingPanel.resize(width, height);
        if(smileyObject != null) {
            smileyObject.resize();
        }
    }
}
