package com.gmail.nossr50.events.skills.axes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerImpactEvent extends McMMOPlayerAxeEvent {
    private ItemStack armor;
    private short durabilityDamage;

    public McMMOPlayerImpactEvent(Player player, ItemStack armor, short durabilityDamage) {
        super(player);
        this.armor = armor;
        this.durabilityDamage = durabilityDamage;
    }

    public ItemStack getArmor() {
        return armor;
    }

    public short getDurabilityDamage() {
        return durabilityDamage;
    }

    public void setDurabilityDamage(short durabilityDamage) {
        this.durabilityDamage = durabilityDamage;
    }
}
