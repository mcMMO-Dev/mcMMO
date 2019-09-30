package com.gmail.nossr50.datatypes.skills.properties;

/**
 * Represents a skill property relating to damage
 */
public interface DamageProperty {
    /**
     * Get the damage modifier property of this skill for PVE interactions
     * @return the PVE modifier for this skill
     */
    double getPVEModifier();

    /**
     * Get the damage modifier property of this skill for PVP interactions
     * @return the PVP modifier for this skill
     */
    double getPVPModifier();
}
