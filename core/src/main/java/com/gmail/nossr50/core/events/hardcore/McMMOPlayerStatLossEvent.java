package com.gmail.nossr50.core.events.hardcore;


import com.gmail.nossr50.core.mcmmo.entity.Player;

import java.util.HashMap;

public class McMMOPlayerStatLossEvent extends McMMOPlayerDeathPenaltyEvent {

    public McMMOPlayerStatLossEvent(Player player, HashMap<String, Integer> levelChanged, HashMap<String, Float> experienceChanged) {
        super(player, levelChanged, experienceChanged);
    }
}
