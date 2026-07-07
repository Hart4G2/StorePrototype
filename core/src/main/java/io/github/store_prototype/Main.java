package io.github.store_prototype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.screens.menu.MenuScreen;

public class Main extends Game {

    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private static Main instance;

    public static Main getInstance() {
        if(instance == null){
            instance = new Main();
        }
        return instance;
    }

    @Override
    public void create() {
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);

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

    public void setMenuScreen() {
        setScreen(menuScreen);
    }

    public void setGameScreen() {
        setScreen(gameScreen);
    }

    public void restartGame() {
        setMenuScreen();
        gameScreen.dispose();
        gameScreen = new GameScreen(this);
        setGameScreen();
    }
}
