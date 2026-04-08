package io.github.store_prototype.objects.screen.person_logic.persons.duck;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.screen.person_logic.persons.Person;

public class DuckChain {

    private List<Person> ducks = new ArrayList<>();

    public DuckChain() {
        ducks.add(new AdultDuck(false));
        ducks.add(new DuckKid("kid_0"));
        ducks.add(new DuckKid("kid_1"));
        ducks.add(new DuckKid("kid_2"));
    }

    public List<Person> getDucks() {
        return ducks;
    }
}
