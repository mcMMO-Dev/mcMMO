package com.gmail.nossr50.events.skills.fishing;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.McMMOPlayerTreasureEvent;

public class McMMOPlayerFishingTreasureEvent extends McMMOPlayerTreasureEvent {
    public McMMOPlayerFishingTreasureEvent(Player player, ItemStack treasure, int xpGained) {
        super(player, SkillType.FISHING, treasure, xpGained);
    }
}
