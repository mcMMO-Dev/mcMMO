package com.gmail.nossr50.config.skills.swords;

import com.gmail.nossr50.config.ConfigConstants;
import com.gmail.nossr50.datatypes.skills.properties.AbstractMaxBonusLevel;
import com.gmail.nossr50.datatypes.skills.properties.MaxBonusLevel;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSwordsRupture {

    private static final double MAX_CHANCE_DEFAULT = 33.0;
    private static final double DAMAGE_PVP_DEFAULT = 2.0;
    private static final double DAMAGE_PVE_DEFAULT = 3.0;
    private static final int BASE_TICKS_DEFAULT = 2;

    @Setting(value = ConfigConstants.MAX_CHANCE_FIELD_NAME, comment = ConfigConstants.MAX_CHANCE_FIELD_DESCRIPTION)
    private double maxChance = MAX_CHANCE_DEFAULT;

    @Setting(value = ConfigConstants.MAX_BONUS_LEVEL_FIELD_NAME)
    private MaxBonusLevel maxBonusLevel = new AbstractMaxBonusLevel(100);

    @Setting(value = "Damage-Per-Tick-PVP")
    private double damagePlayer = DAMAGE_PVP_DEFAULT;

    @Setting(value = "Damage-Per-Tick-PVE")
    private double damageMobs = DAMAGE_PVE_DEFAULT;

    @Setting(value = "Bleed-Ticks", comment = "When Rupture has a tick it applies its damage, the effect wears out after enough ticks have happened." +
            "\nThis is the base amount of ticks that will happen with the lowest skill level, increase this number to have rupture apply for longer across the board." +
            "\nKeep in mind Rupture also increases in tick length as a player levels the skill.")
    private int baseTicks = BASE_TICKS_DEFAULT;

    public double getRuptureMaxChance() {
        return maxChance;
    }

    public MaxBonusLevel getRuptureMaxBonusLevel() {
        return maxBonusLevel;
    }

    public double getRuptureDamagePlayer() {
        return damagePlayer;
    }

    public double getRuptureDamageMobs() {
        return damageMobs;
    }

    public int getRuptureBaseTicks() {
        return baseTicks;
    }
}
