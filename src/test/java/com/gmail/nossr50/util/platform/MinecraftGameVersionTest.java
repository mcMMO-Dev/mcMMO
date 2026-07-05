package com.gmail.nossr50.util.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.MinecraftGameVersionFactory;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class MinecraftGameVersionTest {

    @Test
    void testAtLeast() {
        // TODO: Remove redundant tests
        MinecraftGameVersion oneEightEight = new MinecraftGameVersion(1, 8, 8);
        MinecraftGameVersion oneSixteenFive = new MinecraftGameVersion(1, 16, 5);
        MinecraftGameVersion oneTwo = new MinecraftGameVersion(1, 2);

        // 1.8.8
        assertTrue(oneEightEight.isAtLeast(1, 8, 7));
        assertFalse(oneEightEight.isAtLeast(1, 9, 0));

        // 1.16.5
        assertTrue(oneSixteenFive.isAtLeast(1, 15, 2));
        assertFalse(oneSixteenFive.isAtLeast(1, 17, 0));

        // 1.2
        assertTrue(oneTwo.isAtLeast(1, 2, 0));

        // Test major version number
        MinecraftGameVersion majorVersionTest = new MinecraftGameVersion(2, 0, 0);

        assertFalse(majorVersionTest.isAtLeast(3, 0, 0));
        assertFalse(majorVersionTest.isAtLeast(3, 1, 0));
        assertFalse(majorVersionTest.isAtLeast(3, 0, 2));

        assertTrue(majorVersionTest.isAtLeast(2, 0, 0));
        assertTrue(majorVersionTest.isAtLeast(1, 0, 0));

        // Test minor version number
        MinecraftGameVersion minorVersionTest = new MinecraftGameVersion(0, 3, 0);

        assertFalse(minorVersionTest.isAtLeast(0, 4, 0));
        assertFalse(minorVersionTest.isAtLeast(1, 4, 0));
        assertFalse(minorVersionTest.isAtLeast(0, 4, 1));

        assertTrue(minorVersionTest.isAtLeast(0, 1, 0));
        assertTrue(minorVersionTest.isAtLeast(0, 2, 0));
        assertTrue(minorVersionTest.isAtLeast(0, 2, 1));
        assertTrue(minorVersionTest.isAtLeast(0, 3, 0));

        // Test patch version number

        MinecraftGameVersion patchVersionTest = new MinecraftGameVersion(0, 0, 5);

        assertFalse(patchVersionTest.isAtLeast(1, 0, 0));
        assertFalse(patchVersionTest.isAtLeast(0, 0, 6));
        assertFalse(patchVersionTest.isAtLeast(0, 1, 4));
        assertFalse(patchVersionTest.isAtLeast(1, 1, 4));

        assertTrue(patchVersionTest.isAtLeast(0, 0, 1));
        assertTrue(patchVersionTest.isAtLeast(0, 0, 2));
        assertTrue(patchVersionTest.isAtLeast(0, 0, 3));
        assertTrue(patchVersionTest.isAtLeast(0, 0, 4));
        assertTrue(patchVersionTest.isAtLeast(0, 0, 5));
    }

    @Nested
    class VersionDetectionFromGetVersion {
        // Tests calculateGameVersion() with Bukkit.getVersion() format strings — the actual
        // input mcMMO.java passes at runtime. CraftBukkit always embeds "(MC: X.Y.Z)" in the
        // string, so the primary pattern must handle all server software variants:
        //   Spigot:     "git-Spigot-12345-abcdef (MC: 1.21.4)"
        //   Paper old:  "1.21.11-106-0d768aa (MC: 1.21.11)"
        //   Paper 26+:  "26.1.2-60-b4682bf (MC: 26.1.2)"
        @ParameterizedTest(name = "Verify that \"{0}\" is recognized as {1}.{2}.{3}")
        @MethodSource("getVersionStrings")
        void detectsVersionCorrectly(String getVersionString, int expectedMajor, int expectedMinor, int expectedPatch) {
            // Given
            final mcMMO mockPlugin = Mockito.mock(mcMMO.class);
            Mockito.when(mockPlugin.getName()).thenReturn("mcMMO");
            Mockito.when(mockPlugin.getLogger()).thenReturn(Logger.getLogger("mcMMO"));
            mcMMO.p = mockPlugin;
            try {
                // When
                final MinecraftGameVersion detectedVersion =
                        MinecraftGameVersionFactory.calculateGameVersion(getVersionString);

                // Then
                assertEquals(expectedMajor, detectedVersion.getMajorVersion().asInt());
                assertEquals(expectedMinor, detectedVersion.getMinorVersion().asInt());
                assertEquals(expectedPatch, detectedVersion.getPatchVersion().asInt());
            } finally {
                mcMMO.p = null;
            }
        }

        private static @NotNull Stream<Arguments> getVersionStrings() {
            return Stream.of(
                    // Spigot: "git-Spigot-<hash>-<hash> (MC: X.Y.Z)"
                    // The prefix never starts with digits, so the old regex matched nothing → 0.0.0
                    Arguments.of("git-Spigot-12345-abcdef (MC: 1.13.2)", 1, 13, 2),
                    Arguments.of("git-Spigot-12345-abcdef (MC: 1.17)", 1, 17, 0),
                    Arguments.of("git-Spigot-12345-abcdef (MC: 1.21.4)", 1, 21, 4),
                    // Paper (old versioning): "X.Y.Z-build-hash (MC: X.Y.Z)"
                    Arguments.of("1.21.11-106-0d768aa (MC: 1.21.11)", 1, 21, 11),
                    // Paper (new versioning 26+): "26.X.Y-build-hash (MC: 26.X.Y)"
                    Arguments.of("26.1.2-60-b4682bf (MC: 26.1.2)", 26, 1, 2)
            );
        }
    }

    @Nested
    class VersionDetectionFallback {
        // Tests the fallback regex path for strings that lack "(MC: X.Y.Z)".
        // This covers getBukkitVersion()-style strings and any legacy format.
        @ParameterizedTest(name = "Fallback: verify that \"{0}\" is recognized as {1}.{2}.{3}")
        @MethodSource("getBukkitVersionStrings")
        void detectsVersionFromFallbackFormat(String versionString, int expectedMajor, int expectedMinor, int expectedPatch) {
            // Given
            final mcMMO mockPlugin = Mockito.mock(mcMMO.class);
            Mockito.when(mockPlugin.getName()).thenReturn("mcMMO");
            Mockito.when(mockPlugin.getLogger()).thenReturn(Logger.getLogger("mcMMO"));
            mcMMO.p = mockPlugin;
            try {
                // When
                final MinecraftGameVersion detectedVersion =
                        MinecraftGameVersionFactory.calculateGameVersion(versionString);

                // Then
                assertEquals(expectedMajor, detectedVersion.getMajorVersion().asInt());
                assertEquals(expectedMinor, detectedVersion.getMinorVersion().asInt());
                assertEquals(expectedPatch, detectedVersion.getPatchVersion().asInt());
            } finally {
                mcMMO.p = null;
            }
        }

        private static @NotNull Stream<Arguments> getBukkitVersionStrings() {
            // Samples taken from CraftBukkit's historical pom.xml:
            // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/pom.xml
            return Stream.of(
                    Arguments.of("1.13.2-R0.1-SNAPSHOT", 1, 13, 2),
                    Arguments.of("1.13-R0.2-SNAPSHOT", 1, 13, 0),
                    Arguments.of("1.13-pre7-R0.1-SNAPSHOT", 1, 13, 0),
                    Arguments.of("1.14-pre5-SNAPSHOT", 1, 14, 0),
                    Arguments.of("1.15-R0.1-SNAPSHOT", 1, 15, 0),
                    Arguments.of("1.16.5-R0.1-SNAPSHOT", 1, 16, 5),
                    Arguments.of("1.17-R0.1-SNAPSHOT", 1, 17, 0),
                    Arguments.of("1.21.11-106-0d768aa", 1, 21, 11)
            );
        }
    }

}
