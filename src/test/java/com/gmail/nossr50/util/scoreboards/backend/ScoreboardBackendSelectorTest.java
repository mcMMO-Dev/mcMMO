package com.gmail.nossr50.util.scoreboards.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import org.junit.jupiter.api.Test;

class ScoreboardBackendSelectorTest {
    @Test
    void selectShouldReturnBukkitWhenServerIsNotFolia() {
        // Given - A non-Folia server with any version
        final MinecraftGameVersion gameVersion = new MinecraftGameVersion(99, 9, 9);

        // When - Selecting a backend
        final ScoreboardBackendType backendType = ScoreboardBackendSelector.select(false, gameVersion);

        // Then - Bukkit backend is always selected
        assertThat(backendType).isEqualTo(ScoreboardBackendType.BUKKIT);
    }

    @Test
    void selectShouldReturnPacketWhenFoliaAndVersionAtOrBelowSupportedCap() {
        // Given - Folia with a version at the supported cap
        final MinecraftGameVersion gameVersion = new MinecraftGameVersion(26, 1, 2);

        // When - Selecting a backend
        final ScoreboardBackendType backendType = ScoreboardBackendSelector.select(true, gameVersion);

        // Then - Packet backend is selected
        assertThat(backendType).isEqualTo(ScoreboardBackendType.PACKET);
    }

    @Test
    void selectShouldReturnNoopWhenFoliaAndVersionAboveSupportedCap() {
        // Given - Folia with an unsupported newer version
        final MinecraftGameVersion gameVersion = new MinecraftGameVersion(26, 1, 3);

        // When - Selecting a backend
        final ScoreboardBackendType backendType = ScoreboardBackendSelector.select(true, gameVersion);

        // Then - Noop backend is selected
        assertThat(backendType).isEqualTo(ScoreboardBackendType.NOOP);
    }

    @Test
    void selectShouldNeverReturnBukkitWhenServerIsFolia() {
        // Given - Multiple Folia versions across supported and unsupported ranges
        final MinecraftGameVersion oldVersion = new MinecraftGameVersion(1, 20, 5);
        final MinecraftGameVersion supportedCap = new MinecraftGameVersion(26, 1, 2);
        final MinecraftGameVersion unsupportedVersion = new MinecraftGameVersion(27, 0, 0);

        // When - Selecting backends
        final ScoreboardBackendType oldResult = ScoreboardBackendSelector.select(true, oldVersion);
        final ScoreboardBackendType capResult = ScoreboardBackendSelector.select(true, supportedCap);
        final ScoreboardBackendType unsupportedResult = ScoreboardBackendSelector.select(true,
                unsupportedVersion);

        // Then - Folia never selects Bukkit
        assertThat(oldResult).isIn(ScoreboardBackendType.PACKET, ScoreboardBackendType.NOOP);
        assertThat(capResult).isIn(ScoreboardBackendType.PACKET, ScoreboardBackendType.NOOP);
        assertThat(unsupportedResult).isIn(ScoreboardBackendType.PACKET, ScoreboardBackendType.NOOP);
    }
}
