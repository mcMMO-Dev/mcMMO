package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NotifySquelchReminderTask extends CancellableRunnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (UserManager.getPlayer(player) != null) {
                if (!UserManager.getPlayer(player).useChatNotifications()) {
                    player.sendMessage(LocaleLoader.getString("Reminder.Squelched"));
                }
            }
        }
    }
}
