package com.gmail.nossr50.events.hardcore;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class McMMOPlayerStatLossEvent extends McMMOPlayerDeathPenaltyEvent {

    public McMMOPlayerStatLossEvent(Player player, HashMap<String, Integer> levelChanged, HashMap<String, Double> experienceChanged) {
        super(player, levelChanged, experienceChanged);
    }
}
