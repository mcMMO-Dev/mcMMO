package com.gmail.nossr50.util;

import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.mods.LoadCustomTools;
import com.gmail.nossr50.datatypes.mods.CustomTool;

public class ModChecks {

    /**
     * Check if this custom tool can use abilities.
     *
     * @param item The custom item to check
     * @return true if the tool can use abilities, false otherwise
     */
    public static boolean toolAbilityEnabled(ItemStack item) {
        for (CustomTool tool : LoadCustomTools.getInstance().customTools) {
            if (tool.getItemID() == item.getTypeId()) {
                return tool.isAbilityEnabled();
            }
        }

        return false;
    }

    /**
     * Get the custom tool associated with an item.
     *
     * @param item The item to check
     * @return the tool if it exists, null otherwise
     */
    public static CustomTool getToolFromItemStack(ItemStack item) {
        for (CustomTool tool : LoadCustomTools.getInstance().customTools) {
            if (tool.getItemID() == item.getTypeId()) {
                return tool;
            }
        }

        return null;
    }
}
