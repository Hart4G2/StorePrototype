package io.github.store_prototype.objects.screen.person_logic.persons.duck;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.QueuePerson;
import io.github.store_prototype.objects.screen.person_logic.persons.StoreQueue;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.AnswerText;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.Dialog;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.DialogWindow;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.Text;
import io.github.store_prototype.utils.Utils;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.size.StorePositionHelper;

public class AdultDuck extends QueuePerson {

    private Animation<TextureRegion> walkingRightAnimation;
    private Animation<TextureRegion> walkingLeftAnimation;
    private Texture talkingTexture;
    private Texture standingTexture;
    private float stateTime;
    private Person.PersonState state;
    private boolean talkMode;
    private boolean talking;
    private DialogWindow dialogWindow;
// todo bug with queue
    public AdultDuck(boolean talkMode) {
        this.talkMode = talkMode;
        setAssets();

        if (talkMode) {
            size.setRefPosition(REF_WIDTH + 100, REF_HEIGHT / 2.8f);
            state = Person.PersonState.LEFT;
        } else {
            size.setRefPosition(-100, REF_HEIGHT / 8f);
            state = Person.PersonState.RIGHT;
        }

        refYSpeed = REF_HEIGHT / 90f;
        updateFromReference();
    }

    private void setAssets() {
        Texture texture = new Texture("gamescene/person/duck/person_duck_adult.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/duck/person_duck_adult.json"));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        for (FrameTag tag : data.meta.frameTags) {
            if (tag.name.equals("Right")) {
                walkingRightAnimation = getTextureRegionAnimation(tag, data, texture);
            } else {
                walkingLeftAnimation = getTextureRegionAnimation(tag, data, texture);
            }
        }

        talkingTexture = new Texture("gamescene/person/duck/person_duck_adult_talking.png");
        standingTexture = new Texture("gamescene/person/duck/person_duck_adult_standing.png");
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

        if (talkMode) {
            if (talking) {
                talk();
            } else {
                getToStore(delta);
            }
        }

        animate(delta, batch);
    }

    private boolean dialogShown = false;
    private void talk() {
        if (!dialogShown) {
            List<Dialog> answers = new ArrayList<>();

            AnswerText answer1 = new AnswerText(new Text("Hi!"), false, true);
            AnswerText answer2 = new AnswerText(new Text("Hello, chicken."), false, true);
            AnswerText answer3 = new AnswerText(new Text("Bee-bee!"), false, true);
            AnswerText answer4 = new AnswerText(new Text("Oh, no! "), false, true);
            AnswerText answer5 = new AnswerText(new Text("Yeah, I can help. He fell into that manhole over there."), true, true);
            AnswerText answer6 = new AnswerText(new Text("It's so sad. Maybe you can find it somewhere here..."), false, true);

            answers.add(new Dialog(new Text("Hello, Lamb!"), answer1, answer2, answer3));
            answers.add(new Dialog(new Text("I can't find my child. Have you seen him?"), answer4, answer5, answer6));

            Main.getInstance().getGameScreen().getStage().addActor(dialogWindow = new DialogWindow(answers));
            dialogWindow.show();
            dialogShown = true;
        }
    }

    @Override
    protected void onReachCounter() {
        setState(PersonState.BUYING);
        talking = true;
    }

    @Override
    protected void onLeaveQueue() {
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
        if (Utils.randomInt(1, 3) == 1) {
            setState(PersonState.LEFT);
        } else {
            setState(PersonState.RIGHT);
        }
    }

    @Override
    protected void onQueueWaiting() {

    }

    private void animate(float delta, Batch batch) {
        switch (state) {
            case RIGHT: {
                size.setX(size.getX() + delta * speed);
                render(batch, walkingRightAnimation);
                break;
            }
            case LEFT: {
                size.setX(size.getX() - delta * speed);
                render(batch, walkingLeftAnimation);
                break;
            }
            case STAYING: {
                render(batch, standingTexture);
                break;
            }
            case BUYING: {
                render(batch, talkingTexture);
                break;
            }
        }
        updateReferenceFromActual();
    }

    private void render(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    private void render(Batch batch, Texture texture) {
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    @Override
    public boolean isEnded() {
        if(talkMode){
            return size.getX() < -100;
        } else {
            return size.getX() > Gdx.graphics.getWidth() + 50;
        }
    }

    public void setState(PersonState state) {
        this.state = state;
    }

    @Override
    public PersonState getState() {
        return state;
    }
}
