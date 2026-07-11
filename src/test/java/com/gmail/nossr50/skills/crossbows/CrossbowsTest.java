package com.gmail.nossr50.skills.crossbows;

import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the crossbow hit routing: only player-shot arrows that hit a block are handed to the
 * Trick Shot ricochet, along with the normal of the face they hit.
 */
class CrossbowsTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(CrossbowsTest.class.getName());

    private CrossbowsManager crossbowsManager;
    private Arrow arrow;

    @BeforeEach
    void setUp() {
        mockBaseEnvironment(logger);

        crossbowsManager = mock(CrossbowsManager.class);
        doReturn(crossbowsManager).when(mmoPlayer).getCrossbowsManager();

        arrow = mock(Arrow.class);
        when(arrow.getShooter()).thenReturn(player);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void blockHitsShouldRicochetWithTheHitFaceNormal() {
        // Given - a player-shot arrow landing on the top face of a block
        final ProjectileHitEvent event = new ProjectileHitEvent(arrow, null, mock(Block.class),
                BlockFace.UP);

        // When - the crossbow hit is processed
        Crossbows.processCrossbows(event, mcMMO.p, arrow);

        // Then - the ricochet runs against the upward face normal
        verify(crossbowsManager).handleRicochet(mcMMO.p, arrow, new Vector(0, 1, 0));
    }

    @Test
    void entityHitsShouldNotRicochet() {
        // Given - a player-shot arrow hitting a mob instead of a block
        final ProjectileHitEvent event =
                new ProjectileHitEvent(arrow, mock(LivingEntity.class), null, null);

        // When - the crossbow hit is processed
        Crossbows.processCrossbows(event, mcMMO.p, arrow);

        // Then - there is nothing to ricochet off
        verify(crossbowsManager, never()).handleRicochet(any(Plugin.class), any(Arrow.class),
                any(Vector.class));
    }

    @Test
    void nonPlayerShootersShouldBeIgnored() {
        // Given - a skeleton-shot arrow landing on a block
        when(arrow.getShooter()).thenReturn(mock(ProjectileSource.class));
        final ProjectileHitEvent event = new ProjectileHitEvent(arrow, null, mock(Block.class),
                BlockFace.UP);

        // When - the crossbow hit is processed
        Crossbows.processCrossbows(event, mcMMO.p, arrow);

        // Then - no ricochet runs
        verify(crossbowsManager, never()).handleRicochet(any(Plugin.class), any(Arrow.class),
                any(Vector.class));
    }

    @Test
    void playersWithoutLoadedDataShouldBeIgnored() {
        // Given - a player mcMMO has no loaded data for
        when(UserManager.getPlayer(player)).thenReturn(null);
        final ProjectileHitEvent event = new ProjectileHitEvent(arrow, null, mock(Block.class),
                BlockFace.UP);

        // When - the crossbow hit is processed
        Crossbows.processCrossbows(event, mcMMO.p, arrow);

        // Then - no ricochet runs
        verify(crossbowsManager, never()).handleRicochet(any(Plugin.class), any(Arrow.class),
                any(Vector.class));
    }
}
