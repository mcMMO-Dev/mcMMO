package com.gmail.nossr50.events.skills.unarmed;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOPlayerDisarmEvent extends McMMOPlayerUnarmedEvent {
    private Player defender;
    private ItemStack droppedItem;

    public McMMOPlayerDisarmEvent(Player attacker, Player defender) {
        super(attacker);
        this.defender = defender;
        this.setDroppedItem(defender.getItemInHand());
    }

    public Player getDefender() {
        return defender;
    }

    public ItemStack getDroppedItem() {
        return droppedItem;
    }

    public void setDroppedItem(ItemStack droppedItem) {
        this.droppedItem = droppedItem;
    }
}
