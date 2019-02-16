package com.gmail.nossr50.core.events.skills.repair;

import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.mcmmo.item.ItemStack;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.events.skills.McMMOPlayerSkillEvent;

/**
 * Called just before a player repairs an object with mcMMO.
 */
public class McMMOPlayerRepairCheckEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private short repairAmount;
    private ItemStack repairMaterial;
    private ItemStack repairedObject;
    private boolean cancelled;

    public McMMOPlayerRepairCheckEvent(Player player, short repairAmount, ItemStack repairMaterial, ItemStack repairedObject) {
        super(player, PrimarySkillType.REPAIR);
        this.repairAmount = repairAmount;
        this.repairMaterial = repairMaterial;
        this.repairedObject = repairedObject;
        this.cancelled = false;
    }

    /**
     * @return The amount this item will be repaired.
     */
    public short getRepairAmount() {
        return repairAmount;
    }

    /**
     * @return The material used to repair this item
     */
    public ItemStack getRepairMaterial() {
        return repairMaterial;
    }

    /**
     * @return The item that was repaired
     */
    public ItemStack getRepairedObject() {
        return repairedObject;
    }

    /**
     * Following are required for Cancellable
     **/
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
