package io.github.store_prototype.objects.screen.person_logic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.event_handling.SimpleEventListener;
import io.github.store_prototype.objects.event_handling.events.Event;
import io.github.store_prototype.objects.screen.GUI.EventSign;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.duck.AdultDuck;
import io.github.store_prototype.objects.screen.person_logic.persons.duck.DuckChain;
import io.github.store_prototype.objects.screen.upgrades.UpgradeEvent;
import io.github.store_prototype.objects.screen.upgrades.UpgradeScene;
import io.github.store_prototype.utils.Utils;

public class PersonScene implements SimpleEventListener {

    private static PersonScene object;
    private List<Person> persons = new ArrayList<>();
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
//        persons.add(duckChain.getDucks().stream().filter(duck -> duck.getName().equals("adult")).findFirst().get());

//        persons.add(PersonGenerator.generateRightPerson(1));
//        persons.add(PersonGenerator.generateRightPerson(2));
//        persons.add(PersonGenerator.generateLeftPerson(3));
//        persons.add(PersonGenerator.generateLeftPerson(4));
//        persons.add(PersonGenerator.generateRightSmuggler());

//        persons.add(PersonGenerator.generateLeftPasserby(11));
    }

    private float generateTime = 0;
    private int personIndex = 4;

    public void render(Batch batch, float delta) {
        sortZIndex();

        for (Person p : persons) {
            p.render(delta, batch);
        }

        for (int i = 0; i < persons.size(); i++) {
            if(persons.get(i).isEnded()){
                removePerson(persons.get(i));
            }
        }

        generateTime += delta;

        if(generateTime > 3 && personIndex < 14){
            generateTime = 0;
            if(isVendingBought){
                addPerson(PersonGenerator.generateLeftVendingBuyer(4));
            }
            personIndex++;
        }
    }

    public void sortZIndex() {
        persons.sort((p1, p2) -> Float.compare(p2.getY(), p1.getY()));
    }

    public void addPerson(Person person) {
        persons.add(person);
    }

    public void addEventSign(EventSign eventSign) {
        eventSigns.add(eventSign);
    }

    public void removeEventSign(EventSign eventSign) {
        eventSigns.remove(eventSign);
    }

    public void removePerson(Person person) {
        persons.remove(person);

        if (person instanceof AdultDuck) {
            AdultDuck newAdultDuck = new AdultDuck(true);
            persons.add(newAdultDuck);
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
            UpgradeEvent e = (UpgradeEvent) event;
            if(e.getName() == UpgradeScene.Upgrades.VENDING_MACHINE){
                System.out.println("PersonScene get event with vending machine!");
                isVendingBought = true;
            }
        } catch (Exception ignore){}
    }
}

