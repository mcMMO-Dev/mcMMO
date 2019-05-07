package com.gmail.nossr50.util.player;

import com.gmail.nossr50.config.experience.ExperienceConfig;
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
            int levelCap = mcMMO.getConfigManager().getConfigLeveling().getLevelCap(primarySkillType);
            int cap;

            if(levelCap == Integer.MAX_VALUE || levelCap <= 0)
            {
                cap = mcMMO.isRetroModeEnabled() ? 50 : 5;
            } else {
                cap = (int) (levelCap * mcMMO.getConfigManager().getConfigLeveling().getEarlyGameBoostMultiplier());
            }

            earlyGameBoostCutoffs.put(primarySkillType, cap);
        }
    }

    public int getEarlyGameCutoff(PrimarySkillType primarySkillType)
    {
        return earlyGameBoostCutoffs.get(primarySkillType);
    }
}
