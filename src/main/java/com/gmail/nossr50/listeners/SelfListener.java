package com.gmail.nossr50.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;

public class SelfListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        if (!Config.getInstance().getLevelUpEffectsEnabled()) {
            return;
        }

        int tier = Config.getInstance().getLevelUpEffectsTier();

        if (tier <= 0) {
            return;
        }

        Player player = event.getPlayer();
        float skillValue = event.getSkillLevel();

        if ((skillValue % tier) == 0) {
            ParticleEffectUtils.runescapeModeCelebration(player, event.getSkill());
        }
    }
}
