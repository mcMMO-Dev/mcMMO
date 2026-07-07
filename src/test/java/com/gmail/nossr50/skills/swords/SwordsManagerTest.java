package com.gmail.nossr50.skills.swords;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.LivingEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Covers the Swords combat effects that depend on the committed attack strength of a hit.
 *
 * <p>Regression background: Paper 26.1.2+ resets the attack cooldown ticker before
 * {@code EntityDamageByEntityEvent} fires, so {@code Player#getAttackCooldown()} reads ~0.1
 * mid-event. Rupture must roll its odds with the attack strength scale passed in from
 * CombatUtils, otherwise it silently procs at ~10% of the intended rate.
 */
class SwordsManagerTest extends MMOTestEnvironment {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(
            SwordsManagerTest.class.getName());

    private SwordsManager swordsManager;
    private LivingEntity target;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        swordsManager = new SwordsManager(mmoPlayer);
        target = Mockito.mock(LivingEntity.class);

        // Simulate Paper 26.1.2+ during a damage event: the ticker was already reset, so the
        // live cooldown misreports the charge of the committed hit.
        when(advancedConfig.useAttackCooldown()).thenReturn(true);
        when(player.getAttackCooldown()).thenReturn(0.1f);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void processRuptureShouldRollOddsWithCommittedAttackStrength() {
        // Given - Rupture unlocked at rank 3 with a 33% base chance to apply on hit
        Mockito.when(RankUtils.hasUnlockedSubskill(player, SubSkillType.SWORDS_RUPTURE))
                .thenReturn(true);
        Mockito.when(RankUtils.getRank(player, SubSkillType.SWORDS_RUPTURE)).thenReturn(3);
        when(advancedConfig.getRuptureChanceToApplyOnHit(3)).thenReturn(33.0);
        when(target.hasMetadata(MetadataConstants.METADATA_KEY_RUPTURE)).thenReturn(false);

        try (MockedStatic<ProbabilityUtil> probabilityUtil = mockStatic(ProbabilityUtil.class)) {
            // When - a hit committed at half attack strength lands and the roll fails
            // (mock default) so no rupture task is scheduled
            swordsManager.processRupture(target, 0.5);

            // Then - the odds are the base chance scaled by the committed attack strength,
            // not by the stale live cooldown
            probabilityUtil.verify(() -> ProbabilityUtil.isStaticSkillRNGSuccessful(
                    eq(PrimarySkillType.SWORDS), eq(mmoPlayer), eq(16.5)));
            verify(player, never()).getAttackCooldown();
        }
    }
}
