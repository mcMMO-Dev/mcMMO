package com.gmail.nossr50.config.hocon.skills.archery;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigArcherySkillShot {
    private static final double SKILL_SHOT_DMG_MULTIPLIER_DEFAULT = 10.0D;
    private static final double DAMAGE_CEILING_DEFAULT = 9.0;

    @Setting(value = "Rank-Percentage-Damage-Boost", comment = "How much damage Skill Shot will add per rank, this stacks additively." +
            "\nDefault value: "+SKILL_SHOT_DMG_MULTIPLIER_DEFAULT)
    private double skillShotDamageMultiplier = SKILL_SHOT_DMG_MULTIPLIER_DEFAULT;

    @Setting(value = "Bonus-Damage-Limit", comment = "This is the maximum amount of raw bonus damage that can be added to your arrows as a result of Skill Shot." +
            "\nDefault value: "+DAMAGE_CEILING_DEFAULT)
    private double skillShotDamageCeiling = DAMAGE_CEILING_DEFAULT;

    public double getSkillShotDamageMultiplier() {
        return skillShotDamageMultiplier;
    }

    public double getSkillShotDamageCeiling() {
        return skillShotDamageCeiling;
    }
}
