package io.github.store_prototype.utils.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import io.github.store_prototype.utils.assets.Assets;

public class DayStartAnimation extends Label {



    public DayStartAnimation(Skin skin) {
        super("DAY", skin, "title");
        setWidth(100);
        setHeight(20);
        setX(Gdx.graphics.getWidth() / 2f - 25);
        setY(Gdx.graphics.getHeight() / 2f - 10);
        addAction(sequence(
            alpha(0f)
        ));
    }

    public void play(int dayNumber) {
        setText("Day №" + dayNumber);
        addAction(sequence(
            fadeIn(5f),
            fadeOut(5f)
        ));
    }
}
