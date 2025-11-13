package me.i2000c.newalb.utils.random;

import java.util.Random;

public class RandomUtils{
    private static final Random RANDOM = new Random();
    
    public static Random getGenerator() {
        return RANDOM;
    }
    
    public static void setSeed(long seed) {
        RANDOM.setSeed(seed);
    }
    
    public static int getInt() {
        return RANDOM.nextInt();
    }
    
    public static int getInt(int bound) {
        return RANDOM.nextInt(bound);
    }
    
    public static int getInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }
    
    public static boolean getBoolean() {
        return RANDOM.nextBoolean();
    }
}
