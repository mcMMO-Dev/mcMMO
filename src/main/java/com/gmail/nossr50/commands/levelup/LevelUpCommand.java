package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;

import java.util.Set;
import java.util.function.BiPredicate;

import static java.util.Objects.requireNonNull;

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

    class LevelUpCommandBuilder {
        private Set<PrimarySkillType> skillFilter = null;
        private Set<Integer> levels = null;
        private String commandStr = null;
        private BiPredicate<PrimarySkillType, Integer> predicate = null;
        private boolean logInfo;

        public LevelUpCommandBuilder() {
            this.logInfo = false;
        }

        public LevelUpCommandBuilder withPredicate(BiPredicate<PrimarySkillType, Integer> predicate) {
            this.predicate = predicate;
            return this;
        }

        public LevelUpCommandBuilder withLogInfo(boolean logInfo) {
            this.logInfo = logInfo;
            return this;
        }

        public LevelUpCommandBuilder commandString(String commandStr) {
            this.commandStr = commandStr;
            return this;
        }

        public LevelUpCommandBuilder withLevels(Set<Integer> levels) {
            this.levels = levels;
            return this;
        }

        public LevelUpCommandBuilder withSkillFilter(Set<PrimarySkillType> skillFilter) {
            this.skillFilter = skillFilter;
            return this;
        }

        public LevelUpCommand build() {
            requireNonNull(commandStr, "commandStr is null");
            if (predicate == null) {
                requireNonNull(levels, "levels is null");

                return new LevelUpCommandImpl((skill, level) -> {
                    if (skillFilter == null) {
                        return levels.contains(level);
                    } else {
                        return skillFilter.contains(skill) && levels.contains(level);
                    }
                }, commandStr, logInfo);
            }

            return new LevelUpCommandImpl(predicate, commandStr, logInfo);
        }
    }
}
