package io.github.store_prototype.utills.size;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.store_prototype.utils.size.ScreenScaler;

@RunWith(MockitoJUnitRunner.class)
public class ScreenScalerTest {
    @Mock
    Graphics graphicsMock;
    private Graphics originalGraphics;

    @Before
    public void setUp() {
        originalGraphics = Gdx.graphics;
        Gdx.graphics = graphicsMock;
        when(graphicsMock.getWidth()).thenReturn(800);
        when(graphicsMock.getHeight()).thenReturn(450);
    }

    @After
    public void tearDown() {
        Gdx.graphics = originalGraphics;
    }

    @Test
    public void testScaleX() {
        assertEquals(0.5f, ScreenScaler.getScaleX(), 0.001);
        assertEquals(100f, ScreenScaler.scaleX(200f), 0.001);
    }

    @Test
    public void testInverseTransform() {
        float[] ref = ScreenScaler.inverseTransform(400, 225);
        assertEquals(800f, ref[0], 0.001);
        assertEquals(450f, ref[1], 0.001);
    }
}
