package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.skills.taming.TrackedTamingEntity;
import com.gmail.nossr50.util.MobMetadataUtils;
import com.gmail.nossr50.util.player.UserManager;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class EntityListenerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(EntityListenerTest.class.getName());

    private MockedStatic<WorldBlacklist> worldBlacklistMock;
    private MockedStatic<PersistentDataConfig> persistentDataConfigMock;
    private MockedStatic<MobMetadataUtils> mobMetadataMock;
    private EntityListener entityListener;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);
        // MobMetadataUtils initialization builds NamespacedKeys from the plugin name and reads
        // the persistent data config, neither of which exist in the unit test environment
        when(mcMMO.p.getName()).thenReturn("mcMMO");
        persistentDataConfigMock = mockStatic(PersistentDataConfig.class);
        persistentDataConfigMock.when(PersistentDataConfig::getInstance)
                .thenReturn(mock(PersistentDataConfig.class));
        worldBlacklistMock = mockStatic(WorldBlacklist.class);
        mobMetadataMock = mockStatic(MobMetadataUtils.class);
        entityListener = new EntityListener(mcMMO.p);
    }

    @AfterEach
    void tearDown() {
        worldBlacklistMock.close();
        persistentDataConfigMock.close();
        mobMetadataMock.close();
        cleanUpStaticMocks();
    }

    /**
     * Regression coverage for passenger tracking on spawner spawns: the passenger loop
     * previously re-flagged the mount and never flagged the passenger, so jockey riders from
     * spawners kept rewarding full XP.
     */
    @Test
    void creatureSpawnFromSpawnerShouldFlagMountAndPassenger() {
        // Given - a spawner-spawned mount carrying a passenger
        final LivingEntity mount = mock(LivingEntity.class);
        final LivingEntity passenger = mock(LivingEntity.class);
        when(mount.getWorld()).thenReturn(world);
        when(mount.getPassengers()).thenReturn(List.of((Entity) passenger));
        final CreatureSpawnEvent event = mock(CreatureSpawnEvent.class);
        when(event.getEntity()).thenReturn(mount);
        when(event.getSpawnReason()).thenReturn(CreatureSpawnEvent.SpawnReason.SPAWNER);

        // When - the creature spawn is handled
        entityListener.onCreatureSpawn(event);

        // Then - the mount and its passenger both carry the mob spawner flag
        mobMetadataMock.verify(
                () -> MobMetadataUtils.flagMetadata(MobMetaFlagType.MOB_SPAWNER_MOB, mount));
        mobMetadataMock.verify(
                () -> MobMetadataUtils.flagMetadata(MobMetaFlagType.MOB_SPAWNER_MOB, passenger));
    }

    /**
     * Regression coverage for taming through the API: EntityTameEvent owners are AnimalTamers
     * and not necessarily players, and the handler previously cast the owner unchecked and
     * crashed on non-player tamers.
     */
    @Test
    void entityTameShouldIgnoreNonPlayerOwnersInsteadOfCrashing() {
        // Given - an entity tamed by a non-player AnimalTamer (e.g. through another plugin)
        final LivingEntity tamedEntity = mock(LivingEntity.class);
        when(tamedEntity.getWorld()).thenReturn(world);
        final EntityTameEvent event = mock(EntityTameEvent.class);
        when(event.getEntity()).thenReturn(tamedEntity);
        when(event.getOwner()).thenReturn(mock(AnimalTamer.class));

        // When - the tame event is handled
        // Then - the non-player owner is ignored without an exception and nothing is flagged
        assertThatCode(() -> entityListener.onEntityTame(event)).doesNotThrowAnyException();
        mobMetadataMock.verifyNoInteractions();
    }

    /**
     * A player whose mcMMO data has not loaded yet is not friendly fire: combust events between
     * unrelated players must burn normally instead of being cancelled just because a profile is
     * still loading.
     */
    @Test
    void combustShouldNotBeCancelledWhenPlayerDataIsNotLoaded() {
        // Given - two unrelated players, the attacker's mcMMO data still loading
        when(mcMMO.p.isPartySystemEnabled()).thenReturn(true);
        final Player attacker = mock(Player.class);
        mockedUserManager.when(() -> UserManager.hasPlayerDataKey(player)).thenReturn(true);
        mockedUserManager.when(() -> UserManager.hasPlayerDataKey(attacker)).thenReturn(false);
        final EntityCombustByEntityEvent event = mock(EntityCombustByEntityEvent.class);
        when(event.getEntity()).thenReturn(player);
        when(event.getCombuster()).thenReturn(attacker);

        // When - the combust event is handled
        entityListener.onEntityCombustByEntityEvent(event);

        // Then - the fire is not cancelled
        verify(event, never()).setCancelled(true);
    }

    /**
     * Genuine party friendly fire keeps being blocked: same party, friendly fire disabled in
     * the config, and neither player holding the friendly-fire permission.
     */
    @Test
    void combustShouldBeCancelledForBlockedPartyFriendlyFire() {
        // Given - two party members with loaded data and friendly fire disallowed
        when(mcMMO.p.isPartySystemEnabled()).thenReturn(true);
        final Player attacker = mock(Player.class);
        mockedUserManager.when(() -> UserManager.hasPlayerDataKey(player)).thenReturn(true);
        mockedUserManager.when(() -> UserManager.hasPlayerDataKey(attacker)).thenReturn(true);
        when(generalConfig.getPartyFriendlyFire()).thenReturn(false);
        final PartyManager partyManager = mock(PartyManager.class);
        when(mcMMO.p.getPartyManager()).thenReturn(partyManager);
        when(partyManager.inSameParty(player, attacker)).thenReturn(true);
        final EntityCombustByEntityEvent event = mock(EntityCombustByEntityEvent.class);
        when(event.getEntity()).thenReturn(player);
        when(event.getCombuster()).thenReturn(attacker);

        // When - the combust event is handled
        entityListener.onEntityCombustByEntityEvent(event);

        // Then - the friendly fire is cancelled
        verify(event, org.mockito.Mockito.atLeastOnce()).setCancelled(true);
    }

    /**
     * Regression coverage for summon tracking: a Call of the Wild summon that dies is already
     * invalid when the death event fires, so the old kill-based cleanup skipped it and the
     * tracker kept a strong reference to the dead entity until the timer expired or the player
     * logged out.
     */
    @Test
    void entityDeathShouldUntrackCallOfTheWildSummon() {
        // Given - a tracked Call of the Wild summon
        final LivingEntity summon = mock(LivingEntity.class);
        when(summon.getWorld()).thenReturn(world);
        when(summon.getUniqueId()).thenReturn(UUID.randomUUID());
        final TrackedTamingEntity trackedSummon = new TrackedTamingEntity(summon,
                CallOfTheWildType.WOLF, player);
        mcMMO.getTransientEntityTracker().addSummon(playerUUID, trackedSummon);

        // And - the summon dies (a dying entity is no longer valid)
        final EntityDeathEvent event = mock(EntityDeathEvent.class);
        when(event.getEntity()).thenReturn(summon);

        // When - the death is handled
        entityListener.onEntityDeath(event);

        // Then - the tracker no longer holds the dead summon
        assertThat(mcMMO.getTransientEntityTracker().isTransient(summon)).isFalse();
    }
}
