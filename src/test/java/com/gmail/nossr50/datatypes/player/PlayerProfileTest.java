package com.gmail.nossr50.datatypes.player;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.FormulaManager;
import com.gmail.nossr50.util.skills.SkillTools;
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

    /**
     * Regression coverage for the cumulative XP curve on offline-loaded profiles: the curve
     * levels against the player's power level, which was read through UserManager and crashed
     * for profiles without an online player (offline /inspect, ExperienceAPI offline lookups).
     * The profile's own level sum stands in when nobody is online.
     */
    @Test
    void getXpToLevelShouldUseOwnLevelSumForOfflineProfilesWithCumulativeCurve() {
        // Given - the cumulative curve is enabled and this profile has no online player
        when(ExperienceConfig.getInstance().getCumulativeCurveEnabled()).thenReturn(true);
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);

        final FormulaManager formulaManager = mock(FormulaManager.class);
        mockedMcMMO.when(mcMMO::getFormulaManager).thenReturn(formulaManager);
        final int levelSum = SkillTools.NON_CHILD_SKILLS.size() * STARTING_LEVEL;
        when(formulaManager.getXPtoNextLevel(levelSum, FormulaType.LINEAR)).thenReturn(4242);

        // When - the XP to the next level is requested
        final int xpToLevel = profile.getXpToLevel(PrimarySkillType.MINING);

        // Then - the curve levels against the profile's own level sum instead of crashing
        assertThat(xpToLevel).isEqualTo(4242);
    }

    @Test
    void getXpToLevelShouldUseTheSkillLevelWhenCumulativeCurveIsDisabled() {
        // Given - the default curve
        when(ExperienceConfig.getInstance().getFormulaType()).thenReturn(FormulaType.LINEAR);
        final FormulaManager formulaManager = mock(FormulaManager.class);
        mockedMcMMO.when(mcMMO::getFormulaManager).thenReturn(formulaManager);
        when(formulaManager.getXPtoNextLevel(STARTING_LEVEL, FormulaType.LINEAR))
                .thenReturn(1000);

        // When - the XP to the next level is requested
        final int xpToLevel = profile.getXpToLevel(PrimarySkillType.MINING);

        // Then - the curve levels against the skill's own level
        assertThat(xpToLevel).isEqualTo(1000);
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
