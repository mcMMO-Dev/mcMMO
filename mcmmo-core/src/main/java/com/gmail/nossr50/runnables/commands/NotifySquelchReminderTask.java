package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

//TODO: Rewrite, wrote this in a rush.
public class NotifySquelchReminderTask implements Consumer<Task> {

    private final mcMMO pluginRef;

    public NotifySquelchReminderTask(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public void accept(Task task) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (pluginRef.getUserManager().getPlayer(player) != null) {
                if (!pluginRef.getUserManager().getPlayer(player).useChatNotifications()) {
                    player.sendMessage(pluginRef.getLocaleManager().getString("Reminder.Squelched"));
                }
            }
        }
    }
}
