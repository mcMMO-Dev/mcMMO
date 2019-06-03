package com.gmail.nossr50.datatypes.skills.properties;

/**
 * Represents the level at which skill properties for this skill that scale based on level will reach their maximum benefits
 * If a player is this level or higher, they will have the full-power version of this skill
 */
public interface SkillCeiling extends SkillProperty {
    /**
     * The maximum level for this skill in Retro
     * Defaults to 1000
     * @@return maximum level for this skill in Retro scaling (1-1000)
     */
    default int getRetroMaxLevel() {
        return 1000;
    }

    /**
     * The maximum level for this skill in Standard
     * Defaults to 100
     * @return maximum level for this skill in Standard scaling (1-100)
     */
    default int getStandardMaxLevel() {
        return 100;
    }
}
