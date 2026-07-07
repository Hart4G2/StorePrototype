package io.github.store_prototype.utills;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Random;

import io.github.store_prototype.utils.Utils;

public class UtilsTest {
    @Test
    public void testRoundingDown() {
        assertEquals(3, Utils.roundingDown(3.7f));
        assertEquals(3, Utils.roundingDown(3.0f));
        assertEquals(-4, Utils.roundingDown(-3.2f));
    }

    @Test
    public void testRandomIntWithinBounds() throws Exception {
        Field randomField = Utils.class.getDeclaredField("random");
        randomField.setAccessible(true);
        Random fixedRandom = new Random(123);
        randomField.set(null, fixedRandom);

        int val = Utils.randomInt(10, 20);
        assertTrue(val >= 10 && val < 20);
    }
}
