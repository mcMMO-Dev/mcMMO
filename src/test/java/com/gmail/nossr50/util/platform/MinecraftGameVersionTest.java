package com.gmail.nossr50.util.platform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinecraftGameVersionTest {

    @Test
    public void testAtLeast() {
        //TODO: Remove redundant tests
        MinecraftGameVersion oneEightEight = new MinecraftGameVersion(1, 8, 8);
        MinecraftGameVersion oneSixteenFive = new MinecraftGameVersion(1, 16, 5);
        MinecraftGameVersion oneTwo = new MinecraftGameVersion(1, 2);

        //1.8.8
        assertTrue(oneEightEight.isAtLeast(1, 8, 7));
        assertFalse(oneEightEight.isAtLeast(1, 9, 0));

        //1.16.5
        assertTrue(oneSixteenFive.isAtLeast(1, 15, 2));
        assertFalse(oneSixteenFive.isAtLeast(1, 17, 0));

        //1.2
        assertTrue(oneTwo.isAtLeast(1, 2, 0));

        //Test major version number
        MinecraftGameVersion majorVersionTest = new MinecraftGameVersion(2, 0, 0);

        assertFalse(majorVersionTest.isAtLeast(3, 0, 0));
        assertFalse(majorVersionTest.isAtLeast(3, 1, 0));
        assertFalse(majorVersionTest.isAtLeast(3, 0, 2));

        assertTrue(majorVersionTest.isAtLeast(2, 0, 0));
        assertTrue(majorVersionTest.isAtLeast(1, 0, 0));


        //Test minor version number
        MinecraftGameVersion minorVersionTest = new MinecraftGameVersion(0, 3, 0);

        assertFalse(minorVersionTest.isAtLeast(0, 4, 0));
        assertFalse(minorVersionTest.isAtLeast(1, 4, 0));
        assertFalse(minorVersionTest.isAtLeast(0, 4, 1));

        assertTrue(minorVersionTest.isAtLeast(0, 1, 0));
        assertTrue(minorVersionTest.isAtLeast(0, 2, 0));
        assertTrue(minorVersionTest.isAtLeast(0, 2, 1));
        assertTrue(minorVersionTest.isAtLeast(0, 3, 0));

        //Test patch version number

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
}