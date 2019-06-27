package com.gmail.nossr50.skills.smelting;

import org.bukkit.inventory.ItemStack;

public class Smelting {
    protected static int getResourceXp(ItemStack smelting) {
        return pluginRef.getDynamicSettingsManager().getExperienceManager().getFurnaceItemXP(smelting.getType());
    }
}
