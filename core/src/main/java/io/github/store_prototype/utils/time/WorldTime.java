package io.github.store_prototype.utils.time;

import static io.github.store_prototype.objects.screen.sky.Sky.*;

import io.github.store_prototype.Main;

public class WorldTime {
    public interface DayChangeListener {
        void onDayChanged(int newDay);
    }

    private float currentTime;
    private final float dayLength = 24f;
    private float timeSpeed = 0.5f;
    private int daysCount;
    private DayChangeListener dayChangeListener;

    private static WorldTime instance;

    public static WorldTime getInstance() {
        if(instance == null){
            instance = new WorldTime();
        }
        return instance;
    }

    public void setDay(int day) {
        this.daysCount = day;
        currentTime = 0;
    }

    public int getCurrentDay() {
        return daysCount;
    }

    public void setDayChangeListener(DayChangeListener listener) {
        this.dayChangeListener = listener;
    }

    public void render(float delta) {
        currentTime += timeSpeed * delta;
        if (currentTime > dayLength) {
            nextDay();
        }
    }

    private void nextDay() {
        daysCount++;
        currentTime = 0;
        System.out.println("New day: " + daysCount);
        if (dayChangeListener != null) {
            dayChangeListener.onDayChanged(daysCount);
        }
        Main.getInstance().getGameScreen().playDayStartAnimation(daysCount);
    }

    public float getDayLength() {
        return dayLength;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public SkyState getSkyState() {
        float progress = currentTime / dayLength; // от 0 до 1
        if (progress < 0.25f) {
            return SkyState.MORNING;
        } else if (progress < 0.5f) {
            return SkyState.DAY;
        } else if (progress < 0.75f) {
            return SkyState.EVENING;
        } else {
            return SkyState.NIGHT;
        }
    }

    public void reset() {
        daysCount = 0;
        currentTime = 0;
    }
}

