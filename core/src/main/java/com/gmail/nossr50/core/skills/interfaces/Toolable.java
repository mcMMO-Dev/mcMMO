package com.gmail.nossr50.core.skills.interfaces;


import com.gmail.nossr50.core.mcmmo.item.ItemStack;

import java.util.Collection;

public interface Toolable {
    /**
     * Whether or not this Skill requires a tool
     * Not all skills will require a tool
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
