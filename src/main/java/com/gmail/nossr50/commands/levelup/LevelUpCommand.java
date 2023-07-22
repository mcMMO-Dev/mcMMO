package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import java.util.Set;

public interface LevelUpCommand {
    void apply(McMMOPlayer player, PrimarySkillType primarySkillType, Set<Integer> levelsGained);
}
