package io.github.store_prototype.objects.screen.person_logic.persons;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Person {

    enum PersonState {
        RIGHT, LEFT, STAYING, BUYING, FALLING, SITTING, GETTING_UP
    }

    void render(float delta, Batch batch);
    void resize(float width, float height);
    boolean isEnded();
    PersonState getState();
    float getX();
    float getY();
}

