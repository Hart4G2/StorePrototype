package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.List;

import io.github.store_prototype.screens.GameScreen;
import io.github.store_prototype.utils.Assets;

public class DialogWindow extends Table {

    private List<Dialog> answers;
    private int dialogIndex = 0;
    private Label textLabel;
    private String fullText;
    private Answer answer1;
    private Answer answer2;
    private Answer answer3;

    private Container<Label> textContainer;

    private float x, y, width, height;

    public DialogWindow(List<Dialog> answers) {
        this.answers = answers;

        this.width = Gdx.graphics.getWidth() / 3f; // 400 px
        this.height = Gdx.graphics.getHeight() / 4.5f; // 200 px
        x = Gdx.graphics.getWidth() / 2f - this.width / 2f;
        y = Gdx.graphics.getHeight() / 1.5f; // 600 px

        setBounds(x, y, width, height);

        answer1 = new Answer(answers.get(dialogIndex).getAnswer1());
        answer2 = new Answer(answers.get(dialogIndex).getAnswer2());
        answer3 = new Answer(answers.get(dialogIndex).getAnswer3());
        textLabel = new Label(answers.get(dialogIndex).getText().getComposed(), Assets.getAssets().getSkin(), "white_12");
        textContainer = new Container<>(textLabel);

        for (int i = 0; i < 3; i++) {
            columnDefaults(i).expandX().uniform().center();
        }

        add().expandX();
        add(answer2).pad(10).center();
        add().expandX();
        row();

        add(answer1).pad(10).left();
        add().expandX();
        add(answer3).pad(10).right();
        row();

        add().expandX();
        add(textContainer).pad(10).center();
        add().expandX();

        answer1.setVisible(false);
        answer2.setVisible(false);
        answer3.setVisible(false);
        answer1.setTouchable(Touchable.disabled);
        answer2.setTouchable(Touchable.disabled);
        answer3.setTouchable(Touchable.disabled);

        addListenerToAnswer(answer1);
        addListenerToAnswer(answer2);
        addListenerToAnswer(answer3);
        clickListenerShowText();
    }

    public void show() {
        showText();
    }

    private void showText() {
        fullText = textLabel.getText().toString();
        textLabel.setText("");

        Array<Action> actions = new Array<>();

        StringBuilder visibleText = new StringBuilder();

        for (int i = 0; i < fullText.length(); i++) {
            final char c = fullText.charAt(i);

            actions.add(Actions.delay(0.05f));

            actions.add(Actions.run(() -> {
                visibleText.append(c);
                textLabel.setText(visibleText.toString());
            }));
        }

        textLabel.addAction(
            Actions.sequence(
                Actions.fadeIn(.5f),
                Actions.sequence(
                    actions.toArray(Action.class)
                ),
                Actions.run(this::showAnswers)
            )
        );
    }

    private boolean answersShowed;
    private void showAnswers() {
        showAnswerIfVisible(answer1, -25);
        showAnswerIfVisible(answer2, 0);
        showAnswerIfVisible(answer3, 25);
    }

    private void showAnswerIfVisible(Answer answer, float xShift){
        if (answer.isVisibleFromText()) {
            showAnswer(answer, xShift);
        } else {
            answer.setVisible(false);
            answer.setTouchable(Touchable.disabled);
        }
    }

    private void showAnswer(Answer answer, float xShift) {
        float answerX = answer.getX();
        float answerY = answer.getY();

        answer.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(answer.getX() - xShift, answer.getY() - (float) 25),
                    Actions.fadeOut(.01f)
                ),
                Actions.run(() -> {
                    answer.setVisible(true);
                    answer.setTouchable(Touchable.enabled);
                }),
                Actions.parallel(
                    Actions.moveTo(answerX, answerY, 1f),
                    Actions.fadeIn(1f)
                ),
                Actions.run(() -> answersShowed = true)
            )
        );
    }

    private void clickListenerShowText() {
        textLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!textLabel.getText().toString().equals(fullText)) {
                    textLabel.clearActions();
                    textLabel.setText(fullText);
                    showAnswers();
                }
            }
        });
    }

    private void addListenerToAnswer(final Answer answer) {
        answer.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if(answersShowed) {
                    answer.setBackgroundEnter(true);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if(answersShowed) {
                    answer.setBackgroundEnter(false);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(answersShowed) {
                    nextPart();
                }
                answer.setBackgroundEnter(false);

                System.out.println("Selected: " + answer.getLabel().getText());
            }
        });
    }

    private void nextPart() {
        dialogIndex++;

        if(dialogIndex < answers.size()){
            answersShowed = false;
            textLabel.addAction(Actions.fadeOut(.1f));
            nextAnswerAction(answer1, answers.get(dialogIndex).getAnswer1());
            nextAnswerAction(answer2, answers.get(dialogIndex).getAnswer2());
            nextAnswerAction(answer3, answers.get(dialogIndex).getAnswer3());
            textLabel.setText(answers.get(dialogIndex).getText().getComposed());
            show();
        } else {
            hideDialog();
        }
    }

    private void nextAnswerAction(Answer answer, AnswerText nextAnswer){
        answer.addAction(Actions.sequence(
            Actions.fadeOut(.3f),
            Actions.run(() -> {
                answer.setVisible(false);
                answer.setTouchable(Touchable.disabled);
                answer.setAnswerText(nextAnswer);
                invalidate();
            })
        ));
    }

    public void hideDialog() {
        this.addAction(Actions.sequence(
            Actions.fadeOut(.3f),
            Actions.run(() -> setVisible(false))
        ));
    }

    public void resize(float screenWidth, float screenHeight) {
        width = screenWidth / 4f;
        height = screenHeight / 4.5f;
        x = screenWidth / 2f - this.width / 2f;
        y = screenHeight / 1.5f;

        setBounds(x, y, this.width, this.height);
    }
}
