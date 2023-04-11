package com.gmail.nossr50.util.platform;

import com.gmail.nossr50.util.platform.version.SimpleNumericVersion;
import com.gmail.nossr50.util.platform.version.Versioned;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * These classes are a band-aid solution for adding NMS support into 2.1.XXX
 * In 2.2 we are switching to modules and that will clean things up significantly
 *
 */
public abstract class MajorMinorPatchVersion implements Versioned {

    @NotNull
    private final SimpleNumericVersion majorVersion;
    @NotNull
    private final SimpleNumericVersion minorVersion;
    @NotNull
    private final SimpleNumericVersion patchVersion;

    public MajorMinorPatchVersion(@NotNull SimpleNumericVersion majorVersion, @NotNull SimpleNumericVersion minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = new SimpleNumericVersion(0);
    }

    public MajorMinorPatchVersion(@NotNull SimpleNumericVersion majorVersion, @NotNull SimpleNumericVersion minorVersion, @Nullable SimpleNumericVersion patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;

        this.patchVersion = Objects.requireNonNullElseGet(patchVersion, () -> new SimpleNumericVersion(0));
    }

    public MajorMinorPatchVersion(int majorVerNumber, int minorVerNumber, int patchVerNumber) {
        this.majorVersion = new SimpleNumericVersion(majorVerNumber);
        this.minorVersion = new SimpleNumericVersion(minorVerNumber);
        this.patchVersion = new SimpleNumericVersion(patchVerNumber);
    }

    public MajorMinorPatchVersion(int majorVerNumber, int minorVerNumber) {
        this.majorVersion = new SimpleNumericVersion(majorVerNumber);
        this.minorVersion = new SimpleNumericVersion(minorVerNumber);
        this.patchVersion = new SimpleNumericVersion(0);
    }

    /**
     * Get the major version string
     * @return the major version string
     */
    public @NotNull SimpleNumericVersion getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the minor version string
     * @return the minor version string
     */
    public @NotNull SimpleNumericVersion getMinorVersion() {
        return minorVersion;
    }

    /**
     * Get the patch version string
     * @return patch version string or null if patch numeric value is less than or equal to 0
     */
    public @NotNull SimpleNumericVersion getPatchVersion() {
        return patchVersion;
    }

    @Override
    public String getVersionStr() {
        if(isPatch()) {
            return majorVersion.getVersionString() + "."
                    + minorVersion + "."
                    + patchVersion;
        } else {
            return majorVersion.getVersionString() + "."
                    + minorVersion.getVersionString();
        }
    }

    /**
     * Whether this version of Minecraft is a patch
     * a patch version value above 0 will indicate that this is a patch
     * @return true if this version is a patch
     */
    public boolean isPatch() {
        return patchVersion.asInt() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorMinorPatchVersion that = (MajorMinorPatchVersion) o;
        return majorVersion.equals(that.majorVersion) &&
                minorVersion.equals(that.minorVersion) &&
                patchVersion.equals(that.patchVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion);
    }
}
