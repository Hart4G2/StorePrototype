//package io.github.store_prototype.objects.screen.weather;
//
//import com.badlogic.gdx.utils.Disposable;
//
//import io.github.store_prototype.objects.event_handling.SimplePublisher;
//import io.github.store_prototype.utils.Utils;
//
//public class Weather implements Disposable {
//
//    private RainManager rainManager;
//    private ThunderManager thunderManager;
//    private LeafFallShader leafFallShader;
//    private float stateTime = 0f;
//    private float leafFallDuration;
//    private float rainDuration;
//    private float weatherPeriodicity;
//    private WeatherState state = WeatherState.NONE;
//
//    public Weather() {
//        rainManager = new RainManager();
//        thunderManager = new ThunderManager();
//        leafFallShader = new LeafFallShader();
//
//        SimplePublisher.getPublisher().addListener(rainManager);
//        SimplePublisher.getPublisher().addListener(thunderManager);
//
//        regeneratePeriodicity();
//    }
//
//    public void render(float delta){
//        stateTime += delta;
//
//        if(state == WeatherState.NONE){
//            if(stateTime > weatherPeriodicity){
//                if(Utils.randomInt(1, 3) == 1){
//                    stateTime = 0;
//                    generateRain();
//                } else {
//                    stateTime = 0;
//                    generateLeafFall();
//                }
//            }
//        }
//
//        if(state == WeatherState.RAIN){
//            if(stateTime < rainDuration) {
//                rainManager.render(delta);
//                thunderManager.render(delta);
//            } else {
//                regeneratePeriodicity();
//            }
//        }
//        if(state == WeatherState.LEAFFALL){
//            if(stateTime < leafFallDuration) {
//                leafFallShader.render(delta);
//            } else {
//                regeneratePeriodicity();
//            }
//        }
//    }
//
//    private void regeneratePeriodicity(){
//        state = WeatherState.NONE;
//        weatherPeriodicity = Utils.randomFloat(50f, 70f);
//    }
//
//    private void generateLeafFall() {
//        leafFallDuration = Utils.randomFloat(10f, 20f);
//        state = WeatherState.LEAFFALL;
//    }
//
//    private void generateRain(){
//        if(Utils.randomInt(1, 11) > 6){
//            float[] rainDuration = new float[3];
//
//            rainDuration[0] = Utils.randomFloat(5, 15);
//            rainDuration[1] = rainDuration[0] * 2;
//            rainDuration[2] = Utils.randomFloat(5, 15);
//
//            RainEvent rainEvent = new RainEvent(rainDuration, 1f);
//            SimplePublisher.getPublisher().publish(rainEvent);
//
//            this.rainDuration = rainDuration[0] + rainDuration[1] + rainDuration[2];
//            state = WeatherState.RAIN;
//
//            if(Utils.randomInt(1, 11) > 8){
//                ThunderEvent thunderEvent = new ThunderEvent(rainDuration[1], rainDuration[0]);
//                SimplePublisher.getPublisher().publish(thunderEvent);
//            }
//        }
//    }
//
//    @Override
//    public void dispose() {
//        rainManager.dispose();
//        thunderManager.dispose();
//    }
//}
