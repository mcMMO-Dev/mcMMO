package com.gmail.nossr50.events.skills.repair;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerSkillEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Called after the object has been repaired with mcMMO.
 */
public class McMMOPlayerRepairEvent extends McMMOPlayerSkillEvent {
    private ItemStack repairedItem;
    private short repairedAmount;

    public McMMOPlayerRepairEvent(Player player, short repairedAmount, ItemStack repairedItem) {
        super(player, PrimarySkillType.REPAIR);
        this.repairedItem = repairedItem;
        this.repairedAmount = repairedAmount;
    }

    /**
     * Sets a new result for the repaired item
     * @param newRepairedItem The new result
     */
    public void setRepairedItem(ItemStack newRepairedItem) {
        repairedItem = newRepairedItem;
    }

    /**
     * @return Gets the item that was repaired
     */
    public ItemStack getRepairedItem() {
        return repairedItem;
    }

    /**
     * @return Gets the amount the item was repaired
     */
    public short getRepairedAmount() {
        return repairedAmount;
    }

}
