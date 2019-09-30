package com.gmail.nossr50.config.items;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class ConfigItemsChimaeraWing {

    private static final int USE_COST_DEFAULT = 1;
    private static final int RECIPE_COST_DEFAULT = 40;
    private static final String MINECRAFT_FEATHER = "minecraft:feather";
    private static final int WARMUP_DEFAULT = 5;
    private static final int COOLDOWN_DEFAULT = 240;
    private static final boolean ENABLED = true;
    private static final int HURT_COOLDOWN_DEFAULT = 60;
    private static final boolean PREVENT_UNDERGROUND_USE_DEFAULT = true;
    private static final boolean USE_BED_SPAWN_DEFAULT = true;
    private static final boolean SOUND_ENABLED_DEFAULT = true;

    @Setting(value = "Amount-Consumed-Per-Activation", comment = "How many Chimaera Wings are needed and consumed with each use.")
    private int useCost = USE_COST_DEFAULT;

    @Setting(value = "Recipe-Cost", comment = "How many of the item used to craft Chimaera wing are consumed to make 1 CW." +
            "\nDefault value: "+RECIPE_COST_DEFAULT)
    private int recipeCost = RECIPE_COST_DEFAULT;

    @Setting(value = "Recipe-Ingredient", comment = "The ingredient used to craft a Chimaera wing." +
            "\nDefault value: "+MINECRAFT_FEATHER)
    private String recipeMats = MINECRAFT_FEATHER;

    @Setting(value = "Warmup-Period", comment = "How many seconds a player must sit still and not take damage until the Chimaera Wing activates." +
            "\nDefault value: "+WARMUP_DEFAULT)
    private int warmup = WARMUP_DEFAULT;

    @Setting(value = "Cooldown", comment = "The amount of time players must wait before they are able to use this item again." +
            "\nDefault value: "+COOLDOWN_DEFAULT)
    private int cooldown = COOLDOWN_DEFAULT;

    @Setting(value = "Enable-Chimaera-Wing", comment = "Whether or not the CW will be enabled on the server." +
            "\nDefault value: "+ENABLED)
    private boolean enabled = ENABLED;

    @Setting(value = "Damage-Cooldown", comment = "When players take damage, they must wait this many seconds before the Chimaera Wing is usable." +
            "\nDefault value: "+HURT_COOLDOWN_DEFAULT)
    private int recentlyHurtCooldown = HURT_COOLDOWN_DEFAULT;

    @Setting(value = "Prevent-Underground-Use", comment = "Prevents players from using the CW if solid blocks exist above their character model." +
            "\nThis item is actually a reference to Dragon Quest, can you tell?" +
            "\nDefault value: "+PREVENT_UNDERGROUND_USE_DEFAULT)
    private boolean preventUndergroundUse = PREVENT_UNDERGROUND_USE_DEFAULT;

    @Setting(value = "Use-Bed-Spawn", comment = "Chimaera Wing will attempt to take players to their bed if it exists." +
            "\nIf this setting is turned off, players will be taken to the spawn for the current world they reside in." +
            "\nDefault value: "+USE_BED_SPAWN_DEFAULT)
    private boolean useBedSpawn = USE_BED_SPAWN_DEFAULT;

    @Setting(value = "Play-Sound-On-Use", comment = "Plays a sound effect with the item is used." +
            "\nDefault value: "+SOUND_ENABLED_DEFAULT)
    private boolean soundEnabled = SOUND_ENABLED_DEFAULT;

    public int getUseCost() {
        return useCost;
    }

    public int getRecipeCost() {
        return recipeCost;
    }

    public String getRecipeMats() {
        return recipeMats;
    }

    public int getWarmup() {
        return warmup;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getRecentlyHurtCooldown() {
        return recentlyHurtCooldown;
    }

    public boolean isPreventUndergroundUse() {
        return preventUndergroundUse;
    }

    public boolean isUseBedSpawn() {
        return useBedSpawn;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
