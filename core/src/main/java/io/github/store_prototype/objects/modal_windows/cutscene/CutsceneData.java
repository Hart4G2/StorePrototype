package io.github.store_prototype.objects.modal_windows.cutscene;

import com.badlogic.gdx.graphics.g2d.Animation;

import java.util.ArrayList;
import java.util.List;

public class CutsceneData {
    public String animationPath;
    public String animationTag;
    public float frameDuration = 0.1f;
    public Animation.PlayMode playMode = Animation.PlayMode.NORMAL;
    public float skipHoldDuration = 2f;
    public List<Replica> replicas = new ArrayList<>();

    public static class Replica {
        public String text;
        public float startTime;
        public float duration;

        public Replica(String text, float startTime, float duration) {
            this.text = text;
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    public CutsceneData(String animationPath, String animationTag) {
        this.animationPath = animationPath;
        this.animationTag = animationTag;
    }

    public float getTotalDuration() {
        if (!replicas.isEmpty()) {
            float max = 0;
            for (Replica r : replicas) {
                float end = r.startTime + (r.duration > 0 ? r.duration : 5f);
                if (end > max) max = end;
            }
            return max;
        }
        return 5f;
    }
}
