package com.gmail.nossr50.datatypes.skills.interfaces;

import java.util.Collection;
import org.bukkit.inventory.ItemStack;

public interface Toolable {
    /**
     * Whether this Skill requires a tool Not all skills will require a tool
     *
     * @return true if tool is required
     */
    boolean requiresTool();

    /**
     * The tools associated with this Skill
     *
     * @return the tools
     */
    Collection<ItemStack> getTools();
}
