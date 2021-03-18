package com.gmail.nossr50.util.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUtils {
    /**
     * Check if a player is currently qualifying for the early game boosted XP
     * Will return false only if a player is above the boost level cutoff, it does not check config settings to see if the early game boost is on
     * @param mmoPlayer target player
     * @param primarySkillType target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType) {
        return mmoPlayer.getSkillLevel(primarySkillType) < 1;
    }
}
