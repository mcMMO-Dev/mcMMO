package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.util.compat.CompatibilityManager;
import org.jetbrains.annotations.NotNull;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public class BukkitPlatform extends AbstractPlatform {
    public BukkitPlatform(MinecraftGameVersion minecraftGameVersion) {
        super(minecraftGameVersion, ServerSoftwareType.CRAFT_BUKKIT,
                new CompatibilityManager(minecraftGameVersion));
    }

    @Override
    public @NotNull ServerSoftwareType getServerSoftwareType() {
        return super.serverSoftwareType;
    }

    @Override
    public @NotNull CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    @Override
    public @NotNull MinecraftGameVersion getGameVersion() {
        return super.minecraftGameVersion;
    }

}
