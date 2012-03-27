package com.gmail.nossr50.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.SkillType;

public class McMMOPlayerRepairEvent extends McMMOPlayerSkillEvent{

    private ItemStack item;
    private short repairAmount;

    public McMMOPlayerRepairEvent(Player player, ItemStack item, short repairAmount) {
        super(player, SkillType.REPAIR);
        this.item = item;
        this.repairAmount = repairAmount;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getRepairAmount() {
        return repairAmount;
    }
}
