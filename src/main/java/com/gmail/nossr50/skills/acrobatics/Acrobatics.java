package com.gmail.nossr50.skills.acrobatics;

import java.util.Random;

public class Acrobatics {
    public static final int DODGE_MAX_BONUS_LEVEL = 800;
    public static final int DODGE_XP_MODIFIER = 120;

    public static final int FALL_XP_MODIFIER = 120;
    public static final int ROLL_MAX_BONUS_LEVEL = 1000;
    public static final int ROLL_XP_MODIFIER = 80;

    private static Random random = new Random();

    public static Random getRandom() {
        return random;
    }
}
