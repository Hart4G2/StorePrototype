package io.github.store_prototype.objects.screen.upgrades.window;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;

import io.github.store_prototype.objects.screen.upgrades.UpgradeScene;
import io.github.store_prototype.utils.assets.Assets;

public class UpgradeItem implements Disposable {

    private TextureRegion image;
    private Image upgradeImage;
    private Label nameLabel;
    private Label descLabel;
    private TextButton buyButton;

    public UpgradeItem(String imagePath, String name, String description, String buttonText, UpgradeScene.Upgrades upgrade) {
        image = new TextureRegion(new Texture(imagePath));
        upgradeImage = new Image(image);
        nameLabel = new Label(name, Assets.getAssets().getSkin());
        descLabel = new Label(description, Assets.getAssets().getSkin());
        buyButton = new TextButton(buttonText, Assets.getAssets().getSkin());

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UpgradeScene.getInstance().buyUpgrade(upgrade);
                System.out.println(name + " upgrade bought!");
            }
        });
    }

    public Image getImage() {
        return upgradeImage;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getDescriptionLabel() {
        return descLabel;
    }

    public TextButton getBuyButton() {
        return buyButton;
    }

    @Override
    public void dispose() {
        image.getTexture().dispose();
    }
}
