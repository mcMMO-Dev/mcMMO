package com.gmail.nossr50.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WorldBlacklistTest {

    @AfterEach
    void restoreDefaultBlacklistState() throws ReflectiveOperationException {
        setBlacklist(new ArrayList<>());
    }

    @Nested
    class IsWorldBlacklisted {

        @Test
        void returnsFalseWhenBlacklistFieldIsNull() throws ReflectiveOperationException {
            // Given
            setBlacklist(null);
            final World netherWorld = mock(World.class);
            when(netherWorld.getName()).thenReturn("world_nether");

            // When
            final boolean isBlacklisted = WorldBlacklist.isWorldBlacklisted(netherWorld);

            // Then
            assertFalse(isBlacklisted);
        }

        @Test
        void returnsFalseWhenWorldIsNull() {
            // Given a null world reference

            // When
            final boolean isBlacklisted = WorldBlacklist.isWorldBlacklisted(null);

            // Then
            assertFalse(isBlacklisted);
        }

        @Test
        void returnsTrueForCaseInsensitiveNameMatch() throws ReflectiveOperationException {
            // Given
            final ArrayList<String> configuredBlacklist = new ArrayList<>();
            configuredBlacklist.add("world_nether");
            setBlacklist(configuredBlacklist);
            final World netherWorldUppercase = mock(World.class);
            when(netherWorldUppercase.getName()).thenReturn("WORLD_NETHER");

            // When
            final boolean isBlacklisted = WorldBlacklist.isWorldBlacklisted(netherWorldUppercase);

            // Then
            assertTrue(isBlacklisted);
        }

        @Test
        void returnsFalseForNonMatchingWorldName() throws ReflectiveOperationException {
            // Given
            final ArrayList<String> configuredBlacklist = new ArrayList<>();
            configuredBlacklist.add("world_nether");
            setBlacklist(configuredBlacklist);
            final World overworld = mock(World.class);
            when(overworld.getName()).thenReturn("world");

            // When
            final boolean isBlacklisted = WorldBlacklist.isWorldBlacklisted(overworld);

            // Then
            assertFalse(isBlacklisted);
        }
    }

    private static void setBlacklist(ArrayList<String> replacementList)
            throws ReflectiveOperationException {
        final Field blacklistField = WorldBlacklist.class.getDeclaredField("blacklist");
        blacklistField.setAccessible(true);
        blacklistField.set(null, replacementList);
    }
}
