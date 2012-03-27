package com.gmail.nossr50.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.SkillType;

public class McMMOPlayerRepairCheckEvent extends McMMOPlayerSkillEvent implements Cancellable{

    private int repairAmount;
    private ItemStack repairMaterial;
    private ItemStack repairedObject;
    private boolean cancelled;

    public McMMOPlayerRepairCheckEvent(Player player, int repairAmount, ItemStack repairMaterial, ItemStack repairedObject) {
        super(player, SkillType.REPAIR);
        this.repairAmount = repairAmount;
        this.repairMaterial = repairMaterial;
        this.repairedObject = repairedObject;
        this.cancelled = false;
    }

    public int getRepairAmount() {
        return repairAmount;
    }

    public ItemStack getRepairMaterial() {
        return repairMaterial;
    }

    public ItemStack getRepairedObject() {
        return repairedObject;
    }

    /** Following are required for Cancellable **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
