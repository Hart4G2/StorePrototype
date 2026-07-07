package io.github.store_prototype.utils.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.store_prototype.screens.menu.Settings;

public class Assets {

    private AssetManager manager;
    private Skin skin;
    public static final String MUSIC_MAIN_MENU = "song.mp3";
    private Music currentMusic;

    private static Assets assets;
    public static Assets getAssets() {
        if(assets == null){
            assets = new Assets();
        }
        return assets;
    }

    public Assets() {
        manager = new AssetManager();

        manager.load(MUSIC_MAIN_MENU, Music.class);
        manager.load("skin/skin.json", Skin.class);
        manager.finishLoading();

        skin = manager.get("skin/skin.json", Skin.class);
        for (Texture texture : skin.getAtlas().getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        if (!manager.isLoaded(fileName)) {
            manager.load(fileName, type);
            manager.finishLoading();
        }
        return manager.get(fileName, type);
    }

    public Texture getTexture(String path) {
        return get(path, Texture.class);
    }
    public String getJsonString(String path) {
        return Gdx.files.internal(path).readString();
    }

    public Animation<TextureRegion> getAnimation(String path, String tagName){
        try {
            return AnimationCache.getInstance().getAnimation(path, tagName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Skin getSkin() {
        return skin;
    }

    public void dispose() {
        manager.dispose();
    }

    public Music getMusic(String path) {
        return manager.get(path, Music.class);
    }

    public void playMusic(){
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }

        currentMusic = getMusic(MUSIC_MAIN_MENU);
        currentMusic.setLooping(true);

        currentMusic.setVolume(Settings.getInstance().isMusicOn() ? Settings.getInstance().getMusicVolume() : 0f);

        currentMusic.play();
    }

    public void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(Settings.getInstance().isMusicOn() ? Settings.getInstance().getMusicVolume() : 0f);
        }
    }
}
