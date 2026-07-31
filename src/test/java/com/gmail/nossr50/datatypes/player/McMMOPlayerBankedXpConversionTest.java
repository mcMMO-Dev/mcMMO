package com.gmail.nossr50.datatypes.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Permissions;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers how the XP gain path converts banked XP into levels when the XP curve no longer
 * matches the XP a player has stored.
 *
 * When level requirements change between sessions (edited experience.yml curve values, or a
 * flip of General.RetroMode.Enabled changing Standard/Retro scaling), players can hold XP
 * far above or far below the requirement for their current level. mcMMO only reconciles
 * that on XP gain, so these tests pin the guarantees that reconciliation relies on: banked
 * XP above the requirement converts into levels on the very next gain with the leftover
 * preserved, XP below the requirement keeps accumulating until it crosses it, and
 * conversion respects the level cap instead of running away.
 */
class McMMOPlayerBankedXpConversionTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            McMMOPlayerBankedXpConversionTest.class.getName());
    private static final PrimarySkillType SKILL = PrimarySkillType.MINING;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);

        // Flat linear curve so requirements are easy to derive: each Retro level needs
        // 1000 + level XP, each Standard level sums the ten Retro levels it represents,
        // which works out to 10055 + 100 * level
        when(experienceConfigInstance.getFormulaType()).thenReturn(FormulaType.LINEAR);
        when(experienceConfigInstance.getBase(FormulaType.LINEAR)).thenReturn(1000);
        when(experienceConfigInstance.getMultiplier(FormulaType.LINEAR)).thenReturn(1D);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    private void seedSkill(int level, float bankedXp) {
        playerProfile.modifySkill(SKILL, level);
        playerProfile.addXp(SKILL, bankedXp);
    }

    private void gainXp(float xp) {
        mmoPlayer.applyXpGain(SKILL, xp, XPGainReason.PVE, XPGainSource.SELF);
    }

    @Test
    void bankedXpShouldConvertIntoLevelsOnNextGainWhenRequirementsShrink() {
        // Given - XP banked toward a Standard-scaling requirement of 10555 at level 5
        seedSkill(5, 9000F);

        // And - the server now runs Retro scaling, where level 5 only needs 1005 XP
        mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(true);

        // When - the player gains a small amount of XP
        gainXp(10F);

        // Then - the banked 9010 XP pays for going from level 5 to 13 in one gain
        // (1005 + 1006 + ... + 1012) and the remainder stays banked toward level 14
        assertThat(mmoPlayer.getSkillLevel(SKILL)).isEqualTo(13);
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(SKILL)).isEqualTo(942F);
    }

    @Test
    void xpShouldKeepAccumulatingTowardGrownRequirementWhenScalingFlipsToStandard() {
        // Given - XP banked under Retro scaling, below its 1005 requirement at level 5
        mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(true);
        seedSkill(5, 900F);

        // And - the server now runs Standard scaling, where level 5 needs 10555 XP
        mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(false);

        // When - the player gains XP that still leaves them short of the requirement
        gainXp(100F);

        // Then - no level is gained and nothing is lost, the XP keeps accumulating
        assertThat(mmoPlayer.getSkillLevel(SKILL)).isEqualTo(5);
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(SKILL)).isEqualTo(1000F);

        // When - the player gains exactly enough XP to reach the requirement
        gainXp(9555F);

        // Then - the level-up lands the moment the requirement is met
        assertThat(mmoPlayer.getSkillLevel(SKILL)).isEqualTo(6);
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(SKILL)).isEqualTo(0F);
    }

    @Test
    void bankedXpConversionShouldStopAtTheLevelCap() {
        // Given - Retro scaling with a level cap of 10 and banked XP worth far more levels
        mockedMcMMO.when(mcMMO::isRetroModeEnabled).thenReturn(true);
        when(generalConfig.getLevelCap(SKILL)).thenReturn(10);
        seedSkill(5, 50_000F);

        // When - the player gains a small amount of XP
        gainXp(10F);

        // Then - conversion stops at the cap and the surplus XP is discarded with it
        assertThat(mmoPlayer.getSkillLevel(SKILL)).isEqualTo(10);
        assertThat(mmoPlayer.getProfile().getSkillXpLevelRaw(SKILL)).isEqualTo(0F);
    }
}
