package com.gmail.nossr50.datatypes.party;

import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.util.ItemUtils;

public enum ItemShareType {
    LOOT,
    MINING,
    HERBALISM,
    WOODCUTTING,
    MISC;

    public static ItemShareType getShareType(ItemStack itemStack) {
        if (ItemUtils.isMobDrop(itemStack)) {
            return LOOT;
        }
        else if (ItemUtils.isMiningDrop(itemStack)) {
            return MINING;
        }
        else if (ItemUtils.isHerbalismDrop(itemStack)) {
            return HERBALISM;
        }
        else if (ItemUtils.isWoodcuttingDrop(itemStack)) {
            return WOODCUTTING;
        }
        else if (ItemUtils.isMiscDrop(itemStack)) {
            return MISC;
        }

        return null;
    }
}
