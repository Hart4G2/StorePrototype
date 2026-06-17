package io.github.store_prototype.utils.size;


import static io.github.store_prototype.utils.size.StorePositionHelper.REF_THRESHOLD_Y;

public class VendingPositionHelper {
    public static final float REF_THRESHOLD_NEAR = 1f;
    public static final float REF_VENDING_X = 1097f;

    /**
     * Проверяет, находится ли персонаж на близком расстоянии от автомата (5px при 1600x900).
     */
    public static boolean isWithinNearDistanceFromVending(PersonSize size) {
        float vendingX = ScreenScaler.scaleX(REF_VENDING_X);
        float distance = Math.abs((size.getX() + size.getWidth() / 2f) - vendingX);
        return distance < ScreenScaler.scaleThreshold(REF_THRESHOLD_NEAR);
    }

    /**
     * Проверяет, находится ли персонаж на нужной высоте при отходе от автомата.
     */
    public static boolean isAtNecessaryYLevel(PersonSize size, float y) {
        return Math.abs(size.getY() - y) < ScreenScaler.scaleThreshold(REF_THRESHOLD_Y);
    }
}
