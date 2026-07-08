package io.github.store_prototype.utils.time;

import io.github.store_prototype.objects.screen.person_logic.PersonGenerator;
import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.FishingMen;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.OldWomanWithRadio;
import io.github.store_prototype.utils.Utils;

public class DayEventsManager implements WorldTime.DayChangeListener {

    private float spawnTimer;
    private float smugglerTimer;
    private float smugglerDelay;
    private boolean smugglerSpawnedToday;

    private static final float SPAWN_INTERVAL = 8f;


    private static DayEventsManager instance;

    public static DayEventsManager getInstance() {
        if (instance == null) {
            instance = new DayEventsManager();
        }
        return instance;
    }

    private DayEventsManager() {}

    /**
     * Вызывается при загрузке игры с определённым днём.
     * Устанавливает состояние мира без повторного запуска "новодневных" событий.
     */
    public void initDay(int day) {
        resetSmugglerTimer();
        applyDayEvents(day);
    }

    /**
     * Вызывается при наступлении нового дня.
     */
    @Override
    public void onDayChanged(int newDay) {
        resetSmugglerTimer();
        applyDayEvents(newDay);
    }

    private void applyDayEvents(int day) {
        switch (day) {
            case 1:
                PersonScene.getPersonScene().startDucks();
                break;
            case 2:
                PersonScene.getPersonScene().addPerson(new FishingMen());
                break;
            case 3:
                PersonScene.getPersonScene().addPerson(new OldWomanWithRadio());
                break;
            default:
                break;
        }
    }

    public void reset() {
        instance = null;
    }

    public void update(float delta) {
        spawnTimer += delta;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer -= SPAWN_INTERVAL;
            spawnRandomPerson();
        }

        if (!smugglerSpawnedToday) {
            smugglerTimer += delta;
            if (smugglerTimer >= smugglerDelay) {
                PersonScene.getPersonScene().addPerson(PersonGenerator.generateSmuggler());
                smugglerSpawnedToday = true;
            }
        }
    }

    private void spawnRandomPerson() {
//        // 30% покупатель, 70% прохожий
//        if (Utils.randomInt(0, 10) < 3) {
//            int personNum = Utils.randomInt(4, 8);
//            PersonScene.getPersonScene().addPerson(PersonGenerator.generatePerson(personNum));
//        } else {
//            int personNum = Utils.randomInt(4, 8);
//            PersonScene.getPersonScene().addPerson(PersonGenerator.generatePasserby(personNum));
//        }
    }

    private void resetSmugglerTimer() {
        smugglerSpawnedToday = false;
        smugglerTimer = 0f;
//        smugglerDelay = Utils.randomFloat(10f, 50f);
        smugglerDelay = 0;
    }
}
