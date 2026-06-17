package io.github.store_prototype.objects.screen.upgrades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import io.github.store_prototype.utils.Assets;

public class VendingPriceSign {

    private Label label;

    public VendingPriceSign(Stage stage, String text, float x, float y, float width) {
        label = new Label(text, Assets.getAssets().getSkin());
        label.setWidth(width);
        label.setAlignment(Align.center);
        label.setPosition(x, y);
        label.setColor(Color.YELLOW);

        stage.addActor(label);
        animate();
    }

    private void animate() {
        label.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, 10, 0.6f),
                Actions.fadeIn(0.6f)
            ),
            Actions.delay(0.8f),
            Actions.moveBy(0, -20, 0.3f),
            Actions.fadeOut(0.2f)
        ));
    }
}
