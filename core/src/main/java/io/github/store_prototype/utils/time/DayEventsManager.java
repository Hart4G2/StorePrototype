package io.github.store_prototype.utils.time;

import static io.github.store_prototype.objects.modal_windows.cutscene.CutsceneData.*;

import java.util.List;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.modal_windows.cutscene.CutsceneData;
import io.github.store_prototype.objects.modal_windows.cutscene.CutsceneLauncher;
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
    private CutsceneLauncher cutsceneLauncher;

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
        triggerDayCutscene(day);
    }

    /**
     * Вызывается при наступлении нового дня.
     */
    @Override
    public void onDayChanged(int newDay) {
        resetSmugglerTimer();
        applyDayEvents(newDay);
        triggerDayCutscene(newDay);
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
        smugglerDelay = Utils.randomFloat(10f, 50f);
    }

    private void triggerDayCutscene(int day) {
        if(cutsceneLauncher == null){
            setCutsceneLauncher(Main.getInstance().getGameScreen());
        }

//        switch (day) {
//            case 1: cutsceneLauncher.launchCutscene(createDay1Cutscene()); break;
//        }
    }

    private CutsceneData createDay1Cutscene() {
        CutsceneData cutsceneData = new CutsceneData("cutscenes/scene_1", "anim");
        Replica replica1 = new Replica("When you are young, you want a lot of money, a car, a wife, and a house by the ocean.", 0, 2);
        Replica replica2 = new Replica("But reality can be very cruel.", 3, 2);
        Replica replica3 = new Replica("I was 20, I opened my own shop in the capital, and I had almost everything I had ever dreamed of...", 6, 2);
        Replica replica4 = new Replica("The pandemic. Major chains managed to weather it, but not local shops like mine.", 9, 2);
        Replica replica5 = new Replica("I had to return to the city where I grew up.", 12, 2);
        Replica replica6 = new Replica("But I didn't want to give up at all.", 15, 2);
        Replica replica7 = new Replica("I opened a kiosk.", 18, 2);
        Replica replica8 = new Replica("This was my last chance to start my own business.", 20, 5);

        cutsceneData.replicas.addAll(List.of(new Replica[]{replica1, replica2, replica3, replica4, replica5, replica6, replica7, replica8}));

        return cutsceneData;
    }

    public void setCutsceneLauncher(CutsceneLauncher launcher) {
        this.cutsceneLauncher = launcher;
    }
}
