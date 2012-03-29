package com.gmail.nossr50.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.SkillType;

public class McMMOPlayerRepairEvent extends McMMOPlayerSkillEvent{

    private ItemStack repairedObject;
    private short repairAmount;

    public McMMOPlayerRepairEvent(Player player, ItemStack repairedObject, short repairAmount) {
        super(player, SkillType.REPAIR);
        this.repairedObject = repairedObject;
        this.repairAmount = repairAmount;
    }

    public ItemStack getRepairedObject() {
        return repairedObject;
    }

    public short getRepairAmount() {
        return repairAmount;
    }
}
