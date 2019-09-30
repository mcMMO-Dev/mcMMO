package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

//TODO: Rewrite, wrote this in a rush.
public class NotifySquelchReminderTask extends BukkitRunnable {

    private final mcMMO pluginRef;

    public NotifySquelchReminderTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

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
