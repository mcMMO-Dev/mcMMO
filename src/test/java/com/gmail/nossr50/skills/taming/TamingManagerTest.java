package com.gmail.nossr50.skills.taming;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TamingManagerTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(TamingManagerTest.class.getName());

    private TamingManager tamingManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        tamingManager = new TamingManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void attackTargetShouldUseTargetNearbyEntitiesAndCommandOnlyEligibleOwnedWolves() {
        // Given - a valid ranged target in the same world with mixed nearby entities.
        final LivingEntity target = mock(LivingEntity.class);
        final Entity nonWolfEntity = mock(Entity.class);
        final Wolf untamedWolf = mock(Wolf.class);
        final Wolf foreignWolf = mock(Wolf.class);
        final Wolf sittingOwnedWolf = mock(Wolf.class);
        final Wolf activeOwnedWolf = mock(Wolf.class);
        final Player otherPlayer = mock(Player.class);

        when(player.getWorld()).thenReturn(world);
        when(target.getWorld()).thenReturn(world);
        when(target.getNearbyEntities(5.0, 5.0, 5.0)).thenReturn(List.of(
                nonWolfEntity,
                untamedWolf,
                foreignWolf,
                sittingOwnedWolf,
                activeOwnedWolf
        ));

        when(nonWolfEntity.getType()).thenReturn(EntityType.ZOMBIE);
        when(untamedWolf.getType()).thenReturn(EntityType.WOLF);
        when(foreignWolf.getType()).thenReturn(EntityType.WOLF);
        when(sittingOwnedWolf.getType()).thenReturn(EntityType.WOLF);
        when(activeOwnedWolf.getType()).thenReturn(EntityType.WOLF);

        when(untamedWolf.isTamed()).thenReturn(false);

        when(foreignWolf.isTamed()).thenReturn(true);
        when(foreignWolf.getOwner()).thenReturn(otherPlayer);

        when(sittingOwnedWolf.isTamed()).thenReturn(true);
        when(sittingOwnedWolf.getOwner()).thenReturn(player);
        when(sittingOwnedWolf.isSitting()).thenReturn(true);

        when(activeOwnedWolf.isTamed()).thenReturn(true);
        when(activeOwnedWolf.getOwner()).thenReturn(player);
        when(activeOwnedWolf.isSitting()).thenReturn(false);

        // When - taming attack assist processing runs for the hit target.
        assertThatCode(() -> tamingManager.attackTarget(target)).doesNotThrowAnyException();

        // Then - lookup should be target-centered and only eligible owned wolves should be commanded.
        verify(target).getNearbyEntities(5.0, 5.0, 5.0);
        verify(player, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
        verify(activeOwnedWolf).setTarget(target);
        verify(untamedWolf, never()).setTarget(target);
        verify(foreignWolf, never()).setTarget(target);
        verify(sittingOwnedWolf, never()).setTarget(target);
    }

    @Test
    void attackTargetShouldSkipNearbyLookupWhenTargetIsInDifferentWorld() {
        // Given - an attack target in a different world than the attacking player.
        final LivingEntity target = mock(LivingEntity.class);
        final World otherWorld = mock(World.class);

        when(player.getWorld()).thenReturn(world);
        when(target.getWorld()).thenReturn(otherWorld);

        // When - taming attack assist processing runs.
        tamingManager.attackTarget(target);

        // Then - no nearby-entity lookup should run across worlds.
        verify(target, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
        verify(player, never()).getNearbyEntities(anyDouble(), anyDouble(), anyDouble());
    }
}
