package com.gmail.nossr50.util.compat.layers.persistentdata;

import com.gmail.nossr50.util.compat.layers.AbstractCompatibilityLayer;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractPersistentDataLayer extends AbstractCompatibilityLayer {

    public AbstractPersistentDataLayer() {
        initializeLayer();
    }

    public abstract @Nullable UUID getFurnaceOwner(Furnace furnace);

    public abstract void setFurnaceOwner(Furnace furnace, UUID uuid);

    public abstract void setSuperAbilityBoostedItem(ItemStack itemStack, int originalDigSpeed);

    public abstract boolean isSuperAbilityBoosted(ItemStack itemStack);

    public abstract int getSuperAbilityToolOriginalDigSpeed(ItemStack itemStack);

    public abstract void removeBonusDigSpeedOnSuperAbilityTool(ItemStack itemStack);

}
