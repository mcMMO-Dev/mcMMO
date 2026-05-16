package com.gmail.nossr50.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Applies the bookedMC Gold Crimson visual theme: gold/crimson dust on level-up,
 * crimson burst on every super-ability activation.
 */
public class GoldCrimsonEffectListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLevelUp(McMMOPlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        ParticleEffectUtils.playGoldCrimsonLevelUpEffect(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAbilityActivate(McMMOPlayerAbilityActivateEvent event) {
        Player player = event.getPlayer();
        ParticleEffectUtils.playGoldCrimsonAbilityEffect(player);
    }
}
