package io.github.store_prototype.objects.screen.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.gui.NotifyPlayerValuesChangedEvent;
import io.github.store_prototype.objects.values.PlayerValues;
import io.github.store_prototype.objects.values.Value;

public class Product extends Table implements SimpleEventListener {

    private Value value;
    private Label amountLabel;
    private Image itemImage;

    public Product(Skin skin, Texture imageTexture, Value value) {
        super(skin);
        this.value = value;

        itemImage = new Image(imageTexture);
        amountLabel = new Label(value.getAmount() + "", skin);

        updateDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        add(itemImage).pad(10).left().width(itemImage.getWidth()).height(itemImage.getHeight());
        row();
        add(amountLabel).expand().pad(10).center();

        pack();
    }

    private void updateDimensions(float screenWidth, float screenHeight) {
        itemImage.setSize(screenWidth / 32f, screenHeight / 18f);
    }

    @Override
    public void handleChange(Event event) {
        try {
            NotifyPlayerValuesChangedEvent e = (NotifyPlayerValuesChangedEvent) event;
            updateValue();
        } catch (ClassCastException ignore) {}
    }

    private void updateValue() {
        value.setAmount(PlayerValues.getPlayerValues().getAmount(value.getName()));
        amountLabel.setText(value.getAmount() + "");
    }

    public void resize(float screenWidth, float screenHeight) {
        updateDimensions(screenWidth, screenHeight);
        getCell(itemImage).width(itemImage.getWidth()).height(itemImage.getHeight());
        invalidate();
        pack();
    }
}
