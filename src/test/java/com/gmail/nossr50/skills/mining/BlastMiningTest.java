package com.gmail.nossr50.skills.mining;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Covers the tracked-TNT owner resolution shared by the explosion listeners and the blast
 * mining damage handling. The owner name lives in entity metadata, and the owner may have
 * logged off between placing the TNT and the explosion.
 */
class BlastMiningTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(BlastMiningTest.class.getName());

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void resolveTntOwnerShouldReturnNullForUntrackedTnt() {
        // Given - a TNT entity mcMMO never tracked (e.g. lit by redstone)
        final TNTPrimed tnt = mock(TNTPrimed.class);

        // When - the owner is resolved
        // Then - there is no owner
        assertThat(BlastMining.resolveTntOwner(tnt)).isNull();
    }

    @Test
    void resolveTntOwnerShouldResolveTheTrackingPlayer() {
        // Given - a TNT entity tracked with the owner's name in metadata
        final TNTPrimed tnt = mock(TNTPrimed.class);
        final MetadataValue ownerName = mock(MetadataValue.class);
        when(ownerName.asString()).thenReturn("Herpington");
        when(tnt.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT)).thenReturn(true);
        when(tnt.getMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT))
                .thenReturn(List.of(ownerName));
        when(server.getPlayerExact("Herpington")).thenReturn(player);

        // When - the owner is resolved
        // Then - the online owner is returned
        assertThat(BlastMining.resolveTntOwner(tnt)).isSameAs(player);
    }

    @Test
    void resolveTntOwnerShouldReturnNullWhenTheOwnerIsOffline() {
        // Given - a tracked TNT whose owner logged off before the explosion
        final TNTPrimed tnt = mock(TNTPrimed.class);
        final MetadataValue ownerName = mock(MetadataValue.class);
        when(ownerName.asString()).thenReturn("GoneFishing");
        when(tnt.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT)).thenReturn(true);
        when(tnt.getMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT))
                .thenReturn(List.of(ownerName));
        when(server.getPlayerExact("GoneFishing")).thenReturn(null);

        // When - the owner is resolved
        // Then - no owner is returned instead of a stale reference
        assertThat(BlastMining.resolveTntOwner(tnt)).isNull();
    }

    @Nested
    class SubskillUnlockLevels {
        /**
         * Demolitions Expertise and Bigger Bombs unlock at the first Blast Mining rank whose
         * modifier becomes positive; the scan must return that rank's unlock level.
         */
        @Test
        void demolitionsExpertiseShouldUnlockAtTheFirstDamageReducingRank() {
            // Given - blast damage decreases start at rank 3
            when(advancedConfig.getBlastDamageDecrease(1)).thenReturn(0.0);
            when(advancedConfig.getBlastDamageDecrease(2)).thenReturn(0.0);
            when(advancedConfig.getBlastDamageDecrease(3)).thenReturn(10.0);
            when(RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, 3))
                    .thenReturn(500);

            // When / Then - the unlock level is rank 3's level
            assertThat(BlastMining.getDemolitionExpertUnlockLevel()).isEqualTo(500);
        }

        @Test
        void biggerBombsShouldUnlockAtTheFirstRadiusBoostingRank() {
            // Given - blast radius modifiers start at rank 2
            when(advancedConfig.getBlastRadiusModifier(1)).thenReturn(0.0);
            when(advancedConfig.getBlastRadiusModifier(2)).thenReturn(1.0);
            when(RankUtils.getRankUnlockLevel(SubSkillType.MINING_BLAST_MINING, 2))
                    .thenReturn(250);

            // When / Then - the unlock level is rank 2's level
            assertThat(BlastMining.getBiggerBombsUnlockLevel()).isEqualTo(250);
        }

        @Test
        void allZeroModifiersShouldUnlockAtLevelZero() {
            // Given - no rank ever boosts the radius (config mock defaults to 0.0)
            // When / Then - the scan falls through to level zero
            assertThat(BlastMining.getBiggerBombsUnlockLevel()).isZero();
        }
    }

    @Nested
    class ExplosionDamageProcessing {
        private TNTPrimed tnt;
        private Player defender;
        private EntityDamageByEntityEvent damageEvent;

        @BeforeEach
        void setUpExplosion() {
            tnt = mock(TNTPrimed.class);
            defender = mock(Player.class);
            damageEvent = mock(EntityDamageByEntityEvent.class);
            when(UserManager.hasPlayerDataKey(defender)).thenReturn(true);
        }

        private void wireTntOwner(String ownerName, Player resolvedOwner) {
            final MetadataValue ownerMeta = mock(MetadataValue.class);
            when(ownerMeta.asString()).thenReturn(ownerName);
            when(tnt.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT)).thenReturn(true);
            when(tnt.getMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT))
                    .thenReturn(List.of(ownerMeta));
            when(server.getPlayerExact(ownerName)).thenReturn(resolvedOwner);
        }

        @Test
        void untrackedTntShouldNotBeProcessed() {
            // Given - vanilla TNT
            // When - the explosion damage is processed
            final boolean processed =
                    BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - mcMMO leaves the damage alone
            assertThat(processed).isFalse();
            verify(damageEvent, never()).setDamage(anyDouble());
        }

        @Test
        void bystandersShouldHaveTheirDamageCapped() {
            // Given - a bystander caught in another player's blast for huge damage
            wireTntOwner("Blaster", mock(Player.class));
            when(damageEvent.getDamage()).thenReturn(60.0);
            when(damageEvent.getFinalDamage()).thenReturn(24.0);

            // When - the explosion damage is processed
            final boolean processed =
                    BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - the PVP damage is capped and the event proceeds
            assertThat(processed).isFalse();
            verify(damageEvent).setDamage(24.0);
            verify(damageEvent, never()).setCancelled(true);
        }

        @Test
        void fullyAbsorbedBystanderDamageShouldCancelTheEvent() {
            // Given - a bystander whose armor absorbs the whole capped hit
            wireTntOwner("Blaster", mock(Player.class));
            when(damageEvent.getDamage()).thenReturn(10.0);
            when(damageEvent.getFinalDamage()).thenReturn(0.0);

            // When - the explosion damage is processed
            BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - the empty hit is cancelled outright
            verify(damageEvent).setCancelled(true);
        }

        @Test
        void ownersWithDemolitionsExpertiseShouldTakeReducedDamage() {
            // Given - the blast owner is the defender and knows demolitions expertise
            wireTntOwner("Blaster", defender);
            when(UserManager.getPlayer(defender)).thenReturn(mmoPlayer);
            final MiningManager miningManager = mock(MiningManager.class);
            doReturn(miningManager).when(mmoPlayer).getMiningManager();
            when(miningManager.canUseDemolitionsExpertise()).thenReturn(true);
            when(damageEvent.getDamage()).thenReturn(40.0);
            when(miningManager.processDemolitionsExpertise(40.0)).thenReturn(30.0);
            when(damageEvent.getFinalDamage()).thenReturn(30.0);

            // When - the explosion damage is processed
            final boolean processed =
                    BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - the reduced damage is applied and the event proceeds
            assertThat(processed).isTrue();
            verify(damageEvent).setDamage(30.0);
        }

        @Test
        void ownersWithoutDemolitionsExpertiseShouldTakeFullDamage() {
            // Given - the blast owner is the defender but lacks demolitions expertise
            wireTntOwner("Blaster", defender);
            when(UserManager.getPlayer(defender)).thenReturn(mmoPlayer);
            final MiningManager miningManager = mock(MiningManager.class);
            doReturn(miningManager).when(mmoPlayer).getMiningManager();
            when(miningManager.canUseDemolitionsExpertise()).thenReturn(false);

            // When - the explosion damage is processed
            final boolean processed =
                    BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - the damage is untouched
            assertThat(processed).isFalse();
            verify(damageEvent, never()).setDamage(anyDouble());
        }

        @Test
        void defendersWithoutLoadedDataShouldBeIgnored() {
            // Given - the defender's mcMMO data is not loaded
            wireTntOwner("Blaster", defender);
            when(UserManager.hasPlayerDataKey(defender)).thenReturn(false);

            // When - the explosion damage is processed
            final boolean processed =
                    BlastMining.processBlastMiningExplosion(damageEvent, tnt, defender);

            // Then - mcMMO leaves the damage alone
            assertThat(processed).isFalse();
            verify(damageEvent, never()).setDamage(anyDouble());
        }
    }
}
