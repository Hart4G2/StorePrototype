package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import java.util.function.Consumer;

import io.github.store_prototype.Main;
import io.github.store_prototype.utils.assets.Assets;

public class DialogWindow extends Table {

    private DialogNode currentNode;
    private AnswerOption[] currentOptions;
    private Label textLabel;
    private String fullText;
    private Answer answer1, answer2, answer3;
    private Table textContainer;

    private float x, y, width, height;
    private Runnable onFinished;
    private Consumer<Integer> onAnswerSelected; // передаёт индекс выбранного ответа (0-2)

    private boolean answersShowed;

    public DialogWindow(DialogNode startNode) {
        this.currentNode = startNode;

        this.width = Gdx.graphics.getWidth() / 3f;
        this.height = Gdx.graphics.getHeight() / 4.5f;
        x = Gdx.graphics.getWidth() / 2f - this.width / 2f;
        y = Gdx.graphics.getHeight() / 1.5f;

        setBounds(x, y, width, height);

        answer1 = new Answer(new AnswerText(new Text(""), false, false));
        answer2 = new Answer(new AnswerText(new Text(""), false, false));
        answer3 = new Answer(new AnswerText(new Text(""), false, false));

        textLabel = new Label("", Assets.getAssets().getSkin(), "white_14");
        textContainer = new Table();

        textContainer.setBackground(new TextureRegionDrawable(Assets.getAssets().get("answer_in.png", Texture.class)));
        textContainer.add(textLabel).padBottom(15).padTop(15).padRight(20).padLeft(20);

        // Размещение в таблице
        for (int i = 0; i < 3; i++) {
            columnDefaults(i).expandX().uniform().center();
        }

        add().expandX();
        add(answer2).pad(10).center();
        add().expandX();
        row();

        add(answer1).pad(10).left();
        add(textContainer).pad(10).center().expandX();
        add(answer3).pad(10).right();
        row();

        answer1.setVisible(false);
        answer2.setVisible(false);
        answer3.setVisible(false);
        answer1.setTouchable(Touchable.disabled);
        answer2.setTouchable(Touchable.disabled);
        answer3.setTouchable(Touchable.disabled);

        addListenerToAnswer(answer1, 0);
        addListenerToAnswer(answer2, 1);
        addListenerToAnswer(answer3, 2);
        clickListenerShowText();
    }

    public void show() {
        updateFromNode(currentNode);
        showText();
    }

    private void updateFromNode(DialogNode node) {
        this.currentNode = node;
        currentOptions = node.getAnswers();

        updateAnswer(answer1, currentOptions[0]);
        updateAnswer(answer2, currentOptions[1]);
        updateAnswer(answer3, currentOptions[2]);

        textLabel.setText(node.getText().getComposed());
        fullText = node.getText().getComposed();
    }

    private void updateAnswer(Answer answer, AnswerOption option) {
        if (option != null) {
            answer.setAnswerText(new AnswerText(new Text(option.getText()), option.isHighlighted(), option.isVisible()));
        } else {
            answer.setAnswerText(new AnswerText(new Text(""), false, false));
        }
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
                Actions.sequence(actions.toArray(Action.class)),
                Actions.run(this::showAnswers)
            )
        );
    }

    private void showAnswers() {
        showAnswerIfVisible(answer1, -25);
        showAnswerIfVisible(answer2, 0);
        showAnswerIfVisible(answer3, 25);
    }

    private void showAnswerIfVisible(Answer answer, float xShift) {
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
                    Actions.moveTo(answer.getX() - xShift, answer.getY() - 25),
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
        textContainer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!textLabel.getText().toString().equals(fullText)) {
                    textLabel.clearActions();
                    textLabel.setText(fullText);
                    showAnswers();
                }
            }
        });
    }

    private void addListenerToAnswer(final Answer answer, final int index) {
        answer.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (answersShowed) {
                    answer.setBackgroundEnter(true);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (answersShowed) {
                    answer.setBackgroundEnter(false);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (answersShowed) {
                    onAnswerClicked(index);
                }
                answer.setBackgroundEnter(false);
            }
        });
    }

    private void onAnswerClicked(int index) {
        if (onAnswerSelected != null) {
            onAnswerSelected.accept(index);
        }

        AnswerOption chosen = currentOptions[index];
        if (chosen != null && chosen.getNextNode() != null) {
            answer1.setVisible(false);
            answer1.setTouchable(Touchable.disabled);
            answer1.clearActions();
            answer2.setVisible(false);
            answer2.setTouchable(Touchable.disabled);
            answer2.clearActions();
            answer3.setVisible(false);
            answer3.setTouchable(Touchable.disabled);
            answer3.clearActions();

            answersShowed = false;

            textLabel.addAction(Actions.fadeOut(.1f));
            currentNode = chosen.getNextNode();
            updateFromNode(currentNode);
            show();
        } else {
            // Конец диалога
            hideDialog();
        }
    }

    public void hideDialog() {
        this.addAction(Actions.sequence(
            Actions.fadeOut(.3f),
            Actions.run(() -> {
                setVisible(false);
                if (onFinished != null) onFinished.run();
            })
        ));
    }

    public void setOnAnswerSelected(Consumer<Integer> onAnswerSelected) {
        this.onAnswerSelected = onAnswerSelected;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public void resize(float screenWidth, float screenHeight) {
        width = screenWidth / 4f;
        height = screenHeight / 4.5f;
        x = screenWidth / 2f - this.width / 2f;
        y = screenHeight / 1.5f;
        setBounds(x, y, this.width, this.height);
    }
}
