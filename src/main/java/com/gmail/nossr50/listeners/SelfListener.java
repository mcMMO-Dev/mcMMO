package com.gmail.nossr50.listeners;

import java.util.Calendar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;

public class SelfListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.MONTH) == Calendar.APRIL && today.get(Calendar.DAY_OF_MONTH) == 1) {
            ParticleEffectUtils.runescapeModeCelebration(event.getPlayer(), event.getSkill());
        }
    }
}
