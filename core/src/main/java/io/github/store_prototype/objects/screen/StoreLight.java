package io.github.store_prototype.objects.screen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class StoreLight {

    private float x, y, width, height;

    private Texture lightTexture;

    public StoreLight() {
        lightTexture = new Texture("gamescene/store/store_light.png");
    }

    public void render(Batch batch){
        batch.draw(lightTexture, x, y, width, height);
    }

    public void resize(float worldWidth, float worldHeight){
        x = worldWidth / 3.3f;
        y = worldHeight / 5.14f;
        width = worldWidth / 2.47f;
        height = worldHeight / 5.39f;
    }
}
