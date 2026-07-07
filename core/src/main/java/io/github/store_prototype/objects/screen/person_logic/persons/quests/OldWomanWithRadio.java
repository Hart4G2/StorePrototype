package io.github.store_prototype.objects.screen.person_logic.persons.quests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.screen.person_logic.persons.QueuePerson;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.AnswerOption;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.DialogNode;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.DialogWindow;
import io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog.Text;
import io.github.store_prototype.objects.screen.upgrades.UpgradeScene;
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.PersonSize;

public class OldWomanWithRadio extends QueuePerson {

    public enum Phase {
        GO_TO_STORE,
        DIALOG,
        LEAVE
    }

    private Phase phase = Phase.GO_TO_STORE;

    private Animation<TextureRegion> walkRight, walkLeft;
    private TextureRegion standTexture, buyingTexture;
    private float stateTime;

    public OldWomanWithRadio() {
        size = new PersonSize(40 * 3.5f, 60 * 3.5f);
        refYSpeed = 20f;
        updateFromReference();

        float startX = -200;
        size.setRefPosition(startX, 270);
        updateReferenceFromActual();
        state = PersonState.RIGHT;

        setAssets();
    }

    private void setAssets(){
        walkRight = Assets.getAssets().getAnimation("gamescene/person/quests/oldwoman_with_radio/oldwoman_with_radio_walking", "Right");
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkRight.setFrameDuration(.15f);

        walkLeft = Assets.getAssets().getAnimation("gamescene/person/quests/oldwoman_with_radio/oldwoman_with_radio_walking", "Left");
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        walkLeft.setFrameDuration(.15f);

        standTexture = new TextureRegion(Assets.getAssets().getTexture("gamescene/person/quests/oldwoman_with_radio/oldwoman_with_radio_staying.png"));
        buyingTexture = new TextureRegion(Assets.getAssets().getTexture("gamescene/person/quests/oldwoman_with_radio/oldwoman_with_radio_buying.png"));
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        switch (phase) {
            case GO_TO_STORE:
                // Стандартное поведение очереди
                getToStore(delta);
                // Если оказался активным покупателем, onReachCounter переключит фазу
                break;
            case DIALOG:
                // Диалог уже запущен, ждём завершения
                // и открываем мини игру
                break;
            case LEAVE:
                break;
        }

        animate(delta, batch);
    }

    private void animate(float delta, Batch batch) {
        TextureRegion region;
        switch (state) {
            case RIGHT:
                region = walkRight.getKeyFrame(stateTime);
                moveRight(delta);
                break;
            case LEFT:
                region = walkLeft.getKeyFrame(stateTime);
                moveLeft(delta);
                break;
            case BUYING:
                region = buyingTexture;
                break;
            default:
                region = standTexture;
        }
        batch.draw(region, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    private void moveRight(float delta) {
        size.setX(size.getX() + speed * delta);
        updateReferenceFromActual();
    }

    private void moveLeft(float delta) {
        size.setX(size.getX() - speed * delta);
        updateReferenceFromActual();
    }

    @Override
    protected void onReachCounter() {
        phase = Phase.DIALOG;
        state = PersonState.BUYING;
        startDialog();
    }

    private void startDialog() {
        DialogNode declineNode = new DialogNode(
            new Text("Oh, that's a shame. I'll go to my neighbor's..."),
            new AnswerOption("Goodbye", false, true, null),
            null, null
        );
        DialogNode questionNode = new DialogNode(
            new Text("Hello! I bought a new radio, but I can't tune it. Can you help an old lady?"),
            new AnswerOption("Sorry, I'm busy right now.", false, true, declineNode),
            new AnswerOption("Of course, let me help!", false, true, null),
            null
        );

        int[] selectedAnswerIndex = new int[1];

        DialogWindow dialog = new DialogWindow(questionNode);
        dialog.setOnAnswerSelected(index -> {
            selectedAnswerIndex[0] = index;
        });
        dialog.setOnFinished(() -> {
            if (selectedAnswerIndex[0] == 1) {
                Main.getInstance().getGameScreen().openRadioModal(this::showThanksAndLeave);
            } else {
                onLeaveQueue();
            }
        });
        Main.getInstance().getGameScreen().getStage().addActor(dialog);
        dialog.show();
    }

    private void showThanksAndLeave() {
        DialogNode thanksNode = new DialogNode(
            new Text("Thank you, grandson! Here's your reward."),
            new AnswerOption("You're welcome!", false, true, null),
            null, null
        );
        DialogWindow thanksDialog = new DialogWindow(thanksNode);
        thanksDialog.setOnFinished(() -> {
            UpgradeScene.getInstance().setUpgradeAvailable(UpgradeScene.Upgrades.OLD_RADIO);
            onLeaveQueue();
        });
        Main.getInstance().getGameScreen().getStage().addActor(thanksDialog);
        thanksDialog.show();
    }

    @Override
    protected void onLeaveQueue() {
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
        state = Math.random() > 0.5 ? PersonState.LEFT : PersonState.RIGHT;
        phase = Phase.LEAVE;
    }

    @Override
    protected void onQueueWaiting() {

    }

    @Override
    public boolean isEnded() {
        return phase == Phase.LEAVE && (size.getX() < -200 || size.getX() > Gdx.graphics.getWidth() + 200);
    }
}
