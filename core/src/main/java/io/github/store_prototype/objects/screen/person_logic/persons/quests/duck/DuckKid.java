package io.github.store_prototype.objects.screen.person_logic.persons.quests.duck;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckFallingEvent;
import io.github.store_prototype.objects.screen.GUI.EventSign;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.person_logic.persons.QueuePerson;
import io.github.store_prototype.utils.size.PersonSize;

public class DuckKid extends QueuePerson {

    public enum Phase {
        WALKING_RIGHT, FALLING
    }

    private Phase phase = Phase.WALKING_RIGHT;
    private Animation<TextureRegion> walkingAnimation;
    private float stateTime;
    private float alpha = 1f;
    private float fadeSpeed = 20f;
    private String name;

    public DuckKid(String name, float x) {
        this.name = name;
        setAssets();

        size.setRefPosition(x, REF_HEIGHT / 8f);
        updateFromReference();

        refSpeed = 100f;
        refYSpeed = REF_HEIGHT / 3f;
    }

    private void setAssets() {
        Texture texture = new Texture(Gdx.files.internal("gamescene/person/quests/duck/person_duck_" + name + ".png"));
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/quests/duck/person_duck_" + name + ".json"));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        for (FrameTag tag : data.meta.frameTags) {
            walkingAnimation = getTextureRegionAnimation(tag, data, texture);
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

        switch (phase) {
            case WALKING_RIGHT:
                size.setX(size.getX() + speed * delta);
                updateReferenceFromActual();
                drawAnimation(batch, walkingAnimation, 1f);
                if (isStandingOnSewerage() && name.equals("kid_2")) {
                    phase = Phase.FALLING;
                    SimplePublisher.getPublisher().publish(new DuckFallingEvent());
                    PersonScene.getPersonScene().addEventSign(new EventSign("DuckNoticedEvent", 1100f, 200f));
                }
                break;
            case FALLING:
                size.setY(size.getY() - ySpeed * delta);
                alpha -= fadeSpeed * delta;
                drawAnimation(batch, walkingAnimation, alpha);
                updateReferenceFromActual();
                break;
        }
    }

    private boolean isStandingOnSewerage() {
        float screenWidth = Gdx.graphics.getWidth();
        float refLuxX = REF_WIDTH * 2 / 3f;     // ≈1066.67
        float refLuxWidth = REF_WIDTH / 20f;    // 80
        float scaleX = screenWidth / REF_WIDTH;
        float actualLuxX = refLuxX * scaleX;
        float actualLuxWidth = refLuxWidth * scaleX;

        return size.getX() > actualLuxX - 10 && size.getX() < actualLuxX + actualLuxWidth + 10;
    }

    private void drawAnimation(Batch batch, Animation<TextureRegion> animation, float alpha) {
        ShaderProgram oldShader = batch.getShader();

        // Рисуем с обычным шейдером и прозрачностью
        batch.setShader(null);
        batch.setColor(1f, 1f, 1f, alpha);
        renderAnimation(batch, animation);

        // Восстанавливаем
        batch.setShader(oldShader);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    @Override
    public boolean isEnded() {
        if (name.equals("kid_2")) {
            float screenHeight = Gdx.graphics.getHeight();
            float scaleY = screenHeight / REF_HEIGHT;
            float endY = -100 * scaleY;
            return size.getY() <= endY;
        }
        float screenWidth = Gdx.graphics.getWidth();
        return size.getX() > screenWidth + 100;
    }

    @Override
    protected void onReachCounter() {

    }

    @Override
    protected void onLeaveQueue() {

    }

    @Override
    protected void onQueueWaiting() {

    }
}
