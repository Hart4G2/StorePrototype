package io.github.store_prototype.screens.menu.savings;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveSlotData {
    public int slotIndex;
    public boolean isEmpty;
    public int day;
    public String progressDescription;
    public long lastSaveTimestamp;

    public String getFormattedDate() {
        if (isEmpty) return "Empty";
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(new Date(lastSaveTimestamp));
    }

    public String getSummary() {
        if (isEmpty) return "Empty slot";
        return "Day " + day + " - " + progressDescription + "\n" + getFormattedDate();
    }
}
