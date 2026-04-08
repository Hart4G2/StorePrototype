package io.github.store_prototype.objects.screen.watch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.time.WorldTime;
import io.github.store_prototype.utils.size.ObjectSize;

public class Watch extends Actor {

    private static final String ANIMATION_TEXTURE = "gui/watch.png", ANIMATION_JSON = "gui/watch.json";
    private static final float REF_WIDTH = 75f, REF_X = 60f, REF_Y = 775f;

    private TextureRegion[] frames;
    private final ObjectSize size;
    private int totalFrames;

    public Watch() {
        size = new ObjectSize(REF_X, REF_Y, REF_WIDTH, REF_WIDTH);
        loadFrames();
        setBounds(size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    private void loadFrames() {
        Texture texture = new Texture(ANIMATION_TEXTURE);
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal(ANIMATION_JSON));

        FrameTag tag = data.meta.frameTags.get(0);
        int start = tag.from;
        int end = tag.to;
        totalFrames = end - start + 1;
        frames = new TextureRegion[totalFrames];

        for (int i = 0; i < totalFrames; i++) {
            AsepriteFrame frameData = data.frames.get(start + i);
            Frame f = frameData.frame;
            frames[i] = new TextureRegion(texture, f.x, f.y, f.w, f.h);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        float currentTime = WorldTime.getInstance().getCurrentTime();
        float dayLength = WorldTime.getInstance().getDayLength();
        int frameIndex = (int) (currentTime / dayLength * totalFrames);
        if (frameIndex >= totalFrames) {
            frameIndex = totalFrames - 1;
        }
        batch.draw(frames[frameIndex], size.getX(), size.getY(), size.getWidth(), size.getHeight());
    }

    public void resize() {
        size.updateFromReference();
    }
}
