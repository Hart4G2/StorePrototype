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

import io.github.store_prototype.objects.screen.aserprite.AsepriteData;
import io.github.store_prototype.objects.screen.aserprite.frame.AsepriteFrame;
import io.github.store_prototype.objects.screen.aserprite.frame.Frame;
import io.github.store_prototype.utils.Assets;

public class Answer extends Table {

    private Label label;
    private Skin skin;
    private Animation<TextureRegionDrawable> backgroundAnimation;
    private TextureRegionDrawable backgroundIn;
    private TextureRegionDrawable background;
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

        backgroundIn = new TextureRegionDrawable(new Texture("answer_in.png"));
        background = new TextureRegionDrawable(new Texture("answer.png"));
        backgroundAnimation = getTextureRegionAnimation();

        setBackground(background);
    }

    private static Animation<TextureRegionDrawable> getTextureRegionAnimation() {
        Texture texture = new Texture("answer_highlighted.png");
        Json json = new Json();
        AsepriteData data = json.fromJson(AsepriteData.class, Gdx.files.internal("answer_highlighted.json"));

        Array<TextureRegionDrawable> regions = new Array<>();

        for (int i = 0; i <= 2; i++) {
            AsepriteFrame frameData = data.frames.get(i);
            Frame f = frameData.frame;
            TextureRegionDrawable region = new TextureRegionDrawable(new TextureRegion(texture, f.x, f.y, f.w, f.h));
            regions.add(region);
        }

        return new Animation<>(.2f, regions, Animation.PlayMode.LOOP);
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
                setBackground(backgroundAnimation.getKeyFrame(stateTime));
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
