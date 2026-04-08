package io.github.store_prototype.objects.event_handling;

import com.badlogic.gdx.utils.Array;

import io.github.store_prototype.objects.event_handling.events.Event;

public class SimplePublisher {

    private static SimplePublisher publisher;

    private Array<SimpleEventListener> listeners;

    public SimplePublisher() {
        listeners = new Array<>();
    }

    public void publish(Event event){
        for (int i = 0; i < listeners.size; i++) {
            listeners.get(i).handleChange(event);
        }
    }

    public void addListener(SimpleEventListener listener){
        listeners.add(listener);
    }

    public static SimplePublisher getPublisher() {
        if(publisher == null){
            publisher = new SimplePublisher();
        }
        return publisher;
    }
}
