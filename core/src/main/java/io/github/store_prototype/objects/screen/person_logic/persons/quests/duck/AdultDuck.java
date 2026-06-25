package io.github.store_prototype.objects.screen.person_logic.persons.quests.duck;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckFallingEvent;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckNoticedEvent;
import io.github.store_prototype.objects.event_handling.events.persons.duck.ReturningDuckEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.QueuePerson;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.AnswerOption;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.DialogNode;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.DialogWindow;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.Text;
import io.github.store_prototype.objects.screen.upgrades.UpgradeScene;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class AdultDuck extends QueuePerson implements SimpleEventListener {

    public enum Phase {
        WALKING_OFFSCREEN,
        GOING_TO_STORE,
        TALKING,
        RETURNING_KID,
        LEAVING
    }

    private Phase phase;

    private Animation<TextureRegion> walkRight, walkLeft;
    private Texture talkTexture, standTexture;
    private float stateTime;
    private DialogWindow dialogWindow;
    private boolean isDialogStarted;
    private boolean isDuckNoticed;
    private float returningDelay;
    private boolean isTimerON;

    public AdultDuck(Phase startPhase) {
        this.phase = startPhase;
        setAssets();

        if (phase == Phase.GOING_TO_STORE) {
            size.setRefPosition(REF_WIDTH + 100, 300f);
            state = Person.PersonState.LEFT;
        } else {
            size.setRefPosition(-100, REF_HEIGHT / 8f);
            state = Person.PersonState.RIGHT;
        }

        refYSpeed = REF_HEIGHT / 90f;
        updateFromReference();

        SimplePublisher.getPublisher().addListener(this);
    }

    private void setAssets() {
        Texture texture = new Texture("gamescene/person/quests/duck/person_duck_adult.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/quests/duck/person_duck_adult.json"));

        if (size == null) {
            float refW = data.frames.get(0).frame.w * 1.8f;
            float refH = data.frames.get(0).frame.h * 1.8f;
            size = new PersonSize(refW, refH);
        }

        for (FrameTag tag : data.meta.frameTags) {
            if (tag.name.equals("Right")) {
                walkRight = getTextureRegionAnimation(tag, data, texture);
            } else {
                walkLeft = getTextureRegionAnimation(tag, data, texture);
            }
        }

        talkTexture = new Texture("gamescene/person/quests/duck/person_duck_adult_talking.png");
        standTexture = new Texture("gamescene/person/quests/duck/person_duck_adult_standing.png");
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();
        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }
        return new Animation<>(0.4f, regions, Animation.PlayMode.NORMAL);
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        switch (phase) {
            case WALKING_OFFSCREEN:
                break;

            case GOING_TO_STORE:
                getToStore(delta);
                break;

            case TALKING:
                break;

            case RETURNING_KID:
                if(!isTimerON){
                    goToSewerage(delta);
                }
                if(isTimerON && stateTime > returningDelay){
                    phase = Phase.LEAVING;
                    state = PersonState.RIGHT;
                }
                break;

            case LEAVING:
                break;
        }

        animate(batch, delta);
        updateReferenceFromActual();
    }

    private void goToSewerage(float delta) {
        if(!onSewerageY()){
            size.setY(size.getY() - ySpeed * delta);
        }
        if(!onSewerageY() && onSewerageX()){
            size.setX(size.getX() - speed * delta);
        }

        if(onSewerageX() && onSewerageY()){
            state = PersonState.STAYING;
            returningDelay = stateTime + 2;
            isTimerON = true;
            returnKid();
        }
    }

    private boolean onSewerageY() {
        return size.getY() <= 112f;
    }

    private boolean onSewerageX() {
        float refLuxX = REF_WIDTH * 2 / 3f;     // ≈1066.67
        float scaleX = Gdx.graphics.getWidth() / REF_WIDTH;
        float actualLuxX = refLuxX * scaleX;

        return size.getX() >= actualLuxX - 50;
    }

    private void returnKid() {
        SimplePublisher.getPublisher().publish(new DuckFallingEvent());
        SimplePublisher.getPublisher().publish(new ReturningDuckEvent());
    }

    private void animate(Batch batch, float delta) {
        switch (state) {
            case RIGHT:
                size.setX(size.getX() + speed * delta);
                render(batch, walkRight);
                break;
            case LEFT:
                size.setX(size.getX() - speed * delta);
                render(batch, walkLeft);
                break;
            case STAYING:
                renderTexture(batch, standTexture);
                break;
            case BUYING:
                renderTexture(batch, talkTexture);
                break;
        }
    }

    private void render(Batch batch, Animation<TextureRegion> animation) {
        batch.draw(animation.getKeyFrame(stateTime), size.getX(), size.getY(), size.getWidth(), size.getHeight());
        if (animation.isAnimationFinished(stateTime)) {
            stateTime = 0;
        }
    }

    private void renderTexture(Batch batch, Texture texture) {
        batch.draw(texture, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    @Override
    protected void onReachCounter() {
        phase = Phase.TALKING;
        state = PersonState.BUYING;
        startDialog();
    }

    @Override
    public void handleChange(Event event) {
        try{
            DuckNoticedEvent e = (DuckNoticedEvent) event;
            isDuckNoticed = true;
        } catch (Exception ignore){}
    }

    public void startDialog() {
        if (phase != Phase.TALKING || isDialogStarted) return;
        isDialogStarted = true;
        state = Person.PersonState.BUYING;

        dialogWindow = new DialogWindow(getDialog());
        dialogWindow.setOnAnswerSelected(index -> {
            if (index == 0 && isDuckNoticed) {
                UpgradeScene.getInstance().setUpgradeAvailable(UpgradeScene.Upgrades.GOOSE_FOOT_KEYCHAIN);
            }
        });

        dialogWindow.setOnFinished(this::onLeaveQueue);

        Main.getInstance().getGameScreen().getStage().addActor(dialogWindow);
        dialogWindow.show();
    }

    private DialogNode getDialog() {
        DialogNode thanksNode = new DialogNode(
            new Text("Oh god, thank you!"),
            null,
            new AnswerOption("Your welcome.", true, isDuckNoticed, null),
            null
        );

        DialogNode questionNode = new DialogNode(
            new Text("My little duckling has disappeared somewhere. Have you seen him?"),
            new AnswerOption("Yes, he's at the hatch!", true, isDuckNoticed, thanksNode),
            new AnswerOption("No, I haven't seen him.", false, true, null),
            new AnswerOption("Somewhere around here there was a quack...", false, true, null)
        );

        DialogNode niceNode = new DialogNode(
            new Text("The kids and I are dressed as ducks today. And you, I see, are a sheep?"),
            null,
            new AnswerOption("Baa-baa-baa!", false, true, questionNode),
            null
        );

        DialogNode greetingNode = new DialogNode(
            new Text("Quack-quack! Oh, I mean, hello!"),
            null,
            new AnswerOption("Hi! How are you?", false, true, niceNode),
            null
        );

        return greetingNode;
    }

    @Override
    protected void onLeaveQueue() {
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
        state = PersonState.RIGHT;
        phase = Phase.RETURNING_KID;
        refYSpeed = 60f;
        ySpeed = ScreenScaler.scaleUniform(refYSpeed);
    }

    @Override
    protected void onQueueWaiting() {
        // Утка в очереди просто ждёт
    }

    @Override
    public boolean isEnded() {
        if (phase == Phase.WALKING_OFFSCREEN) {
            return size.getX() > Gdx.graphics.getWidth() + 50;
        }
        if (phase == Phase.LEAVING) {
            return size.getX() < -100;
        }
        return false;
    }

    public boolean isTalkMode() {
        return phase != Phase.WALKING_OFFSCREEN;
    }

    public void setDuckNoticed(boolean duckNoticed) {
        this.isDuckNoticed = duckNoticed;
    }

    public boolean isDuckNoticed() {
        return isDuckNoticed;
    }
}


