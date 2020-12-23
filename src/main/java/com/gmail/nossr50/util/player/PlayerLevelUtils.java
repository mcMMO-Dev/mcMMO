package com.gmail.nossr50.util.player;

import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.skill.RootSkill;
import org.jetbrains.annotations.NotNull;

public class PlayerLevelUtils {
    /**
     * Check if a player is currently qualifying for the early game boosted XP
     * Will return false only if a player is above the boost level cutoff, it does not check config settings to see if the early game boost is on
     * @param mmoPlayer target player
     * @param rootSkill target skill
     * @return if the player would qualify for the XP boost if its enabled
     */
    public static boolean qualifiesForEarlyGameBoost(@NotNull OnlineMMOPlayer mmoPlayer, @NotNull RootSkill rootSkill) {
        return mmoPlayer.getSkillLevel(rootSkill) < 1;
    }
}
