package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.upgrades.VendingBuyingEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.values.Value;
import io.github.store_prototype.objects.values.ValueNames;
import io.github.store_prototype.utils.Utils;
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.size.StorePositionHelper;
import io.github.store_prototype.utils.size.VendingPositionHelper;

public class VendingBuyer implements Person {

    private PersonSize size;
    private PersonState state;
    private float refSpeed = 100f;
    private float refYSpeed = 20f;
    private float speed, ySpeed;
    private float necesseryY;

    private Animation<TextureRegion> walkRight, walkLeft;
    private Texture buyingTexture;
    private float stateTime;
    private Value value;

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

    protected void updateFromReference() {
        if (size != null) size.updateFromReference();
        speed = ScreenScaler.scaleUniform(refSpeed);
        ySpeed = ScreenScaler.scaleUniform(refYSpeed);
    }

    protected void updateReferenceFromActual() {
        if (size != null) size.updateReferenceFromActual();
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
                if(time > stopBuying) {
                    leave();
                }
            } else {
                buy();
            }
        }

        animate(delta, batch);
    }

    private void leave(){
        isBought = true;
        if (Utils.randomInt(1, 3) == 1) {
            setState(PersonState.LEFT);
        } else {
            setState(PersonState.RIGHT);
        }
        size.setWidth(size.getWidth() * 2);
    }

    protected void buy() {
        if(VendingPositionHelper.isWithinNearDistanceFromVending(size)){
            setState(PersonState.BUYING);
            stopBuying = time + 2;
            size.setWidth(size.getWidth() / 2);
            SimplePublisher.getPublisher().publish(new VendingBuyingEvent(value));
        }
    }

    private void animate(float delta, Batch batch){
        switch (state){
            case RIGHT: {
                renderAnimation(batch, walkRight);
                size.setX(size.getX() + delta * speed);
                updateReferenceFromActual();
                break;
            }
            case LEFT: {
                renderAnimation(batch, walkLeft);
                size.setX(size.getX() - delta * speed);
                updateReferenceFromActual();
                break;
            }
            case BUYING: {
                renderAnimation(batch, buyingTexture);
                break;
            }
        }

        if(isBought){
            if (!VendingPositionHelper.isAtNecessaryYLevel(size, necesseryY)) {
                goByY(delta, -1);
            }
        } else {
            if (!StorePositionHelper.isAtStoreYLevel(size)) {
                goByY(delta, 1);
            }
        }
    }

    private void goByY(float delta, float up) {
        size.setY(size.getY() + ScreenScaler.scaleY(delta * ySpeed) * up);
        updateReferenceFromActual();
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
    public void resize(float width, float height) {
        updateFromReference();
    }

    @Override
    public boolean isEnded() {
        if(state == PersonState.BUYING) {
            return false;
        }
        return state == PersonState.RIGHT ? size.getX() >= Gdx.graphics.getWidth() + 200 : size.getX() <= -200;
    }

    @Override
    public PersonState getState() {
        return state;
    }

    @Override
    public float getX() {
        return size.getX();
    }

    @Override
    public float getY() {
        return size.getY();
    }

    public void setState(PersonState state) {
        this.state = state;
    }
}
