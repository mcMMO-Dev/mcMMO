package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.locale.LocaleLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NotifySquelchReminderTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(mcMMO.getUserManager().getPlayer(player) != null)
            {
                if(!mcMMO.getUserManager().getPlayer(player).useChatNotifications())
                {
                    player.sendMessage(LocaleLoader.getString("Reminder.Squelched"));
                }
            }
        }
    }
}
