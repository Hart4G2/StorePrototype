package io.github.store_prototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.screens.ScreenFactory;
import io.github.store_prototype.screens.menu.MenuScreen;

public class Main extends Game {

    private ScreenFactory screenFactory = new ScreenFactory() {
        public MenuScreen createMenuScreen() { return new MenuScreen(); }
        public GameScreen createGameScreen() { return new GameScreen(); }
    };

    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private static Main instance;

    public static Main getInstance() {
        if(instance == null){
            instance = new Main();
        }
        return instance;
    }

    @Override
    public void create() {
        menuScreen = screenFactory.createMenuScreen();
        gameScreen = screenFactory.createGameScreen();
        setScreen(menuScreen);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(Color.BLACK);
        getScreen().render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public GameScreen getGameScreen(){
        return gameScreen;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public void setMenuScreen() {
        setScreen(menuScreen);
    }

    public void setGameScreen(int saveSlot) {
        gameScreen.setSaveSlot(saveSlot);
        setScreen(gameScreen);
    }

    public void setGameScreen() {
        setScreen(gameScreen);
    }

    public void restartGame() {
        setMenuScreen();
        gameScreen.dispose();
        gameScreen = screenFactory.createGameScreen();
        setGameScreen();
    }

    public void setScreenFactory(ScreenFactory factory) {
        this.screenFactory = factory;
    }
}
