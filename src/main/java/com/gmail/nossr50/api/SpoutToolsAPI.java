package com.gmail.nossr50.api;

import java.util.ArrayList;
import java.util.List;

import org.getspout.spoutapi.inventory.SpoutItemStack;

public class SpoutToolsAPI {
    public static List<SpoutItemStack> spoutSwords = new ArrayList<SpoutItemStack>();

    /**
     * Add a custom Spout sword to mcMMO for XP gain & ability use.
     * </br>
     * This function is designed for API usage.
     *
     * @param spoutSword The sword to add
     */
    public void addCustomSword(SpoutItemStack spoutSword) {
        spoutSwords.add(spoutSword);
    }
}
