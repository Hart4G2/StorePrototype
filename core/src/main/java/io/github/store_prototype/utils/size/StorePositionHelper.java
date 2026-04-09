package io.github.store_prototype.utils.size;

import com.badlogic.gdx.Gdx;

import io.github.store_prototype.objects.screen.person_logic.persons.Person.PersonState;

public class StorePositionHelper {
    public static final float REF_THRESHOLD_Y = 20f;       // порог по Y (близость к уровню магазина)
    public static final float REF_THRESHOLD_NEAR = 5f;    // близкое расстояние (у магазина)
    public static final float REF_THRESHOLD_FAR = 100f;    // дальнее расстояние (заметил магазин)
    public static final float REF_STEP = 20f;              // шаг между персонажами в очереди
    public static final float REF_STORE_X = 800f;
    public static final float REF_STORE_Y = 391f;

    /**
     * Проверяет, находится ли персонаж на нужной высоте у магазина.
     */
    public static boolean isAtStoreYLevel(PersonSize size) {
        float storeY = ScreenScaler.scaleY(REF_STORE_Y);
        return Math.abs(size.getY() - storeY) < ScreenScaler.scaleThreshold(REF_THRESHOLD_Y);
    }

    /**
     * Проверяет, находится ли персонаж на дальнем расстоянии от магазина (100px при 1600x900).
     */
    public static boolean isWithinFarDistanceFromStore(PersonSize size) {
        float storeX = ScreenScaler.scaleX(REF_STORE_X);
        float distance = Math.abs((size.getX() + size.getWidth() / 2f) - storeX);
        return distance < ScreenScaler.scaleThreshold(REF_THRESHOLD_FAR);
    }

    /**
     * Проверяет, находится ли персонаж на близком расстоянии от магазина (5px при 1600x900).
     */
    public static boolean isWithinNearDistanceFromStore(PersonSize size) {
        float storeX = ScreenScaler.scaleX(REF_STORE_X);
        float distance = Math.abs((size.getX() + size.getWidth() / 2f) - storeX);
        return distance < ScreenScaler.scaleThreshold(REF_THRESHOLD_NEAR);
    }

    /**
     * Возвращает X-координату места в очереди для заданного индекса.
     */
    public static float getQueuePositionX(int index, PersonSize size) {
        float storeX = ScreenScaler.scaleX(REF_STORE_X);
        float step = ScreenScaler.scaleThreshold(REF_STEP);
        return storeX - (size.getWidth() + step) * (index + 1);
    }

    /**
     * Проверяет, находится ли персонаж на своём месте в очереди.
     */
    public static boolean isAtQueuePosition(PersonSize size, PersonState state, int index) {
        float queueX = getQueuePositionX(index, size);
        float distance = state == PersonState.LEFT ? (size.getX() + size.getWidth() / 2f) - queueX : queueX - (size.getX() + size.getWidth() / 2f);
        return distance < ScreenScaler.scaleThreshold(REF_THRESHOLD_NEAR);
    }

    /**
     * Определяет направление движения (вправо или влево) для достижения заданной точки.
     */
    public static PersonState getDirectionToTarget(float placeX, float currentX) {
        return placeX > currentX ? PersonState.RIGHT : PersonState.LEFT;
    }

    public static PersonState getDirectionToStore(float currentX) {
        float storeX = ScreenScaler.scaleX(REF_STORE_X);
        return storeX > currentX ? PersonState.RIGHT : PersonState.LEFT;
    }
}
