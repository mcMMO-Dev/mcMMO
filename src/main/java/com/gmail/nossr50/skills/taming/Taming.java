package com.gmail.nossr50.skills.taming;

import java.util.Random;

public class Taming {
    public static final int ENVIRONMENTALLY_AWARE_ACTIVATION_LEVEL = 100;

    public static final int FAST_FOOD_SERVICE_ACTIVATION_CHANCE = 50;
    public static final int FAST_FOOD_SERVICE_ACTIVATION_LEVEL = 50;

    public static final int GORE_BLEED_TICKS = 2;
    public static final int GORE_MAX_BONUS_LEVEL = 1000;
    public static final int GORE_MULTIPLIER = 2;

    public static final int SHARPENED_CLAWS_ACTIVATION_LEVEL = 750;
    public static final int SHARPENED_CLAWS_BONUS = 2;

    public static final int SHOCK_PROOF_ACTIVATION_LEVEL = 500;
    public static final int SHOCK_PROOF_MODIFIER = 6;

    public static final int THICK_FUR_ACTIVATION_LEVEL = 250;
    public static final int THICK_FUR_MODIFIER = 2;

    private static Random random = new Random();

    public static Random getRandom() {
        return random;
    }
}
