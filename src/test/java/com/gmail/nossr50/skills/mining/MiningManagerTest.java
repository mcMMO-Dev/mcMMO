package com.gmail.nossr50.skills.mining;

import static java.util.logging.Logger.getLogger;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MiningManagerTest extends MMOTestEnvironment {
    private static final Logger logger = getLogger(MiningManagerTest.class.getName());

    private MiningManager miningManager;

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
        miningManager = new MiningManager(mmoPlayer);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * The remote detonation TNT search must honor the distance configured in advanced.yml
     * instead of a hardcoded maximum, so admins can limit how far away players may detonate.
     */
    @ParameterizedTest(name = "configuredDistance={0}")
    @ValueSource(ints = {1, 5, 30, 100})
    void remoteDetonationShouldScanForTntUpToConfiguredDistance(final int configuredDistance) {
        // Given - an admin configured a maximum remote detonation distance
        when(advancedConfig.getRemoteDetonationDistanceLimit()).thenReturn(configuredDistance);

        // And - the player is aiming at a non-TNT block somewhere within scan range
        final Block targetBlock = mock(Block.class);
        when(targetBlock.getType()).thenReturn(Material.STONE);
        when(player.getTargetBlock(anySet(), anyInt())).thenReturn(targetBlock);

        // When - the player attempts a remote detonation
        miningManager.remoteDetonation();

        // Then - the TNT target search is limited to the configured distance
        verify(player).getTargetBlock(anySet(), eq(configuredDistance));
    }
}
