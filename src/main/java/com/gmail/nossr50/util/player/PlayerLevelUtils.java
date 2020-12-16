package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUtils {
    public int getEarlyGameCutoff(@NotNull PrimarySkillType primarySkillType)
    {
        return 1;
    }

    /**
     * Check if a player is currently qualifying for the early game boosted XP
     * Will return false only if a player is above the boost level cutoff, it does not check config settings to see if the early game boost is on
     * @param mmoPlayer target player
     * @param primarySkillType target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType) {
        return mmoPlayer.getSkillLevel(primarySkillType) < mcMMO.getPlayerLevelUtils().getEarlyGameCutoff(primarySkillType);
    }
}
