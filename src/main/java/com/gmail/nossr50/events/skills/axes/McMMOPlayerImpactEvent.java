package com.gmail.nossr50.events.skills.axes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerImpactEvent extends McMMOPlayerAxeEvent {
    private ItemStack armor;

    public McMMOPlayerImpactEvent(Player player, ItemStack armor) {
        super(player);
        this.armor = armor;
    }

    public ItemStack getArmor() {
        return armor;
    }
}
