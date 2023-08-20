package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
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
        private LinkedList<String> commands = null;
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

        public LevelUpCommandBuilder command(@NotNull String command) {
            this.commands = new LinkedList<>();
            this.commands.add(command);
            return this;
        }

        public LevelUpCommandBuilder commands(@NotNull Collection<String> command) {
            this.commands = new LinkedList<>(command);
            return this;
        }

        public LevelUpCommandBuilder withLevels(@NotNull Collection<Integer> levels) {
            requireNonNull(levels, "levels is null!");
            this.levels = Set.copyOf(levels);
            return this;
        }

        public LevelUpCommandBuilder withSkillFilter(@NotNull Set<PrimarySkillType> skillFilter) {
            requireNonNull(skillFilter, "skillFilter is null!");
            if (skillFilter.isEmpty()) {
                throw new IllegalArgumentException("skillFilter is empty");
            }
            this.skillFilter = skillFilter;
            return this;
        }

        public LevelUpCommandBuilder withSkillFilter(@NotNull PrimarySkillType skill) {
            requireNonNull(skill, "skill is null!");
            this.skillFilter = Set.of(skill);
            return this;
        }

        public LevelUpCommand build() {
            requireNonNull(commands, "commandStr is null");
            if (predicate == null) {
                requireNonNull(levels, "levels is null");

                return new LevelUpCommandImpl((skill, level) -> {
                    if (skillFilter == null) {
                        return levels.contains(level);
                    } else {
                        return skillFilter.contains(skill) && levels.contains(level);
                    }
                }, commands, logInfo);
            }

            return new LevelUpCommandImpl(predicate, commands, logInfo);
        }
    }
}
