package io.github.store_prototype.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.store_prototype.screens.menu.Settings;

public class Assets {

    private AssetManager assetManager;
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
        assetManager = new AssetManager();

        load();
        skin = assetManager.get("skin/skin.json");

        for (Texture texture : skin.getAtlas().getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    private void load(){
        assetManager.load(MUSIC_MAIN_MENU, Music.class);
        assetManager.load("skin/skin.json", Skin.class);
        assetManager.finishLoading();
    }

    public Music getMusic(String path) {
        return assetManager.get(path, Music.class);
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

    public Skin getSkin() {
        return skin;
    }
}
