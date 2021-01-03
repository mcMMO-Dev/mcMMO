package com.gmail.nossr50.datatypes;

import org.bukkit.Location;
import org.junit.Assert;
import org.junit.Test;

public class BlockLocationHistoryTest {
    @Test
    public void testRemovesOldestElement() {
        BlockLocationHistory history = new BlockLocationHistory(2);
        Location locationA = new Location(null, 0, 1, 2);
        Location locationB = new Location(null, 1, 2, 3);
        Location locationC = new Location(null, 2, 3, 4);

        history.add(locationA);
        history.add(locationB);
        history.add(locationC);
        Assert.assertFalse(history.contains(locationA));
        Assert.assertTrue(history.contains(locationB));
        Assert.assertTrue(history.contains(locationC));
    }

    @Test
    public void testSupportsDuplicateElement() {
        BlockLocationHistory history = new BlockLocationHistory(2);
        Location locationA = new Location(null, 0, 1, 2);
        Location locationB = new Location(null, 1, 2, 3);

        history.add(locationA);
        history.add(locationA);
        history.add(locationB);
        Assert.assertTrue(history.contains(locationA));
        Assert.assertTrue(history.contains(locationB));
        history.add(locationB);
        Assert.assertFalse(history.contains(locationA));
    }
}
