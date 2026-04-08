package io.github.store_prototype.utils.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import javax.swing.GroupLayout;

import io.github.store_prototype.Main;
import io.github.store_prototype.utils.Assets;

public class DayStartAnimation extends Label {

    public DayStartAnimation() {
        super("DAY", Assets.getAssets().getSkin(), "title");
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
