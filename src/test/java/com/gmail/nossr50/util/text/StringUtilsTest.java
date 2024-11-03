package com.gmail.nossr50.util.text;

import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @BeforeEach
    void setUp() {
        // Clear caches before each test to ensure test isolation
        clearCaches();
    }

    /**
     * Utility method to clear all caches in StringUtils.
     * Reflection is used since the caches are private.
     */
    private void clearCaches() {
        try {
            java.lang.reflect.Field entityCache = StringUtils.class.getDeclaredField("formattedEntityStrings");
            entityCache.setAccessible(true);
            ((java.util.Map<?, ?>) entityCache.get(null)).clear();

            java.lang.reflect.Field superAbilityCache = StringUtils.class.getDeclaredField("formattedSuperAbilityStrings");
            superAbilityCache.setAccessible(true);
            ((java.util.Map<?, ?>) superAbilityCache.get(null)).clear();

            java.lang.reflect.Field materialCache = StringUtils.class.getDeclaredField("formattedMaterialStrings");
            materialCache.setAccessible(true);
            ((java.util.Map<?, ?>) materialCache.get(null)).clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to clear caches: " + e.getMessage());
        }
    }

    // Tests for getCapitalized(String target)
    @Test
    void testGetCapitalized_NullInput() {
        assertNull(StringUtils.getCapitalized(null));
    }

    @Test
    void testGetCapitalized_EmptyString() {
        assertEquals("", StringUtils.getCapitalized(""));
    }

    @Test
    void testGetCapitalized_SingleCharacter() {
        assertEquals("A", StringUtils.getCapitalized("a"));
        assertEquals("Z", StringUtils.getCapitalized("Z"));
    }

    @Test
    void testGetCapitalized_AllUppercase() {
        assertEquals("Test", StringUtils.getCapitalized("TEST"));
    }

    @Test
    void testGetCapitalized_AllLowercase() {
        assertEquals("Test", StringUtils.getCapitalized("test"));
    }

    @Test
    void testGetCapitalized_MixedCase() {
        assertEquals("Test", StringUtils.getCapitalized("tEsT"));
    }

    @Test
    void testGetCapitalized_NonASCII() {
        assertEquals("Ñandú", StringUtils.getCapitalized("ñandú"));
    }

    // Tests for ticksToSeconds(double ticks)
    @Test
    void testTicksToSeconds_PositiveTicks() {
        assertEquals("1.5", StringUtils.ticksToSeconds(30));
    }

    @Test
    void testTicksToSeconds_ZeroTicks() {
        assertEquals("0.0", StringUtils.ticksToSeconds(0));
    }

    @Test
    void testTicksToSeconds_FractionalTicks() {
        assertEquals("1.5", StringUtils.ticksToSeconds(30.0));
        assertEquals("1.5", StringUtils.ticksToSeconds(30.0));
        assertEquals("1.0", StringUtils.ticksToSeconds(20.0));
        assertEquals("0.1", StringUtils.ticksToSeconds(2.0));
    }

    @Test
    void testTicksToSeconds_NegativeTicks() {
        assertEquals("-1.0", StringUtils.ticksToSeconds(-20));
    }

    // Tests for getPrettySuperAbilityString(SuperAbilityType superAbilityType)
    @Test
    void testGetPrettySuperAbilityString_NullInput() {
        assertThrows(NullPointerException.class, () -> {
            StringUtils.getPrettySuperAbilityString(null);
        });
    }

    @Test
    void testGetPrettySuperAbilityString_ValidInput() {
        SuperAbilityType superAbilityType = SuperAbilityType.SUPER_BREAKER;
        String expected = "Super Breaker";
        String actual = StringUtils.getPrettySuperAbilityString(superAbilityType);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrettySuperAbilityString_Caching() {
        SuperAbilityType superAbilityType = SuperAbilityType.SUPER_BREAKER;

        // First call should compute and cache
        String firstCall = StringUtils.getPrettySuperAbilityString(superAbilityType);

        // Second call should retrieve from cache
        String secondCall = StringUtils.getPrettySuperAbilityString(superAbilityType);

        assertSame(firstCall, secondCall, "Cached value should be the same instance");
    }

    // Tests for getPrettyEntityTypeString(EntityType entityType)
    @Test
    void testGetPrettyEntityTypeString_ValidInput() {
        EntityType zombie = EntityType.ZOMBIE;
        String expected = "Zombie";
        String actual = StringUtils.getPrettyEntityTypeString(zombie);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrettyEntityTypeString_WithUnderscores() {
        EntityType entity = EntityType.SKELETON_HORSE;
        String expected = "Skeleton Horse";
        String actual = StringUtils.getPrettyEntityTypeString(entity);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrettyEntityTypeString_Caching() {
        EntityType skeleton = EntityType.SKELETON;

        // First call should compute and cache
        String firstCall = StringUtils.getPrettyEntityTypeString(skeleton);

        // Second call should retrieve from cache
        String secondCall = StringUtils.getPrettyEntityTypeString(skeleton);

        assertSame(firstCall, secondCall, "Cached value should be the same instance");
    }

    // Tests for getFormattedMaterialString(Material material)
    @Test
    void testGetPrettyMaterialString_ValidInput() {
        Material diamondSword = Material.DIAMOND_SWORD;
        String expected = "Diamond Sword";
        String actual = StringUtils.getPrettyMaterialString(diamondSword);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrettyMaterialString_WithUnderscores() {
        Material goldenApple = Material.GOLDEN_APPLE;
        String expected = "Golden Apple";
        String actual = StringUtils.getPrettyMaterialString(goldenApple);
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrettyMaterialString_Caching() {
        Material ironPickaxe = Material.IRON_PICKAXE;

        // First call should compute and cache
        String firstCall = StringUtils.getPrettyMaterialString(ironPickaxe);

        // Second call should retrieve from cache
        String secondCall = StringUtils.getPrettyMaterialString(ironPickaxe);

        assertSame(firstCall, secondCall, "Cached value should be the same instance");
    }

    // Tests for buildStringAfterNthElement(String[] args, int index)
    @Test
    void testBuildStringAfterNthElement_IndexZero() {
        String[] args = {"Hello", "World", "Test"};
        String expected = "Hello World Test";
        String actual = StringUtils.buildStringAfterNthElement(args, 0);
        assertEquals(expected, actual);
    }

    @Test
    void testBuildStringAfterNthElement_IndexMiddle() {
        String[] args = {"This", "is", "a", "test"};
        String expected = "a test";
        String actual = StringUtils.buildStringAfterNthElement(args, 2);
        assertEquals(expected, actual);
    }

    @Test
    void testBuildStringAfterNthElement_IndexLast() {
        String[] args = {"Only", "One"};
        String expected = "One";
        String actual = StringUtils.buildStringAfterNthElement(args, 1);
        assertEquals(expected, actual);
    }

    @Test
    void testBuildStringAfterNthElement_IndexOutOfBounds() {
        String[] args = {"Too", "Short"};
        String expected = "";
        String actual = StringUtils.buildStringAfterNthElement(args, 5);
        assertEquals(expected, actual);
    }

    @Test
    void testBuildStringAfterNthElement_EmptyArray() {
        String[] args = {};
        String expected = "";
        String actual = StringUtils.buildStringAfterNthElement(args, 0);
        assertEquals(expected, actual);
    }

    @Test
    void testBuildStringAfterNthElement_ArgsWithSpaces() {
        String[] args = {"Multiple", " ", "Spaces"};
        String expected = "  Spaces";
        String actual = StringUtils.buildStringAfterNthElement(args, 1);
        assertEquals(expected, actual);
    }

    // Tests for isInt(String string)
    @Test
    void testIsInt_ValidIntegers() {
        assertTrue(StringUtils.isInt("123"));
        assertTrue(StringUtils.isInt("-456"));
        assertTrue(StringUtils.isInt("0"));
    }

    @Test
    void testIsInt_InvalidIntegers() {
        assertFalse(StringUtils.isInt("123.45"));
        assertFalse(StringUtils.isInt("abc"));
        assertFalse(StringUtils.isInt(""));
        assertFalse(StringUtils.isInt(" "));
        assertFalse(StringUtils.isInt(null)); // This will throw NullPointerException
    }

    // Tests for isDouble(String string)
    @Test
    void testIsDouble_ValidDoubles() {
        assertTrue(StringUtils.isDouble("123.45"));
        assertTrue(StringUtils.isDouble("-456.78"));
        assertTrue(StringUtils.isDouble("0.0"));
        assertTrue(StringUtils.isDouble("1e10"));
    }

    @Test
    void testIsDouble_InvalidDoubles() {
        assertFalse(StringUtils.isDouble("abc"));
        assertFalse(StringUtils.isDouble(""));
        assertFalse(StringUtils.isDouble(" "));
        assertFalse(StringUtils.isDouble("123.45.67"));
    }

    @Test
    void testIsDouble_NullInput() {
        assertThrows(NullPointerException.class, () -> {
            StringUtils.isDouble(null);
        });
    }

    @Test
    void testCachingMechanism_EntityType() {
        EntityType zombie = EntityType.ZOMBIE;

        String firstCall = StringUtils.getPrettyEntityTypeString(zombie);
        String secondCall = StringUtils.getPrettyEntityTypeString(zombie);

        assertSame(firstCall, secondCall, "EntityType caching failed");
    }

    @Test
    void testCachingMechanism_Material() {
        Material diamondSword = Material.DIAMOND_SWORD;

        String firstCall = StringUtils.getPrettyMaterialString(diamondSword);
        String secondCall = StringUtils.getPrettyMaterialString(diamondSword);

        assertSame(firstCall, secondCall, "Material caching failed");
    }

    // Tests for createPrettyString via public methods
    @Test
    void testCreatePrettyString_Spaces() {
        String[] args = {"hello", "world"};
        String expected = "hello world";
        String actual = StringUtils.buildStringAfterNthElement(args, 0);
        assertEquals(expected, actual);
    }

    @Test
    void testPrettify_Substrings() {
        Material goldenApple = Material.GOLDEN_APPLE;
        String expected = "Golden Apple";
        String actual = StringUtils.getPrettyMaterialString(goldenApple);
        assertEquals(expected, actual);
    }
}
