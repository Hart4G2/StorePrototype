package io.github.store_prototype.utills.size;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static io.github.store_prototype.objects.screen.person_logic.persons.Person.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.utils.size.PersonSize;
import io.github.store_prototype.utils.size.ScreenScaler;
import io.github.store_prototype.utils.size.StorePositionHelper;

@RunWith(MockitoJUnitRunner.class)
public class StorePositionHelperTest {

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
        when(graphicsMock.getHeight()).thenReturn(900);
    }

    @After
    public void tearDown() {
        Gdx.graphics = originalGraphics;
    }

    @Test
    public void testIsAtStoreYLevel() {
        float storeY = ScreenScaler.scaleY(StorePositionHelper.REF_STORE_Y);
        when(personSizeMock.getY()).thenReturn(storeY + 10f);
        assertTrue(StorePositionHelper.isAtStoreYLevel(personSizeMock));

        when(personSizeMock.getY()).thenReturn(storeY + 30f);
        assertFalse(StorePositionHelper.isAtStoreYLevel(personSizeMock));
    }

    @Test
    public void testIsWithinFarDistanceFromStore() {
        // REF_STORE_X = 800, scaleX = 1.0 → storeX = 800
        // REF_THRESHOLD_FAR = 100, scaleThreshold = 100
        // (size.getX() + size.getWidth()/2f) = 750 → расстояние 50 (< 100) → true
        when(personSizeMock.getX()).thenReturn(700f);
        when(personSizeMock.getWidth()).thenReturn(100f);
        assertTrue(StorePositionHelper.isWithinFarDistanceFromStore(personSizeMock));

        // расстояние 200 -> false
        when(personSizeMock.getX()).thenReturn(550f);
        assertFalse(StorePositionHelper.isWithinFarDistanceFromStore(personSizeMock));
    }

    @Test
    public void testGetQueuePositionX() {
        // storeX = 800, step = 20, size.width = 50
        // index=0 → 800 - (50 + 20)*1 = 730
        when(personSizeMock.getWidth()).thenReturn(50f);
        float pos = StorePositionHelper.getQueuePositionX(0, personSizeMock);
        assertEquals(730f, pos, 0.001);

        // index=1 → 800 - (50+20)*2 = 660
        float pos1 = StorePositionHelper.getQueuePositionX(1, personSizeMock);
        assertEquals(660f, pos1, 0.001);
    }

    @Test
    public void testGetDirectionToTarget() {
        assertEquals(PersonState.RIGHT, StorePositionHelper.getDirectionToTarget(100, 50));
        assertEquals(PersonState.LEFT, StorePositionHelper.getDirectionToTarget(50, 100));
    }

    @Test
    public void testGetDirectionToStore() {
        // storeX = 800
        assertEquals(PersonState.RIGHT, StorePositionHelper.getDirectionToStore(500));
        assertEquals(PersonState.LEFT, StorePositionHelper.getDirectionToStore(900));
    }
}
