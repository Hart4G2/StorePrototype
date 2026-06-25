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
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class FishingMen extends QueuePerson {

    public enum Phase {
        GO_TO_STORE,
        DIALOG,
        LEAVE
    }

    private Phase phase = Phase.GO_TO_STORE;
    private Animation<TextureRegion> men1WalkRight, men1WalkLeft;
    private Animation<TextureRegion> men2WalkRight, men2WalkLeft;
    private TextureRegion men1StandTexture, men1BuyingTexture;
    private TextureRegion men2StandTexture, men2BuyingTexture;
    private float stateTime;
    private float refPadding, padding;

    public FishingMen() {
        refYSpeed = 20f;
        refPadding = 40f;
        updateFromReference();

        size = new PersonSize(40 * 3.5f, 60 * 3.5f);

        float startX = -200;
        size.setRefPosition(startX, 270);
        updateReferenceFromActual();
        state = PersonState.RIGHT;

        setAssets();
    }

    private void setAssets(){
        initAnimations("man1_walking");
        initAnimations("man2_walking");

        men1StandTexture = new TextureRegion(new Texture("gamescene/person/quests/fishing_men/man1_staying.png"));
        men1BuyingTexture = new TextureRegion(new Texture("gamescene/person/quests/fishing_men/man1_buying.png"));

        men2StandTexture = new TextureRegion(new Texture("gamescene/person/quests/fishing_men/man2_staying.png"));
        men2BuyingTexture = new TextureRegion(new Texture("gamescene/person/quests/fishing_men/man2_buying.png"));
    }

    private void initAnimations(String filename){
        Texture texture = new Texture("gamescene/person/quests/fishing_men/" + filename + ".png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/person/quests/fishing_men/" + filename + ".json"));
        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> anim = createAnimation(tag, data, texture);
            if (tag.name.equals("Right")) men1WalkRight = anim;
            else if (tag.name.equals("Left")) men1WalkLeft = anim;
            else if (tag.name.equals("Right2")) men2WalkRight = anim;
            else if (tag.name.equals("Left2")) men2WalkLeft = anim;
        }
    }

    private Animation<TextureRegion> createAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();
        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            regions.add(new TextureRegion(texture, f.x, f.y, f.w, f.h));
        }
        return new Animation<>(0.15f, regions, Animation.PlayMode.LOOP);
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
        TextureRegion men1, men2;
        switch (state) {
            case RIGHT:
                men1 = men1WalkRight.getKeyFrame(stateTime);
                men2 = men2WalkRight.getKeyFrame(stateTime);
                moveRight(delta);
                break;
            case LEFT:
                men1 = men1WalkLeft.getKeyFrame(stateTime);
                men2 = men2WalkLeft.getKeyFrame(stateTime);
                moveLeft(delta);
                break;
            case BUYING:
                men1 = men1BuyingTexture;
                men2 = men2BuyingTexture;
                break;
            default:
                men1 = men1StandTexture;
                men2 = men2StandTexture;
        }
        batch.draw(men1, size.getX(), size.getY(), size.getWidth(), size.getHeight());

        batch.draw(men2, size.getX() + padding, size.getY(), size.getWidth(), size.getHeight());
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
        DialogNode noNode = new DialogNode(
            new Text("Fine, we'll handle it ourselves..."),
            null, new AnswerOption("Bye.", false, true, null), null
        );
        DialogNode questionNode = new DialogNode(
            new Text("Hey, buddy! We were getting ready to go fishing, but the line got terribly tangled. Can you help untangle it?"),
            new AnswerOption("No, I don't have time.", false, true, noNode),
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
                Main.getInstance().getGameScreen().openFishingModal(this::showThanksAndLeave);
            } else {
                onLeaveQueue();
            }
        });
        Main.getInstance().getGameScreen().getStage().addActor(dialog);
        dialog.show();
    }

    private void showThanksAndLeave() {
        DialogNode thanksNode = new DialogNode(
            new Text("Thank you, buddy! Here's your reward."),
            new AnswerOption("You're welcome!", false, true, null),
            null, null
        );
        DialogWindow thanksDialog = new DialogWindow(thanksNode);
        thanksDialog.setOnFinished(() -> {
            UpgradeScene.getInstance().setUpgradeAvailable(UpgradeScene.Upgrades.REPUTATION_INCREASE);
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

    @Override
    protected void updateFromReference() {
        super.updateFromReference();
        padding = ScreenScaler.scaleX(refPadding);
    }
}
