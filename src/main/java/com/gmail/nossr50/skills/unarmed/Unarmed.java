package com.gmail.nossr50.skills.unarmed;

import java.util.Random;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;

public class Unarmed {
    public static final int DEFLECT_MAX_BONUS_LEVEL = 1000;
    public static final int DISARM_MAX_BONUS_LEVEL = 1000;
    public static final int IRON_GRIP_MAX_BONUS_LEVEL = 1000;

    private static Random random = new Random();

    /**
     * Apply bonus to Unarmed damage.
     *
     * @param PPa Profile of the attacking player
     * @param event The event to modify
     */
    public static void unarmedBonus(PlayerProfile PPa, EntityDamageByEntityEvent event) {
        final int MAX_BONUS = 8;
        int bonus = 3;

        bonus += PPa.getSkillLevel(SkillType.UNARMED) / 50; //Add 1 DMG for every 50 skill levels

        if (bonus > MAX_BONUS) {
            bonus = MAX_BONUS;
        }

        event.setDamage(event.getDamage() + bonus);
    }

    protected static Random getRandom() {
        return random;
    }
}
