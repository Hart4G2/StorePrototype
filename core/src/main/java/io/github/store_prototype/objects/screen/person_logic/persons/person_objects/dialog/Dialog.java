package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

public class Dialog {

    private Text text;
    private AnswerText answer1;
    private AnswerText answer2;
    private AnswerText answer3;

    public Dialog(Text text, AnswerText answer1, AnswerText answer2, AnswerText answer3) {
        this.text = text;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
    }

    public Text getText() {
        return text;
    }

    public AnswerText getAnswer1() {
        return answer1;
    }

    public AnswerText getAnswer2() {
        return answer2;
    }

    public AnswerText getAnswer3() {
        return answer3;
    }
}
