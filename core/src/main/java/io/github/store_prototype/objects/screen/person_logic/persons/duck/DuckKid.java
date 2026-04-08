package io.github.store_prototype.objects.screen.person_logic.persons.duck;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckFallingEvent;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckNoticedEvent;
import io.github.store_prototype.objects.event_handling.storage.EventStorage;
import io.github.store_prototype.objects.screen.GUI.EventSign;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class DuckKid extends Actor implements Person {

    private PersonSize size;
    private float refXSpeed, refFallingSpeed;
    private float xSpeed, fallingSpeed;

    private Animation<TextureRegion> walkingRightAnimation;
    private Animation<TextureRegion> outlinedWalkingRightAnimation;
    private float stateTime;
    private PersonState state;
    private String name;
    private boolean showOutline;

    public DuckKid(String name) {
        this.name = name;
        setAssets();

        float x = 0;
        if (name.equals("kid_0")) {
            x = -220;
        }
        if (name.equals("kid_1")) {
            x = -340;
        }
        if (name.equals("kid_2")) {
            x = -460;
        }
        size.setRefPosition(x, REF_HEIGHT / 8f);

        refXSpeed = 100f;
        refFallingSpeed = REF_HEIGHT / 3f;

        state = PersonState.RIGHT;

        Main.getInstance().getGameScreen().getStage().addActor(this);
        setBounds(size.getX(), size.getY(), size.getWidth(),  size.getHeight());
        setTouchable(Touchable.enabled);

        if (name.equals("kid_2")) {
            setListeners();
        }

        updateFromReference();
    }

    private void setAssets() {
        Texture texture = new Texture(Gdx.files.internal("gamescene/person/duck/person_duck_" + name + ".png"));
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/duck/person_duck_" + name + ".json"));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        if (name.equals("kid_2")) {
            for (FrameTag tag : data.meta.frameTags) {
                if (tag.name.equals("Right")) {
                    walkingRightAnimation = getTextureRegionAnimation(tag, data, texture);
                } else if (tag.name.equals("Outlined")) {
                    outlinedWalkingRightAnimation = getTextureRegionAnimation(tag, data, texture);
                }
            }
        } else {
            for (FrameTag tag : data.meta.frameTags) {
                walkingRightAnimation = getTextureRegionAnimation(tag, data, texture);
            }
        }
    }

    private void setListeners() {
        this.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                showOutline = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                showOutline = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!EventStorage.getInstance().isInStorage("DuckNoticedEvent")) {
                    SimplePublisher.getPublisher().publish(new DuckNoticedEvent());
                }
            }
        });
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

    private float alpha = 1f;
    private float fadeSpeed = 20f;

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if (state == PersonState.RIGHT) {
            size.setX(size.getX() + delta * xSpeed);
            updateReferenceFromActual();

            if (name.equals("kid_2") && showOutline) {
                renderAnimation(batch, outlinedWalkingRightAnimation);
            } else {
                renderAnimation(batch, walkingRightAnimation);
            }
        }
        if (state == PersonState.FALLING) {
            size.setY(size.getY() - delta * fallingSpeed);
            alpha -= fadeSpeed * delta;

            // Сохраняем текущий шейдер и цвет
            ShaderProgram oldShader = batch.getShader();

            // Рисуем с обычным шейдером и прозрачностью
            batch.setShader(null);
            batch.setColor(1f, 1f, 1f, alpha);
            renderAnimation(batch, walkingRightAnimation);

            // Восстанавливаем
            batch.setShader(oldShader);
            batch.setColor(1f, 1f, 1f, 1f);
            updateReferenceFromActual();
        }
        if (name.equals("kid_2") && isStandingOnSewerage() && state != PersonState.FALLING) {
            SimplePublisher.getPublisher().publish(new DuckFallingEvent());
            state = PersonState.FALLING;
            PersonScene.getPersonScene().addEventSign(new EventSign("DuckNoticedEvent", 1100f, 200f));
        }

        setBounds(size.getX(), size.getY(), size.getWidth(),  size.getHeight());
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

    private void renderAnimation(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    @Override
    public void resize(float screenWidth, float screenHeight) {
        updateFromReference();
    }

    private void updateFromReference() {
        if (size != null) size.updateFromReference();
        xSpeed = ScreenScaler.scaleUniform(refXSpeed);
        fallingSpeed = ScreenScaler.scaleUniform(refFallingSpeed);
    }

    private void updateReferenceFromActual() {
        if (size != null) size.updateReferenceFromActual();
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
    public PersonState getState() {
        return PersonState.RIGHT;
    }

}
