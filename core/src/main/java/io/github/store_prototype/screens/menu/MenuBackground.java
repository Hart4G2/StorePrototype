package io.github.store_prototype.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.assets.Assets;

public class MenuBackground extends Group {

    private static TextureRegionDrawable leftTexture, rightTexture, straightTexture;
    private static boolean texturesLoaded = false;

    private Image leftImage;
    private Image rightImage;
    private Image straightImage;
    private Image activeImage;

    private static final float FADE_DURATION = .05f;

    public MenuBackground() {
        if (!texturesLoaded) {
            loadTextures();
        }

        leftImage = new Image(leftTexture);
        rightImage = new Image(rightTexture);
        straightImage = new Image(straightTexture);

        addActor(leftImage);
        addActor(rightImage);
        addActor(straightImage);

        setImagesBounds();

        straightImage.getColor().a = 1f;
        leftImage.getColor().a = 0f;
        rightImage.getColor().a = 0f;
        activeImage = straightImage;
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setImagesBounds();
    }

    private void setImagesBounds() {
        leftImage.setBounds(0, 0, getWidth(), getHeight());
        rightImage.setBounds(0, 0, getWidth(), getHeight());
        straightImage.setBounds(0, 0, getWidth(), getHeight());
    }

    public void setLeftTexture() {
        switchTo(leftImage);
    }

    public void setRightTexture() {
        switchTo(rightImage);
    }

    public void setStraightTexture() {
        switchTo(straightImage);
    }

    boolean transitioning;

    private void switchTo(Image targetImage) {
        if (activeImage == targetImage || transitioning) {
            return;
        }

        transitioning = true;

        activeImage.clearActions();
        targetImage.clearActions();

        activeImage.addAction(Actions.fadeOut(FADE_DURATION * 10));
        targetImage.addAction(Actions.sequence(
            Actions.fadeIn(FADE_DURATION),
            Actions.run(() -> {
                activeImage = targetImage;
                activeImage.setAlign(targetImage.getAlign());
                transitioning = false;
            })
        ));
    }

    private static void loadTextures() {
        Texture texture = Assets.getAssets().getTexture("main_screen.png");

        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("main_screen.json"));

        for (FrameTag tag : data.meta.frameTags) {
            TextureRegionDrawable drawable = createDrawable(tag, data, texture);
            switch (tag.name) {
                case "right":
                    rightTexture = drawable;
                    break;
                case "left":
                    leftTexture = drawable;
                    break;
                case "straight":
                    straightTexture = drawable;
                    break;
                default:
                    Gdx.app.log("MenuBackground", "Неизвестный тег: " + tag.name);
                    break;
            }
        }
        texturesLoaded = true;
    }

    private static TextureRegionDrawable createDrawable(FrameTag tag, AsepriteData data, Texture texture) {
        int i = tag.from;
        AsepriteFrame frameData = data.frames.get(i);
        Frame f = frameData.frame;
        return new TextureRegionDrawable(new TextureRegion(texture, f.x, f.y, f.w, f.h));
    }

    public static void dispose() {
        if (leftTexture != null) leftTexture.getRegion().getTexture().dispose();
        if (rightTexture != null) rightTexture.getRegion().getTexture().dispose();
        if (straightTexture != null) straightTexture.getRegion().getTexture().dispose();
        leftTexture = null;
        rightTexture = null;
        straightTexture = null;
        texturesLoaded = false;
    }
}
