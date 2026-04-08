//package io.github.store_prototype.objects.screen.sky.clouds;
//
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.utils.Disposable;
//
//import io.github.store_prototype.objects.event_handling.events.weather.ChangeDayPartEvent;
//import io.github.store_prototype.objects.event_handling.SimpleEventListener;
//import io.github.store_prototype.objects.event_handling.events.Event;
//import io.github.store_prototype.objects.screen.sky.shader_objects.TimesOfDay;
//
//public class CloudsManager implements Disposable, SimpleEventListener {
//
//    private CloudsAnimation cloudsAnimation;
//    private float[] duration;
//    private TimesOfDay state;
//
//    public CloudsManager(TimesOfDay state, float[] duration) {
//        this.state = state;
//        this.duration = duration;
//
//        regenerateAnimation();
//    }
//
//    public void regenerateAnimation(){
//        String cloudsName = "";
//
//        switch (state) {
//            case MORNING:  {
//                cloudsName = "day";
//                break;
//            }
//            case NIGHT:  {
//                cloudsName = "rain";
//                break;
//            }
//            default:
//                cloudsName = "blue";
//        }
//
//        cloudsAnimation = new CloudsAnimation(cloudsName, duration);
//    }
//
//    public void update(float delta){
//        cloudsAnimation.update(delta);
//    }
//
//    public void render(Batch batch){
//        cloudsAnimation.render(batch);
//    }
//
//    public void resize(int width, int height){
//        cloudsAnimation.resize(width, height);
//    }
//
//    @Override
//    public void dispose() {
//        cloudsAnimation.dispose();
//    }
//
//    @Override
//    public void handleChange(Event event) {
//        try{
//            ChangeDayPartEvent changeEvent = (ChangeDayPartEvent)event;
//
//            this.state = changeEvent.getState();
//            this.duration = changeEvent.getDuration();
//
//            regenerateAnimation();
//        } catch (ClassCastException ignore){}
//    }
//}
