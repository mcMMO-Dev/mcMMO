package com.gmail.nossr50.config.hocon.experience;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceCombat {

    private static final HashMap<String, Double> COMBAT_EXPERIENCE_DEFAULT;
    private static final HashMap<String, Double> SPECIAL_COMBAT_EXPERIENCE_DEFAULT;
    private static final boolean PVP_XP_ENABLED_DEFAULT = false;

    static {
        COMBAT_EXPERIENCE_DEFAULT = new HashMap<>();


        COMBAT_EXPERIENCE_DEFAULT.put("creeper", 4.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("cat", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("fox", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("panda", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("pillager", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("ravager", 4.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("trader_llama", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("skeleton", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("spider", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("giant", 4.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("slime", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("ghast", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("pig_zombie", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("enderman", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("cave_spider", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("silverfish", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("blaze", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("magma_cube", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("ender_dragon", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("wither", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("witch", 0.1D);
        COMBAT_EXPERIENCE_DEFAULT.put("iron_golem", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("wither_skeleton", 4.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("endermite", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("guardian", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("elder_guardian", 4.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("shulker", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("donkey", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("mule", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("horse", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie_villager", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("skeleton_horse", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie_horse", 1.2D);
        COMBAT_EXPERIENCE_DEFAULT.put("husk", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("evoker", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("polar_bear", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("llama", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("vindicator", 3.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("stray", 2.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("rabbit", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("chicken", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("bat", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("mushroom_cow", 1.2D);
        COMBAT_EXPERIENCE_DEFAULT.put("cow", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("turtle", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("sheep", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("pig", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("squid", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("ocelot", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("villager", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("snowman", 0.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("parrot", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("illusioner", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("drowned", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("dolphin", 1.0D);
        COMBAT_EXPERIENCE_DEFAULT.put("phantom", 4.0D);

        //SPECIAL
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT = new HashMap<>();
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put("animals", 1.0D); //TODO: this seems like a dumb config option
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put("spawned", 0.0D);
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put("pvp", 1.0D);
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put("player-bred-mobs", 1.0D);
    }

    @Setting(value = "Combat-XP-Multipliers")
    private HashMap<String, Double> combatExperienceMap = COMBAT_EXPERIENCE_DEFAULT;

    @Setting(value = "Special-Combat-XP-Multipliers")
    private HashMap<String, Double> specialCombatExperienceMap = COMBAT_EXPERIENCE_DEFAULT;

    @Setting(value = "PVP-XP", comment = "If true, players will gain XP from PVP interactions." +
            "\nBe careful turning this on as this can potentially allow for unwanted behaviour from players." +
            "\nDefault value: " + PVP_XP_ENABLED_DEFAULT)
    private boolean pvpXPEnabled = PVP_XP_ENABLED_DEFAULT;

    public boolean isPvpXPEnabled() {
        return pvpXPEnabled;
    }

    public HashMap<String, Double> getCombatExperienceMap() {
        return combatExperienceMap;
    }

    public double getSpawnedMobXPMult() {
        return specialCombatExperienceMap.get("mobspawners");
    }

    public double getPVPXPMult() {
        return specialCombatExperienceMap.get("pvp");
    }

    public double getAnimalsXPMult() {
        return specialCombatExperienceMap.get("animals");
    }

    public double getPlayerBredMobsXPMult() {
        return specialCombatExperienceMap.get("player-bred-mobs");
    }
}