package com.gmail.nossr50.datatypes.player;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerProfileTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(PlayerProfileTest.class.getName());

    private static final int STARTING_LEVEL = 10;

    private PlayerProfile profile;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        profile = new PlayerProfile("Herb", UUID.randomUUID(), STARTING_LEVEL);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for adding levels to a child skill: child skills have no level entry
     * of their own, and the read-then-modify in addLevels crashed on the missing entry before
     * the child guard in modifySkill could run. Child levels split across the parents instead,
     * matching how child XP and the offline ExperienceAPI variants behave.
     */
    @Test
    void addLevelsShouldSplitChildSkillLevelsAcrossParents() {
        // Given - the child skill Smelting with its two parents at the starting level
        final var parents = mcMMO.p.getSkillTools()
                .getChildSkillParents(PrimarySkillType.SMELTING);

        // When - levels are added to the child skill
        profile.addLevels(PrimarySkillType.SMELTING, 4);

        // Then - each parent receives an equal share
        for (final PrimarySkillType parent : parents) {
            assertThat(profile.getSkillLevel(parent)).isEqualTo(STARTING_LEVEL + 2);
        }
    }

    @Test
    void addLevelsShouldAddToANonChildSkillDirectly() {
        // Given - a non-child skill at the starting level
        // When - levels are added
        profile.addLevels(PrimarySkillType.MINING, 5);

        // Then - the skill is raised and no other skill is touched
        assertThat(profile.getSkillLevel(PrimarySkillType.MINING))
                .isEqualTo(STARTING_LEVEL + 5);
        assertThat(profile.getSkillLevel(PrimarySkillType.HERBALISM)).isEqualTo(STARTING_LEVEL);
    }

}
