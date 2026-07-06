package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Something that reacts to a player leveling up. Implementations are registered with the
 * {@link LevelUpCommandManager} and invoked once per level up event.
 */
@FunctionalInterface
public interface LevelUpAction {

    /**
     * Reacts to a level up. Invoked on the thread the level up event fired on.
     *
     * @param mmoPlayer the player who leveled up
     * @param primarySkillType the skill that leveled up
     * @param levelsGained every skill level reached during this level up
     * @param powerLevelsGained every power level reached during this level up
     */
    void onLevelUp(@NotNull McMMOPlayer mmoPlayer, @NotNull PrimarySkillType primarySkillType,
            @NotNull Set<Integer> levelsGained, @NotNull Set<Integer> powerLevelsGained);
}
