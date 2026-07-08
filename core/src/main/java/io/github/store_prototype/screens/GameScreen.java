package io.github.store_prototype.screens;

import static io.github.store_prototype.objects.screen.sky.Sky.*;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import io.github.store_prototype.objects.event_handling.events.gui.ModalClosedEvent;
import io.github.store_prototype.objects.screen.City;
import io.github.store_prototype.objects.screen.GUI.Inventory;
import io.github.store_prototype.objects.screen.GUI.ProductsPanel;
import io.github.store_prototype.objects.screen.Store;
import io.github.store_prototype.objects.screen.mini_games.fishing.FishingModal;
import io.github.store_prototype.objects.screen.mini_games.radio.RadioModal;
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.person_logic.persons.StoreQueue;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.FishingMen;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.OldManWithBag;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.OldWomanWithRadio;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.duck.DuckChain;
import io.github.store_prototype.objects.screen.road.Road;
import io.github.store_prototype.objects.screen.road.RoadLight;
import io.github.store_prototype.objects.screen.road.Sewerage;
import io.github.store_prototype.objects.screen.sky.Sky;
import io.github.store_prototype.objects.screen.upgrades.UpgradeScene;
import io.github.store_prototype.objects.screen.upgrades.window.UpgradeWindow;
import io.github.store_prototype.objects.screen.watch.Watch;
import io.github.store_prototype.objects.shaders.DayNightShader;
import io.github.store_prototype.screens.menu.SettingsDialog;
import io.github.store_prototype.screens.menu.savings.SaveManager;
import io.github.store_prototype.screens.menu.savings.SaveSlotData;
import io.github.store_prototype.utils.assets.Assets;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.time.DayEventsManager;
import io.github.store_prototype.utils.time.DayStartAnimation;
import io.github.store_prototype.utils.time.WorldTime;

public class GameScreen implements Screen, WorldTime.DayChangeListener {

    private SpriteBatch spriteBatch;
    private ScreenViewport viewport;
    private Stage stage;
    private Skin skin;

    // savings
    private int currentSaveSlot = -1;
    private SaveManager saveManager;

    private OrthographicCamera camera;
    private DayNightShader dayNightShader;

    private Sky sky;
    private City city;
    private Road road;
    private ProductsPanel productsPanel;
    private Store store;
    private Sewerage sewerage;
    private Watch watch;
    private Image bench;

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
    private Button settingsButton;

    // upgrades
    private TextButton upgradesButton;
    private UpgradeWindow upgradesWindow;

    // modal windows
    private Stage modalStage;
    private Actor currentModal;
    private boolean isModalOpen = false;

    private boolean initialized = false;

    public GameScreen() {
        skin = Assets.getAssets().getSkin();
        this.spriteBatch = new SpriteBatch();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = .95f;

        viewport = new ScreenViewport(camera);
        stage = new Stage(viewport);

        pauseStage = new Stage(viewport);
        createPauseMenu();

        modalStage = new Stage(viewport);

        modalStage.addListener(event -> {
            if (event instanceof ModalClosedEvent) {
                closeModal();
                return true;
            }
            return false;
        });

        modalStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if(currentModal == upgradesWindow){
                        closeModal();
                    }
                    return true;
                }
                return false;
            }
        });

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

        upgradesButton = new TextButton("Upgrades", skin);
        upgradesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openUpgradesWindow();
            }
        });
        stage.addActor(upgradesButton);

        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        bloomEffect = new BloomEffect();
        vignetteEffect = new VignettingEffect(false);
        vfxManager.addEffect((ChainVfxEffect) bloomEffect);
        vfxManager.addEffect((ChainVfxEffect) vignetteEffect);
    }

    public void init() {
        if (initialized) return;
        initialized = true;

        dayStartAnimation = new DayStartAnimation(skin);

        dayNightShader = new DayNightShader();

        sky = new Sky();
        city = new City();
        road = new Road();
        store = new Store();
        SimplePublisher.getPublisher().addListener(store);
        sewerage = new Sewerage();
        productsPanel = new ProductsPanel(skin);
        watch = new Watch();

        bench = new Image(Assets.getAssets().getTexture("gamescene/road/bench.png"));
        bench.setBounds(ScreenScaler.scaleX(1300f), ScreenScaler.scaleY(420f), ScreenScaler.scaleX(195f), ScreenScaler.scaleY(120f));
        stage.addActor(bench);

        stage.addActor(store);
        stage.addActor(productsPanel);
        UpgradeScene.getInstance().init(stage);
        stage.addActor(dayStartAnimation);
        stage.addActor(watch);

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

        settingsButton = new Button(skin, "settingsButton");
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                togglePause();
            }
        });
        stage.addActor(settingsButton);

