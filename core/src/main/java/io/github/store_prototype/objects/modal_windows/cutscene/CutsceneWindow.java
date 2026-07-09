package io.github.store_prototype.objects.modal_windows.cutscene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.store_prototype.utils.assets.Assets;

public class CutsceneWindow extends Stack{
    private final CutsceneData data;
    private final Animation<TextureRegion> animation;
    private final Image animationImage;
    private final ProgressBar skipBar;
    private final Label replicaLabel;
    private float stateTime, totalTime, skipTimer;
    private boolean skipHeld, finished;
    private TextAnimation currentReplicaAnim;
    private int currentReplicaIndex = -1;
    private Runnable onFinished;

    public CutsceneWindow(CutsceneData data, Skin skin, Runnable onFinished) {
        this.data = data;
        this.onFinished = onFinished;
        setFillParent(true);

        animation = Assets.getAssets().getAnimation(data.animationPath, data.animationTag);
        animation.setFrameDuration(data.frameDuration);
        animation.setPlayMode(data.playMode);

        animationImage = new Image();
        add(animationImage);

        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.bottom();

        replicaLabel = new Label("", skin);
        Texture bgTexture = Assets.getAssets().getTexture("bg.png");
        if (bgTexture != null) {
            replicaLabel.getStyle().background = new TextureRegionDrawable(new TextureRegion(bgTexture));
        }
        replicaLabel.setWrap(true);
        uiTable.add(replicaLabel)
            .padLeft(50).padRight(50).padBottom(10)
            .growX()
            .height(Gdx.graphics.getHeight() / 4f)
            .left()
            .row();

        skipBar = new ProgressBar(0f, data.skipHoldDuration, 0.01f, false, skin);
        uiTable.add(skipBar)
            .width(200)
            .padRight(20).padBottom(20)
            .bottom().right();

        add(uiTable);

        // Слушатели
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                skipHeld = true; skipTimer = 0; return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                skipHeld = false; skipTimer = 0; skipBar.setValue(0);
            }
        });

        addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (currentReplicaAnim != null && !currentReplicaAnim.isFinished())
                    currentReplicaAnim.finishImmediately();
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (finished) return;
        stateTime += delta;
        totalTime += delta;

        animationImage.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(stateTime)));

        if (skipHeld) {
            skipTimer += delta;
            skipBar.setValue(skipTimer);
            if (skipTimer >= data.skipHoldDuration) { finishScene(); return; }
        } else skipBar.setValue(0);

        if (currentReplicaIndex < data.replicas.size() - 1) {
            CutsceneData.Replica next = data.replicas.get(currentReplicaIndex + 1);
            if (totalTime >= next.startTime) {
                currentReplicaIndex++;
                currentReplicaAnim = new TextAnimation(next.text, 30f);
            }
        }
        if (currentReplicaAnim != null) {
            currentReplicaAnim.update(delta);
            replicaLabel.setText(currentReplicaAnim.getVisibleText());
        }

        if (totalTime >= data.getTotalDuration() && (currentReplicaAnim == null || currentReplicaAnim.isFinished()))
            finishScene();
    }

    private void finishScene() {
        finished = true;
        onFinished.run();
        remove();
    }
}
