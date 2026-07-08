package com.gmail.nossr50.listeners;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.config.PersistentDataConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.util.MobMetadataUtils;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
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
}
