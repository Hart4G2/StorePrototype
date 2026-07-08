package io.github.store_prototype.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.RadialDistortionEffect;
import com.crashinvaders.vfx.effects.VfxEffect;
import com.crashinvaders.vfx.effects.VignettingEffect;

import io.github.store_prototype.Main;
import io.github.store_prototype.screens.menu.savings.SaveSelectionWindow;
import io.github.store_prototype.utils.assets.Assets;

public class MenuScreen implements Screen {
    private Viewport viewport;
    private Stage uiStage;
    private OrthographicCamera camera;
    private TextButton gameButton;
    private MenuBackground menuBackground;
    private TextButton settingsButton;
    private TextButton exitButton;
    private SettingsDialog settingsDialog;
    private String currentZone;

    private SaveSelectionWindow saveSelectionWindow;
    private Group mainMenuGroup;

    // background and effect
    private Stage backgroundStage;
    private VfxManager vfxManager;
    private VfxEffect bloomEffect;
    private VfxEffect vignetteEffect;
    private VfxEffect curvatureEffect;

    public MenuScreen() {
        Skin skin = Assets.getAssets().getSkin();

        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1f;
        viewport = new ScreenViewport(camera);

        backgroundStage = new Stage(viewport);
        menuBackground = new MenuBackground();
        menuBackground.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundStage.addActor(menuBackground);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        bloomEffect = new BloomEffect();
        vignetteEffect = new VignettingEffect(false);
        curvatureEffect = new RadialDistortionEffect();
        vfxManager.addEffect((ChainVfxEffect) bloomEffect);
        vfxManager.addEffect((ChainVfxEffect) vignetteEffect);
        vfxManager.addEffect((ChainVfxEffect) curvatureEffect);
        uiStage = new Stage(viewport);

        float buttonWidth = Gdx.graphics.getWidth() / 10f;
        float buttonHeight = Gdx.graphics.getHeight() / 20f;

        gameButton = new TextButton("Start", skin);
        gameButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 1.75f, buttonWidth, buttonHeight);

//        gameButton.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);
//                openGame();
//            }
//        });

        settingsButton = new TextButton("Settings", skin);
        settingsButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 2f, buttonWidth, buttonHeight);

        settingsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                showSettings();
            }
        });

        exitButton = new TextButton("Exit", skin, "red");
        exitButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 2.25f, buttonWidth, buttonHeight);

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                exitGame();
            }
        });

        settingsDialog = new SettingsDialog("Settings");

        mainMenuGroup = new Group();
        mainMenuGroup.addActor(gameButton);
        mainMenuGroup.addActor(settingsButton);
        mainMenuGroup.addActor(exitButton);
        uiStage.addActor(mainMenuGroup);

        gameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSaveSelection();
            }
        });
    }

    private void showSaveSelection() {
        mainMenuGroup.setVisible(false);
        saveSelectionWindow = new SaveSelectionWindow(uiStage);
        saveSelectionWindow.setVisible(false);
        saveSelectionWindow.show();
        uiStage.addActor(saveSelectionWindow);
    }

    public void showMainMenu() {
        mainMenuGroup.setVisible(true);
    }

    public void openGame(int slot) {
        Main.getInstance().setGameScreen(slot);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(uiStage);
        Assets.getAssets().playMusic();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        int mouseX = Gdx.input.getX();
        float third = Gdx.graphics.getWidth() / 3f;
        String newZone;

        if (mouseX < third) {
            newZone = "left";
        } else if (mouseX < 2 * third) {
            newZone = "straight";
        } else {
            newZone = "right";
        }

        if (!newZone.equals(currentZone)) {
            currentZone = newZone;
            switch (newZone) {
                case "left": menuBackground.setLeftTexture(); break;
                case "straight": menuBackground.setStraightTexture(); break;
                case "right": menuBackground.setRightTexture(); break;
            }
        }

        backgroundStage.act(delta);
        uiStage.act(delta);
        bloomEffect.update(delta);
        vignetteEffect.update(delta);
        curvatureEffect.update(delta);

        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        backgroundStage.draw();

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        vfxManager.resize(width, height);
        viewport.update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
        menuBackground.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float buttonWidth = Gdx.graphics.getWidth() / 10f;
        float buttonHeight = Gdx.graphics.getHeight() / 20f;
        gameButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 1.75f, buttonWidth, buttonHeight);
        settingsButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 2f, buttonWidth, buttonHeight);
        exitButton.setBounds(Gdx.graphics.getWidth() / 2f - buttonWidth / 2f, Gdx.graphics.getHeight() / 2.35f, buttonWidth, buttonHeight);
        settingsDialog.resize(width, height);

        if(saveSelectionWindow != null) {
            saveSelectionWindow.invalidateHierarchy();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        vfxManager.dispose();
        bloomEffect.dispose();
        vignetteEffect.dispose();
        curvatureEffect.dispose();

        uiStage.dispose();
        MenuBackground.dispose();
    }

    public void openGame(){
        Main.getInstance().setGameScreen();
    }

    public void showSettings(){
        settingsDialog.update();
        settingsDialog.show(uiStage);
    }

    public void exitGame(){
        Gdx.app.exit();
    }
}
