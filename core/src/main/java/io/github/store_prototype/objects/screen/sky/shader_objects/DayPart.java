//package io.github.store_prototype.objects.screen.sky.shader_objects;
//
//
//import io.github.store_prototype.objects.screen.sky.utils.SkyColorGenerator;
//import io.github.store_prototype.utils.Utils;
//
//public class DayPart {
//
//    private TimesOfDay dayState;
//    private float[] durations;
//    private float[] colors;
//    private boolean isCloudy;
//    private float alphaStars;
//    private float alphaStarsEndValue;
//    private float stateTime;
//
//    public DayPart(TimesOfDay dayState, float duration) {
//        this.dayState = dayState;
//
//        generateAlpha();
//        generateDuration(duration);
//        generateColors();
//    }
//
//    public void regenerate(){
//        stateTime = 0f;
//        generateColors();
//        generateAlpha();
//    }
//
//    private void generateColors() {
//        colors = SkyColorGenerator.generateSkyColors(dayState);
//    }
//
//    private void generateDuration(float duration) {
//        durations = new float[3];
//        durations[0] = duration / 4;
//        durations[1] = duration / 2;
//        durations[2] = duration / 4;
//    }
//
//    private void generateAlpha() {
//        alphaStars = 0;
//        alphaStarsEndValue = 0;
//
//        int random = Utils.randomInt(0, 2);
//
//        if(dayState != TimesOfDay.DAY){
//            alphaStarsEndValue = dayState == TimesOfDay.NIGHT ? 1f : random;
//        }
//    }
//
//    public void render(float delta){
//        stateTime += delta;
//
//        if(stateTime < durations[0]){
//            if(alphaStars < alphaStarsEndValue){
//                alphaStars += alphaStarsEndValue / durations[0];
//            }
//        } else if(stateTime < durations[0] + durations[1]){
//            alphaStars = alphaStarsEndValue;
//        } else if(stateTime < durations[0] + durations[1] + durations[2]){
//            if(alphaStars != 0){
//                alphaStars -= alphaStarsEndValue / durations[0];
//            }
//        }
//    }
//
//    public boolean isEnded() {
//        return stateTime > durations[0] + durations[1] + durations[2];
//    }
//
//    public float getAlpha() {
//        return alphaStars;
//    }
//
//    public float[] getDurations() {
//        return durations;
//    }
//
//    public float[] getColors() {
//        return colors;
//    }
//
//    public TimesOfDay getDayState() {
//        return dayState;
//    }
//}
