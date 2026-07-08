package io.github.store_prototype.objects.screen.person_logic.persons;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.SimplePublisher;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.PersonLeftStoreEvent;

public class StoreQueue implements SimpleEventListener {

    private List<Person> queue;
    private Person activeBuyer;

    private static StoreQueue instance;

    public static StoreQueue getInstance() {
        if(instance == null){
            instance = new StoreQueue();
        }
        return instance;
    }

    public StoreQueue() {
        SimplePublisher.getPublisher().addListener(this);
        queue = new LinkedList<>();
    }

    @Override
    public void handleChange(Event event) {
        try{
            PersonLeftStoreEvent e = (PersonLeftStoreEvent) event;
            if (!queue.isEmpty()) {
                activeBuyer = queue.remove(0);
                System.out.println(queue.size());
            } else {
                activeBuyer = null;
            }
        } catch (ClassCastException ignore){}
    }

    public void addPerson(Person person){
        if(getActiveBuyer() == null){
            activeBuyer = person;
        } else {
            queue.add(person);
        }
    }

    public Person getActiveBuyer(){
        return activeBuyer;
    }

    public int getSize(){
        return queue.size();
    }

    public int getIndexInQueue(Person person) {
        return queue.indexOf(person);
    }

    public void clear() {
        queue.clear();
        activeBuyer = null;
    }
}
