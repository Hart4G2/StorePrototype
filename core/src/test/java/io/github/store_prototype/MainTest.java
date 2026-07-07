package io.github.store_prototype;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.screens.ScreenFactory;
import io.github.store_prototype.screens.menu.MenuScreen;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {

    @Mock
    private MenuScreen menuScreenMock;
    @Mock
    private GameScreen gameScreenMock;
    @Mock
    private ScreenFactory screenFactoryMock;
    @Mock
    private Graphics graphicsMock;

    private Main main;
    private Graphics originalGraphics; // сохраним оригинал, если был

    @Before
    public void setUp() throws Exception {
        // Сброс синглтона Main
        java.lang.reflect.Field instanceField = Main.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        // Сохраняем оригинальный Gdx.graphics и подменяем моком
        originalGraphics = Gdx.graphics;
        Gdx.graphics = graphicsMock;
        when(graphicsMock.getWidth()).thenReturn(800);
        when(graphicsMock.getHeight()).thenReturn(600);

        // Создаём объект Main
        main = new Main();

        // Настраиваем фабрику, чтобы она возвращала моки
        when(screenFactoryMock.createMenuScreen()).thenReturn(menuScreenMock);
        when(screenFactoryMock.createGameScreen()).thenReturn(gameScreenMock);
        main.setScreenFactory(screenFactoryMock);
    }

    @After
    public void tearDown() {
        // Возвращаем оригинальное значение Gdx.graphics (или null)
        Gdx.graphics = originalGraphics;
    }

    @Test
    public void testCreateUsesFactoryAndSetsMenuScreen() {
        main.create();
        verify(screenFactoryMock).createMenuScreen();
        verify(screenFactoryMock).createGameScreen();
        assertSame(menuScreenMock, main.getScreen());
    }

    @Test
    public void testSetGameScreen() {
        main.create();
        main.setGameScreen();
        assertSame(gameScreenMock, main.getScreen());
    }

    @Test
    public void testRestartGameDisposesOldAndCreatesNew() {
        main.create();
        GameScreen oldGameScreen = main.getGameScreen();
        assertSame(gameScreenMock, oldGameScreen);

        GameScreen newGameScreen = mock(GameScreen.class);
        when(screenFactoryMock.createGameScreen()).thenReturn(newGameScreen);

        main.restartGame();

        verify(oldGameScreen).dispose();
        assertSame(newGameScreen, main.getScreen());
    }

    @Test
    public void testSingleton() {
        Main first = Main.getInstance();
        Main second = Main.getInstance();
        assertSame(first, second);
    }
}
