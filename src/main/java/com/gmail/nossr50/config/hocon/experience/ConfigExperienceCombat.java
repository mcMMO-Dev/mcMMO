package com.gmail.nossr50.config.hocon.experience;

import com.gmail.nossr50.datatypes.experience.SpecialXPKey;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public class ConfigExperienceCombat {

    private static final HashMap<String, Float> COMBAT_EXPERIENCE_DEFAULT;
    private static final HashMap<SpecialXPKey, Float> SPECIAL_COMBAT_EXPERIENCE_DEFAULT;
    private static final boolean PVP_XP_ENABLED_DEFAULT = false;

    static {
        COMBAT_EXPERIENCE_DEFAULT = new HashMap<>();

        COMBAT_EXPERIENCE_DEFAULT.put("creeper", 4.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("cat", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("fox", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("panda", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("pillager", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("ravager", 4.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("trader_llama", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("skeleton", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("spider", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("giant", 4.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("slime", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("ghast", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("pig_zombie", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("enderman", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("cave_spider", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("silverfish", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("blaze", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("magma_cube", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("ender_dragon", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("wither", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("witch", 0.1F);
        COMBAT_EXPERIENCE_DEFAULT.put("iron_golem", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("wither_skeleton", 4.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("endermite", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("guardian", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("elder_guardian", 4.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("shulker", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("donkey", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("mule", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("horse", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie_villager", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("skeleton_horse", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("zombie_horse", 1.2F);
        COMBAT_EXPERIENCE_DEFAULT.put("husk", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("evoker", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("polar_bear", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("llama", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("vindicator", 3.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("stray", 2.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("rabbit", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("chicken", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("bat", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("mushroom_cow", 1.2F);
        COMBAT_EXPERIENCE_DEFAULT.put("cow", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("turtle", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("sheep", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("pig", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("squid", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("ocelot", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("villager", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("snowman", 0.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("parrot", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("illusioner", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("drowned", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("dolphin", 1.0F);
        COMBAT_EXPERIENCE_DEFAULT.put("phantom", 4.0F);

        //SPECIAL
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT = new HashMap<>();
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put(SpecialXPKey.ANIMALS, 1.0F); //TODO: this seems like a dumb config option
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put(SpecialXPKey.SPAWNED, 0.0F);
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put(SpecialXPKey.PVP, 1.0F);
        SPECIAL_COMBAT_EXPERIENCE_DEFAULT.put(SpecialXPKey.PETS, 1.0F);
    }

    @Setting(value = "Combat-XP-Multipliers")
    private HashMap<String, Float> combatExperienceMap = COMBAT_EXPERIENCE_DEFAULT;

    @Setting(value = "Special-Combat-XP-Multipliers", comment = "Special XP settings which apply to a mobs matching certain criteria" +
            "\nAnimals - Non-hostile mobs, anything not considered a Monster" +
            "\nSpawned - Unnatural mobs, can be from mob spawners, eggs, or otherwise" +
            "\nPVP - XP gains relating to hitting other players" +
            "\nPets - Either tamed or from breeding" +
            "\nThese all default to 1.0 except for spawned, which defaults to 0.0" +
            "\nIf you want spawned mobs to give XP simply turn the value for spawned above 0.0")
    private HashMap<SpecialXPKey, Float> specialCombatExperienceMap = SPECIAL_COMBAT_EXPERIENCE_DEFAULT;

    @Setting(value = "PVP-XP", comment = "If true, players will gain XP from PVP interactions." +
            "\nBe careful turning this on as this can potentially allow for unwanted behaviour from players." +
            "\nDefault value: " + PVP_XP_ENABLED_DEFAULT)
    private boolean pvpXPEnabled = PVP_XP_ENABLED_DEFAULT;

    public boolean isPvpXPEnabled() {
        return pvpXPEnabled;
    }

    public HashMap<String, Float> getCombatExperienceMap() {
        return combatExperienceMap;
    }

    public HashMap<SpecialXPKey, Float> getSpecialCombatExperienceMap() {
        return specialCombatExperienceMap;
    }
}