package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;

public class PlayerLevelUtils {
//    HashMap<PrimarySkillType, Integer> earlyGameBoostCutoffs;
//
//    public PlayerLevelUtils()
//    {
//        earlyGameBoostCutoffs = new HashMap<>();
//        calculateEarlyGameBoostCutoffs();
//    }
//    private void calculateEarlyGameBoostCutoffs()
//    {
//        for(PrimarySkillType primarySkillType : PrimarySkillType.values())
//        {
//            int levelCap = Config.getInstance().getLevelCap(primarySkillType);
//            int cap;
//
//            if (levelCap == Integer.MAX_VALUE || levelCap <= 0)
//            {
//                cap = Config.getInstance().getIsRetroMode() ? 50 : 5;
//            } else {
//                cap = (int) (levelCap * ExperienceConfig.getInstance().getEarlyGameBoostMultiplier());
//            }
//
//            earlyGameBoostCutoffs.put(primarySkillType, cap);
//        }
//    }

    public int getEarlyGameCutoff(PrimarySkillType primarySkillType) {
        return 1;
    }

    /**
     * Check if a player is currently qualifying for the early game boosted XP Will return false
     * only if a player is above the boost level cutoff, it does not check config settings to see if
     * the early game boost is on
     *
     * @param mmoPlayer target player
     * @param primarySkillType target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(McMMOPlayer mmoPlayer,
            PrimarySkillType primarySkillType) {
        return mmoPlayer.getSkillLevel(primarySkillType) < mcMMO.getPlayerLevelUtils()
                .getEarlyGameCutoff(primarySkillType);
    }
}
