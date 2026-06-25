package io.github.store_prototype.objects.screen.person_logic.persons.quests.duck;

import static io.github.store_prototype.utils.size.ScreenScaler.REF_WIDTH;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.screen.person_logic.persons.Person;

public class DuckChain {

    private List<Person> ducks = new ArrayList<>();

    public DuckChain() {
        ducks.add(new AdultDuck(AdultDuck.Phase.WALKING_OFFSCREEN));
        ducks.add(new DuckKid("kid_0", -220));
        ducks.add(new DuckKid("kid_1", -340));
        ducks.add(new DuckKid("kid_2", -460));
    }

    public List<Person> getDucks() {
        return ducks;
    }

    public DuckKid returningKid(){
        return new DuckKid("kid_0", REF_WIDTH * 2 / 3f);
    }
}
