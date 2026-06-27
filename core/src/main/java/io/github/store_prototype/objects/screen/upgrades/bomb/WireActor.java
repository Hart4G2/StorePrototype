package io.github.store_prototype.objects.screen.upgrades.bomb;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class WireActor extends Actor {
    public enum State { NORMAL, HOVER, CUT }

    private TextureRegion normalRegion, hoverRegion, cutRegion;
    private State state = State.NORMAL;
    private boolean cuttable = true;

    private Runnable onCut;

    public WireActor(TextureRegion normal, TextureRegion hover, TextureRegion cut) {
        this.normalRegion = normal;
        this.hoverRegion = hover;
        this.cutRegion = cut;
        setSize(normal.getRegionWidth() * 2, normal.getRegionHeight() * 2);

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (state == State.NORMAL && cuttable) {
                    setState(State.HOVER);
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (state == State.HOVER && cuttable) {
                    setState(State.NORMAL);
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (cuttable && state != State.CUT) {
                    cut();
                    event.stop();
                    return true;
                }
                return false;
            }
        });
    }

    public void setOnCut(Runnable onCut) {
        this.onCut = onCut;
    }

    public void cut() {
        if (!cuttable) return;
        cuttable = false;
        setState(State.CUT);
        if (onCut != null) onCut.run();
    }

    private void setState(State newState) {
        state = newState;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region;
        switch (state) {
            case HOVER: region = hoverRegion; break;
            case CUT:   region = cutRegion;   break;
            default:    region = normalRegion;
        }
        batch.setColor(getColor());
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public void setCuttable(boolean cuttable) {
        this.cuttable = cuttable;
    }
}
