package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerShakeEvent extends McMMOPlayerFishingCombatEvent {
    private ItemStack drop;

    public McMMOPlayerShakeEvent(Player player, Fish hook, ItemStack drop, LivingEntity target, double damage) {
        super(player, hook, target, DamageCause.PROJECTILE, damage);
        this.drop = drop;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public void setDrop(ItemStack drop) {
        this.drop = drop;
    }
}
