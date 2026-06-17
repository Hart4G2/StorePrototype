package io.github.store_prototype.objects.event_handling.events.upgrades;

import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.values.Value;

public class VendingBuyingEvent implements Event {

    private Value value;

    public VendingBuyingEvent(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
