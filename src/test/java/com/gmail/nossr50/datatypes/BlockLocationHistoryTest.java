package com.gmail.nossr50.datatypes;

import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BlockLocationHistoryTest {
    @Test
    void testRemovesOldestElement() {
        BlockLocationHistory history = new BlockLocationHistory(2);
        Location locationA = new Location(null, 0, 1, 2);
        Location locationB = new Location(null, 1, 2, 3);
        Location locationC = new Location(null, 2, 3, 4);

        history.add(locationA);
        history.add(locationB);
        history.add(locationC);
        Assertions.assertFalse(history.contains(locationA));
        Assertions.assertTrue(history.contains(locationB));
        Assertions.assertTrue(history.contains(locationC));
    }

    @Test
    void testSupportsDuplicateElement() {
        BlockLocationHistory history = new BlockLocationHistory(2);
        Location locationA = new Location(null, 0, 1, 2);
        Location locationB = new Location(null, 1, 2, 3);

        history.add(locationA);
        history.add(locationA);
        history.add(locationB);
        Assertions.assertTrue(history.contains(locationA));
        Assertions.assertTrue(history.contains(locationB));
        history.add(locationB);
        Assertions.assertFalse(history.contains(locationA));
    }
}
