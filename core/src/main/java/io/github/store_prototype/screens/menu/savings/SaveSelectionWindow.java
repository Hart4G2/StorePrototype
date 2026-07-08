package io.github.store_prototype.screens.menu.savings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.store_prototype.Main;
import io.github.store_prototype.utils.assets.Assets;

public class SaveSelectionWindow extends Table {
    private final SaveManager saveManager;
    private final SaveSlotActor[] slotActors = new SaveSlotActor[3];
    private final TextButton backButton;
    private final Stage stage;
    private final float[] currentAngle = new float[3];
    private final float[] targetAngle = new float[3];

    public SaveSelectionWindow(Stage stage) {
        super(Assets.getAssets().getSkin());
        this.stage = stage;
        this.saveManager = new SaveManager();

        setSize(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.7f);
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2f,
            (Gdx.graphics.getHeight() - getHeight()) / 2f);

        Label titleLabel = new Label("Select Save Slot", Assets.getAssets().getSkin(), "title");
        add(titleLabel).padBottom(40).colspan(3).row();

        HorizontalGroup slotsGroup = new HorizontalGroup();
        slotsGroup.space(120);
        slotsGroup.center();
        for (int i = 0; i < 3; i++) {
            SaveSlotData data = saveManager.getSlot(i);
            int slot = i;
            SaveSlotActor actor = new SaveSlotActor(i, data,
                () -> onSlotSelected(slot),
                () -> showDeleteConfirmation(slot));
            slotActors[i] = actor;
            slotsGroup.addActor(actor);
        }
        add(slotsGroup).pad(200, 0, 200, 0).center().row();

        backButton = new TextButton("Back", Assets.getAssets().getSkin(), "red");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
        add(backButton).width(180).height(60).colspan(3).center().padTop(20);
    }

    private void showDeleteConfirmation(int slot) {
        Dialog dialog = new Dialog("Delete Save", Assets.getAssets().getSkin()) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    saveManager.deleteSlot(slot);
                    refreshSlots();
                }
            }
        };
        dialog.text("Are you sure you want to delete this save?");
        // Добавляем отступы вокруг текста
        dialog.getContentTable().pad(20); // по 20 пикселей со всех сторон

        TextButton yesButton = new TextButton("Yes", Assets.getAssets().getSkin());
        TextButton noButton = new TextButton("No", Assets.getAssets().getSkin());
        yesButton.pad(10, 30, 10, 30);
        noButton.pad(10, 30, 10, 30);

        dialog.button(yesButton, true);
        dialog.button(noButton, false);

        dialog.show(stage);
    }

    private void onSlotSelected(int slot) {
        SaveSlotData data = saveManager.getSlot(slot);
        if (data.isEmpty) {
            data.isEmpty = false;
            data.day = 1;
            data.progressDescription = "New game";
            data.lastSaveTimestamp = System.currentTimeMillis();
            saveManager.saveSlot(data);
        }
        Main.getInstance().setGameScreen(slot);
        hide();
    }

    private void refreshSlots() {
        for (int i = 0; i < 3; i++) {
            slotActors[i].updateData(saveManager.getSlot(i));
        }
    }

    public void show() {
        refreshSlots();
        stage.addActor(this);
        setVisible(true);
    }

    public void hide() {
        Main.getInstance().getMenuScreen().showMainMenu();
        setVisible(false);
        remove();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        Vector2 mouseScreen = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        Vector2 mouseStage = stage.screenToStageCoordinates(new Vector2(mouseScreen));

        boolean mouseInWindow = mouseStage.x >= getX() && mouseStage.x <= getX() + getWidth()
            && mouseStage.y >= getY() && mouseStage.y <= getY() + getHeight();

        for (int i = 0; i < slotActors.length; i++) {
            SaveSlotActor slot = slotActors[i];

            if (!mouseInWindow) {
                slot.setHovered(false);
                targetAngle[i] = 0f;
                slot.setScale(1f);
                continue;
            }

            // Новый, стабильный способ определения попадания
            Vector2 localCoords = slot.screenToLocalCoordinates(new Vector2(mouseScreen));
            boolean inside = localCoords.x >= 0 && localCoords.x < slot.getWidth()
                && localCoords.y >= 0 && localCoords.y < slot.getHeight();
            slot.setHovered(inside);

            // Центр в stage-координатах для расчёта наклона
            Vector2 slotPos = slot.localToStageCoordinates(new Vector2(0, 0));
            float slotCenterX = slotPos.x + slot.getWidth() / 2f;
            float slotCenterY = slotPos.y + slot.getHeight() / 2f;

            float dx = mouseStage.x - slotCenterX;
            float dy = mouseStage.y - slotCenterY;
            float factorX = Math.max(-1f, Math.min(1f, dx / (Gdx.graphics.getWidth() / 2f)));
            float factorY = Math.max(-1f, Math.min(1f, dy / (Gdx.graphics.getHeight() / 2f)));

            float angleX = inside ? factorX * 15f : factorX * 2f;
            float angleY = inside ? factorY * 8f : factorY * 1f;
            targetAngle[i] = angleX + angleY;

            float smoothing = 10f;
            currentAngle[i] += (targetAngle[i] - currentAngle[i]) * Math.min(1f, delta * smoothing);
            slot.setRotation(currentAngle[i]);

            float targetScale = inside ? 1.2f : 1.0f;
            float currentScale = slot.getScaleX();
            slot.setScale(currentScale + (targetScale - currentScale) * Math.min(1f, delta * 10f));
        }
    }
}
