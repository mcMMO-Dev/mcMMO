package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.compat.layers.AbstractCompatibilityLayer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractPersistentDataLayer extends AbstractCompatibilityLayer {

    public final NamespacedKey superAbilityBoosted;
    public final String SUPER_ABILITY_BOOSTED = "super_ability_boosted";

    public AbstractPersistentDataLayer() {
        superAbilityBoosted = getNamespacedKey(SUPER_ABILITY_BOOSTED);
        initializeLayer();
    }

    public @NotNull NamespacedKey getNamespacedKey(@NotNull String key) {
        return new NamespacedKey(mcMMO.p, key);
    }

    public abstract @Nullable UUID getFurnaceOwner(@NotNull Furnace furnace);

    public abstract void setFurnaceOwner(@NotNull Furnace furnace, @NotNull UUID uuid);

    public abstract void setSuperAbilityBoostedItem(@NotNull ItemStack itemStack, int originalDigSpeed);

    public abstract boolean isSuperAbilityBoosted(@NotNull ItemStack itemStack);

    public abstract int getSuperAbilityToolOriginalDigSpeed(@NotNull ItemStack itemStack);

    public abstract void removeBonusDigSpeedOnSuperAbilityTool(@NotNull ItemStack itemStack);

}
