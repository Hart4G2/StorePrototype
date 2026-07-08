package io.github.store_prototype.utils;


import java.util.Random;

public class Utils {

    private static Random random = new Random();

    public static int randomInt(int a, int b){
        return random.nextInt(a, b);
    }

    public static float randomFloat(float a, float b){
        return random.nextFloat(a, b);
    }

    public static int roundingDown(float number) {
        return (int) number > number ? (int) number - 1 : (int) number;
    }
}
