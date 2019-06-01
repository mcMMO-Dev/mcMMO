package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

import java.util.HashMap;

public class PlayerLevelUtils {
    HashMap<PrimarySkillType, Integer> earlyGameBoostCutoffs;

    public PlayerLevelUtils()
    {
        earlyGameBoostCutoffs = new HashMap<>();
        calculateEarlyGameBoostCutoffs();
    }

    private void calculateEarlyGameBoostCutoffs()
    {
        for(PrimarySkillType primarySkillType : PrimarySkillType.values())
        {
            int levelCap = Config.getInstance().getLevelCap(primarySkillType);
            int cap;

            if(levelCap == Integer.MAX_VALUE || levelCap <= 0)
            {
                cap = Config.getInstance().getIsRetroMode() ? 50 : 5;
            } else {
                cap = (int) (levelCap * ExperienceConfig.getInstance().getEarlyGameBoostMultiplier());
            }

            earlyGameBoostCutoffs.put(primarySkillType, cap);
        }
    }

    public int getEarlyGameCutoff(PrimarySkillType primarySkillType)
    {
        return earlyGameBoostCutoffs.get(primarySkillType);
    }

    /**
     * Check if a player is currently qualifying for the early game boosted XP
     * Will return false only if a player is above the boost level cutoff, it does not check config settings to see if the early game boost is on
     * @param mcMMOPlayer target player
     * @param primarySkillType target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(McMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType) {
        return mcMMOPlayer.getSkillLevel(primarySkillType) < mcMMO.getPlayerLevelUtils().getEarlyGameCutoff(primarySkillType);
    }
}
