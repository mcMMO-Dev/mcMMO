package com.gmail.nossr50.skills.mining;

import static java.util.logging.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.util.MetadataConstants;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.MetadataValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
}
