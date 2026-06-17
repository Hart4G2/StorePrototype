package io.github.store_prototype.objects.screen.upgrades;

import static io.github.store_prototype.objects.screen.upgrades.UpgradeScene.*;

import io.github.store_prototype.objects.event_handling.events.Event;

public class UpgradeEvent implements Event {

    private Upgrades name;

    public UpgradeEvent(Upgrades name) {
        this.name = name;
    }

    public Upgrades getName() {
        return name;
    }
}
