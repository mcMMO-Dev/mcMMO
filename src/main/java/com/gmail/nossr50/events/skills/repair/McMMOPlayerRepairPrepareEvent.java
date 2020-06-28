package com.gmail.nossr50.events.skills.repair;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Called when determining the repair material for an object with mcMMO.
 */
public class McMMOPlayerRepairPrepareEvent extends McMMOPlayerSkillEvent {
    private ItemStack repairMaterial;
    private ItemStack repairedObject;

    public McMMOPlayerRepairPrepareEvent(Player player, ItemStack repairMaterial, ItemStack repairedObject) {
        super(player, PrimarySkillType.REPAIR);
        this.repairMaterial = repairMaterial;
        this.repairedObject = repairedObject;
    }

    /**
     * @return The material used to repair this item
     */
    public ItemStack getRepairMaterial() {
        return repairMaterial;
    }

    /**
     * Sets a new material to be used for repairing this item
     * @param newRepairMaterial The new item to be used for repairing
     */
    public void setRepairMaterial(ItemStack newRepairMaterial) {
        repairMaterial = newRepairMaterial;
    }

    /**
     * @return The item that was repaired
     */
    public ItemStack getRepairedObject() {
        return repairedObject;
    }
}
