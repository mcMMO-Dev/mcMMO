package com.gmail.nossr50.events.skills.repair;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * Called just before a player repairs an object with mcMMO.
 */
public class McMMOPlayerRepairCheckEvent extends McMMOPlayerSkillEvent implements Cancellable {
    private final short repairAmount;
    private final ItemStack repairMaterial;
    private final ItemStack repairedObject;
    private boolean cancelled;

    public McMMOPlayerRepairCheckEvent(Player player, short repairAmount, ItemStack repairMaterial,
            ItemStack repairedObject) {
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
