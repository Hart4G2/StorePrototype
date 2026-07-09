package io.github.store_prototype.objects.modal_windows.cutscene;

public class TextAnimation {
    private final String fullText;
    private final float charsPerSecond;
    private float timer;
    private int visibleChars;
    private boolean finished;

    public TextAnimation(String text, float charsPerSecond) {
        this.fullText = text;
        this.charsPerSecond = charsPerSecond;
    }

    public void update(float delta) {
        if (finished) return;
        timer += delta;
        visibleChars = Math.min((int)(timer * charsPerSecond), fullText.length());
        if (visibleChars >= fullText.length()) finished = true;
    }

    public void finishImmediately() {
        visibleChars = fullText.length();
        finished = true;
    }

    public String getVisibleText() {
        return fullText.substring(0, visibleChars);
    }

    public boolean isFinished() {
        return finished;
    }
}