//        PersonScene.getPersonScene().addPerson(new OldManWithBag(bench, stage));
    }

    @Override
    public void show() {
        init();

        isPaused = false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        float gameDelta = (isPaused || isModalOpen) ? 0f : delta;
        ScreenUtils.clear(1, 1, 1, 1f);

        WorldTime.getInstance().render(gameDelta);

        SkyState newSkyState = WorldTime.getInstance().getSkyState();
        if (sky.getState() != newSkyState) {
            sky.setState(newSkyState);
        }

        float normalizedTime = 1;

        switch (sky.getState()){
            case NIGHT:{
                normalizedTime = -.1f;
                city.setState(City.CityState.DARK);
                store.setState(Store.StoreState.LIGHT);
                road.setState(road.getState(), RoadLight.LightState.OFF);
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
                road.setLightState(RoadLight.LightState.ON);
                break;
            }
            case MORNING:{
                store.setState(Store.StoreState.LIGHT);
                city.setState(City.CityState.LIGHT);
                normalizedTime = sky.getKeyFrameIndex() * .1f;
                road.setLightState(RoadLight.LightState.ON);
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

        if (isModalOpen) {
            modalStage.act(isPaused ? gameDelta : delta);
            modalStage.draw();
        }

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
        sky.resize();
        store.resize(width, height);
        productsPanel.resize(width, height);
        sewerage.resize(width, height);
        PersonScene.getPersonScene().resize(width, height);
        watch.resize();
        bench.setBounds(ScreenScaler.scaleX(1300f), ScreenScaler.scaleY(420f), ScreenScaler.scaleX(195f), ScreenScaler.scaleY(120f));

        settingsButton.setSize(width / 45f, width / 45f);
        settingsButton.setPosition(width - settingsButton.getWidth() * 3f, height - settingsButton.getWidth() * 2f);

        float buttonWidth = width / 10f;
        float buttonHeight = height / 20f;

        resumeButton.setBounds(width / 2f - buttonWidth / 2f, height / 1.75f, buttonWidth, buttonHeight);
        settingsPauseButton.setBounds(width / 2f - buttonWidth / 2f, height / 2f, buttonWidth, buttonHeight);
        menuButton.setBounds(width / 2f - buttonWidth / 2f, height / 2.35f, buttonWidth, buttonHeight);
        settingsDialog.resize(width, height);

        upgradesButton.setSize(width / 12f, width / 45f);
        upgradesButton.setPosition(width - upgradesButton.getHeight() * 7f, height - upgradesButton.getHeight() * 2f);

        Inventory.getInstance().resize(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    private void createPauseMenu() {
        resumeButton = new TextButton("Resume", skin);
        settingsPauseButton = new TextButton("Settings", skin);
        menuButton = new TextButton("Main Menu", skin, "red");
        settingsDialog = new SettingsDialog("Settings");

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
                    Main.getInstance().restartGame();   // перезапуск игры
                    settingsDialog.hide(); // закрыть диалог
                });
                settingsDialog.show(pauseStage);
            }
        });

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setMenuScreen();
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

    private void openUpgradesWindow() {
        if (isModalOpen) return;

        upgradesWindow = new UpgradeWindow("Upgrades", skin);
        upgradesWindow.show(modalStage);
        modalStage.setScrollFocus(upgradesWindow.getScrollPane());
        currentModal = upgradesWindow;

        Gdx.input.setInputProcessor(modalStage);
        isModalOpen = true;
    }

    public void openRadioModal(Runnable onCompleted) {
        if (isModalOpen) return;
        RadioModal radio = new RadioModal(skin, () -> {
            closeModal();
            onCompleted.run();
        });
        currentModal = radio;
        modalStage.addActor(radio);
        Gdx.input.setInputProcessor(modalStage);
        isModalOpen = true;
    }

    public void openFishingModal(Runnable onCompleted) {
        if (isModalOpen) return;
        FishingModal fishing = new FishingModal(skin, () -> {
            closeModal();
            onCompleted.run();
        });
        currentModal = fishing;
        modalStage.addActor(fishing);
        Gdx.input.setInputProcessor(modalStage);
        isModalOpen = true;
    }

    public void closeModal() {
        if (!isModalOpen) return;
        if(currentModal != null){
            currentModal.remove();
            currentModal = null;
        }
        isModalOpen = false;
        Gdx.input.setInputProcessor(isPaused ? pauseStage : stage);
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        reset();
    }

    public void reset() {
        WorldTime.getInstance().reset();

        PersonScene.getPersonScene().clearAllPersons();
        StoreQueue.getInstance().clear();

        DayEventsManager.getInstance().reset();

        if (isModalOpen) closeModal();
        isPaused = false;

        Gdx.input.setInputProcessor(stage);

        currentSaveSlot = -1;
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

    public void playDayStartAnimation(int daysCount) {
        dayStartAnimation.play(daysCount);
    }

    public void setSaveSlot(int slot) {
        this.currentSaveSlot = slot;
        if (saveManager == null) saveManager = new SaveManager();
        SaveSlotData data = saveManager.getSlot(slot);
        WorldTime.getInstance().setDay(data.day);

        DayEventsManager.getInstance().initDay(data.day);

        WorldTime.getInstance().setDayChangeListener(this);
    }

    @Override
    public void onDayChanged(int newDay) {
        DayEventsManager.getInstance().onDayChanged(newDay);

        if (currentSaveSlot >= 0) {
            SaveSlotData data = saveManager.getSlot(currentSaveSlot);
            data.day = newDay;
            data.progressDescription = "Day " + newDay;
            data.lastSaveTimestamp = System.currentTimeMillis();
            saveManager.saveSlot(data);
        }
    }
}
