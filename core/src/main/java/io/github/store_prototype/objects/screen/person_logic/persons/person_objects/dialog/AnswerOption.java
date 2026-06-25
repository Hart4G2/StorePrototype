package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

public class AnswerOption {
    private String text;
    private boolean highlighted;
    private boolean visible;
    private DialogNode nextNode; // null, если это конечная реплика

    public AnswerOption(String text, boolean highlighted, boolean visible, DialogNode nextNode) {
        this.text = text;
        this.highlighted = highlighted;
        this.visible = visible;
        this.nextNode = nextNode;
    }

    public String getText() { return text; }
    public boolean isHighlighted() { return highlighted; }
    public boolean isVisible() { return visible; }
    public DialogNode getNextNode() { return nextNode; }
}
