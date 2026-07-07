package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class Passerby implements Person {
    private PersonSize size;
    private float refSpeed = 120f;
    private float speed;

    private Animation<TextureRegion> walkRight, walkLeft;
    private float stateTime;
    private PersonState state;

    public Passerby(float x, float y, PersonState state, int personNum) {
        setAssets(personNum);
        size.setRefPosition(x, y);
        this.state = state;
        updateFromReference();
    }

    private void setAssets(int personNum){
        Assets assets = Assets.getAssets();

        walkRight = assets.getAnimation("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking", "Right");
        walkRight.setFrameDuration(0.1f);

        walkLeft = assets.getAnimation("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking", "Left");
        walkLeft.setFrameDuration(0.1f);

        if (size == null) {
            TextureRegion firstFrame = walkRight.getKeyFrame(0);
            float refW = firstFrame.getRegionWidth() * 3f;
            float refH = firstFrame.getRegionHeight() * 3f;
            size = new PersonSize(refW, refH);
        }
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if(state == PersonState.RIGHT){
            size.setX(size.getX() + delta * speed);
            renderAnimation(batch, walkRight);
        } else {
            size.setX(size.getX() - delta * speed);
            renderAnimation(batch, walkLeft);
        }

        updateReferenceFromActual();
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation){
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if(animation.isAnimationFinished(stateTime)){
            stateTime = 0;
        }
    }

    @Override
    public void resize(float width, float height){
        updateFromReference();
    }

    private void updateFromReference() {
        if (size != null) size.updateFromReference();
        speed = ScreenScaler.scaleUniform(refSpeed);
    }

    private void updateReferenceFromActual() {
        if (size != null) size.updateReferenceFromActual();
    }

    @Override
    public boolean isEnded() {
        return state == PersonState.RIGHT ? size.getX() >= Gdx.graphics.getWidth() + 100 : size.getX() <= - 100;
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
}
