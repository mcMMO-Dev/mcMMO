package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import java.util.Set;

/**
 * Represents a command to be executed on a level up
 */
public interface LevelUpCommand {
    /**
     * Process the command
     *
     * @param player            the player
     * @param primarySkillType  the skill type
     * @param levelsGained      the levels gained
     */
    void process(McMMOPlayer player, PrimarySkillType primarySkillType, Set<Integer> levelsGained);

    /**
     * Execute the command
     */
    void executeCommand();
}
