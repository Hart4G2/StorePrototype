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
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.ObjectSize;
import io.github.store_prototype.utils.size.ScreenScaler;

public class SmileyObject {

    private static final String LIKE_PATH = "products/smiling_smiley";
    private static final String DISLIKE_PATH = "products/crying_smiley";

    // Эталонные размеры (при 1600x900)
    private static final float REF_WIDTH = 1600f / 19.7f;   // ≈81.22
    private static final float REF_HEIGHT = 900f / 14.2f;   // ≈63.38

    private Animation<TextureRegion> animation;
    private float stateTime;
    private ObjectSize size;
    private boolean isPlaying;
    private int playedCount;

    public SmileyObject(boolean like) {
        if (like) {
            init(LIKE_PATH);
        } else {
            init(DISLIKE_PATH);
        }
        playedCount = 0;
        size = new ObjectSize(REF_WIDTH, REF_HEIGHT);
    }

    private void init(String path) {
        animation = Assets.getAssets().getAnimation(path, "anim");
        animation.setFrameDuration(0.1f);
    }

    public void render(float delta, Batch batch, float cenerX, float y) {
        if (isPlaying) {
            stateTime += delta;
            batch.draw(animation.getKeyFrame(stateTime), cenerX - size.getWidth() / 2f, y, size.getWidth(), size.getHeight());
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
        size.updateFromReference();
    }
}
