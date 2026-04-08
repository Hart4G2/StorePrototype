package io.github.store_prototype.objects.screen.person_logic.persons.smuggler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.event_handling.events.gui.BuyingEvent;
import io.github.store_prototype.objects.values.PlayerValues;
import io.github.store_prototype.objects.values.Value;
import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.utils.Assets;

public class SmugglerPanel extends Table {

    private Texture backgroundTexture;
    private float x, y, width, height;

    public SmugglerPanel(Value value, int cost) {
        super(Assets.getAssets().getSkin());

        backgroundTexture = new Texture("gamescene/person/smuggler/smuggler_bag.png");
        setBackground(new TextureRegionDrawable(backgroundTexture));

        this.width = Gdx.graphics.getWidth() / 8f;
        this.height = Gdx.graphics.getHeight() / 4.5f;
        x = Gdx.graphics.getWidth() / 2f - this.width / 2f;
        y = Gdx.graphics.getHeight() / 1.5f;

        Image itemImage = new Image(getProductTexture(value));
        Label textLabel = new Label("Item1", getSkin(), "white_14");
        TextButton cancelButton = new TextButton("Cancel", getSkin(), "red");
        TextButton sellButton = new TextButton("Buy", getSkin());

        itemImage.setSize(Gdx.graphics.getWidth() / 5.5f, Gdx.graphics.getHeight() / 10f);

        add(itemImage).expand().pad(10).center();
        row();
        add(textLabel).expand().pad(10).center();
        row();
        add(cancelButton).expand().pad(10).left().width(90).height(30);
        add(sellButton).expand().pad(10).right().width(90).height(30);

        cancelButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                setVisible(false);
            }
        });

        sellButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if(PlayerValues.getPlayerValues().buyProducts(cost, value)) {
                    SimplePublisher publisher = SimplePublisher.getPublisher();
                    publisher.publish(new BuyingEvent());
                }
            }
        });
    }

    private static Texture getProductTexture(Value value) {
        String productImageName = "cookies";
        switch (value.getName()){
            case NEWSPAPERS: {
                productImageName = "newspaper";
                break;
            }
            case CHIPS: {
                productImageName = "chips";
                break;
            }
            case COKE: {
                productImageName = "coke";
                break;
            }
        }

        return new Texture("products/" + productImageName + ".png");
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setBounds(x, y, width, height);
    }

    public void resize(float width, float height) {
        this.width = width / 8f;
        this.height = height / 6.75f;
        x = width / 2f - this.width / 2f;
        y = height / 1.5f;
    }
}

