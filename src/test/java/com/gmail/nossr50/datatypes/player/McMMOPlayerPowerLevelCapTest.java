package com.gmail.nossr50.datatypes.player;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.Permissions;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Covers the power level cap checks in {@link McMMOPlayer}, which compare a permission-free
 * sum of all skill levels against the cap and only fall back to the permission-aware
 * {@link McMMOPlayer#getPowerLevel()} when that sum has reached the cap. The two behaviors
 * that matter: players below the cap (or servers with the cap disabled) must not trigger
 * skill permission checks, and players whose level total only reaches the cap through
 * skills they lack permission for must not be treated as capped.
 */
class McMMOPlayerPowerLevelCapTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(
            McMMOPlayerPowerLevelCapTest.class.getName());
    private static final int CAP_DISABLED = Integer.MAX_VALUE;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void hasReachedPowerLevelCapShouldBeFalseWithoutPermissionChecksWhenCapDisabled() {
        // Given - the power level cap is disabled and the player has a huge level total
        when(generalConfig.getPowerLevelCap()).thenReturn(CAP_DISABLED);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 5000);

        // When - the power level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedPowerLevelCap();

        // Then - the player is not capped and no skill permission was ever consulted
        assertThat(reachedCap).isFalse();
        mockedPermissions.verify(
                () -> Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)),
                never());
    }

    @Test
    void hasReachedPowerLevelCapShouldBeFalseWithoutPermissionChecksWhenLevelSumBelowCap() {
        // Given - a configured cap that the sum of every skill level does not reach
        when(generalConfig.getPowerLevelCap()).thenReturn(1000);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 600);
        mmoPlayer.modifySkill(PrimarySkillType.HERBALISM, 399);

        // When - the power level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedPowerLevelCap();

        // Then - being below the cap is decided without any skill permission checks
        assertThat(reachedCap).isFalse();
        mockedPermissions.verify(
                () -> Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)),
                never());
    }

    /**
     * Boundary coverage: the permission-free sum is only an upper bound, so once it reaches
     * the cap the permission-aware count must produce the final verdict. With all skill
     * permissions granted, both counts agree, and the answer must flip exactly at the cap.
     */
    @ParameterizedTest
    @CsvSource({
            "999, false",
            "1000, true",
            "1500, true"
    })
    void hasReachedPowerLevelCapShouldFlipExactlyAtCapWhenAllSkillsPermitted(int miningLevel,
            boolean expectedCapped) {
        // Given - a cap of 1000 and a player with permission for every skill
        when(generalConfig.getPowerLevelCap()).thenReturn(1000);
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, miningLevel);

        // When - the power level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedPowerLevelCap();

        // Then - the verdict flips exactly when the level total reaches the cap
        assertThat(reachedCap).isEqualTo(expectedCapped);
    }

    @Test
    void hasReachedPowerLevelCapShouldBeFalseWhenCapOnlyReachedThroughUnpermittedSkills() {
        // Given - the level total reaches the cap, but only when counting a skill the
        // player has no permission for; the permission-aware power level is below the cap
        when(generalConfig.getPowerLevelCap()).thenReturn(1000);
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        when(Permissions.skillEnabled(player, PrimarySkillType.HERBALISM)).thenReturn(false);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 600);
        mmoPlayer.modifySkill(PrimarySkillType.HERBALISM, 400);

        // When - the power level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedPowerLevelCap();

        // Then - the permission-aware count corrects the over-counting upper bound
        assertThat(reachedCap).isFalse();
        mockedPermissions.verify(
                () -> Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)),
                atLeastOnce());
    }

    @Test
    void hasReachedLevelCapShouldBeTrueWhenSkillAtItsLevelCapEvenWithPowerCapDisabled() {
        // Given - no power level cap, but the skill sits at its own level cap
        when(generalConfig.getPowerLevelCap()).thenReturn(CAP_DISABLED);
        when(generalConfig.getLevelCap(PrimarySkillType.MINING)).thenReturn(100);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 100);

        // When - the level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedLevelCap(PrimarySkillType.MINING);

        // Then - the per-skill cap still applies on its own
        assertThat(reachedCap).isTrue();
    }

    @Test
    void hasReachedLevelCapShouldBeFalseWithoutPermissionChecksWhenBelowBothCaps() {
        // Given - a player below both the skill level cap and the power level cap
        when(generalConfig.getPowerLevelCap()).thenReturn(1000);
        when(generalConfig.getLevelCap(PrimarySkillType.MINING)).thenReturn(100);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 99);

        // When - the level cap is checked
        final boolean reachedCap = mmoPlayer.hasReachedLevelCap(PrimarySkillType.MINING);

        // Then - the answer is reached without consulting skill permissions
        assertThat(reachedCap).isFalse();
        mockedPermissions.verify(
                () -> Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)),
                never());
    }

    @Test
    void modifyXpGainShouldReturnZeroWhenPowerLevelCapReached() {
        // Given - a player with skill permission whose power level sits at the cap
        when(generalConfig.getPowerLevelCap()).thenReturn(100);
        when(generalConfig.getLevelCap(PrimarySkillType.MINING)).thenReturn(10000);
        when(Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)))
                .thenReturn(true);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 100);

        // When - an XP gain is modified
        final float modifiedXp = mmoPlayer.modifyXpGain(PrimarySkillType.MINING, 10F);

        // Then - a capped player gains no XP
        assertThat(modifiedXp).isZero();
    }

    @Test
    void modifyXpGainShouldApplyMultipliersWithoutPermissionCountingWhenCapDisabled() {
        // Given - no power level cap, neutral multipliers, and a high skill level that
        // would have forced a permission-aware power level count before the upper bound
        // shortcut existed
        when(generalConfig.getPowerLevelCap()).thenReturn(CAP_DISABLED);
        when(generalConfig.getLevelCap(PrimarySkillType.MINING)).thenReturn(10000);
        when(ExperienceConfig.getInstance().getFormulaSkillModifier(PrimarySkillType.MINING))
                .thenReturn(1.0);
        when(ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()).thenReturn(1.0);
        mmoPlayer.modifySkill(PrimarySkillType.MINING, 5000);

        // When - an XP gain is modified
        final float modifiedXp = mmoPlayer.modifyXpGain(PrimarySkillType.MINING, 10F);

        // Then - the XP passes through unchanged and no skill permission was consulted
        assertThat(modifiedXp).isEqualTo(10F);
        mockedPermissions.verify(
                () -> Permissions.skillEnabled(any(Player.class), any(PrimarySkillType.class)),
                never());
    }
}
