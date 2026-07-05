package com.gmail.nossr50.util.scoreboards.backend;

import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardBackendSelector {
    // Highest Minecraft release line verified against the bundled scoreboard-library version.
    // The whole patch line is allowed (e.g. 26.2.1) since patch releases do not change
    // scoreboard packets; scoreboard-library also probes unmarked versions at init and mcMMO
    // falls back to the no-op backend if that probe fails.
    public static final int MAX_PACKET_MAJOR = 26;
    public static final int MAX_PACKET_MINOR = 2;
    public static final String MAX_PACKET_VERSION_STRING = "26.2.x";

    private ScoreboardBackendSelector() {
    }

    public static @NotNull ScoreboardBackendType select(final boolean isFolia,
            final @NotNull MinecraftGameVersion version) {
        final MinecraftGameVersion gameVersion = Objects.requireNonNull(version,
                "version cannot be null");

        if (!isFolia) {
            return ScoreboardBackendType.BUKKIT;
        }

        final boolean versionWithinPacketSupport = !gameVersion.isAtLeast(
                MAX_PACKET_MAJOR,
                MAX_PACKET_MINOR + 1,
                0);

        return versionWithinPacketSupport
                ? ScoreboardBackendType.PACKET
                : ScoreboardBackendType.NOOP;
    }
}
