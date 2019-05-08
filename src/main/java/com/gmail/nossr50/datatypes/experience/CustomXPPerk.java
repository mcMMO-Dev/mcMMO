package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import java.util.HashMap;

public class CustomXPPerk {

    private String perkName;
    private HashMap<PrimarySkillType, Float> customXPMultiplierMap;

    public CustomXPPerk(String perkName) {
        this.perkName = perkName;
        customXPMultiplierMap = new HashMap<>();
    }

    /**
     * Set the value of a specific skills XP Multiplier
     *
     * @param primarySkillType target skill
     * @param xpMult           xp multiplier
     */
    public void setCustomXPValue(PrimarySkillType primarySkillType, float xpMult) {
        customXPMultiplierMap.put(primarySkillType, xpMult);
    }

    /**
     * Get the value of a specific skills XP multiplier for this CustomXPPerk
     * 1.0D is used for non-existent values
     *
     * @param primarySkillType target skill
     * @return this custom perks XP multiplier for target skill, defaults to 1.0D if it doesn't exist
     */
    public float getXPMultiplierValue(PrimarySkillType primarySkillType) {
        if (customXPMultiplierMap.get(primarySkillType) == null)
            return 1.0F;

        return customXPMultiplierMap.get(primarySkillType);
    }

    /**
     * Get the name of this Custom XP Perk
     *
     * @return the name of this Custom XP Perk
     */
    public String getPerkName() {
        return perkName;
    }

    /**
     * Get the address of this custom XP perk permission
     * This is the fully qualified name for this permission
     *
     * @return the perk permission name
     */
    public String getPerkPermissionAddress() {
        return "mcmmo.customperks.xp." + perkName;
    }
}
