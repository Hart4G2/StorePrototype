package io.github.store_prototype.objects.event_handling;

import io.github.store_prototype.objects.event_handling.events.Event;

public interface SimpleEventListener {

    void handleChange(Event event);

}
