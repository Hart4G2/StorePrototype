package io.github.store_prototype.screens.menu.savings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.store_prototype.utils.assets.Assets;

public class SaveSlotActor extends Table {
    private final int slotIndex;
    private SaveSlotData data;
    private final Label titleLabel;
    private final Label infoLabel;
    private final TextButton selectButton;
    private boolean hovered;

    public SaveSlotActor(int slotIndex, SaveSlotData data, Runnable onSelect) {
        super(Assets.getAssets().getSkin());
        this.slotIndex = slotIndex;
        this.data = data;

        setWidth(250);
        setHeight(300);
        setTouchable(Touchable.enabled);
        setTransform(true);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setBackground(new TextureRegionDrawable(Assets.getAssets().getTexture("bg.png")));

        titleLabel = new Label("Slot " + (slotIndex + 1), Assets.getAssets().getSkin(), "title");
        titleLabel.setAlignment(Align.center);
        infoLabel = new Label(data.getSummary(), Assets.getAssets().getSkin());
        infoLabel.setAlignment(Align.center);
        infoLabel.setWrap(true);
        selectButton = new TextButton(data.isEmpty ? "New Game" : "Load", Assets.getAssets().getSkin());

        add(titleLabel).pad(20, 10, 10, 10).row();
        add(infoLabel).pad(10).expand().fillX().row();
        add(selectButton).pad(10, 20, 20, 20).width(160).height(50).row();

        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSelect.run();
            }
        });
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void updateData(SaveSlotData newData) {
        this.data = newData;
        infoLabel.setText(newData.getSummary());
        selectButton.setText(newData.isEmpty ? "New Game" : "Load");
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}
