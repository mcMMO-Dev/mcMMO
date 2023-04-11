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

    /**
     * Returns whether the Minecraft version is at least equal to or higher than a target version
     * @param majorVerNumber target major version number - for example 1.16.5 , the 1 is the major version
     * @param minorVerNumber target minor version number - for example 1.16.5, the 16 is the minor version
     * @param patchVerNumber target patch version number - for example 1.16.5, the 5 is the patch version number
     *
     * @return returns true if Minecraft is at least a certain version
     */
    public boolean isAtLeast(int majorVerNumber, int minorVerNumber, int patchVerNumber) {
        //First check if the major version is higher, if it is we have no need to check minor version or patch version

        if(getMajorVersion().asInt() > majorVerNumber) {
            return true; //Major version is one higher and hierarchically more important than the other versions
        }

        if(getMajorVersion().asInt() < majorVerNumber) {
            return false; //Major version is below, so return false
        }

        //Major version meets the requirement, check minor version

        if(getMinorVersion().asInt() > minorVerNumber) {
            return true; //Minor version is one higher and hierarchically more important than patch version, so exit here
        }

        if(getMinorVersion().asInt() < minorVerNumber) {
            return false; //Minor version is at least one version behind, return false
        }

        //Minor version meets the requirement, check patch version
        return getPatchVersion().asInt() >= patchVerNumber;
    }

}
