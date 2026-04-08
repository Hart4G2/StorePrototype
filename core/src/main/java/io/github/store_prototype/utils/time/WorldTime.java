package io.github.store_prototype.utils.time;

import static io.github.store_prototype.objects.screen.sky.Sky.*;

import io.github.store_prototype.Main;

public class WorldTime {
    private float elapsedTime = 0f;
    private final float dayLength = 24f;
    private int daysCount;
    private float currentTime;

    private static WorldTime instance;

    public static WorldTime getInstance() {
        if(instance == null){
            instance = new WorldTime();
        }
        return instance;
    }

    public WorldTime() {
        daysCount = 0;
    }

    public void nextDay(){
        System.out.println("New day");
        daysCount++;
        Main.getInstance().getGameScreen().playDayStartAnimation(daysCount);
        currentTime = 0;
    }

    public float getDayLength() {
        return dayLength;
    }

    private float timeSpeed = 0.5f;

    public void render(float delta){
        currentTime += timeSpeed * delta;

        if(currentTime > dayLength){
            nextDay();
        }
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
}

