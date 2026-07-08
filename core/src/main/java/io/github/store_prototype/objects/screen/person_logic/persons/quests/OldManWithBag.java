package io.github.store_prototype.objects.screen.person_logic.persons.quests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.screen.GUI.Inventory;
import io.github.store_prototype.objects.screen.GUI.Item;
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

public class OldManWithBag extends QueuePerson {

    public enum Phase {
        GO_TO_BENCH,
        AT_BENCH,
        LEAVE_BENCH,
        WAITING,
        GO_TO_STORE,
        DIALOG,
        GOING_TO_TAKE_BAG,
        TAKE_BAG,
        LEAVE
    }

    private Phase phase = Phase.GO_TO_BENCH;

    private Animation<TextureRegion> walkRight, walkLeft, walkLeftWithoutBag, walkRightWithoutBag, sitting, gettingUp;
    private TextureRegion standTexture, buyingTexture;
    private float stateTime;

    private Image bench;
    private Stage stage;

    private Image bagOnBench;
    private Texture bagTexture;

    private float timer;
    private final float sitDuration = 3f;
    private final float takingBagDuration = 1f;
    private final float returnDelay = 10f; // 10 секунд для теста

    private boolean bagPickedUp = false;
    private boolean dialogFinished = false;
    private boolean hasBag = true;

    // Координаты для лавки
    private float benchX, benchY;

    public OldManWithBag(Image bench, Stage stage) {
        this.bench = bench;
        this.stage = stage;

        size = new PersonSize(40 * 3f, 60 * 3f);
        refYSpeed = 8f;
        updateFromReference();

        setAssets();

        size.setRefPosition(-200, 300);
        updateReferenceFromActual();
        state = PersonState.RIGHT;

        benchX = bench.getX() + bench.getWidth() / 2 - size.getWidth() / 2;
        benchY = bench.getY();
    }

    private void setAssets(){
        Assets assets = Assets.getAssets();

        walkRight = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_walking", "Right");
        walkRight.setFrameDuration(.15f);
        walkRight.setPlayMode(Animation.PlayMode.LOOP);
        walkLeft = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_walking", "Left");
        walkLeft.setFrameDuration(.15f);
        walkLeft.setPlayMode(Animation.PlayMode.LOOP);
        walkRightWithoutBag = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_walking", "Right_without_bag");
        walkRightWithoutBag.setFrameDuration(.15f);
        walkRightWithoutBag.setPlayMode(Animation.PlayMode.LOOP);
        walkLeftWithoutBag = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_walking", "Left_without_bag");
        walkLeftWithoutBag.setFrameDuration(.15f);
        walkLeftWithoutBag.setPlayMode(Animation.PlayMode.LOOP);
        sitting = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_sitting", "Sitting");
        sitting.setFrameDuration(.15f);
        gettingUp = assets.getAnimation("gamescene/person/quests/oldman_with_bag/person_8_sitting", "Getting_up");
        gettingUp.setFrameDuration(.15f);

        standTexture = new TextureRegion(assets.getTexture("gamescene/person/quests/oldman_with_bag/person_8_standing.png"));
        bagTexture = assets.getTexture("gamescene/person/quests/oldman_with_bag/bag.png");
        buyingTexture = new TextureRegion(assets.getTexture("gamescene/person/quests/oldman_with_bag/person_8_buying.png"));
    }

