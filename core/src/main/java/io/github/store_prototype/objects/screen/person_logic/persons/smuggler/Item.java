package io.github.store_prototype.objects.screen.person_logic.persons.smuggler;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_HEIGHT;
import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.Assets;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.ui.CustomTextTooltip;

public class Item extends Actor {

    public enum ItemNames {
        ALCOHOL, MAGAZINES, AUDIOCASSETTES;
    }

    private float refWidth;
    private float refHeight;

    private TextureRegion textureWithoutStroke;
    private TextureRegion textureWithStroke;
    private boolean showStroke;

    public Item(ItemNames itemName) {
        generateTextures(itemName);

        refWidth = REF_WIDTH / 16f;
        refHeight = REF_HEIGHT / 9f;

        CustomTextTooltip textTooltip = new CustomTextTooltip(itemName.toString(), Assets.getAssets().getSkin(), "none");
        this.addListener(textTooltip);

        addListener(new ClickListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                showStroke = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                showStroke = false;
            }
        });

        updateFromReference();
    }

    private void updateFromReference() {
        setWidth(ScreenScaler.scaleX(refWidth));
        setHeight(ScreenScaler.scaleY(refHeight));
    }

    private void generateTextures(ItemNames itemName) {
        Texture texture = new Texture("gamescene/smuggler/" + itemName.toString().toLowerCase() + ".png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("gamescene/smuggler/" + itemName.toString().toLowerCase() + ".json"));

        AsepriteFrame frameData = data.frames.get(0);
        Frame f = frameData.frame;
        textureWithoutStroke = new TextureRegion(texture, f.x, f.y, f.w, f.h);

        frameData = data.frames.get(1);
        f = frameData.frame;
        textureWithStroke = new TextureRegion(texture, f.x, f.y, f.w, f.h);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (showStroke) {
            batch.draw(textureWithStroke, getX(), getY(), getWidth(), getHeight());
        } else {
            batch.draw(textureWithoutStroke, getX(), getY(), getWidth(), getHeight());
        }
    }
}
