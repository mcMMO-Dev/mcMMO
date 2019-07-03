package com.gmail.nossr50.runnables.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NotifySquelchReminderTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (pluginRef.getUserManager().getPlayer(player) != null) {
                if (!pluginRef.getUserManager().getPlayer(player).useChatNotifications()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Reminder.Squelched"));
                }
            }
        }
    }
}
