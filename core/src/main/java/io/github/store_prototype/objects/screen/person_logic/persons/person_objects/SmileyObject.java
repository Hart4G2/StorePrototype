package io.github.store_prototype.objects.screen.person_logic.persons.person_objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.size.ScreenScaler;

public class SmileyObject {

    private static final String DISLIKE_TEXTURE = "products/crying_smiley.png";
    private static final String DISLIKE_JSON = "products/crying_smiley.json";
    private static final String LIKE_TEXTURE = "products/smiling_smiley.png";
    private static final String LIKE_JSON = "products/smiling_smiley.json";

    // Эталонные размеры (при 1600x900)
    private static final float REF_WIDTH = 1600f / 19.7f;   // ≈81.22
    private static final float REF_HEIGHT = 900f / 14.2f;   // ≈63.38

    private Animation<TextureRegion> animation;
    private float stateTime;
    private float width, height;
    private boolean isPlaying;
    private int playedCount;

    public SmileyObject(boolean like) {
        if (like) {
            init(LIKE_TEXTURE, LIKE_JSON);
        } else {
            init(DISLIKE_TEXTURE, DISLIKE_JSON);
        }
        playedCount = 0;
        updateFromReference();
    }

    private void init(String texturePath, String jsonPath) {
        Texture texture = new Texture(texturePath);
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal(jsonPath));

        for (FrameTag tag : data.meta.frameTags) {
            Animation<TextureRegion> animation = getTextureRegionAnimation(tag, data, texture);
            if (tag.name.equals("anim")) {
                this.animation = animation;
            }
        }
    }

    private static Animation<TextureRegion> getTextureRegionAnimation(FrameTag tag, AsepriteData data, Texture texture) {
        Array<TextureRegion> regions = new Array<>();
        for (int i = tag.from; i <= tag.to; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
            regions.add(region);
        }
        return new Animation<>(0.1f, regions, Animation.PlayMode.NORMAL);
    }

    private void updateFromReference() {
        width = ScreenScaler.scaleX(REF_WIDTH);
        height = ScreenScaler.scaleY(REF_HEIGHT);
    }

    public void render(float delta, Batch batch, float cenerX, float y) {
        if (isPlaying) {
            stateTime += delta;
            batch.draw(animation.getKeyFrame(stateTime), cenerX - width / 2f, y, width, height);
            if (animation.isAnimationFinished(stateTime)) {
                if (playedCount == 2) {
                    isPlaying = false;
                } else {
                    playedCount++;
                    stateTime = 0;
                }
            }
        }
    }

    public void play() {
        isPlaying = true;
    }

    public void resize() {
        updateFromReference();
    }
}
