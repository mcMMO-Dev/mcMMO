package com.gmail.nossr50.events.hardcore;

import java.util.HashMap;
import org.bukkit.entity.Player;

public class McMMOPlayerVampirismEvent extends McMMOPlayerDeathPenaltyEvent {
    private final boolean isVictim;

    public McMMOPlayerVampirismEvent(Player player, boolean isVictim,
            HashMap<String, Integer> levelChanged, HashMap<String, Float> experienceChanged) {
        super(player, levelChanged, experienceChanged);
        this.isVictim = isVictim;
    }

    public boolean isVictim() {
        return isVictim;
    }
}
