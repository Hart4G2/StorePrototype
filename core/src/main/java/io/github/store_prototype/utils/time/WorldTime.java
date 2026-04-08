package io.github.store_prototype.utils.time;

import io.github.store_prototype.Main;

public class WorldTime {
    private float elapsedTime = 0f;
//    private final float dayLength = 600f;
    private final float dayLength = 22f;
    private int daysCount;

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
    }
}

