package com.gmail.nossr50.util.platform;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public class PlatformBuilder {
    private MinecraftGameVersion minecraftGameVersion;

    public PlatformBuilder() {

    }

    public PlatformBuilder setMinecraftGameVersion(
            @NotNull MinecraftGameVersion minecraftGameVersion) {
        this.minecraftGameVersion = minecraftGameVersion;
        return this;
    }

    public @Nullable Platform build() {
        return new BukkitPlatform(minecraftGameVersion);
    }

}
