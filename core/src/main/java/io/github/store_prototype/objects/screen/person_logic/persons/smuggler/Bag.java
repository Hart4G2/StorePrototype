package io.github.store_prototype.objects.screen.person_logic.persons.smuggler;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.utils.Assets;
import io.github.store_prototype.utils.size.ScreenScaler;

public class Bag extends Actor {

    private float refX, refY, refWidth, refHeight;
    private float refPad = 15f;
    private float refCloseButtonWidth = 100f;
    private float refCloseButtonHeight = 50f;

    public TextButton closeButton;

    private Texture backgroundTexture;
    private float x, y, width, height;

    private List<BagItem> items;
    private boolean isOpen;

    public Bag() {
        this.backgroundTexture = new Texture("gamescene/smuggler/smuggler_bag.png");

        refWidth = REF_WIDTH / 4f;
        refHeight = REF_HEIGHT / 4.5f;
        refX = REF_WIDTH / 2f - refWidth / 2f;
        refY = REF_HEIGHT / 1.5f;

        closeButton = new TextButton("Close", Assets.getAssets().getSkin(), "red");
        closeButton.addListener(generateCancelListener());

        items = new ArrayList<>();
        generateItems();

        updateFromReference();

        isOpen = false;
    }

    private void updateFromReference() {
        x = ScreenScaler.scaleX(refX);
        y = ScreenScaler.scaleY(refY);
        width = ScreenScaler.scaleX(refWidth);
        height = ScreenScaler.scaleY(refHeight);

        float btnWidth = ScreenScaler.scaleX(refCloseButtonWidth);
        float btnHeight = ScreenScaler.scaleY(refCloseButtonHeight);
        float btnX = x + 3 * width / 8;
        float btnY = y - height / 8f;
        closeButton.setBounds(btnX, btnY, btnWidth, btnHeight);
    }

    private ClickListener generateCancelListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                isOpen = false;
                setVisible(false);
            }
        };
    }

    private void generateItems() {
        addItem(new BagItem(Inventory.Items.ALCOHOL));
        addItem(new BagItem(Inventory.Items.MAGAZINES));
        addItem(new BagItem(Inventory.Items.AUDIOCASSETTES));
    }

    private void addItem(BagItem item) {
        items.add(item);
    }

    private void recalculateAllItemsCoordinates() {
        if (items.isEmpty()) return;

        float pad = ScreenScaler.scaleX(refPad);
        float startX = x + pad;
        float startY = y + height - pad;

        float currentX = startX;
        float currentY = startY;

        for (BagItem item : items) {
            float itemWidth = item.getWidth();
            float itemHeight = item.getHeight();

            if (currentX + itemWidth > x + width - pad) {
                currentX = startX;
                currentY -= itemHeight + pad;
            }

            item.setBounds(currentX, currentY - itemHeight, itemWidth, itemHeight);
            currentX += itemWidth + pad;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(backgroundTexture, x, y, width, height);
    }

    public void resize() {
        updateFromReference();
        recalculateAllItemsCoordinates();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        closeButton.setVisible(visible);
        for (BagItem i : items) {
            i.setVisible(visible);
        }
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(closeButton);
            for (BagItem item : items) {
                stage.addActor(item);
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
