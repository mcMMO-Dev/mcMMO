package com.gmail.nossr50.util;

import static com.gmail.nossr50.util.PotionUtil.convertLegacyNames;
import static com.gmail.nossr50.util.PotionUtil.matchPotionType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.CompatibilityManager;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class PotionUtilTest {

    MockedStatic<mcMMO> mockedStaticMcMMO;

    @BeforeEach
    void setUp() {
        mockedStaticMcMMO = mockStatic(mcMMO.class);
        CompatibilityManager compatibilityManager = mock(CompatibilityManager.class);
        MinecraftGameVersion minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(compatibilityManager.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);
        when(minecraftGameVersion.isAtLeast(1, 20, 5)).thenReturn(true);
        when(mcMMO.getCompatibilityManager()).thenReturn(compatibilityManager);
    }

    @AfterEach
    void tearDown() {
        mockedStaticMcMMO.close();
    }

    @Test
    void testMatchPotionTypeStrengthII() {
        final String potionTypeStr = "STRENGTH";
        final PotionType potionType = matchPotionType(potionTypeStr, true, false);
        assertEquals(PotionType.STRONG_STRENGTH, potionType);
    }

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