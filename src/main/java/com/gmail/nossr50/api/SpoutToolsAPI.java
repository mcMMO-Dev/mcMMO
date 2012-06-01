package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class SpoutToolsAPI {
    public static List<ItemStack> spoutSwords = new ArrayList<ItemStack>();

    /**
     * Add a custom Spout sword to mcMMO for XP gain & ability use.
     * </br>
     * This function is designed for API usage.
     *
     * @param spoutSword The sword to add
     */
    public void addCustomSword(ItemStack spoutSword) {
        spoutSwords.add(spoutSword);
    }
}
