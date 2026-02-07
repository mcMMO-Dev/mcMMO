package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class MinecraftGameVersionFactory {
    public static @NotNull MinecraftGameVersion calculateGameVersion(
            @NotNull String platformVersionString) {
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
}
