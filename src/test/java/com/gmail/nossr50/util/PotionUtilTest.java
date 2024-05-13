package com.gmail.nossr50.util;

import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.Test;

import static com.gmail.nossr50.util.PotionUtil.convertLegacyNames;
import static com.gmail.nossr50.util.PotionUtil.matchPotionType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PotionUtilTest {

    @Test
    void testMatchPotionTypeRegen() {
        final String potionTypeStr = "REGEN";
        final PotionType potionType = matchPotionType(potionTypeStr, false, false);
        assertEquals(PotionType.REGENERATION, potionType);
    }

    @Test
    void testMatchPotionTypeUncraftable() {
        final String potionTypeStr = "UNCRAFTABLE";
        final PotionType potionType = matchPotionType(potionTypeStr, false, false);
        assertEquals(PotionType.MUNDANE, potionType);
    }

    @Test
    void testConvertLegacyNamesUncraftable() {
        final String potionTypeStr = "UNCRAFTABLE";
        final String converted = convertLegacyNames(potionTypeStr);
        assertEquals("MUNDANE", converted);
    }

    @Test
    void testConvertLegacyNamesRegen() {
        final String potionTypeStr = "REGEN";
        final String converted = convertLegacyNames(potionTypeStr);
        assertEquals("REGENERATION", converted);
    }
}