package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.util.platform.version.SimpleNumericVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX
 * In 2.2 we are switching to modules and that will clean things up significantly
 *
 */
public class MinecraftGameVersion extends MajorMinorPatchVersion {
    public MinecraftGameVersion(@NotNull SimpleNumericVersion majorVersion, @NotNull SimpleNumericVersion minorVersion) {
        super(majorVersion, minorVersion);
    }

    public MinecraftGameVersion(@NotNull SimpleNumericVersion majorVersion, @NotNull SimpleNumericVersion minorVersion, @Nullable SimpleNumericVersion patchVersion) {
        super(majorVersion, minorVersion, patchVersion);
    }

    public MinecraftGameVersion(int majorVerNumber, int minorVerNumber, int patchVerNumber) {
        super(majorVerNumber, minorVerNumber, patchVerNumber);
    }

    public MinecraftGameVersion(int majorVerNumber, int minorVerNumber) {
        super(majorVerNumber, minorVerNumber);
    }

}
