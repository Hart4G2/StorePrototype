//package io.github.store_prototype.objects.screen.weather.thunder;
//
//import io.github.store_prototype.objects.event_handling.SimpleEventListener;
//import io.github.store_prototype.objects.event_handling.events.Event;
//import io.github.store_prototype.objects.event_handling.events.weather.ThunderEvent;
//
//public class ThunderManager implements SimpleEventListener {
//
//    private boolean isThundering;
//    private float delay;
//    private float duration;
//    private float stateTime;
//    private ThunderShader thunderShader;
//
//    public ThunderManager() {
//        thunderShader = new ThunderShader();
//        stateTime = 0f;
//    }
//
//    public void render(float delta){
//        if(isThundering){
//            stateTime += delta;
//
//            if(stateTime > delay){
//                thunderShader.render();
//
//                isThundering = stateTime < delay + duration;
//            }
//        } else {
//            stateTime = 0f;
//        }
//    }
//
//    @Override
//    public void handleChange(Event event) {
//        try{
//            ThunderEvent thunderEvent = (ThunderEvent) event;
//            this.duration = thunderEvent.getDuration();
//            this.delay = thunderEvent.getDelay();
//            isThundering = true;
//        } catch (ClassCastException ignore){}
//    }
//
//    public void dispose(){
//        thunderShader.dispose();
//    }
//}
