package com.gmail.nossr50.skills.smelting;

import com.gmail.nossr50.mcMMO;
import org.bukkit.inventory.ItemStack;

public class Smelting {
    protected static int getResourceXp(ItemStack smelting) {
        return mcMMO.getDynamicSettingsManager().getExperienceManager().getFurnaceItemXP(smelting.getType());
    }
}
