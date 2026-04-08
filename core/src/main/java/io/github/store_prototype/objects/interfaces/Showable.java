package io.github.store_prototype.objects.interfaces;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

public interface Showable extends Disposable {

    void render(Batch batch);

    void resize(int width, int height);
}
