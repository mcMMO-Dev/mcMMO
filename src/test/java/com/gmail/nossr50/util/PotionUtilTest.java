package com.gmail.nossr50.util;

import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.Test;

import static com.gmail.nossr50.util.PotionUtil.convertLegacyNames;
import static com.gmail.nossr50.util.PotionUtil.matchPotionType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PotionUtilTest {

    @Test
    void testDisplay() {
//        System.out.println("\n");
//        System.out.println("\n");
//        System.out.println("\n");
//        System.out.println("\n");
//        for(var s : PotionType.values()) {
//            System.out.println("PotionType.getKey().getKey(): " + s.getKey().getKey());
//            System.out.println("PotionType.name(): " + s.name());
//            System.out.println("PotionType.toString():" + s.toString());
//            System.out.println("\n");
//        }
    }

    @Test
    void testMatchPotionType() {
        String potionTypeStr = "UNCRAFTABLE";
        PotionType potionType = matchPotionType(potionTypeStr, false, false);
        assertEquals(PotionType.WATER, potionType);

        String potionTypeStr2 = "NIGHT_VISION";
        PotionType potionType2 = matchPotionType(potionTypeStr2, false, false);
        assertEquals(PotionType.NIGHT_VISION, potionType2);

        String nightVisionLong = "NIGHT_VISION";
        PotionType potionType3 = matchPotionType(nightVisionLong, false, true);
        assertEquals(PotionType.LONG_NIGHT_VISION, potionType3);

        nightVisionLong = "LONG_NIGHT_VISION";
        potionType3 = matchPotionType(nightVisionLong, false, true);
        assertEquals(PotionType.LONG_NIGHT_VISION, potionType3);
    }

    @Test
    void testConvertLegacyNames() {
        final String potionTypeStr = "UNCRAFTABLE";
        final String converted = convertLegacyNames(potionTypeStr);
        assertEquals("WATER", converted);
    }
}