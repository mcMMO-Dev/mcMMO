package com.gmail.nossr50.events.skills.fishing;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class McMMOPlayerMasterAnglerEvent extends McMMOPlayerFishingEvent {

    public McMMOPlayerMasterAnglerEvent(@NotNull McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer);
    }

}