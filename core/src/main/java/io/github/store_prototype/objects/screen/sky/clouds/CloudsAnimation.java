//package io.github.store_prototype.objects.screen.sky.clouds;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.utils.Array;
//
//public class CloudsAnimation {
//
//    private Array<Cloud> clouds;
//    private float[] duration;
//
//    public CloudsAnimation(String textureName, float[] duration) {
//        if(duration.length != 3) {
//            throw new ArrayStoreException("Animation duration array must be 3 element length");
//        }
//        this.duration = duration;
//
//        clouds = new Array<>();
//
//        float x1 = Gdx.graphics.getWidth() / -2f - 700;
//        float x2 = Gdx.graphics.getWidth() / 2f + 100;
//
//        float y1 = Gdx.graphics.getHeight() / 5f + 100;
//        float y2 = Gdx.graphics.getHeight() / 7f - 100;
//
//        Texture t1 = new Texture("clouds/" + textureName + "1.png");
//        Texture t2 = new Texture("clouds/" + textureName + "2.png");
//        Texture t3 = new Texture("clouds/" + textureName + "3.png");
//        Texture t4 = new Texture("clouds/" + textureName + "4.png");
//
//        clouds.add(new Cloud(t1, x1, y1));
//        clouds.add(new Cloud(t2, x1, y2));
//        clouds.add(new Cloud(t3, x2, y1));
//        clouds.add(new Cloud(t4, x2, y2));
//    }
//
//    public void update(float delta){
//        for (Cloud c : clouds) {
//            c.update(delta, duration);
//        }
//    }
//
//    public void render(Batch batch){
//        for (Cloud c : clouds) {
//            c.render(batch);
//        }
//    }
//
//    public void resize(float width, float height){
//        for (Cloud c : clouds) {
//            c.resize(width, height);
//        }
//    }
//
//    public void dispose() {
//        for (Cloud c : clouds) {
//            c.dispose();
//        }
//    }
//}
