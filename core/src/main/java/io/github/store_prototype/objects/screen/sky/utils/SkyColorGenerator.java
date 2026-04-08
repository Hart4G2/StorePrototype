//package io.github.store_prototype.objects.screen.sky.utils;
//
//import io.github.store_prototype.objects.screen.sky.shader_objects.TimesOfDay;
//import io.github.store_prototype.utils.Utils;
//
//public class SkyColorGenerator {
//
//    public static float[] generateSkyColors(TimesOfDay state){
//        return generateColors(state);
//    }
//
//    private static float[] generateColors(TimesOfDay state){
//        float baseR = 0f;
//        float baseG = 0f;
//        float baseB = 0f;
//
//        switch (state){
//            case DAY:{
//                if(Utils.randomInt(1, 3) == 1){
//                    baseR = Utils.randomFloat(0, .1f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(.8f, 1f);
//                } else {
//                    baseR = Utils.randomFloat(.1f, .2f);
//                    baseG = Utils.randomFloat(0f, .1f);
//                    baseB = Utils.randomFloat(.9f, 1f);
//                }
//                break;
//            }
//            case MORNING:{
//                if(Utils.randomInt(1, 3) == 1){
//                    baseR = Utils.randomFloat(0, .3f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(.1f, .4f);
//                } else {
//                    baseR = Utils.randomFloat(.2f, .5f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(.3f, .6f);
//                }
//                break;
//            }
//            case NIGHT:{
//                if(Utils.randomInt(1, 3) == 1){
//                    baseR = Utils.randomFloat(0, .2f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(0f, .2f);
//                } else {
//                    baseR = Utils.randomFloat(.1f, .25f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(.2f, .4f);
//                }
//                break;
//            }
//            case TWILIGHT:{
//                if(Utils.randomInt(1, 3) == 1){
//                    baseR = Utils.randomFloat(0, .3f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(0f, .2f);
//                } else {
//                    baseR = Utils.randomFloat(.2f, .5f);
//                    baseG = Utils.randomFloat(0, .1f);
//                    baseB = Utils.randomFloat(0f, .2f);
//                }
//                break;
//            }
//        }
//
//        boolean darkening = state == TimesOfDay.NIGHT || state == TimesOfDay.TWILIGHT;
//        return generateSkyColors(baseR, baseG, baseB, darkening);
//    }
//
//    private static float[] generateSkyColors(float baseR, float baseG, float baseB, boolean darkening) {
//        final int stops = 3;
//        float[] skyColors = new float[stops * 3];
//
//        skyColors[0] = baseR;
//        skyColors[1] = baseG;
//        skyColors[2] = baseB;
//
//        for(int i = 3; i < stops * 3; i += 3){
//            skyColors[i] = generateRColor(skyColors[i-3], darkening);
//            skyColors[i + 1] = generateGColor(skyColors[i-2], darkening);
//            skyColors[i + 2] = generateBColor(skyColors[i-1], darkening);
//        }
//
//        return skyColors;
//    }
//
//    private static float generateRColor(float baseR, boolean darkening) {
//        if(darkening){
//            return baseR == 0 ? 0 : Utils.randomFloat(0, baseR);
//        } else {
//            return baseR == 1 ? 1 : Utils.randomFloat(baseR, 1f);
//        }
//    }
//
//    private static float generateGColor(float baseG, boolean darkening) {
//        if(darkening){
//            return baseG == 0 ? 0 : Utils.randomFloat(0, .1f);
//        } else {
//            return baseG > .1f ? baseG - .1f : Utils.randomFloat(baseG, .1f);
//        }
//    }
//
//    private static float generateBColor(float baseB, boolean darkening) {
//        if(darkening){
//            return baseB == 0 ? Utils.randomFloat(baseB, .1f) : Utils.randomFloat(0, baseB);
//        } else {
//            return baseB == 1 ? 1 : Utils.randomFloat(baseB, 1f);
//        }
//    }
//}
