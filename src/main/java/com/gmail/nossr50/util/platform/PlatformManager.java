package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX In 2.2 we are switching
 * to modules and that will clean things up significantly
 */
public class PlatformManager {
    protected Platform platform; //current platform

    public PlatformManager() {
        init();
    }

    private void init() {
        platform = loadPlatform();
    }

    public Platform getPlatform() {
        return platform;
    }

    private @Nullable Platform loadPlatform() {
        PlatformBuilder platformBuilder = new PlatformBuilder();
        MinecraftGameVersion gameVersion = determineGameVersion(Bukkit.getBukkitVersion());

        return platformBuilder
                .setMinecraftGameVersion(gameVersion)
                .build();
    }

    //TODO: make this work on things other than bukkit
    @Deprecated //Only good for determining bukkit game versions
    private @NotNull MinecraftGameVersion determineGameVersion(String platformVersionString) {
        int major = 0, minor = 0, patch = 0;

        LogUtils.debug(mcMMO.p.getLogger(), "Platform String: " + platformVersionString);

        // Gets two numbers separated by . and optional third number after next dot. Must end with - or _
        Matcher versionMatch = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?[-_].*")
                .matcher(platformVersionString);

        if (versionMatch.find()) {
            major = Integer.parseInt(versionMatch.group(1));
            minor = Integer.parseInt(versionMatch.group(2));

            if (versionMatch.group(3) != null) {
                patch = Integer.parseInt(versionMatch.group(3));
            }
        }

        LogUtils.debug(mcMMO.p.getLogger(), "Minecraft version determined to be - "
                + major + "."
                + minor + "."
                + patch);

        return new MinecraftGameVersion(major, minor, patch);
    }

    public @Nullable CompatibilityManager getCompatibilityManager() {
        return platform.getCompatibilityManager();
    }
}
