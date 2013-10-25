package com.gmail.nossr50.events.skills.excavation;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerTreasureEvent;

public class McMMOPlayerExcavationTreasureEvent extends McMMOPlayerTreasureEvent {
    private Block block;

    public McMMOPlayerExcavationTreasureEvent(Player player, ItemStack treasure, int xpGained, Block block) {
        super(player, SkillType.EXCAVATION, treasure, xpGained);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
