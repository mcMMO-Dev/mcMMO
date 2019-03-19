package com.gmail.nossr50.config.hocon.superabilities;

import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigSuperAbilities {

    public static final boolean SUPER_ABILITY_DEFAULT = true;
    public static final boolean MUST_SNEAK_TO_ACTIVATE_DEFAULT = false;

    @Setting(value = "Enable-Super-Abilities",
            comment = "Turn this off to disable all super abilities." +
                    "\nDefault value: "+SUPER_ABILITY_DEFAULT)
    private boolean superAbilitiesEnabled = SUPER_ABILITY_DEFAULT;

    @Setting(value = "Require-Sneaking",
            comment = "Players must be sneaking in order to activate super abilities." +
                    "\nDefault value: "+MUST_SNEAK_TO_ACTIVATE_DEFAULT)
    private boolean mustSneakToActivate = MUST_SNEAK_TO_ACTIVATE_DEFAULT;

    @Setting(value = "Super-Ability-Cooldowns",
            comment = "How many seconds players must wait before they can use a super ability again.")
    private ConfigSectionSuperAbilityCooldowns superAbilityCooldowns = new ConfigSectionSuperAbilityCooldowns();

    @Setting(value = "Super-Ability-Max-Length",
            comment = "The maximum amount of time in seconds that a super ability can last." +
            "\nMost super abilities get longer as a player grows in skill.")
    private ConfigSectionSuperAbilityMaxLength superAbilityMaxLength = new ConfigSectionSuperAbilityMaxLength();

    @Setting(value = "Super-Ability-Settings", comment = "Change specific parameters for super abilities.")
    private ConfigSectionSuperAbilityLimits superAbilityLimits = new ConfigSectionSuperAbilityLimits();

    public boolean isSuperAbilitiesEnabled() {
        return superAbilitiesEnabled;
    }

    public boolean isMustSneakToActivate() {
        return mustSneakToActivate;
    }

    public ConfigSectionSuperAbilityCooldowns getSuperAbilityCooldowns() {
        return superAbilityCooldowns;
    }

    public ConfigSectionSuperAbilityMaxLength getSuperAbilityMaxLength() {
        return superAbilityMaxLength;
    }

    public ConfigSectionSuperAbilityLimits getSuperAbilityLimits() {
        return superAbilityLimits;
    }

    public int getCooldownForSuper(SuperAbilityType superAbilityType)
    {
        switch(superAbilityType)
        {
            case BERSERK:
                return superAbilityCooldowns.getBerserk();
            case GREEN_TERRA:
                return superAbilityCooldowns.getGreenTerra();
            case TREE_FELLER:
                return superAbilityCooldowns.getTreeFeller();
            case BLAST_MINING:
                return superAbilityCooldowns.getBlastMining();
            case SUPER_BREAKER:
                return superAbilityCooldowns.getSuperBreaker();
            case SKULL_SPLITTER:
                return superAbilityCooldowns.getSkullSplitter();
            case SERRATED_STRIKES:
                return superAbilityCooldowns.getSerratedStrikes();
            case GIGA_DRILL_BREAKER:
                return superAbilityCooldowns.getGigaDrillBreaker();
            default:
                mcMMO.p.getLogger().severe("Cooldown Parameter not found for "+superAbilityType.toString());
                return 240;
        }
    }

    public int getMaxLengthForSuper(SuperAbilityType superAbilityType)
    {
        switch(superAbilityType)
        {
            case BERSERK:
                return superAbilityMaxLength.getBerserk();
            case GREEN_TERRA:
                return superAbilityMaxLength.getGreenTerra();
            case TREE_FELLER:
                return superAbilityMaxLength.getTreeFeller();
            case SUPER_BREAKER:
                return superAbilityMaxLength.getSuperBreaker();
            case SKULL_SPLITTER:
                return superAbilityMaxLength.getSkullSplitter();
            case SERRATED_STRIKES:
                return superAbilityMaxLength.getSerratedStrikes();
            case GIGA_DRILL_BREAKER:
                return superAbilityMaxLength.getGigaDrillBreaker();
            default:
                mcMMO.p.getLogger().severe("Max Length Parameter not found for "+superAbilityType.toString());
                return 60;
        }
    }
}
