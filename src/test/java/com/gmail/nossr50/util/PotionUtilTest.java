package com.gmail.nossr50.util;

import static com.gmail.nossr50.util.PotionUtil.convertLegacyNames;
import static com.gmail.nossr50.util.PotionUtil.matchPotionType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.platform.MinecraftGameVersion;
import java.util.stream.Stream;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

class PotionUtilTest {

    MockedStatic<mcMMO> mockedStaticMcMMO;

    @BeforeEach
    void setUp() {
        mockedStaticMcMMO = mockStatic(mcMMO.class);
        MinecraftGameVersion minecraftGameVersion = mock(MinecraftGameVersion.class);
        when(minecraftGameVersion.isAtLeast(1, 20, 5)).thenReturn(true);
        when(mcMMO.getMinecraftGameVersion()).thenReturn(minecraftGameVersion);
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

    /**
     * Guards the (isUpgraded, isExtended) parameter order: the treasure configs once passed
     * (extended, upgraded) and handed out the wrong potion variant. An extended-only request must
     * select the LONG_ variant, never the STRONG_ one.
     */
    @Test
    void matchPotionTypeShouldSelectLongVariantWhenExtendedOnly() {
        // Given - a potion requested as extended (longer duration) but not upgraded
        final String potionTypeStr = "SWIFTNESS";

        // When - matched with isUpgraded=false, isExtended=true
        final PotionType potionType = matchPotionType(potionTypeStr, false, true);

        // Then - the LONG_ (extended) variant is selected
        assertEquals(PotionType.LONG_SWIFTNESS, potionType);
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

    private static Stream<Arguments> legacyNameConversions() {
        return Stream.of(
                // legacy names map to their modern names
                Arguments.of("REGEN", "REGENERATION"),
                Arguments.of("SPEED", "SWIFTNESS"),
                Arguments.of("JUMP", "LEAPING"),
                Arguments.of("INSTANT_HEAL", "HEALING"),
                Arguments.of("INSTANT_DAMAGE", "HARMING"),
                Arguments.of("UNCRAFTABLE", "MUNDANE"),
                // strong/long prefixes are preserved while the base name is mapped
                Arguments.of("STRONG_REGEN", "STRONG_REGENERATION"),
                Arguments.of("LONG_REGEN", "LONG_REGENERATION"),
                // modern names pass through untouched
                Arguments.of("REGENERATION", "REGENERATION"),
                Arguments.of("LONG_REGENERATION", "LONG_REGENERATION"),
                Arguments.of("STRONG_SWIFTNESS", "STRONG_SWIFTNESS"),
                Arguments.of("HEALING", "HEALING"),
                Arguments.of("WATER", "WATER")
        );
    }

    /**
     * Regression coverage for legacy potion name conversion: substring replacement previously
     * corrupted modern names that contain a legacy name (e.g. REGENERATION contains REGEN and
     * became REGENERATIONERATION). Conversion must map exact tokens only.
     */
    @ParameterizedTest
    @MethodSource("legacyNameConversions")
    void convertLegacyNamesShouldMapExactTokensWithoutCorruptingModernNames(String input,
            String expected) {
        // Given - a potion type name as it may appear in a config file
        // When - legacy names are converted
        final String converted = convertLegacyNames(input);

        // Then - legacy tokens map to modern names and modern names pass through untouched
        assertThat(converted).isEqualTo(expected);
    }
}