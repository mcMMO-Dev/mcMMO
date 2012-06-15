package com.gmail.nossr50.skills.unarmed;

import java.util.Random;

public class Unarmed {
    public static final int BONUS_DAMAGE_MAX_BONUS_MODIFIER = 8;
    public static final int BONUS_DAMAGE_INCREASE_LEVEL = 50;
    public static final int DEFLECT_MAX_BONUS_LEVEL = 1000;
    public static final int DISARM_MAX_BONUS_LEVEL = 1000;
    public static final int IRON_GRIP_MAX_BONUS_LEVEL = 1000;

    private static Random random = new Random();

    protected static Random getRandom() {
        return random;
    }
}
