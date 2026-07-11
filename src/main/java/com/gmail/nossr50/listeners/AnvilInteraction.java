package com.gmail.nossr50.listeners;

import java.util.function.BooleanSupplier;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Pure decision ladder for clicks on repair/salvage anvils, extracted from the interact
 * listener. Repair is checked before salvage, so when both anvils are configured to the same
 * material the repair use wins - preserving the historical behavior.
 */
final class AnvilInteraction {

    enum Use {
        REPAIR,
        SALVAGE,
        /**
         * A salvage that would work except Scrap Collector is still locked; the perform path
         * reports it so the player can be told the level requirement instead of the click
         * falling through to vanilla behavior like armor equipping.
         */
        SALVAGE_LOCKED,
        NONE
    }

    private AnvilInteraction() {
    }

    /**
     * Decides whether a click on a block counts as a repair or salvage anvil use. Suppliers are
     * evaluated lazily in the historical order (permission, then item eligibility).
     *
     * @param clickedType the clicked block's material
     * @param repairAnvilType the configured repair anvil material
     * @param salvageAnvilType the configured salvage anvil material
     * @param performingUse whether this click performs the anvil use (right click) rather than
     * cancels a pending confirmation (left click); the perform path requires a single held item
     * and an unlocked Scrap Collector
     * @param heldAmount the held stack size
     */
    static @NotNull Use resolve(@Nullable Material clickedType,
            @Nullable Material repairAnvilType, @Nullable Material salvageAnvilType,
            boolean performingUse, int heldAmount,
            @NotNull BooleanSupplier canRepair, @NotNull BooleanSupplier itemRepairable,
            @NotNull BooleanSupplier canSalvage, @NotNull BooleanSupplier itemSalvageable,
            @NotNull BooleanSupplier scrapCollectorUnlocked) {
        if (clickedType == repairAnvilType && canRepair.getAsBoolean()
                && itemRepairable.getAsBoolean() && (!performingUse || heldAmount <= 1)) {
            return Use.REPAIR;
        }

        if (clickedType == salvageAnvilType && canSalvage.getAsBoolean()
                && itemSalvageable.getAsBoolean() && (!performingUse || heldAmount <= 1)) {
            if (performingUse && !scrapCollectorUnlocked.getAsBoolean()) {
                return Use.SALVAGE_LOCKED;
            }

            return Use.SALVAGE;
        }

        return Use.NONE;
    }
}
