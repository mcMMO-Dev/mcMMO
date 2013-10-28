package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;

public class SelfListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        if (!Config.getInstance().getLevelUpEffectsEnabled()) {
            return;
        }

        if ((event.getSkillLevel() % Config.getInstance().getLevelUpEffectsTier()) == 0) {
            event.getSkill().celebrateLevelUp(event.getPlayer());
        }
    }
}
