package com.gmail.nossr50.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.util.player.UserManager;
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerXpGain(McMMOPlayerXpGainEvent event) {
        int threshold = Config.getInstance().getExperienceDeminishedReturnsThreshold();

        if (threshold <= 0) {
            return;
        }

        Player player = event.getPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        SkillType skillType = event.getSkill();

        if (skillType.isChildSkill()) {
            return;
        }

        float difference = (mcMMOPlayer.getProfile().getRegisteredXpGain(skillType) - threshold) / threshold;

        if (difference > 0) {
//            System.out.println("Total XP Earned: " + mcMMOPlayer.getProfile().getRegisteredXpGain(skillType) + " / Threshold value: " + threshold);
//            System.out.println(difference * 100 + "% over the threshold!");
//            System.out.println("Previous: " + event.getRawXpGained());
//            System.out.println("Adjusted XP " + (event.getRawXpGained() - (event.getRawXpGained() * difference)));
            float newValue = event.getRawXpGained() - (event.getRawXpGained() * difference);

            event.setRawXpGained(newValue);
        }

        mcMMOPlayer.getProfile().registeredXpGain(skillType, event.getRawXpGained());
    }
}
