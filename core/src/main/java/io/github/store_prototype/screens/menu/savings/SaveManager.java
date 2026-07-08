package io.github.store_prototype.screens.menu.savings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    private static final int MAX_SLOTS = 3;
    private final Preferences prefs;

    public SaveManager() {
        prefs = Gdx.app.getPreferences("game_saves");
    }

    public SaveManager(Preferences prefs) {
        this.prefs = prefs;
    }

    public SaveSlotData getSlot(int index) {
        String json = prefs.getString("slot_" + index, null);
        if (json == null) {
            SaveSlotData empty = new SaveSlotData();
            empty.slotIndex = index;
            empty.isEmpty = true;
            return empty;
        }
        // восстановление из JSON (можно использовать Json из libGDX)
        return new Json().fromJson(SaveSlotData.class, json);
    }

    public void saveSlot(SaveSlotData data) {
        String json = new Json().toJson(data);
        prefs.putString("slot_" + data.slotIndex, json);
        prefs.flush();
    }

    public void deleteSlot(int index) {
        prefs.remove("slot_" + index);
        prefs.flush();
    }
}
