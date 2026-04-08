package io.github.store_prototype.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.VfxEffect;
import com.crashinvaders.vfx.effects.VignettingEffect;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.screen.City;
import io.github.store_prototype.objects.screen.GUI.ProductsPanel;
import io.github.store_prototype.objects.screen.Store;
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.road.Road;
import io.github.store_prototype.objects.screen.road.RoadLight;
import io.github.store_prototype.objects.screen.road.Sewerage;
import io.github.store_prototype.objects.screen.sky.Sky;
import io.github.store_prototype.objects.shaders.DayNightShader;
import io.github.store_prototype.screens.menu.SettingsDialog;
import io.github.store_prototype.utils.Assets;
import io.github.store_prototype.utils.time.DayStartAnimation;

public class GameScreen implements Screen {

    private final Main game;
    private SpriteBatch spriteBatch;
    private ScreenViewport viewport;
    private Stage stage;
    private Skin skin;

    private OrthographicCamera camera;
    private DayNightShader dayNightShader;

    private Sky sky;
    private City city;
    private Road road;
    private ProductsPanel productsPanel;
    private Store store;
    private Sewerage sewerage;

    private DayStartAnimation dayStartAnimation;


    // background and effect
    private VfxManager vfxManager;
    private VfxEffect bloomEffect;
    private VfxEffect vignetteEffect;

    // pause
    private boolean isPaused = false;
    private Stage pauseStage;
    private TextButton resumeButton, settingsPauseButton, menuButton;
    private SettingsDialog settingsDialog;
    private Button gearButton;

    private boolean initialized = false;

