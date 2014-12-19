package com.gmail.nossr50.util.temp;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;

import com.gmail.nossr50.config.experience.ExperienceConfig;

public class CompatableGuardianXP {
    public static double get(Entity target) {
        if (((Guardian) target).isElder()) {
            return ExperienceConfig.getInstance().getElderGuardianXP();
        } else {
            return ExperienceConfig.getInstance().getCombatXP(EntityType.GUARDIAN);
        }
    }
}
