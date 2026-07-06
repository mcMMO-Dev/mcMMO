package com.gmail.nossr50.commands.levelup;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable description of when a level up command fires: reaching one of the listed levels in
 * one of the listed skills, or reaching one of the listed power levels. Skill and power level
 * triggers are independent; a condition may carry either or both.
 */
public final class LevelUpCondition {
    private final @NotNull Set<PrimarySkillType> skills;
    private final @NotNull Set<Integer> levels;
    private final @NotNull Set<Integer> powerLevels;

    private LevelUpCondition(@NotNull Collection<PrimarySkillType> skills,
            @NotNull Collection<Integer> levels, @NotNull Collection<Integer> powerLevels) {
        this.skills = Set.copyOf(skills);
        this.levels = Set.copyOf(levels);
        this.powerLevels = Set.copyOf(powerLevels);
        final boolean hasSkillTrigger = !this.skills.isEmpty() && !this.levels.isEmpty();
        final boolean hasPowerLevelTrigger = !this.powerLevels.isEmpty();
        if (!hasSkillTrigger && !hasPowerLevelTrigger) {
            throw new IllegalArgumentException(
                    "condition needs skills with levels, power levels, or both");
        }
        if (this.skills.isEmpty() != this.levels.isEmpty()) {
            throw new IllegalArgumentException("skills and levels must be provided together");
        }
    }

    public static @NotNull LevelUpCondition skillLevels(
            @NotNull Collection<PrimarySkillType> skills, @NotNull Collection<Integer> levels) {
        return new LevelUpCondition(skills, levels, Set.of());
    }

    public static @NotNull LevelUpCondition powerLevels(@NotNull Collection<Integer> powerLevels) {
        return new LevelUpCondition(Set.of(), Set.of(), powerLevels);
    }

    public static @NotNull LevelUpCondition of(@NotNull Collection<PrimarySkillType> skills,
            @NotNull Collection<Integer> levels, @NotNull Collection<Integer> powerLevels) {
        return new LevelUpCondition(skills, levels, powerLevels);
    }

    /**
     * The listed levels that this level up reached for a listed skill, sorted ascending.
     * Empty when the skill is not listed or none of its listed levels were reached.
     */
    public @NotNull SortedSet<Integer> matchedSkillLevels(@NotNull PrimarySkillType skill,
            @NotNull Set<Integer> levelsGained) {
        final SortedSet<Integer> matched = new TreeSet<>();
        if (skills.contains(skill)) {
            for (int level : levelsGained) {
                if (levels.contains(level)) {
                    matched.add(level);
                }
            }
        }
        return matched;
    }

    /**
     * The listed power levels that this level up reached, sorted ascending.
     */
    public @NotNull SortedSet<Integer> matchedPowerLevels(@NotNull Set<Integer> powerLevelsGained) {
        final SortedSet<Integer> matched = new TreeSet<>();
        for (int powerLevel : powerLevelsGained) {
            if (powerLevels.contains(powerLevel)) {
                matched.add(powerLevel);
            }
        }
        return matched;
    }

    public @NotNull Set<PrimarySkillType> getSkills() {
        return skills;
    }

    public @NotNull Set<Integer> getLevels() {
        return levels;
    }

    public @NotNull Set<Integer> getPowerLevels() {
        return powerLevels;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LevelUpCondition that = (LevelUpCondition) o;
        return skills.equals(that.skills) && levels.equals(that.levels)
                && powerLevels.equals(that.powerLevels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skills, levels, powerLevels);
    }

    @Override
    public String toString() {
        return "LevelUpCondition{skills=" + skills + ", levels=" + levels
                + ", powerLevels=" + powerLevels + '}';
    }
}
