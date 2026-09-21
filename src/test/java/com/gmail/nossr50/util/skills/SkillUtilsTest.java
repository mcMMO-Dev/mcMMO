package com.gmail.nossr50.util.skills;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EnchantmentMapper;
import com.gmail.nossr50.util.ThrowingRecipeIterator;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SkillUtilsTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(SkillUtilsTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);

        // Enchantment cannot be mocked (its static initializer requires a live registry),
        // so the mapper returns null and the mocked ItemStack reports enchant level 0
        final EnchantmentMapper enchantmentMapper = mock(EnchantmentMapper.class);
        when(mcMMO.p.getEnchantmentMapper()).thenReturn(enchantmentMapper);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    /**
     * Covers #5182: durability applied by super abilities must cap at the item's
     * max_damage component when present. Capping at the vanilla material maximum
     * instead used to snap a custom item's damage value backwards, which players
     * observed as durability being "restored".
     */
    @Test
    void handleDurabilityChangeShouldCapDamageAtMaxDamageComponent() {
        // Given - a pickaxe with max_damage 3000 that is close to breaking; the
        // vanilla diamond pickaxe maximum (1561) must not be used as the cap
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.hasItemMeta()).thenReturn(true);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(damageableMeta.isUnbreakable()).thenReturn(false);
        when(damageableMeta.hasMaxDamage()).thenReturn(true);
        when(damageableMeta.getMaxDamage()).thenReturn(3000);
        when(damageableMeta.getDamage()).thenReturn(2990);

        // When - more damage is applied than the item can absorb
        SkillUtils.handleDurabilityChange(pickaxe, 50, 1.0);

        // Then - damage is capped at the component max, not the vanilla material max
        verify(damageableMeta).setDamage(3000);
    }

    @Test
    void handleDurabilityChangeShouldCapDamageAtVanillaMaxWhenNoComponent() {
        // Given - a vanilla pickaxe (no max_damage component) close to breaking
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.hasItemMeta()).thenReturn(true);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(pickaxe.getType()).thenReturn(Material.DIAMOND_PICKAXE);
        when(damageableMeta.isUnbreakable()).thenReturn(false);
        when(damageableMeta.hasMaxDamage()).thenReturn(false);
        when(damageableMeta.getDamage()).thenReturn(1500);

        // When - more damage is applied than the item can absorb
        SkillUtils.handleDurabilityChange(pickaxe, 100, 1.0);

        // Then - damage is capped at the vanilla material maximum
        verify(damageableMeta).setDamage(Material.DIAMOND_PICKAXE.getMaxDurability());
    }

    @Test
    void handleDurabilityChangeShouldNotTouchUnbreakableItems() {
        // Given - an unbreakable item
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.hasItemMeta()).thenReturn(true);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(damageableMeta.isUnbreakable()).thenReturn(true);

        // When - durability loss is applied
        SkillUtils.handleDurabilityChange(pickaxe, 50, 1.0);

        // Then - the item is left untouched
        verify(damageableMeta, never()).setDamage(anyInt());
    }

    /**
     * Spigot 26.3 throws AbstractMethodError from its recipe iterator for each data-driven brewing
     * recipe. Salvage and Repair config loading count ingredients through this method during
     * onEnable, so one such recipe used to stop mcMMO from enabling at all.
     */
    @Test
    void getRepairAndSalvageQuantitiesShouldCountIngredientsWhenServerCannotConvertEveryRecipe() {
        // Given - a diamond pickaxe recipe listed between recipes the server fails to convert
        final RecipeChoice diamond = new RecipeChoice.MaterialChoice(Material.DIAMOND);
        final RecipeChoice stick = new RecipeChoice.MaterialChoice(Material.STICK);
        final ShapedRecipe pickaxeRecipe = mock(ShapedRecipe.class);
        when(pickaxeRecipe.getResult()).thenReturn(new ItemStack(Material.DIAMOND_PICKAXE));
        when(pickaxeRecipe.getChoiceMap()).thenReturn(
                Map.of('a', diamond, 'b', diamond, 'c', diamond, 'd', stick, 'e', stick));
        when(server.recipeIterator()).thenAnswer(invocation ->
                new ThrowingRecipeIterator(Arrays.asList(null, pickaxeRecipe, null)));

        // When - the diamond cost of a diamond pickaxe is counted
        final int quantity = SkillUtils.getRepairAndSalvageQuantities(
                Material.DIAMOND_PICKAXE, Material.DIAMOND);

        // Then - the unconvertible recipes are skipped and the three diamonds are counted
        assertThat(quantity).isEqualTo(3);
    }
}
