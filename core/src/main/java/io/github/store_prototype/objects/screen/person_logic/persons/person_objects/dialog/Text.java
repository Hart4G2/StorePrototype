package io.github.store_prototype.objects.screen.person_logic.persons.person_objects.dialog;

public class Text {

    private String original;
    private String composed;

    public Text(String text) {
        this.original = text;
        composed = compactText(text);
    }

    private String compactText(String text) {
        if (text.length() <= 15) {
            return text;
        }

        StringBuilder compacted = new StringBuilder();
        String[] words = text.split(" ");

        int currentLineLength = 0;

        for (String word : words) {

            if (currentLineLength + word.length() > 20) {
                compacted.append("\n");
                currentLineLength = 0;
            }

            compacted.append(word).append(" ");
            currentLineLength += word.length() + 1;
        }

        return compacted.toString().trim();
    }

    public String getOriginal() {
        return original;
    }

    public String getComposed() {
        return composed;
    }
}
