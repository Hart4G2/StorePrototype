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

import io.github.store_prototype.utils.size.PersonSize;

@RunWith(MockitoJUnitRunner.class)
public class PersonSizeTest {
    @Mock
    Graphics graphicsMock;
    private Graphics origGraphics;

    @Before
    public void setUp() {
        origGraphics = Gdx.graphics;
        Gdx.graphics = graphicsMock;
        when(graphicsMock.getWidth()).thenReturn(1600);
        when(graphicsMock.getHeight()).thenReturn(900);
    }

    @After
    public void tearDown() {
        Gdx.graphics = origGraphics;
    }

    @Test
    public void testUpdateFromReference() {
        PersonSize ps = new PersonSize(100, 200);
        assertEquals(100f, ps.getWidth(), 0.01);
        assertEquals(200f, ps.getHeight(), 0.01);
    }

    @Test
    public void testSetRefPosition() {
        PersonSize ps = new PersonSize(50, 50);
        ps.setRefPosition(100, 200);
        assertEquals(100f, ps.getX(), 0.01);
        assertEquals(200f, ps.getY(), 0.01);
    }
}
