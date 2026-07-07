package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github.store_prototype.Main;
import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.assets.Assets;

public class Answer extends Table {

    private Label label;
    private Skin skin;
    private Animation<TextureRegion> backgroundAnimation;
    private TextureRegionDrawable backgroundIn, background;
    private boolean backgroundEnter = false;
    private boolean highlighted;
    private float stateTime;
    private boolean visibleFromText;

    public Answer(AnswerText answerText) {
        this.skin = Assets.getAssets().getSkin();
        this.highlighted = answerText.isHighlighted();
        this.visibleFromText = answerText.isVisible();

        label = new Label(answerText.getText().getComposed(), skin, "white_14");
        label.setColor(Color.BLACK);

        add(label).center().pad(30);
        pack();

        backgroundIn = new TextureRegionDrawable(Assets.getAssets().getTexture("answer_in.png"));
        background = new TextureRegionDrawable(Assets.getAssets().getTexture("answer.png"));
        backgroundAnimation = Assets.getAssets().getAnimation("answer_highlighted",  "loop");

        setBackground(background);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;
        updateBackground();
    }

    private void updateBackground() {
        if (backgroundEnter) {
            label.setColor(Color.WHITE);
            setBackground(backgroundIn);
        } else {
            label.setColor(Color.BLACK);
            if (highlighted) {
                setBackground(new TextureRegionDrawable(backgroundAnimation.getKeyFrame(stateTime)));
            } else {
                setBackground(background);
            }
        }
    }

    public void setAnswerText(AnswerText answerText) {
        this.highlighted = answerText.isHighlighted();
        this.visibleFromText = answerText.isVisible();

        changeText(answerText.getText().getComposed());
    }

    private void changeText(String newText){
        label.setText(newText);
        pack();
    }

    public void setBackgroundEnter(boolean backgroundEnter){
        this.backgroundEnter = backgroundEnter;
    }

    public boolean isVisibleFromText() {
        return visibleFromText;
    }

    public Label getLabel() {
        return label;
    }
}
