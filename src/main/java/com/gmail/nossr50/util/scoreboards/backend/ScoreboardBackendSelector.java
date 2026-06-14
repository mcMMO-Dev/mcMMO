package com.gmail.nossr50.util.scoreboards.backend;

import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class ScoreboardBackendSelector {
    public static final int MAX_PACKET_MAJOR = 26;
    public static final int MAX_PACKET_MINOR = 1;
    public static final int MAX_PACKET_PATCH = 2;
    public static final String MAX_PACKET_VERSION_STRING = "26.1.2";

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
                MAX_PACKET_MINOR,
                MAX_PACKET_PATCH + 1);

        return versionWithinPacketSupport
                ? ScoreboardBackendType.PACKET
                : ScoreboardBackendType.NOOP;
    }
}
