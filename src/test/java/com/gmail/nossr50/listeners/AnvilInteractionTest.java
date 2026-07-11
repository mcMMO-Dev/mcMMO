package com.gmail.nossr50.listeners;

import static org.assertj.core.api.Assertions.assertThat;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

/**
 * Contract coverage for the repair/salvage anvil decision ladder. The perform path requires a
 * single held item and an unlocked Scrap Collector, the cancel path requires neither, and
 * repair wins when both anvils share a material.
 */
class AnvilInteractionTest {

    @Test
    void clickOnRepairAnvilWithRepairableSingleItemShouldRepair() {
        // Given - a repairable single item clicked against the repair anvil
        // When - the use is resolved on the perform path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.IRON_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> true, () -> true, () -> true, () -> true, () -> true);

        // Then - the click repairs
        assertThat(use).isEqualTo(AnvilInteraction.Use.REPAIR);
    }

    @Test
    void stackedItemsShouldNotRepairOnThePerformPath() {
        // Given - a stack of two repairable items
        // When - the use is resolved on the perform path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.IRON_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 2,
                () -> true, () -> true, () -> true, () -> true, () -> true);

        // Then - the perform path rejects stacked items
        assertThat(use).isEqualTo(AnvilInteraction.Use.NONE);
    }

    @Test
    void stackedItemsShouldStillMatchOnTheCancelPath() {
        // Given - a stack of two repairable items
        // When - the use is resolved on the cancel-confirmation path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.IRON_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, false, 2,
                () -> true, () -> true, () -> true, () -> true, () -> true);

        // Then - the cancel path has no single-item requirement
        assertThat(use).isEqualTo(AnvilInteraction.Use.REPAIR);
    }

    @Test
    void clickOnSalvageAnvilWithSalvageableItemShouldSalvage() {
        // Given - a salvageable item clicked against the salvage anvil
        // When - the use is resolved
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.GOLD_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> true, () -> false, () -> true, () -> true, () -> true);

        // Then - the click salvages
        assertThat(use).isEqualTo(AnvilInteraction.Use.SALVAGE);
    }

    @Test
    void missingPermissionShouldResolveToNone() {
        // Given - a click on the repair anvil without the repair permission
        // When - the use is resolved
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.IRON_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> false, () -> true, () -> false, () -> true, () -> true);

        // Then - nothing happens
        assertThat(use).isEqualTo(AnvilInteraction.Use.NONE);
    }

    @Test
    void unrelatedBlockShouldResolveToNone() {
        // Given - a click on a block that is neither anvil
        // When - the use is resolved
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.STONE,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> true, () -> true, () -> true, () -> true, () -> true);

        // Then - nothing happens
        assertThat(use).isEqualTo(AnvilInteraction.Use.NONE);
    }

    @Test
    void repairShouldWinWhenBothAnvilsShareAMaterial() {
        // Given - repair and salvage anvils configured to the same material, item eligible for both
        // When - the use is resolved
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.IRON_BLOCK,
                Material.IRON_BLOCK, Material.IRON_BLOCK, true, 1,
                () -> true, () -> true, () -> true, () -> true, () -> true);

        // Then - repair is checked first and wins, preserving historical behavior
        assertThat(use).isEqualTo(AnvilInteraction.Use.REPAIR);
    }

    @Test
    void lockedScrapCollectorWithSalvageableItemShouldResolveToSalvageLocked() {
        // Given - a salvageable single item and salvage permission, but Scrap Collector locked
        // When - the use is resolved on the perform path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.GOLD_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> true, () -> false, () -> true, () -> true, () -> false);

        // Then - the click reports the locked salvage so the player can be told why
        assertThat(use).isEqualTo(AnvilInteraction.Use.SALVAGE_LOCKED);
    }

    @Test
    void lockedScrapCollectorWithUnsalvageableItemShouldResolveToNone() {
        // Given - Scrap Collector locked and an item that could never be salvaged anyway
        // When - the use is resolved on the perform path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.GOLD_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, true, 1,
                () -> true, () -> false, () -> true, () -> false, () -> false);

        // Then - the click stays vanilla; no locked message for unsalvageable items
        assertThat(use).isEqualTo(AnvilInteraction.Use.NONE);
    }

    @Test
    void lockedScrapCollectorShouldStillMatchOnTheCancelPath() {
        // Given - Scrap Collector locked and a pending-confirmation cancel click
        // When - the use is resolved on the cancel-confirmation path
        final AnvilInteraction.Use use = AnvilInteraction.resolve(Material.GOLD_BLOCK,
                Material.IRON_BLOCK, Material.GOLD_BLOCK, false, 1,
                () -> true, () -> false, () -> true, () -> true, () -> false);

        // Then - the cancel path never gates on Scrap Collector, matching historical behavior
        assertThat(use).isEqualTo(AnvilInteraction.Use.SALVAGE);
    }
}
