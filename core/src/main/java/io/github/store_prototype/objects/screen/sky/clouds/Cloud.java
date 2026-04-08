//package io.github.store_prototype.objects.screen.sky.clouds;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.utils.Disposable;
//
//public class Cloud implements Disposable {
//
//    private float alpha = 0f;
//    private float x, y, width, height;
//    private float stateTime;
//    private Texture texture;
//    private boolean isFloatingToRight;
//
//    public Cloud(Texture texture, float x, float y) {
//        this.texture = texture;
//        this.x = x;
//        this.y = y;
//
//        this.width = Gdx.graphics.getWidth() / 4f;
//        this.height = Gdx.graphics.getHeight() / 4f;
//
//        isFloatingToRight = x < Gdx.graphics.getWidth() / -2f;
//
//        stateTime = 0;
//    }
//
//    public void render(Batch batch){
//        batch.setColor(1, 1, 1, alpha);
//        batch.draw(texture, x, y, width, height);
//        batch.setColor(1, 1, 1, 1);
//    }
//
//    public void update(float delta, float[] duration){
//        stateTime += delta;
//
//        animate(duration);
//    }
//
//    private void animate(float[] duration){
//        if (stateTime < duration[0] + duration[1]){
//            if(isFloatingToRight) {
//                floatingToRight(false);
//            } else {
//                floatingToLeft(false);
//            }
//
//            alpha += alpha < 1f ? .0005f : 0;
//        } else if(stateTime < duration[0] + duration[1] + duration[2]){
//            if(isFloatingToRight) {
//                floatingToLeft(true);
//            } else {
//                floatingToRight(true);
//            }
//
//            alpha -= alpha > 0f ? .0005f : 0;
//        }
//    }
//
//    private void floatingToRight(boolean exiting){
//        float halfWidth = width / 2;
//
//        if(y > Gdx.graphics.getHeight() / 5f){
//            if(x < Gdx.graphics.getWidth() / -3.5f - halfWidth || exiting) {
//                x += 1;
//            }
//        } else {
//            if(x < Gdx.graphics.getWidth() / -4.5f - halfWidth || exiting) {
//                x += 1;
//            }
//        }
//    }
//
//    private void floatingToLeft(boolean exiting){
//        float halfWidth = width / 2;
//
//        if(y > Gdx.graphics.getHeight() / 5f) {
//            if (x > Gdx.graphics.getWidth() / 4.5f - halfWidth || exiting) {
//                x -= 1;
//            }
//        } else {
//            if (x > Gdx.graphics.getWidth() / 3.5f - halfWidth || exiting) {
//                x -= 1;
//            }
//        }
//    }
//
//    public void resize(float width, float height) {
//        this.width = width / 5f;
//        this.height = height / 5f;
//    }
//
//    @Override
//    public void dispose() {
//        texture.dispose();
//    }
//}