    @Override
    public void render(float delta, Batch batch) {
        stateTime += delta;

        if (debugFastTimers && timer > 0) {
            timer = 0;
        }

        switch (phase) {
            case GO_TO_BENCH:
                if (size.getY() < benchY) {
                    float newY = size.getY() + ySpeed * delta;
                    if (newY > benchY) newY = benchY;
                    size.setY(newY);
                    updateReferenceFromActual();
                }
                if (size.getX() >= benchX && size.getY() >= benchY) {
                    size.setX(benchX);
                    size.setY(benchY);
                    updateReferenceFromActual();
                    stateTime = 0;
                    state = PersonState.SITTING;
                    timer = sitDuration;
                    phase = Phase.AT_BENCH;
                }
                break;
            case AT_BENCH:
                timer -= delta;
                if (timer <= 0 && state != PersonState.GETTING_UP) {
                    dropBag();
                    state = PersonState.GETTING_UP;
                    stateTime = 0;
                    hasBag = false;
                }
                break;
            case LEAVE_BENCH:
                if(size.getY() > 300){
                    size.setY(size.getY() - ySpeed * delta);
                    updateReferenceFromActual();
                }

                if (size.getX() < -200) {
                    timer = returnDelay;
                    phase = Phase.WAITING;
                }
                break;
            case WAITING:
                timer -= delta;
                if (timer <= 0) {
                    // Перемещаемся к магазину снизу
                    resetForStore();
                    phase = Phase.GO_TO_STORE;
                }
                break;
            case GO_TO_STORE:
                // Стандартное поведение очереди
                getToStore(delta);
                // Если оказался активным покупателем, onReachCounter переключит фазу
                break;
            case DIALOG:
                // Диалог уже запущен, ждём завершения
                break;
            case GOING_TO_TAKE_BAG:
                if (size.getY() < benchY) {
                    float newY = size.getY() + ySpeed * delta;
                    if (newY > benchY) newY = benchY;
                    size.setY(newY);
                    updateReferenceFromActual();
                }
                if (size.getX() >= benchX && size.getY() >= benchY) {
                    size.setX(benchX);
                    size.setY(benchY);
                    updateReferenceFromActual();
                    state = PersonState.BUYING;
                    bagOnBench.remove();
                    timer = takingBagDuration;
                    phase = Phase.TAKE_BAG;
                }
                break;
            case TAKE_BAG:
                timer -= delta;
                if (timer <= 0 && !hasBag) {
                    state = PersonState.RIGHT;
                    hasBag = true;
                }
                break;
            case LEAVE:
                if(!hasBag) hasBag = true;
                if (size.getX() < -200 || size.getX() > Gdx.graphics.getWidth() + 200) {
                    // Можно пометить как завершённого для удаления
                }
                break;
        }

        animate(delta, batch);
    }

