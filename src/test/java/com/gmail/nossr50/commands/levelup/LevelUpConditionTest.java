package com.gmail.nossr50.commands.levelup;

import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.HERBALISM;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.MINING;
import static com.gmail.nossr50.datatypes.skills.PrimarySkillType.WOODCUTTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Unit tests for {@link LevelUpCondition} matching and validation. The condition is the heart
 * of config-driven level up commands, so invalid combinations must fail fast at construction
 * rather than silently never firing.
 */
class LevelUpConditionTest {

    @ParameterizedTest(name = "skills={0}, levels={1}, powerLevels={2}")
    @MethodSource("invalidTriggerCombinations")
    void conditionShouldThrowWhenTriggerIsIncomplete(final List<String> skillNames,
            final List<Integer> levels, final List<Integer> powerLevels) {
        // Given - an incomplete trigger definition
        final var skills = skillNames.stream().map(PrimarySkillType::valueOf).toList();

        // When / Then - construction is rejected
        assertThatThrownBy(() -> LevelUpCondition.of(skills, levels, powerLevels))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Arguments> invalidTriggerCombinations() {
        return Stream.of(
                // nothing at all
                Arguments.of(List.of(), List.of(), List.of()),
                // skills without levels
                Arguments.of(List.of("MINING"), List.of(), List.of()),
                // levels without skills
                Arguments.of(List.of(), List.of(10), List.of()),
                // power levels alone are fine, but skills without levels still are not
                Arguments.of(List.of("MINING"), List.of(), List.of(100))
        );
    }

    @Test
    void matchedSkillLevelsShouldReturnSortedIntersection() {
        // Given - a condition listing three Mining milestones
        final LevelUpCondition condition = LevelUpCondition.skillLevels(Set.of(MINING),
                Set.of(10, 20, 30));

        // When - a level up crosses two of them, provided in no particular order
        final var matched = condition.matchedSkillLevels(MINING, Set.of(30, 9, 10, 11));

        // Then - both milestones match, sorted ascending
        assertThat(matched).containsExactly(10, 30);
    }

    @Test
    void matchedSkillLevelsShouldBeEmptyWhenSkillNotListed() {
        // Given - a condition that only watches Mining
        final LevelUpCondition condition = LevelUpCondition.skillLevels(Set.of(MINING),
                Set.of(10));

        // When - another skill reaches a listed level
        final var matched = condition.matchedSkillLevels(WOODCUTTING, Set.of(10));

        // Then - nothing matches
        assertThat(matched).isEmpty();
    }

    @Test
    void matchedPowerLevelsShouldReturnSortedIntersection() {
        // Given - power level milestones
        final LevelUpCondition condition = LevelUpCondition.powerLevels(Set.of(100, 200));

        // When - a level up crosses one listed and one unlisted power level
        final var matched = condition.matchedPowerLevels(Set.of(200, 199));

        // Then - only the listed milestone matches
        assertThat(matched).containsExactly(200);
    }

    @Test
    void matchedSkillLevelsShouldBeEmptyForPowerOnlyCondition() {
        // Given - a condition with only a power level trigger
        final LevelUpCondition condition = LevelUpCondition.powerLevels(Set.of(100));

        // When - any skill levels up
        final var matched = condition.matchedSkillLevels(HERBALISM, Set.of(100));

        // Then - the skill trigger never matches
        assertThat(matched).isEmpty();
    }

    @Test
    void gettersShouldExposeImmutableSets() {
        // Given - a fully populated condition
        final LevelUpCondition condition = LevelUpCondition.of(Set.of(MINING), Set.of(10),
                Set.of(100));

        // When / Then - the exposed sets reject mutation
        assertThatThrownBy(() -> condition.getSkills().add(WOODCUTTING))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> condition.getLevels().add(11))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> condition.getPowerLevels().add(101))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
