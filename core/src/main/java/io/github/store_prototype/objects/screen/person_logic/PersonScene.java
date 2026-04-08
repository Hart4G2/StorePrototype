package io.github.store_prototype.objects.screen.person_logic;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.duck.AdultDuck;
import io.github.store_prototype.objects.screen.person_logic.persons.duck.DuckChain;
import io.github.store_prototype.utils.Utils;

public class PersonScene {

    private static PersonScene object;
    private DuckChain duckChain;

    public static PersonScene getPersonScene() {
        if (object == null) {
            object = new PersonScene();
        }
        return object;
    }

    private List<Person> persons = new ArrayList<>();

    public PersonScene() {
        duckChain = new DuckChain();
        persons.addAll(duckChain.getDucks());
//        persons.add(duckChain.getDucks().stream().filter(duck -> duck.getName().equals("adult")).findFirst().get());

        persons.add(PersonGenerator.generateRightPerson(1));
        persons.add(PersonGenerator.generateRightPerson(2));
        persons.add(PersonGenerator.generateLeftPerson(3));
//        persons.add(PersonGenerator.generateLeftPerson(4));
//        persons.add(PersonGenerator.generateRightSmuggler());
    }

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
    }

    public void sortZIndex() {
        persons.sort((p1, p2) -> Float.compare(p2.getY(), p1.getY()));
    }

    public void addPerson(Person person) {
        persons.add(person);
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
    }
}

