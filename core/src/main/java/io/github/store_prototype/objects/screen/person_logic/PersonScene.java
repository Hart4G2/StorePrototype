package io.github.store_prototype.objects.screen.person_logic;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.event_handling.events.persons.duck.ReturningDuckEvent;
import io.github.store_prototype.objects.event_handling.events.upgrades.VendingBoughtEvent;
import io.github.store_prototype.objects.screen.GUI.EventSign;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.duck.AdultDuck;
import io.github.store_prototype.objects.screen.person_logic.persons.quests.duck.DuckChain;

public class PersonScene implements SimpleEventListener {

    private static PersonScene object;
    private List<Person> persons = new ArrayList<>();
    private List<Person> pendingAdditions = new ArrayList<>();

    private List<EventSign> eventSigns = new ArrayList<>();

    private boolean isVendingBought = false;
    private DuckChain duckChain;

    public static PersonScene getPersonScene() {
        if (object == null) {
            object = new PersonScene();
        }
        return object;
    }

    public PersonScene() {
//        duckChain = new DuckChain();
//        persons.addAll(duckChain.getDucks());

//        addPerson(new OldWomanWithRadio());
//        addPerson(new FishingMen());
    }

    private float generateTime = 0;
    private int personIndex = 4;
    private boolean isOldWoman = false;
    private boolean isDucks = false;

    public void render(Batch batch, float delta) {
        sortZIndex();

        List<Person> toRemove = new ArrayList<>();

        for (Person p : persons) {
            p.render(delta, batch);
            if (p.isEnded()) {
                toRemove.add(p);
            }
        }

        for (Person p : toRemove) {
            persons.remove(p);
            if (p instanceof AdultDuck && !((AdultDuck) p).isTalkMode()) {
                AdultDuck talkingDuck = new AdultDuck(AdultDuck.Phase.GOING_TO_STORE);
                talkingDuck.setDuckNoticed(((AdultDuck) p).isDuckNoticed());
                pendingAdditions.add(talkingDuck);
            }
        }

        persons.addAll(pendingAdditions);
        pendingAdditions.clear();

        generateTime += delta;
//
//        if(generateTime > 3){
//            generateTime = 0;
//            if(isVendingBought){
//                addPerson(PersonGenerator.generateLeftVendingBuyer(4));
//            }
//            personIndex++;
//        }

//        if(generateTime > 3 && !isOldWoman){
//            generateTime = 0;
//            addPerson(new OldWomanWithRadio());
//            personIndex++;
//            isOldWoman = true;
//        }

//        if(generateTime > 1 && !isDucks){
//            duckChain = new DuckChain();
//            pendingAdditions.addAll(duckChain.getDucks());
//            personIndex++;
//            isDucks = true;
//        }
    }

    public void sortZIndex() {
        persons.sort((p1, p2) -> Float.compare(p2.getY(), p1.getY()));
    }

    public void addPerson(Person person) {
        pendingAdditions.add(person);
    }

    public void removePerson(Person person) {
        persons.remove(person);
        if (person instanceof AdultDuck && !((AdultDuck) person).isTalkMode()) {
            AdultDuck talkingDuck = new AdultDuck(AdultDuck.Phase.GOING_TO_STORE);
            talkingDuck.setDuckNoticed(((AdultDuck) person).isDuckNoticed());
            addPerson(talkingDuck);
        }
    }

    public void resize(float width, float height){
        for (Person p : persons) {
            p.resize(width, height);
        }

        for (EventSign eventSign : eventSigns) {
            eventSign.resize();
        }
    }

    @Override
    public void handleChange(Event event) {
        try{
            VendingBoughtEvent e = (VendingBoughtEvent) event;
            System.out.println("PersonScene get event with vending machine!");
            isVendingBought = true;
        } catch (Exception ignore){}

        try {
            ReturningDuckEvent e = (ReturningDuckEvent) event;
            addPerson(duckChain.returningKid());
        } catch (Exception ignore) {}
    }

    public void addEventSign(EventSign eventSign) {
        eventSigns.add(eventSign);
    }

    public void removeEventSign(EventSign eventSign) {
        eventSigns.remove(eventSign);
    }
}


