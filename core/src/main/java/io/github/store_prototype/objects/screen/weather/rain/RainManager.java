//package io.github.store_prototype.objects.screen.weather.rain;
//
//import io.github.store_prototype.objects.event_handling.SimpleEventListener;
//import io.github.store_prototype.objects.event_handling.events.Event;
//import io.github.store_prototype.utils.Utils;
//
//public class RainManager implements SimpleEventListener {
//
//    private float[] duration;
//    private float stateTime;
//    private float intensity;
//    private float finalIntensity;
//    private float angle;
//    private boolean isRaining;
//    private RainShader rainShader;
//
//    public RainManager() {
//        rainShader = new RainShader();
//        isRaining = false;
//        stateTime = 0f;
//        intensity = 0f;
//        angle = -.15f;
//    }
//
//    public void render(float delta){
//        if(isRaining){
//            stateTime += delta;
//
//            if(stateTime < duration[0]){
//                if(intensity < finalIntensity){
//                    intensity += duration[0] / 10000;
//                }
//            } else if(stateTime < duration[0] + duration[1]) {
//                intensity = finalIntensity;
//            } else if(stateTime < duration[0] + duration[1] + duration[2]){
//                if(intensity > 0){
//                    intensity -= duration[0] / 10000;
//                }
//            }
//
//            rainShader.setIntensity(intensity);
//            rainShader.setAngle(angle);
//
//            rainShader.render(delta);
//
//            isRaining = stateTime < duration[0] + duration[1] + duration[2];
//        } else {
//            stateTime = 0f;
//            intensity = 0f;
//        }
//    }
//
//    @Override
//    public void handleChange(Event event) {
//        try{
//            RainEvent rainEvent = (RainEvent) event;
//            this.duration = rainEvent.getDuration();
//            this.finalIntensity = rainEvent.getIntensity();
//            angle = Utils.randomInt(1, 3) == 1 ? Utils.randomFloat(.1f, .3f) : Utils.randomFloat(-.3f, -.1f);
//            isRaining = true;
//        } catch (ClassCastException ignore){}
//    }
//
//    public void dispose(){
//        rainShader.dispose();
//    }
//}
