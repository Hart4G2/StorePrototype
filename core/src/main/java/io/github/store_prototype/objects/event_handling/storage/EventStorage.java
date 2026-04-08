package io.github.store_prototype.objects.event_handling.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.persons.duck.DuckNoticedEvent;

public class EventStorage implements SimpleEventListener {

    private static EventStorage eventStorage;

    public static EventStorage getInstance() {
        if(eventStorage == null){
            return eventStorage = new EventStorage();
        }
        return eventStorage;
    }

    public EventStorage() {
        SimplePublisher.getPublisher().addListener(this);
    }

    private HashMap<String, Event> events = new HashMap<>();

    public boolean isInStorage(String event){
        return events.containsKey(event);
    }

    public void addEvent(String name, Event event){
        events.put(name, event);
    }

    @Override
    public void handleChange(Event event) {
        try{
            Event e = (DuckNoticedEvent) event;
            getInstance().addEvent("DuckNoticedEvent", e);
        } catch (ClassCastException ignore){}
    }
}
