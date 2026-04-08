package io.github.store_prototype.objects.screen.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.values.PlayerValues;
import io.github.store_prototype.objects.values.ValueNames;

public class ProductsPanel extends Table {

    private Array<Product> products;
    private float x, y, width, height;

    public ProductsPanel(Skin skin) {
        super(skin);

        updateDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        products = new Array<>();
        products.add(createProduct(ValueNames.CHIPS));
        products.add(createProduct(ValueNames.COKE));
        products.add(createProduct(ValueNames.COOKIES));
        products.add(createProduct(ValueNames.NEWSPAPERS));

        setBackground(new TextureRegionDrawable(new Texture("gamescene/products/panel.png")));

        add(products.get(0)).pad(10);
        add(products.get(1)).pad(10);
        add(products.get(2)).pad(10);
        add(products.get(3)).pad(10);

        pack();
    }

    private void updateDimensions(float screenWidth, float screenHeight) {
        width = screenWidth / 4f;
        height = screenHeight / 9f;
        x = screenWidth / 2f - width / 2f;
        y = screenHeight * 0.85f;
        setBounds(x, y, width, height);
    }

    public void resize(float screenWidth, float screenHeight) {
        updateDimensions(screenWidth, screenHeight);
        invalidate();
        for (Product product : products) {
            product.resize(screenWidth, screenHeight);
        }
    }

    private Product createProduct(ValueNames name) {
        Product product = new Product(getSkin(),
            new Texture("gamescene/products/" + name.getName().toLowerCase() + ".png"),
            PlayerValues.getPlayerValues().getValue(name));
        SimplePublisher.getPublisher().addListener(product);
        return product;
    }
}
