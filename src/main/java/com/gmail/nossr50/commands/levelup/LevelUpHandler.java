package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.Set;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A callback other plugins register through
 * {@link com.gmail.nossr50.api.LevelUpCommandAPI#registerHandler(LevelUpHandler)} to run their
 * own code when a player levels up. Handlers are invoked for every level up of every skill —
 * filter inside the handler for the skills and levels you care about.
 */
@FunctionalInterface
public interface LevelUpHandler {

    /**
     * Called when a player levels up a skill. Invoked on the thread the level up event fired
     * on (a region thread on Folia); schedule your own tasks if you need a different context.
     *
     * @param player the player who leveled up
     * @param primarySkillType the skill that leveled up
     * @param levelsGained every skill level reached during this level up
     * @param powerLevel the player's power level after this level up
     */
    void onLevelUp(@NotNull Player player, @NotNull PrimarySkillType primarySkillType,
            @NotNull Set<Integer> levelsGained, int powerLevel);
}
