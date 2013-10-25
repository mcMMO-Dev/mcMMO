package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.nossr50.events.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.events.experience.levels.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.xp.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

public class ScoreboardsListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardManager.setupPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ScoreboardManager.teardownPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        ScoreboardManager.handleLevelUp(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerXp(McMMOPlayerXpGainEvent event) {
        ScoreboardManager.handleXp(event.getPlayer(), event.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAbility(McMMOPlayerAbilityActivateEvent event) {
        ScoreboardManager.cooldownUpdate(event.getPlayer(), event.getSkill());
    }
}
