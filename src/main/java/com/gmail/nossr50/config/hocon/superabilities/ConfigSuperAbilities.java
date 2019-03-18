package com.gmail.nossr50.config.hocon.superabilities;

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
            comment = "The maximum amount of time a player can use a super ability." +
            "\nMost super abilities get longer as a player grows in skill.")
    private ConfigSectionSuperAbilityMaxLength superAbilityMaxLength = new ConfigSectionSuperAbilityMaxLength();

    @Setting(value = "Super-Ability-Settings", comment = "Change specific parameters for super abilities.")
    private ConfigSectionSuperAbilityLimits superAbilityLimits = new ConfigSectionSuperAbilityLimits();
}
