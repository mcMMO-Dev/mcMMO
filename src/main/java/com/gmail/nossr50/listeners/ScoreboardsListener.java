package com.gmail.nossr50.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ScoreboardsListener implements Listener {
    private final mcMMO plugin;

    public ScoreboardsListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        ScoreboardManager.setupPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        ScoreboardManager.teardownPlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent e) {
        ScoreboardManager.handleLevelUp(e.getPlayer(), e.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerXp(McMMOPlayerXpGainEvent e) {
        ScoreboardManager.handleXp(e.getPlayer(), e.getSkill());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAbility(McMMOPlayerAbilityActivateEvent e) {
        ScoreboardManager.cooldownUpdate(e.getPlayer(), e.getSkill(), SkillUtils.calculateTimeLeft(UserManager.getPlayer(e.getPlayer()).getProfile().getSkillDATS(e.getAbility()) * Misc.TIME_CONVERSION_FACTOR, e.getAbility().getCooldown(), e.getPlayer()));
    }
}
