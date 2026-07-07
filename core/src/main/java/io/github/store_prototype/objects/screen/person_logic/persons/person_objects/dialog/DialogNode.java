package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

public class DialogNode {
    private Text text;
    private AnswerOption[] answers;

    public DialogNode(Text text, AnswerOption ans1, AnswerOption ans2, AnswerOption ans3) {
        this.text = text;
        this.answers = new AnswerOption[]{ans1, ans2, ans3};
    }

    public Text getText() { return text; }
    public AnswerOption[] getAnswers() { return answers; }
}
