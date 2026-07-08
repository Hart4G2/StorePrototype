package io.github.store_prototype.utils.time;

import io.github.store_prototype.objects.screen.person_logic.PersonScene;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.FishingMen;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.OldWomanWithRadio;

public class DayEventsManager implements WorldTime.DayChangeListener {
    private static DayEventsManager instance;

    public static DayEventsManager getInstance() {
        if (instance == null) {
            instance = new DayEventsManager();
        }
        return instance;
    }

    private DayEventsManager() {

    }

    /**
     * Вызывается при загрузке игры с определённым днём.
     * Устанавливает состояние мира без повторного запуска "новодневных" событий.
     */
    public void initDay(int day) {
        applyDayEvents(day);
    }

    /**
     * Вызывается при наступлении нового дня.
     */
    @Override
    public void onDayChanged(int newDay) {
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
}
