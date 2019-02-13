package com.gmail.nossr50.core.mcmmo.item;

import com.gmail.nossr50.core.mcmmo.nbt.NBT;

import java.util.ArrayList;

/**
 * Represents an ItemStack in Minecraft
 */
public interface ItemStack {

    /**
     * The maximum amount of this item allowed in a stack
     * @return the maximum stack size of the item
     */
    int getMaxStackSize();

    /**
     * The current amount of items in this stack
     * @return the amount of items
     */
    int getAmount();

    /**
     * Replaces the lore on an item stack
     * @param replacementLore the new lore for this item
     */
    void setItemLore(ArrayList<String> replacementLore);

    /**
     * Unlocalized name of this item
     * @return the unlocalized name of this item (english)
     */
    String getUnlocalizedName();

    /**
     * The maximum amount of damage this item can take before it breaks
     * @return the maximum damage allowed on this item
     */
    int getMaxDamage();

    /**
     * The id of this item
     * @return this item's id
     */
    int getItemId();

    /**
     * Returns the cooldown for an item
     * @return this item's cooldown
     */
    int getCoolDown();

    /**
     * Returns the compound NBT data for this item
     * @return this item's NBT data
     */
    NBT getNBT();
}
