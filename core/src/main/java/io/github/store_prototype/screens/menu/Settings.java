package io.github.store_prototype.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {

    private static final String PREFS_NAME = "MyGamePrefs";
    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_IS_MUSIC_ON = "isMusicOn";
    private static final String KEY_IS_SOUND_ON = "isSoundOn";
    private static final String RESOLUTION_WIDTH = "resolutionWidth";
    private static final String RESOLUTION_HEIGHT = "resolutionHeight";
    private static final String FULLSCREEN = "fullscreen";

    private Preferences prefs;

    private static Settings instance;

    public static Settings getInstance() {
        if(instance == null){
            instance = new Settings();
        }
        return instance;
    }

    public Settings() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    public float getMusicVolume() {
        return prefs.getFloat(KEY_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        prefs.putFloat(KEY_MUSIC_VOLUME, volume);
        prefs.flush();
    }

    public boolean isMusicOn() {
        return prefs.getBoolean(KEY_IS_MUSIC_ON, true);
    }

    public void setMusicOn(boolean on) {
        prefs.putBoolean(KEY_IS_MUSIC_ON, on);
        prefs.flush();
    }

    public int getWidth() {
        return prefs.getInteger(RESOLUTION_WIDTH, 1600);
    }

    public int getHeight() {
        return prefs.getInteger(RESOLUTION_HEIGHT, 900);
    }
    public void setResolution(int width, int height) {
        prefs.putInteger(RESOLUTION_WIDTH, width);
        prefs.putInteger(RESOLUTION_HEIGHT, height);
        prefs.flush();
    }

    public boolean isFullscreen() {
        return prefs.getBoolean(FULLSCREEN, true);
    }
    public void setFullscreen(boolean on) {
        prefs.putBoolean(FULLSCREEN, on);
        prefs.flush();
    }
}
