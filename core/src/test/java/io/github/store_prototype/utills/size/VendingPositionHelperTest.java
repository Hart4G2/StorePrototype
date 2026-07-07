package io.github.store_prototype.utills.size;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.VendingPositionHelper;

@RunWith(MockitoJUnitRunner.class)
public class VendingPositionHelperTest {

    @Mock
    private Graphics graphicsMock;
    @Mock
    private PersonSize personSizeMock;

    private Graphics originalGraphics;

    @Before
    public void setUp() {
        originalGraphics = Gdx.graphics;
        Gdx.graphics = graphicsMock;
        when(graphicsMock.getWidth()).thenReturn(1600);
    }

    @After
    public void tearDown() {
        Gdx.graphics = originalGraphics;
    }

    @Test
    public void testIsWithinNearDistanceFromVending() {
        // REF_VENDING_X = 1097, scaleX = 1.0 → vendingX = 1097
        // REF_THRESHOLD_NEAR = 1, scaleThreshold = 1
        // персонаж на расстоянии 0.5 от автомата -> true
        when(personSizeMock.getX()).thenReturn(1072f);
        when(personSizeMock.getWidth()).thenReturn(50f); // центр = 1072 + 25 = 1097, расстояние 0
        assertTrue(VendingPositionHelper.isWithinNearDistanceFromVending(personSizeMock));

        // расстояние 2 -> false
        when(personSizeMock.getX()).thenReturn(1070f);
        assertFalse(VendingPositionHelper.isWithinNearDistanceFromVending(personSizeMock));
    }

    @Test
    public void testIsAtNecessaryYLevel() {
        // REF_THRESHOLD_Y = 20, scaleThreshold = 20
        // персонаж на высоте 100, целевая y = 100 -> true
        when(personSizeMock.getY()).thenReturn(100f);
        assertTrue(VendingPositionHelper.isAtNecessaryYLevel(personSizeMock, 100f));

        // отклонились на 25 -> false
        when(personSizeMock.getY()).thenReturn(125f);
        assertFalse(VendingPositionHelper.isAtNecessaryYLevel(personSizeMock, 100f));
    }
}
