package com.gmail.nossr50.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gmail.nossr50.MMOTestEnvironment;
import com.gmail.nossr50.api.exceptions.InvalidSkillException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemUtilsTest extends MMOTestEnvironment {
    private static final Logger logger = Logger.getLogger(ItemUtilsTest.class.getName());

    @BeforeEach
    void setUp() throws InvalidSkillException {
        mockBaseEnvironment(logger);
    }

    @AfterEach
    void tearDown() {
        cleanUpStaticMocks();
    }

    @Test
    void craftBukkitStylePlainSeedRemovalDoesNotMatchRenamedSeedStack() {
        // CraftInventory.removeItem(ItemStack...) matches with ItemStack.isSimilar rather than
        // with the broader material-only check used by Inventory.contains(Material).
        final ItemStack renamedSeed = createRenamedSeedStack();
        final ItemStack plainSeedRequest = new ItemStack(Material.WHEAT_SEEDS, 1);

        assertEquals(Material.WHEAT_SEEDS, renamedSeed.getType(),
                "Renamed seeds still satisfy material-only inventory checks.");
        assertFalse(renamedSeed.isSimilar(plainSeedRequest),
                "A plain seed removal request does not match a renamed seed under CraftBukkit similarity rules.");
    }

    @Test
    void removeItemIncludingOffHandConsumesRenamedSeedStackFromStorage() {
        final AtomicReference<ItemStack[]> storageContents = new AtomicReference<>(
                new ItemStack[]{createRenamedSeedStack()});

        when(playerInventory.contains(Material.WHEAT_SEEDS)).thenReturn(true);
        when(playerInventory.getStorageContents()).thenAnswer(invocation -> storageContents.get());
        doAnswer(invocation -> {
            final ItemStack[] updatedStorage = invocation.getArgument(0);
            storageContents.set(updatedStorage);
            return null;
        }).when(playerInventory).setStorageContents(any(ItemStack[].class));
        when(playerInventory.getItemInOffHand()).thenReturn(new ItemStack(Material.AIR));

        // The helper should consume by material directly from storage so renamed seeds are not
        // skipped by CraftBukkit's stricter ItemStack similarity matcher.
        ItemUtils.removeItemIncludingOffHand(player, Material.WHEAT_SEEDS, 1);

        assertNull(storageContents.get()[0],
                "Material-based removal should consume the renamed seed from storage.");
        verify(playerInventory, never()).removeItem(any(ItemStack.class));
    }

    private ItemStack createRenamedSeedStack() {
        // This models the only behavior that matters for the regression: the stack has the
        // correct material, but CraftBukkit's similarity matcher treats it as different from a
        // fresh plain seed ItemStack.
        final ItemStack seedStack = mock(ItemStack.class);

        when(seedStack.getType()).thenReturn(Material.WHEAT_SEEDS);
        when(seedStack.getAmount()).thenReturn(1);
        when(seedStack.isSimilar(any(ItemStack.class))).thenReturn(false);

        return seedStack;
    }

    /**
     * Items can carry a max_damage component that overrides the vanilla material
     * maximum; the durability paths rely on this helper picking the right source.
     */
    @Test
    void getItemMaxDamageShouldPreferMaxDamageComponentWhenPresent() {
        // Given - an item whose meta declares a max_damage larger than the vanilla maximum
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(damageableMeta.hasMaxDamage()).thenReturn(true);
        when(damageableMeta.getMaxDamage()).thenReturn(3000);

        // When - the effective max damage is resolved
        final int maxDamage = ItemUtils.getItemMaxDamage(pickaxe);

        // Then - the component value wins over the vanilla material maximum
        assertThat(maxDamage).isEqualTo(3000);
    }

    @Test
    void getItemMaxDamageShouldPreferMaxDamageComponentWhenLowerThanVanillaMax() {
        // Given - a component max BELOW the vanilla maximum; such items break earlier
        // than their material suggests, so the component must still win
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(damageableMeta.hasMaxDamage()).thenReturn(true);
        when(damageableMeta.getMaxDamage()).thenReturn(100);

        // When - the effective max damage is resolved
        final int maxDamage = ItemUtils.getItemMaxDamage(pickaxe);

        // Then - the smaller component value is respected
        assertThat(maxDamage).isEqualTo(100);
    }

    @Test
    void getItemMaxDamageShouldFallBackToVanillaMaxWhenComponentAbsent() {
        // Given - a damageable item without a max_damage component
        final ItemStack pickaxe = mock(ItemStack.class);
        final Damageable damageableMeta = mock(Damageable.class);
        when(pickaxe.getItemMeta()).thenReturn(damageableMeta);
        when(pickaxe.getType()).thenReturn(Material.DIAMOND_PICKAXE);
        when(damageableMeta.hasMaxDamage()).thenReturn(false);

        // When - the effective max damage is resolved
        final int maxDamage = ItemUtils.getItemMaxDamage(pickaxe);

        // Then - the vanilla material maximum applies
        assertThat(maxDamage).isEqualTo((int) Material.DIAMOND_PICKAXE.getMaxDurability());
    }

    @Test
    void getItemMaxDamageShouldFallBackToVanillaMaxWhenMetaNotDamageable() {
        // Given - an item whose meta does not support durability at all
        final ItemStack stick = mock(ItemStack.class);
        final ItemMeta plainMeta = mock(ItemMeta.class);
        when(stick.getItemMeta()).thenReturn(plainMeta);
        when(stick.getType()).thenReturn(Material.STICK);

        // When - the effective max damage is resolved
        final int maxDamage = ItemUtils.getItemMaxDamage(stick);

        // Then - the vanilla material maximum applies (0 for non-damageable materials)
        assertThat(maxDamage).isEqualTo((int) Material.STICK.getMaxDurability());
    }
}