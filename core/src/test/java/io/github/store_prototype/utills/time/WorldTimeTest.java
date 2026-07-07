package io.github.store_prototype.utills.time;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.store_prototype.Main;
import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.utils.time.WorldTime;

@RunWith(MockitoJUnitRunner.class)
public class WorldTimeTest {

    @Test
    public void testNextDayResetsTime() {
        try (MockedStatic<Main> mainMock = mockStatic(Main.class)) {
            Main mainInstance = mock(Main.class);
            GameScreen gsMock = mock(GameScreen.class);
            mainMock.when(Main::getInstance).thenReturn(mainInstance);
            when(mainInstance.getGameScreen()).thenReturn(gsMock);

            WorldTime wt = new WorldTime();
            // Передаём чуть больше времени, чтобы гарантированно превысить dayLength
            wt.render(24f / 0.5f + 0.1f); // ~48.1 сек → currentTime ≈ 24.05 > 24
            verify(gsMock).playDayStartAnimation(1);
        }
    }
}
