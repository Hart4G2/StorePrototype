package io.github.store_prototype.objects.screen.upgrades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BeautifulPriceTags extends Actor{

    private float x, y, width, height;
    private float textureAspect = 1f;

    private TextureRegion texture;

    public BeautifulPriceTags() {
        texture = new TextureRegion(new Texture("gamescene/upgrades/beautiful_price_tags.png"));

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(float worldWidth, float worldHeight){
        this.height = worldHeight  / 2.3f;
        this.width = this.height * textureAspect;
        this.x = worldWidth / 2f - this.width / 2f;
        this.y = worldHeight / 2.3f;

        setBounds(x, y, this.width, this.height);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if(isVisible()) {
            batch.draw(texture, x, y, width, height);
        }
    }
}
