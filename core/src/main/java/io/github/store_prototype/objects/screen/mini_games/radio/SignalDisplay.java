package io.github.store_prototype.objects.screen.mini_games.radio;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SignalDisplay extends Actor {
    private float frequency = 88f;
    private float amplitude = 0.5f;
    private float targetFrequency = 103.7f;
    private float targetAmplitude = 0.8f;
    private TextureRegion whitePixel;
    private float dispFrequency = 88f;
    private float dispAmplitude = 0.5f;
    private float smoothSpeed = 10f;
    private boolean successAnimating = false;
    private float successTimer = 0;
    private final float successDuration = 1.5f;

    public SignalDisplay(TextureRegion whitePixel) {
        this.whitePixel = whitePixel;
        setSize(200, 80);
    }

    public void setTargets(float targetFreq, float targetAmp) {
        this.targetFrequency = targetFreq;
        this.targetAmplitude = targetAmp;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float factor = 1f - (float)Math.exp(-smoothSpeed * delta);
        dispFrequency = MathUtils.lerp(dispFrequency, frequency, factor);
        dispAmplitude = MathUtils.lerp(dispAmplitude, amplitude, factor);

        if (successAnimating) {
            successTimer += delta;
            if (successTimer >= successDuration) {
                successTimer = 0;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        batch.setColor(0.1f, 0.1f, 0.1f, 0.8f);
//        batch.draw(whitePixel, getX(), getY(), getWidth(), getHeight());

        float midY = getY() + getHeight() / 2f;
        float maxAmp = getHeight() / 2f - 5;
        float currentAmp = dispAmplitude * maxAmp;
        float step = 2f;

        float animBoost = 1f;
        if (successAnimating) {
            float t = successTimer / successDuration;
            animBoost = 1f + 0.5f * MathUtils.sin(t * MathUtils.PI);
        }

        Color color;
        if (successAnimating) {
            color = new Color(0.2f, 1f, 0.5f, 1f);
        } else {
            color = getSinColor();
        }
        batch.setColor(color);

        // Рисуем синусоиду
        float effectiveAmp = currentAmp * animBoost;
        for (float x = 0; x < getWidth(); x += step) {
            float angle = (x / getWidth()) * 2f * MathUtils.PI * (dispFrequency / 40f);
            float y = midY + effectiveAmp * MathUtils.sin(angle);
            batch.draw(whitePixel, getX() + x, y, 1, 2);
        }
        batch.setColor(1, 1, 1, 1);
    }

    private Color getSinColor() {
        float freqError = Math.abs(frequency - targetFrequency) / 20f;
        float ampError = Math.abs(amplitude - targetAmplitude);
        float totalError = MathUtils.clamp((freqError + ampError) / 2f, 0f, 1f);

        Color color;
        if (totalError < 0.5f) {
            float t = totalError * 2f;
            color = new Color(t, 1f, 0.2f, 1);
        } else {
            float t = (totalError - 0.5f) * 2f;
            color = new Color(1f, (1f - t), 0.2f, 1);
        }
        if (totalError < 0.1f) {
            color = new Color(0.2f, 1f, 0.5f, 1);
        }
        return color;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = MathUtils.clamp(amplitude, 0f, 1f);
    }

    public void playSuccessAnimation() {
        successAnimating = true;
        successTimer = 0;
    }
}
