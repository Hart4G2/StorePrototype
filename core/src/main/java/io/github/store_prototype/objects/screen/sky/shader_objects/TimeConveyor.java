//package io.github.store_prototype.objects.screen.sky.shader_objects;
//
//public class TimeConveyor {
//
//    private DayPart[] dayParts;
//    private DayPart currentDayPart;
//    private SkyShader shader;
//
//    public TimeConveyor(SkyShader shader, float duration) {
//        this.shader = shader;
//
//        generateDayParts(duration);
//        currentDayPart = dayParts[0];
//    }
//
//    private void generateDayParts(float duration){
//        dayParts = new DayPart[4];
//
//        dayParts[0] = new DayPart(TimesOfDay.MORNING, duration / 6);
//        dayParts[1] = new DayPart(TimesOfDay.DAY, duration / 3);
//        dayParts[2] = new DayPart(TimesOfDay.TWILIGHT, duration / 6);
//        dayParts[3] = new DayPart(TimesOfDay.NIGHT, duration / 3);
//    }
//
//    public void render(float delta){
//        if(currentDayPart.isEnded()){
//            currentDayPart = getNextDayPart();
//            configureShader();
//        }
//
//        currentDayPart.render(delta);
//        shader.setAlphaStars(currentDayPart.getAlpha());
//        shader.render(delta);
//    }
//
//    private DayPart getNextDayPart() {
//        for(int i = 0; i < dayParts.length; i++){
//            if(dayParts[i] == currentDayPart){
//                return i + 1 == dayParts.length ? dayParts[0] : dayParts[i + 1];
//            }
//        }
//        return dayParts[0];
//    }
//
//    private void configureShader(){
//        currentDayPart.regenerate();
//        shader.setSkyColors(currentDayPart.getColors());
//        shader.setHoldTimes(currentDayPart.getDurations());
//    }
//
//    public TimesOfDay getCurrentDayState() {
//        return currentDayPart.getDayState();
//    }
//
//    public float[] getCurrentDayPartDuration() {
//        return currentDayPart.getDurations();
//    }
//
//    public boolean isCurrentDayPartEnded() {
//        return currentDayPart.isEnded();
//    }
//}
//
//
