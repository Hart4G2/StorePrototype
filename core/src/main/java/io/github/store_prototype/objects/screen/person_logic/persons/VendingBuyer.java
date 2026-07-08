package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.upgrades.VendingBuyingEvent;
import io.github.store_prototype.objects.values.Value;
import io.github.store_prototype.objects.values.ValueNames;
import io.github.store_prototype.utils.Utils;
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.size.StorePositionHelper;
import io.github.store_prototype.utils.size.VendingPositionHelper;

public class VendingBuyer extends QueuePerson {

    private float necesseryY;

    private Animation<TextureRegion> walkRight, walkLeft;
    private Texture buyingTexture;
    private float stateTime;
    private Value value;

    private float time = 0, stopBuying = 0;
    private boolean isBought = false;

    public VendingBuyer(float x, float y, PersonState state, int personNum) {
        this.necesseryY = y;
        setAssets(personNum);
        size.setRefPosition(x, y);
        this.state = state;

        value= new Value(ValueNames.COKE, 1);

        updateFromReference();
    }

    private void setAssets(int personNum){
        Assets assets = Assets.getAssets();

        walkRight = assets.getAnimation("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking", "Right");
        walkLeft = assets.getAnimation("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking", "Left");

        if (size == null) {
            TextureRegion firstFrame = walkRight.getKeyFrame(0);
            float refW = firstFrame.getRegionWidth() * 3f;
            float refH = firstFrame.getRegionHeight() * 3f;
            size = new PersonSize(refW, refH);
        }

        buyingTexture = assets.getTexture("gamescene/person/person_" + personNum + "/person_" + personNum + "_buying.png");
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;
        time += delta;

        if(!isBought) {
            if(state == PersonState.BUYING){
                if(time > stopBuying) {
                    onLeaveQueue();
                }
            } else {
                onReachCounter();
            }
        }

        animate(delta, batch);
    }

    @Override
    protected void onReachCounter() {
        if(VendingPositionHelper.isWithinNearDistanceFromVending(size)){
            setState(PersonState.BUYING);
            stopBuying = time + 2;
            size.setWidth(size.getWidth() / 2);
            SimplePublisher.getPublisher().publish(new VendingBuyingEvent(value));
        }
    }

    @Override
    protected void onLeaveQueue() {
        isBought = true;
        if (Utils.randomInt(1, 3) == 1) {
            setState(PersonState.LEFT);
        } else {
            setState(PersonState.RIGHT);
        }
        size.setWidth(size.getWidth() * 2);
    }

    @Override
    protected void onQueueWaiting() {

    }

    private void animate(float delta, Batch batch){
        switch (state){
            case RIGHT: {
                moveRight(delta);
                render(batch, walkRight);
                break;
            }
            case LEFT: {
                moveLeft(delta);
                render(batch, walkLeft);
                break;
            }
            case BUYING: {
                render(batch, buyingTexture);
                break;
            }
        }

        if(isBought){
            if (!VendingPositionHelper.isAtNecessaryYLevel(size, necesseryY)) {
                moveDown(delta);
            }
        } else {
            if (!StorePositionHelper.isAtStoreYLevel(size)) {
                moveUp(delta);
            }
        }
    }

    private void render(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if(animation.isAnimationFinished(stateTime)){
            stateTime = 0;
        }
    }

    private void render(Batch batch, Texture texture){
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    @Override
    public boolean isEnded() {
        if(state == PersonState.BUYING) {
            return false;
        }
        return state == PersonState.RIGHT ? size.getX() >= Gdx.graphics.getWidth() + 200 : size.getX() <= -200;
    }
}
