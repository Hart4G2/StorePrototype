package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.size.StorePositionHelper;

public abstract class QueuePerson implements Person {

    protected PersonSize size;
    protected PersonState state;
    protected boolean isInQueue = false;
    protected float refSpeed = 100f;
    protected float refYSpeed = 20f;
    protected float speed, ySpeed;

    protected void updateFromReference() {
        if (size != null) size.updateFromReference();
        speed = ScreenScaler.scaleUniform(refSpeed);
        ySpeed = ScreenScaler.scaleUniform(refYSpeed);
    }

    protected void updateReferenceFromActual() {
        if (size != null) size.updateReferenceFromActual();
    }

    protected void goToStoreByY(float delta) {
        size.setY(size.getY() + ScreenScaler.scaleY(delta * ySpeed));
        updateReferenceFromActual();
    }

    protected boolean isFarFromStore() {
        return StorePositionHelper.isWithinFarDistanceFromStore(size)
            && StorePositionHelper.isAtStoreYLevel(size);
    }

    protected void joinQueue() {
        StoreQueue.getInstance().addPerson(this);
        isInQueue = true;
    }

    protected void moveToQueuePosition(int queueIndex) {
        float targetX = StorePositionHelper.getQueuePositionX(queueIndex, size);
        setState(StorePositionHelper.getDirectionToTarget(targetX, size.getX()));
    }

    protected void getToStore(float delta) {
        if (!StorePositionHelper.isAtStoreYLevel(size)) {
            goToStoreByY(delta);
        }

        if (isFarFromStore() && !isInQueue) {
            joinQueue();
            int index = StoreQueue.getInstance().getIndexInQueue(this);
            if (index >= 0) {
                moveToQueuePosition(index);
            }
        }

        if (isInQueue) {
            Person active = StoreQueue.getInstance().getActiveBuyer();
            if (active == this) {
                moveToStoreCounter();
            } else {
                int index = StoreQueue.getInstance().getIndexInQueue(this);
                if (index >= 0) {
                    float queuePosition = StorePositionHelper.getQueuePositionX(index, size);
                    PersonState newState = StorePositionHelper.getDirectionToTarget(queuePosition, size.getX());
                    if(StorePositionHelper.isAtQueuePosition(size, newState, index)){
                        setState(PersonState.STAYING);
                        onQueueWaiting();
                    } else {
                        setState(newState);
                        moveToQueuePosition(index);
                    }
                }
            }
        }
    }

    protected void moveToStoreCounter() {
        setState(StorePositionHelper.getDirectionToStore(size.getX() + size.getWidth() / 2f));

        if (StorePositionHelper.isWithinNearDistanceFromStore(size)) {
            onReachCounter();
        }
    }

    protected abstract void onReachCounter();
    protected abstract void onLeaveQueue();
    protected abstract void onQueueWaiting();

    @Override
    public abstract void render(float delta, Batch batch);

    @Override
    public void resize(float width, float height) {
        updateFromReference();
    }

    @Override
    public boolean isEnded() {
        return false;
    }

    @Override
    public PersonState getState() {
        return state;
    }

    public void setState(PersonState state) {
        this.state = state;
    }

    @Override
    public float getX() {
        return size.getX();
    }

    @Override
    public float getY() {
        return size.getY();
    }

    protected void moveRight(float delta){
        size.setX(size.getX() + delta * speed);
        updateReferenceFromActual();
    }

    protected void moveLeft(float delta){
        size.setX(size.getX() - delta * speed);
        updateReferenceFromActual();
    }

    protected void moveUp(float delta){
        size.setY(size.getY() + delta * speed);
        updateReferenceFromActual();
    }

    protected void moveDown(float delta){
        size.setY(size.getY() - delta * speed);
        updateReferenceFromActual();
    }


}