    public GameScreen(Main game) {
        this.game = game;
        this.spriteBatch = new SpriteBatch();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = .95f;

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        skin = Assets.getAssets().getSkin();

        pauseStage = new Stage(viewport);
        createPauseMenu();

        gearButton = new Button(skin, "settingsButton");
        gearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
        stage.addActor(gearButton);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    togglePause();
                    return true;
                }
                return false;
            }
        });

        pauseStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    togglePause();
                    return true;
                }
                return false;
            }
        });

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        bloomEffect = new BloomEffect();
        vignetteEffect = new VignettingEffect(false);
        vfxManager.addEffect((ChainVfxEffect) bloomEffect);
        vfxManager.addEffect((ChainVfxEffect) vignetteEffect);
    }

    public void init() {
        if (initialized) return;
        initialized = true;

        dayStartAnimation = new DayStartAnimation();

        dayNightShader = new DayNightShader();

        sky = new Sky();
        city = new City();
        road = new Road();
        store = new Store();
        sewerage = new Sewerage();
        SimplePublisher.getPublisher().addListener(store);
        productsPanel = new ProductsPanel(Assets.getAssets().getSkin());

        stage.addActor(store);
        stage.addActor(productsPanel);
        stage.addActor(dayStartAnimation);

        productsPanel.setVisible(false);

        store.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                productsPanel.setVisible(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                productsPanel.setVisible(false);
            }
        });
    }

    @Override
    public void show() {
        init();

        isPaused = false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        float gameDelta = isPaused ? 0f : delta;

        ScreenUtils.clear(1, 1, 1, 1f);

        float normalizedTime = 1;

        switch (sky.getState()){
            case NIGHT:{
                normalizedTime = -.1f;
                city.setState(City.CityState.DARK);
                store.setState(Store.StoreState.LIGHT);
                road.setState(road.getState(), RoadLight.LightState.ON);
                break;
            }
            case DAY:{
                store.setState(Store.StoreState.DARK);
                road.setState(road.getState(), RoadLight.LightState.OFF);
                break;
            }
            case EVENING:{
                store.setState(Store.StoreState.LIGHT);
                city.setState(City.CityState.DARK1);
                normalizedTime = (8 - sky.getKeyFrameIndex()) * .1f;
                road.setState(road.getState(), RoadLight.LightState.ON);
                break;
            }
            case MORNING:{
                store.setState(Store.StoreState.LIGHT);
                city.setState(City.CityState.LIGHT);
                normalizedTime = sky.getKeyFrameIndex() * .1f;
                road.setState(road.getState(), RoadLight.LightState.ON);
                break;
            }
        }

        bloomEffect.update(gameDelta);
        vignetteEffect.update(gameDelta);
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        dayNightShader.updateUniform(normalizedTime);

        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        sky.render(gameDelta, spriteBatch);
        spriteBatch.end();

        spriteBatch.setShader(dayNightShader.getShader());
        spriteBatch.begin();
        {
            city.render(gameDelta, spriteBatch);
            road.render(gameDelta, spriteBatch);
        }
        spriteBatch.end();
        spriteBatch.setShader(null);

        stage.act(gameDelta);
        stage.draw();

        spriteBatch.begin();
        store.drawLight(spriteBatch);
        road.renderLight(gameDelta, spriteBatch);
        spriteBatch.end();

        spriteBatch.begin();
        spriteBatch.setShader(dayNightShader.getShader());
        PersonScene.getPersonScene().render(spriteBatch, gameDelta);
        spriteBatch.setShader(null);
        spriteBatch.end();

        if (!isPaused) {
            moveCamera(gameDelta);
        }

        vfxManager.endInputCapture();
        vfxManager.applyEffects();
        vfxManager.renderToScreen();

        if (isPaused) {
            pauseStage.act(delta);
            pauseStage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        vfxManager.resize(width, height);
        stage.getViewport().update(width, height);
        viewport.update(width, height, true);
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();

        city.resize(width, height);
        road.resize(width, height);
        sky.resize(width, height);
        store.resize(width, height);
        productsPanel.resize(width, height);
        sewerage.resize(width, height);
        PersonScene.getPersonScene().resize(width, height);

        gearButton.setSize(width / 40f, width / 40f);
        gearButton.setPosition(width - gearButton.getWidth() * 3f, height - gearButton.getWidth() * 2f);

        float buttonWidth = width / 10f;
        float buttonHeight = height / 20f;

        resumeButton.setBounds(width / 2f - buttonWidth / 2f, height / 1.75f, buttonWidth, buttonHeight);
        settingsPauseButton.setBounds(width / 2f - buttonWidth / 2f, height / 2f, buttonWidth, buttonHeight);
        menuButton.setBounds(width / 2f - buttonWidth / 2f, height / 2.35f, buttonWidth, buttonHeight);
        settingsDialog.resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    private void createPauseMenu() {
        resumeButton = new TextButton("Resume", skin);
        settingsPauseButton = new TextButton("Settings", skin);
        menuButton = new TextButton("Main Menu", skin, "red");
        settingsDialog = new SettingsDialog("Settings", skin);

        resumeButton.pad(8, 16, 8, 16);
        settingsPauseButton.pad(8, 16, 8, 16);
        menuButton.pad(8, 16, 8, 16);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });

        settingsPauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsDialog.setOnResolutionChanged(() -> {
                    game.restartGame();   // перезапуск игры
                    settingsDialog.hide(); // закрыть диалог
                });
                settingsDialog.show(pauseStage);
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setMenuScreen();
            }
        });

        pauseStage.addActor(resumeButton);
        pauseStage.addActor(settingsPauseButton);
        pauseStage.addActor(menuButton);
    }

    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            Gdx.input.setInputProcessor(pauseStage);
        } else {
            Gdx.input.setInputProcessor(stage);
        }
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        dayNightShader.dispose();
        vfxManager.dispose();
        bloomEffect.dispose();
        vignetteEffect.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    ///                      PARALLAX

    private void moveCamera(float delta){
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float screenWidthPart = screenWidth * .2f;
        float screenHeightPart = screenHeight * .1f;
        float edgeWidthZone = screenWidth * .01f;
        float edgeHeightZone = screenHeight * .01f;
        float moveSpeed = 20f * delta;

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        if (mouseX < screenWidthPart && camera.position.x > screenWidth / 2f - edgeWidthZone) {
            camera.position.x -= moveSpeed;
        } else if (mouseX > screenWidth - screenWidthPart && camera.position.x < screenWidth / 2f + edgeWidthZone) {
            camera.position.x += moveSpeed; }

        if (mouseY < screenHeightPart && camera.position.y < screenHeight / 2f + edgeHeightZone) {
            camera.position.y += moveSpeed;
        } else if (mouseY > screenHeight - screenHeightPart && camera.position.y > screenHeight / 2f - edgeHeightZone) {
            camera.position.y -= moveSpeed;
        }

        setCameraMoved(delta);
    }

    private void setCameraMoved(float delta) {
        cameraMoved = camera.position.x != Gdx.graphics.getWidth() / 2f || camera.position.y != Gdx.graphics.getHeight() / 2f;
        if(cameraMoved){
            if(timeToReset == 0){
                timeToReset = delta + 2;
                timeNotMoving = delta;
            }

            timeNotMoving += delta;
            if (timeNotMoving > timeToReset){
                timeToReset = 0;
                resetPosition();
            }
        }

        if(cameraReturning) {
            moveCameraToCenter(delta);
        }
    }

    private boolean cameraMoved = false;
    private boolean cameraReturning = false;
    private float timeNotMoving;
    private float timeToReset;

    private void resetPosition(){
        cameraReturning = true;
        cameraMoved = false;
    }

    private void moveCameraToCenter(float delta){
        float moveSpeed = 10f * delta;

        if(camera.position.x > Gdx.graphics.getWidth() / 2f + 10 || camera.position.x < Gdx.graphics.getWidth() / 2f - 10) {
            if (camera.position.x > Gdx.graphics.getWidth() / 2f) {
                camera.position.x -= moveSpeed;
            } else {
                camera.position.x += moveSpeed;
            }
        }

        if(camera.position.y != Gdx.graphics.getHeight() / 2f) {
            if (camera.position.y > Gdx.graphics.getHeight() / 2f) {
                camera.position.y -= moveSpeed;
            } else {
                camera.position.y += moveSpeed;
            }
        }
    }

    public boolean isCameraReturning(){
        return cameraReturning;
    }

    public void playDayStartAnimation(int daysCount) {
        dayStartAnimation.play(daysCount);
    }
}
