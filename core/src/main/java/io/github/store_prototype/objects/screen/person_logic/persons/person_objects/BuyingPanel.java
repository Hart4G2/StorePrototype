package io.github.store_prototype.objects.screen.person_logic.persons.person_objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;
import io.github.store_prototype.objects.event_handling.events.gui.BuyingEvent;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.objects.values.PlayerValues;
import io.github.store_prototype.objects.values.Value;
import io.github.store_prototype.utils.Assets;

public class BuyingPanel extends Table {

    private Animation<TextureRegion> backgroundAnimation;
    private float stateTime;
    private float x, y, width, height;

    private boolean isBought;
    private Image itemImage;

    public BuyingPanel(Value value, int cost) {
        super(Assets.getAssets().getSkin());

        Texture texture = new Texture("products/element.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("products/element.json"));
        backgroundAnimation = getTextureRegionAnimation(data, texture);

        isBought = false;

        itemImage = new Image(getProductTexture(value));
        Label textLabel = new Label("BUY", getSkin());
        TextButton buyButton = new TextButton("Buy", getSkin());

        add(itemImage).expand().pad(10).center();
        row();
        add(textLabel).expand().pad(10).center();
        row();
        add(buyButton).expand().pad(10).center().height(30).width(90);

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (PlayerValues.getPlayerValues().buyProducts(cost, value)) {
                    SimplePublisher publisher = SimplePublisher.getPublisher();
                    publisher.publish(new BuyingEvent());
                    isBought = true;
                }
            }
        });

        updateDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void updateDimensions(float screenWidth, float screenHeight) {
        this.width = screenWidth / 16f;
        this.height = screenHeight / 4.5f;   // было /4.5f в конструкторе, оставим единообразно
        this.x = screenWidth / 2f - this.width / 2f;
        this.y = screenHeight / 1.5f;

        setBounds(x, y, width, height);

        itemImage.setSize(screenWidth / 5.5f, screenHeight / 10f);

        invalidate();
        pack();
    }

    private static Texture getProductTexture(Value value) {
        String productImageName = "cookies";
        switch (value.getName()) {
            case NEWSPAPERS:
                productImageName = "newspaper";
                break;
            case CHIPS:
                productImageName = "chips";
                break;
            case COKE:
                productImageName = "coke";
                break;
        }
        return new Texture("products/" + productImageName + ".png");
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();
        for (int i = 0; i <= 8; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }
        return new Animation<>(1f, regions, Animation.PlayMode.NORMAL);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible()) {
            stateTime += delta;
            setBackground(new TextureRegionDrawable(backgroundAnimation.getKeyFrame(stateTime)));
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setBounds(x, y, width, height);
    }

    public void resize(float screenWidth, float screenHeight) {
        updateDimensions(screenWidth, screenHeight);
    }

    public boolean isBought() {
        return isBought;
    }
}
