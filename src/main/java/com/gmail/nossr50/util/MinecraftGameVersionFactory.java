package com.gmail.nossr50.util;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class MinecraftGameVersionFactory {
    // Primary: matches the "(MC: X.Y.Z)" segment that CraftBukkit always embeds in
    // Bukkit.getVersion() output, regardless of server software (Spigot, Paper, Folia, etc.)
    // e.g. "git-Spigot-12345-abcdef (MC: 1.21.4)" or "26.1.2-60-b4682bf (MC: 26.1.2)"
    private static final Pattern MC_VERSION_PATTERN =
            Pattern.compile("\\(MC: (\\d+)\\.(\\d+)(?:\\.(\\d+))?\\)");

    // Fallback: matches getBukkitVersion()-style strings where the MC version leads
    // e.g. "1.21.11-R0.1-SNAPSHOT" or "1.21.11-106-0d768aa"
    private static final Pattern BUKKIT_VERSION_PATTERN =
            Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?[-_].*");

    public static @NotNull MinecraftGameVersion calculateGameVersion(
            @NotNull String platformVersionString) {
        int major = 0, minor = 0, patch = 0;

        LogUtils.debug(mcMMO.p.getLogger(), "Platform String: " + platformVersionString);

        // Primary: extract from "(MC: X.Y.Z)" — reliable for all Bukkit.getVersion() outputs
        final Matcher mcVersionMatch = MC_VERSION_PATTERN.matcher(platformVersionString);

        if (mcVersionMatch.find()) {
            major = Integer.parseInt(mcVersionMatch.group(1));
            minor = Integer.parseInt(mcVersionMatch.group(2));
            if (mcVersionMatch.group(3) != null) {
                patch = Integer.parseInt(mcVersionMatch.group(3));
            }
        } else {
            // Fallback: handle strings without "(MC: ...)" such as getBukkitVersion() format
            final Matcher builtinVersionMatch = BUKKIT_VERSION_PATTERN.matcher(platformVersionString);
            if (builtinVersionMatch.find()) {
                major = Integer.parseInt(builtinVersionMatch.group(1));
                minor = Integer.parseInt(builtinVersionMatch.group(2));
                if (builtinVersionMatch.group(3) != null) {
                    patch = Integer.parseInt(builtinVersionMatch.group(3));
                }
            }
        }

        LogUtils.debug(mcMMO.p.getLogger(), "Minecraft version determined to be - "
                + major + "."
                + minor + "."
                + patch);

        return new MinecraftGameVersion(major, minor, patch);
    }
}
