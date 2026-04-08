package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

public class AnswerText {

    private Text text;
    private boolean highlighted = false;
    private boolean visible = false;

    public AnswerText(Text text, boolean highlighted, boolean visible) {
        this.text = text;
        this.highlighted = highlighted;
        this.visible = visible;
    }

    public Text getText() {
        return text;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public boolean isVisible() {
        return visible;
    }
}
