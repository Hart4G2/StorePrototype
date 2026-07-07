package io.github.store_prototype.screens.menu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import io.github.store_prototype.utils.assets.Assets;

public class SettingsDialog extends Dialog {

    private final Skin skin;
    private SelectBox<String> resolutionSelect;
    private CheckBox soundCheckbox;
    private Slider volumeSlider;
    private CheckBox fullscreenCheckbox;

    public SettingsDialog(String title) {
        super(title, Assets.getAssets().getSkin());
        this.skin = Assets.getAssets().getSkin();
        this.setMovable(false);
        init();
    }

    private void init() {
        soundCheckbox = new CheckBox(" Sound On", skin);
        soundCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.getInstance().setMusicOn(soundCheckbox.isChecked());
                settingsChanged();
            }
        });

        volumeSlider = new Slider(0f, 1f, 0.01f, false, skin);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.getInstance().setMusicVolume(volumeSlider.getValue());
                settingsChanged();
            }
        });

        fullscreenCheckbox = getFullscreenCheckBox();
        resolutionSelect = getResolutionSelectBox();

        getContentTable().add(fullscreenCheckbox).left().padTop(10).row();
        getContentTable().add(new Label("Resolution:", skin)).left().padTop(10);
        getContentTable().add(resolutionSelect).width(200).padLeft(10).row();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.pad(8, 16, 8, 16);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        getContentTable().pad(20);
        getContentTable().add(soundCheckbox).left().row();
        getContentTable().add(new Label("Volume:", skin)).left().padTop(10);
        getContentTable().add(volumeSlider).width(200).padLeft(10).row();
        getContentTable().add(closeButton).center().padTop(30);

        key(Input.Keys.ESCAPE, true);

        update();
    }

    public void update(){
        soundCheckbox.setChecked(Settings.getInstance().isMusicOn());
        volumeSlider.setValue(Settings.getInstance().getMusicVolume());
        fullscreenCheckbox.setChecked(Settings.getInstance().isFullscreen());

        String currentRes = Settings.getInstance().getWidth() + "x" + Settings.getInstance().getHeight();
        resolutionSelect.setSelected(currentRes);
        updateResolutionControlsState(Settings.getInstance().isFullscreen());
    }

    private CheckBox getFullscreenCheckBox() {
        CheckBox fullscreenCheckbox = new CheckBox(" Fullscreen", skin);
        fullscreenCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isFullscreen = fullscreenCheckbox.isChecked();
                Settings.getInstance().setFullscreen(isFullscreen);
                applyDisplaySettings();
                if (isFullscreen) {
                    syncResolutionWithCurrentDisplayMode();
                }
                updateResolutionControlsState(isFullscreen);
            }
        });
        return fullscreenCheckbox;
    }

    private SelectBox<String> getResolutionSelectBox() {
        Set<String> availableSet = getAvailableSet();
        String[] resolutions = availableSet.toArray(new String[0]);

        SelectBox<String> resolutionSelect = new SelectBox<>(skin);
        resolutionSelect.setItems(resolutions);

        resolutionSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selected = resolutionSelect.getSelected();
                String[] parts = selected.split("x");
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                Settings.getInstance().setResolution(width, height);
                applyDisplaySettings();
            }
        });

        return resolutionSelect;
    }

    private static Set<String> getAvailableSet() {
        Set<String> popularSet = new HashSet<>(Arrays.asList(
            "1600x900", "1920x1080", "1920x1200", "2560x1080",
            "2560x1440"
        ));

        Graphics.DisplayMode[] displayModes = Gdx.graphics.getDisplayModes();
        Set<String> availableSet = new TreeSet<>();

        for (Graphics.DisplayMode mode : displayModes) {
            String res = mode.width + "x" + mode.height;
            if (popularSet.contains(res)) {
                availableSet.add(res);
            }
        }

        if (availableSet.isEmpty()) {
            availableSet.addAll(popularSet);
        }
        return availableSet;
    }

    private void settingsChanged() {
        Assets.getAssets().updateMusicVolume();
    }

    private Runnable onResolutionChanged;

    public void setOnResolutionChanged(Runnable runnable) {
        this.onResolutionChanged = runnable;
    }

    private void applyDisplaySettings() {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        int width = Settings.getInstance().getWidth();
        int height = Settings.getInstance().getHeight();
        boolean fullscreen = Settings.getInstance().isFullscreen();

        if (fullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(width, height);
        }

        if (onResolutionChanged != null) {
            onResolutionChanged.run();
        }
    }

    private void updateResolutionControlsState(boolean isFullscreen) {
        resolutionSelect.setDisabled(isFullscreen);
    }

    private void syncResolutionWithCurrentDisplayMode() {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;
        Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
        String currentRes = mode.width + "x" + mode.height;

        Settings.getInstance().setResolution(mode.width, mode.height);

        if (resolutionSelect != null) {
            resolutionSelect.setSelected(currentRes);
        }
    }

    public void resize(int screenWidth, int screenHeight) {
        pack();
        setPosition((screenWidth - getWidth()) / 2, (screenHeight - getHeight()) / 2);
    }
}
