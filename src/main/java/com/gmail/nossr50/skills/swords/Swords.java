package com.gmail.nossr50.skills.swords;

import java.util.Random;

public class Swords {
    public static final int BLEED_MAX_BONUS_LEVEL = 750;
    public static final int MAX_BLEED_TICKS = 3;
    public static final int BASE_BLEED_TICKS = 2;

    public static final int COUNTER_ATTACK_MAX_BONUS_LEVEL = 600;
    public static final int COUNTER_ATTACK_MODIFIER = 2;

    public static final int SERRATED_STRIKES_MODIFIER = 4;
    public static final int SERRATED_STRIKES_BLEED_TICKS = 5;

    private static Random random = new Random();

    protected static Random getRandom() {
        return random;
    }
}
