package io.github.store_prototype.objects.screen.person_logic;

import static io.github.store_prototype.objects.screen.person_logic.persons.Person.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import io.github.store_prototype.objects.screen.person_logic.persons.Buyer;
import io.github.store_prototype.objects.screen.person_logic.persons.Passerby;
import io.github.store_prototype.objects.screen.person_logic.persons.Person;
import io.github.store_prototype.objects.screen.person_logic.persons.smuggler.Smuggler;
import io.github.store_prototype.utils.Utils;

public class PersonGenerator {

//    public static Person generatePerson(int personNum){
//        float startY = Utils.randomFloat(Gdx.graphics.getHeight() / 3.6f, Gdx.graphics.getHeight() / 3f);
//        PersonState state = Utils.randomInt(1, 3) == 1 ? PersonState.RIGHT : PersonState.LEFT;
//        float startX = state == PersonState.RIGHT ? -100 : Gdx.graphics.getWidth() + 100;
//
////        return Utils.randomInt(1, 3) == 1 ? new Passerby(startX, startY, state) : new Buyer(startX, startY, state);
//        return new Buyer(startX, startY, state, personNum);
//    }

    public static Person generatePerson(int personNum){
        float startY = Utils.randomFloat(250f, 300f);
        PersonState state = Utils.randomInt(1, 3) == 1 ? PersonState.RIGHT : PersonState.LEFT;
        float startX = state == PersonState.RIGHT ? -100 : 1700;

    //        return Utils.randomInt(1, 3) == 1 ? new Passerby(startX, startY, state) : new Buyer(startX, startY, state);
        return new Buyer(startX, startY, state, personNum);
    }

    public static Person generateRightPerson(int personNum) {
        float startY = Utils.randomFloat(250f, 300f);

        return new Buyer(-100, startY, PersonState.RIGHT, personNum);
    }

    public static Person generateLeftPerson(int personNum) {
        float startY = Utils.randomFloat(250f, 300f);

        return new Buyer(1700, startY, PersonState.LEFT, personNum);
    }

    public static Person generateSmuggler(){
        PersonState state = Utils.randomInt(1, 3) == 1 ? PersonState.RIGHT : PersonState.LEFT;
        float startX = state == PersonState.RIGHT ? -100 : 1700;

        return new Smuggler(startX, 900, state);
    }

    public static Person generateLeftSmuggler(){
        return new Smuggler(1700, 900 / 2.8f, PersonState.LEFT);
    }

    public static Person generateRightSmuggler(){
        return new Smuggler(-100, 300f, PersonState.RIGHT);
    }

    public static Array<Person> generatePersonsForZIndexText(){
        Array<Person> persons = new Array<>();

        persons.add(new Buyer(-100, Gdx.graphics.getHeight() / 3.3f, PersonState.RIGHT, 1, true));
        persons.add(new Buyer(-100, Gdx.graphics.getHeight() / 3.2f, PersonState.RIGHT, 2));

        return persons;
    }
}
