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
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;

public class DuckKid extends QueuePerson {

    public enum Phase {
        WALKING_RIGHT, FALLING
    }

    private Phase phase = Phase.WALKING_RIGHT;
    private Animation<TextureRegion> walk;
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

    private void setAssets(){
        walk = Assets.getAssets().getAnimation("gamescene/person/quests/duck/person_duck_" + name, "Right");

        if (size == null) {
            TextureRegion firstFrame = walk.getKeyFrame(0);
            float refW = firstFrame.getRegionWidth() * 3f;
            float refH = firstFrame.getRegionHeight() * 3f;
            size = new PersonSize(refW, refH);
        }
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        switch (phase) {
            case WALKING_RIGHT:
                size.setX(size.getX() + speed * delta);
                updateReferenceFromActual();
                drawAnimation(batch, walk, 1f);
                if (isStandingOnSewerage() && name.equals("kid_2")) {
                    phase = Phase.FALLING;
                    SimplePublisher.getPublisher().publish(new DuckFallingEvent());
                    PersonScene.getPersonScene().addEventSign("DuckNoticedEvent", 1100f, 200f);
                }
                break;
            case FALLING:
                size.setY(size.getY() - ySpeed * delta);
                alpha -= fadeSpeed * delta;
                drawAnimation(batch, walk, alpha);
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
