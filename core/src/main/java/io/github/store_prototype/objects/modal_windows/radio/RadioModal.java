package io.github.store_prototype.objects.modal_windows.radio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import io.github.store_prototype.utils.assets.Assets;

public class RadioModal extends Table {
    private KnobActor knobFM, knobVolume;
    private float targetFrequency = 103.7f;
    private float targetVolume = 0.8f;
    private boolean taskCompleted = false;
    private TextButton okButton;
    private Runnable onCompleted;
    private SignalDisplay signalDisplay;

    private float knobSize = 73, knobY = 495, fmKnobX = 60, volumeKnobX = 720;
    private float displayWidth = 505, displayHeight = 48, displayX = fmKnobX + 115, displayY = knobY + 15;

    private Label freqLabel, volLabel;

    private Sound noiseSound;
    private Music music;
    private long noiseId, musicId;

    public RadioModal(Skin skin, Runnable onCompleted) {
        this.onCompleted = onCompleted;
        setFillParent(true);
        align(Align.center);

        Texture radioTex = Assets.getAssets().getTexture("gamescene/mini_games/radio/radio.png");
        Texture knobFMTex = Assets.getAssets().getTexture("gamescene/mini_games/radio/knob_fm.png");
        Texture knobVolumeTex = Assets.getAssets().getTexture("gamescene/mini_games/radio/knob_volume.png");

        Image radioBody = new Image(new TextureRegionDrawable(new TextureRegion(radioTex)));

        knobFM = new KnobActor(new TextureRegion(knobFMTex));
        knobVolume = new KnobActor(new TextureRegion(knobVolumeTex));

        noiseSound = Gdx.audio.newSound(Gdx.files.internal("gamescene/mini_games/radio/noise.ogg"));
        music = Gdx.audio.newMusic(Gdx.files.internal("song.mp3"));

        okButton = new TextButton("OK", skin);
        okButton.setVisible(false);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (taskCompleted) close();
            }
        });

        Texture whiteTex = new Texture("white_pixel.png");
        TextureRegion whitePixel = new TextureRegion(whiteTex);
        signalDisplay = new SignalDisplay(whitePixel);

        Group controlsGroup = new Group();
        controlsGroup.setSize(radioBody.getWidth(), radioBody.getHeight());

        knobFM.setBounds(fmKnobX, knobY, knobSize, knobSize);
        knobVolume.setBounds(volumeKnobX, knobY, knobSize, knobSize);

        signalDisplay.setBounds(displayX, displayY, displayWidth, displayHeight);

        controlsGroup.addActor(signalDisplay);
        controlsGroup.addActor(knobFM);
        controlsGroup.addActor(knobVolume);

        Stack radioStack = new Stack();
        radioStack.add(radioBody);
        radioStack.add(controlsGroup);

        add(radioStack).size(radioBody.getWidth() * 3, radioBody.getHeight() * 3).center().pad(10);
        row();
        add(okButton).center().width(100).height(50).pad(20);

        addKnobListener(knobFM);
        addKnobListener(knobVolume);

        signalDisplay.setTargets(targetFrequency, targetVolume);

        freqLabel = new Label("88.0 FM", skin, "white_12");
        volLabel = new Label("Vol: 0.50", skin, "white_12");
        controlsGroup.addActor(freqLabel);
        controlsGroup.addActor(volLabel);
        freqLabel.setPosition(displayX, displayY + displayHeight + 5);
        volLabel.setPosition(displayX + displayWidth - volLabel.getWidth(), displayY + displayHeight + 5);

        showRadio();
    }

    public void showRadio() {
        noiseId = noiseSound.loop(0f);
        music.setLooping(true);
        music.play();
        music.setVolume(0f);
    }

    private void addKnobListener(KnobActor knob) {
        knob.addListener(new DragListener() {
            private float startAngle;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                startAngle = angleToCenter(x, y);
            }
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if(!taskCompleted) {
                    float current = angleToCenter(x, y);
                    float delta = current - startAngle;
                    if (delta > 180) delta -= 360;
                    if (delta < -180) delta += 360;
                    knob.setAngle(knob.getAngle() + delta);
                    startAngle = current;
                }
            }
            private float angleToCenter(float x, float y) {
                float dx = x - knob.getOriginX();
                float dy = y - knob.getOriginY();
                return MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!taskCompleted) {
            float freq = knobFM.getFrequency();
            float volAngle = knobVolume.getAngle();
            float minVolAngle = knobVolume.getMinAngle();
            float maxVolAngle = knobVolume.getMaxAngle();
            float volume = MathUtils.map(maxVolAngle, minVolAngle, 0f, 1f, volAngle);
            volume = MathUtils.clamp(volume, 0f, 1f);

            freqLabel.setText(String.format("%.1f FM", freq));
            volLabel.setText(String.format("Vol: %.2f", volume));

            float freqError = Math.abs(freq - targetFrequency) / 20f;
            float volError = Math.abs(volume - targetVolume);
            float totalError = MathUtils.clamp((freqError + volError) / 2f, 0f, 1f);

            float noiseVolume = totalError;
            float musicVolume = MathUtils.clamp(1f - totalError / 0.3f, 0f, 1f);

            noiseSound.setVolume(noiseId, noiseVolume * 0.0008f);
            music.setVolume(musicVolume * 0.005f);

            signalDisplay.setFrequency(freq);
            signalDisplay.setAmplitude(volume);

            float freqDist = Math.abs(freq - targetFrequency);
            float volDist = Math.abs(volume - targetVolume);

            if (freqDist < 0.5f && volDist < 0.03f) {
                taskCompleted = true;
                knobFM.clearListeners();
                knobVolume.clearListeners();
                okButton.setVisible(true);
                signalDisplay.playSuccessAnimation();
            }
        }
    }

    private void close() {
        noiseSound.stop(noiseId);
        noiseSound.dispose();
        music.stop();
        music.dispose();
        remove();
        if (onCompleted != null) onCompleted.run();
    }
}
