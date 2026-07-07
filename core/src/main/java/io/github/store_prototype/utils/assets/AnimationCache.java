package io.github.store_prototype.utils.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.FrameTag;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;

public class AnimationCache {
    private final Assets assets;
    private final ObjectMap<String, Animation<TextureRegion>> cache = new ObjectMap<>();

    private static AnimationCache instance;
    public static AnimationCache getInstance() {
        if(instance == null){
            instance = new AnimationCache();
        }
        return instance;
    }

    public AnimationCache() {
        assets = Assets.getAssets();
    }

    public Animation<TextureRegion> getAnimation(String path, String tagName) throws Exception {
        String key = path + ":" + tagName;
        if (!cache.containsKey(key)) {
            Texture texture = assets.getTexture(path + ".png");
            String json = assets.getJsonString(path + ".json");
            AsepriteData data = new Json().fromJson(AsepriteData.class, json);
            Animation<TextureRegion> anim = buildAnimation(data, texture, tagName);
            cache.put(key, anim);
        }
        return cache.get(key);
    }

    private Animation<TextureRegion> buildAnimation(AsepriteData data, Texture texture, String tagName) throws Exception {
        for (FrameTag tag : data.meta.frameTags) {
            if(tag.name.equals(tagName)){
                Array<TextureRegion> regions = new Array<>();

                for (int i = tag.from; i <= tag.to; i++) {
                    AsepriteFrame frameData = data.frames.get(i);
                    Frame f = frameData.frame;
                    TextureRegion region = new TextureRegion(texture, f.x, f.y, f.w, f.h);
                    regions.add(region);
                }

                return new Animation<>(0.2f, regions, Animation.PlayMode.NORMAL);
            }
        }

        throw(new Exception("No tag with name " + tagName + " in animation."));
    }
}