    private void dropBag() {
        bagOnBench = new Image(bagTexture);
        bagOnBench.setSize(50, 50);
        bagOnBench.setPosition(bench.getX() + bench.getWidth() - 75, bench.getY() + bench.getHeight() / 2 - 15);
        bagOnBench.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!bagPickedUp) {
                    bagPickedUp = true;
                    Inventory.getInstance().addItem(new Item(Inventory.Items.BAG, "gamescene/person/quests/oldman_with_bag/bag.png"));
                    bagOnBench.remove();
                }
            }
        });
        stage.addActor(bagOnBench);
    }

    private void resetForStore() {
        refYSpeed = 20f;
        size.setRefPosition(-200, 300);
        updateReferenceFromActual();
        state = PersonState.RIGHT;
        isInQueue = false;
    }

    @Override
    protected void onReachCounter() {
        phase = Phase.DIALOG;
        startDialog();
        state = PersonState.BUYING;
    }

    private boolean bagReturned = false;

    private void startDialog() {
        // Текст благодарности (конечный узел)
        DialogNode thanksNode = new DialogNode(
            new Text("Thank you, grandson! Here's your reward."),
            null,
            new AnswerOption("Thank you!", false, true, null),
            null
        );
        // Текст сожаления
        DialogNode pityNode = new DialogNode(
            new Text("Ah, that's a pity. Oh well..."),
            null,
            emptyAnswer(),
            null
        );
        // Текст недоумения
        DialogNode confusedNode = new DialogNode(
            new Text("The package, I say, the package! Forget it."),
            null,
            emptyAnswer(),
            null
        );

        Inventory inventory = Inventory.getInstance();

        // Главный вопрос
        DialogNode questionNode = new DialogNode(
            new Text("Hello! I forgot my package here. Have you seen it?"),
            new AnswerOption("No, I haven't seen it.", false, !inventory.hasItem(Inventory.Items.BAG), pityNode),
            new AnswerOption("Yes, here is your package.", true, inventory.hasItem(Inventory.Items.BAG), thanksNode),
            new AnswerOption("What package?", false, !inventory.hasItem(Inventory.Items.BAG), confusedNode)
        );

        DialogWindow dialogWindow = getDialogWindow(questionNode);

        stage.addActor(dialogWindow);
        dialogWindow.show();
    }

    private AnswerOption emptyAnswer() {
        return new AnswerOption("...", false, true, null);
    }

    private DialogWindow getDialogWindow(DialogNode questionNode) {
        DialogWindow dialogWindow = new DialogWindow(questionNode);
        dialogWindow.setOnAnswerSelected(index -> {
            if (index == 1 && Inventory.getInstance().hasItem(Inventory.Items.BAG)) { // выбран ответ "Да, вот ваш пакет"
                bagReturned = true;
                Inventory.getInstance().removeItem(Inventory.Items.BAG);
                UpgradeScene.getInstance().setUpgradeAvailable(UpgradeScene.Upgrades.VENDING_MACHINE);
            }
        });
        dialogWindow.setOnFinished(this::onLeaveQueue);
        return dialogWindow;
    }

    @Override
    protected void onLeaveQueue() {
        SimplePublisher.getPublisher().publish(new PersonLeftStoreEvent());
        if(bagReturned){
            state = Math.random() > 0.5 ? PersonState.LEFT : PersonState.RIGHT;
            phase = Phase.LEAVE;
        } else {
            phase = Phase.GOING_TO_TAKE_BAG;
            state = PersonState.RIGHT;
            bagOnBench.clearListeners();
        }
    }

    @Override
    protected void onQueueWaiting() {
        // ничего
    }

    @Override
    public boolean isEnded() {
        return phase == Phase.LEAVE && (size.getX() < -200 || size.getX() > Gdx.graphics.getWidth() + 200);
    }

    private void animate(float delta, Batch batch) {
        TextureRegion region;
        switch (state) {
            case RIGHT:
                if(hasBag) {
                    region = walkRight.getKeyFrame(stateTime);
                } else {
                    region = walkRightWithoutBag.getKeyFrame(stateTime);
                }
                moveRight(delta);
                break;
            case LEFT:
                if(hasBag) {
                    region = walkLeft.getKeyFrame(stateTime);
                } else {
                    region = walkLeftWithoutBag.getKeyFrame(stateTime);
                }
                moveLeft(delta);
                break;
            case SITTING:
                region = sitting.getKeyFrame(stateTime);
                break;
            case GETTING_UP:
                region = gettingUp.getKeyFrame(stateTime);
                if(gettingUp.isAnimationFinished(stateTime)){
                    state = PersonState.LEFT;
                    phase = Phase.LEAVE_BENCH;
                }
                break;
            case BUYING:
                region = buyingTexture;
                break;
            default:
                region = standTexture;
        }
        batch.draw(region, size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    private static boolean debugFastTimers = false;

    public static void setDebugFastTimers(boolean fast) {
        debugFastTimers = fast;
    }

    public void debugSkipToPhase(Phase phase) {
        this.phase = phase;
        switch (phase) {
            case AT_BENCH:
                size.setRefPosition(1380, 400);
//                size.setRefPosition(benchX, benchY);
                updateReferenceFromActual();
                state = PersonState.SITTING;
                timer = sitDuration;
                hasBag = true;
                break;
            case GO_TO_STORE:
                resetForStore();
                break;
            case DIALOG:
                size.setRefPosition(770, 380);
                updateReferenceFromActual();
                state = PersonState.BUYING;
                startDialog();
                break;
        }
        updateReferenceFromActual();
    }
}

/*
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                    switch (keycode) {
                        case Input.Keys.F1:
                            OldmanWithBag.setDebugFastTimers(true);
                            System.out.println("Debug: Fast timers ON");
                            return true;
                        case Input.Keys.F2:
                            OldmanWithBag.setDebugFastTimers(false);
                            System.out.println("Debug: Fast timers OFF");
                            return true;
                        case Input.Keys.F3:
                            if (oldman != null) {
                                oldman.debugSkipToPhase(OldmanWithBag.Phase.AT_BENCH);
                            }
                            return true;
                        case Input.Keys.F4:
                            if (oldman != null) {
                                oldman.debugSkipToPhase(OldmanWithBag.Phase.GO_TO_STORE);
                            }
                            return true;
                        case Input.Keys.F5:
                            if (oldman != null) {
                                oldman.debugSkipToPhase(OldmanWithBag.Phase.DIALOG);
                            }
                            return true;
                        case Input.Keys.F6:
                            // Добавить пакет в инвентарь
                            if (!inventory.hasItem("bag")) {
                                inventory.addItem(new Item("bag", "gamescene/person/quests/oldman_with_bag/bag.png"));
                                System.out.println("Debug: Bag added to inventory");
                            }
                            return true;
                        case Input.Keys.F7:
                            inventory.removeItem("bag");
                            System.out.println("Debug: Bag removed from inventory");
                            return true;
                    }
                }
                return false;
            }
        });
 */
