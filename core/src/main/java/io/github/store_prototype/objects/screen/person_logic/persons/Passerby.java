package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
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
import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class Passerby implements Person {
    private PersonSize size;
    private float refSpeed = 80f;
    private float speed;

    private Animation<TextureRegion> walkingRightAnimation, walkingLeftAnimation;
    private float stateTime;
    private PersonState state;

    public Passerby(float x, float y, PersonState state, int personNum) {
        setAssets(personNum);
        size.setRefPosition(x, y);
        this.state = state;
        updateFromReference();
    }

    private void setAssets(int personNum){
        Texture texture = new Texture(Gdx.files.internal("gamescene/person/person_" + personNum + "/person_" + personNum + "_walking.png"));
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
                    Gdx.app.log("Passerby", "Неизвестный тег анимации: " + tag.name);
                    break;
            }
        }
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

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if(state == PersonState.RIGHT){
            size.setX(size.getX() + delta * speed);
            renderAnimation(batch, walkingRightAnimation);
        } else {
            size.setX(size.getX() - delta * speed);
            renderAnimation(batch, walkingLeftAnimation);
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
