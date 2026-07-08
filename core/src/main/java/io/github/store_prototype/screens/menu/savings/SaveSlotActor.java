package io.github.store_prototype.screens.menu.savings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.ScreenScaler;

public class SaveSlotActor extends Stack {
    private final int slotIndex;
    private SaveSlotData data;
    private final Label titleLabel;
    private final Label infoLabel;
    private final TextButton selectButton;
    private final Button deleteButton;
    private boolean hovered;

    private final Image backgroundImage;
    private ShaderProgram hologramShader;
    private float time = 0f;
    private boolean effectEnabled = true;

    private float rotationX = 0f;
    private float rotationY = 0f;

    private static final float REF_CARD_WIDTH = 300f;
    private static final float REF_CARD_HEIGHT = 350f;
    private static final float REF_PAD_SMALL = 5f;
    private static final float REF_PAD_MEDIUM = 10f;
    private static final float REF_PAD_LARGE = 20f;
    private static final float REF_DELETE_BUTTON_SIZE = 25f;
    private static final float REF_SELECT_BUTTON_WIDTH = 160f;
    private static final float REF_SELECT_BUTTON_HEIGHT = 50f;

    public SaveSlotActor(int slotIndex, SaveSlotData data, Runnable onSelect, Runnable onDelete) {
        this.slotIndex = slotIndex;
        this.data = data;

        float width = ScreenScaler.scaleUniform(REF_CARD_WIDTH);
        float height = ScreenScaler.scaleUniform(REF_CARD_HEIGHT);
        setWidth(width);
        setHeight(height);
        setTouchable(Touchable.enabled);
        setTransform(true);
        setOrigin(width / 2f, height / 2f);

        ShaderProgram.pedantic = false;
        hologramShader = new ShaderProgram(
            Gdx.files.internal("shaders/default.vert"),
            Gdx.files.internal("shaders/hologram.frag")
        );
        if (!hologramShader.isCompiled()) {
            Gdx.app.error("Shader", hologramShader.getLog());
        }

        backgroundImage = new Image(Assets.getAssets().getTexture("bg.png")) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                if (effectEnabled && hologramShader.isCompiled()) {
                    batch.setShader(hologramShader);
                    hologramShader.setUniformf("u_time", time);
                    hologramShader.setUniformf("u_rotationX", rotationX);
                    hologramShader.setUniformf("u_rotationY", rotationY);
                    hologramShader.setUniformf("u_pixelSize", 0.02f);
                    super.draw(batch, parentAlpha);
                    batch.setShader(null);
                } else {
                    super.draw(batch, parentAlpha);
                }
            }
        };
        backgroundImage.setFillParent(true);
        addActor(backgroundImage);

        Table content = new Table();
        content.setFillParent(true);
        content.setTouchable(Touchable.enabled);

        titleLabel = new Label("Slot " + (slotIndex + 1), Assets.getAssets().getSkin(), "title");
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(1.7f);
        titleLabel.setColor(Color.YELLOW);

        infoLabel = new Label(data.getSummary(), Assets.getAssets().getSkin(), "white_18");
        infoLabel.setAlignment(Align.center);
        infoLabel.setWrap(true);
        infoLabel.setColor(data.isEmpty ? Color.BROWN : Color.YELLOW);
        selectButton = new TextButton(data.isEmpty ? "New Game" : "Load", Assets.getAssets().getSkin());

        deleteButton = new TextButton("X", Assets.getAssets().getSkin(), "red");
        if (deleteButton.getStyle() == null) {
            deleteButton.setStyle(new Button.ButtonStyle());
            deleteButton.add(new Label("X", Assets.getAssets().getSkin()));
        }
        deleteButton.setVisible(!data.isEmpty);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onDelete.run();
            }
        });

        float padSmall = ScreenScaler.scaleUniform(REF_PAD_SMALL);
        float padMedium = ScreenScaler.scaleUniform(REF_PAD_MEDIUM);
        float padLarge = ScreenScaler.scaleUniform(REF_PAD_LARGE);
        float delBtnSize = ScreenScaler.scaleUniform(REF_DELETE_BUTTON_SIZE);
        float selBtnW = ScreenScaler.scaleUniform(REF_SELECT_BUTTON_WIDTH);
        float selBtnH = ScreenScaler.scaleUniform(REF_SELECT_BUTTON_HEIGHT);

        content.add(deleteButton).width(delBtnSize).height(delBtnSize).pad(padSmall).right().row();
        content.add(titleLabel).pad(padLarge).row();
        content.add(infoLabel).pad(padMedium).expand().fillX().row();
        content.add(selectButton).pad(padMedium, padLarge, padLarge, padLarge).width(selBtnW).height(selBtnH).row();

        selectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSelect.run();
            }
        });

        addActor(content);
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
        deleteButton.setVisible(!newData.isEmpty);
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (effectEnabled && hovered) time += delta;
    }

    public void setEffectEnabled(boolean enabled) {
        this.effectEnabled = enabled;
    }

    public void setHoloRotation(float angleX, float angleY) {
        this.rotationX = angleX;
        this.rotationY = angleY;
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return getHeight();
    }
}
