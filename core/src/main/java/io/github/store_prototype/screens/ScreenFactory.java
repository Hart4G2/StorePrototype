package io.github.store_prototype.screens;

import io.github.store_prototype.screens.menu.MenuScreen;

public interface ScreenFactory {
    MenuScreen createMenuScreen();
    GameScreen createGameScreen();
}
