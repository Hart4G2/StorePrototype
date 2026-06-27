package io.github.store_prototype.objects.screen.GUI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Item {
    private Inventory.Items name;
    private Image image;

    public Item(Inventory.Items name, String texturePath) {
        this.name = name;
        this.image = new Image(new Texture(texturePath));

        image.setName(name.toString());
    }

    public Item(Inventory.Items name, TextureRegion textureRegion) {
        this.name = name;
        this.image = new Image(textureRegion);

        image.setName(name.toString());
    }

    public Inventory.Items getName() { return name; }
    public Image getImage() { return image; }
}
