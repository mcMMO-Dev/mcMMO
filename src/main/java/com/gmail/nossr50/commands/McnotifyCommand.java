package com.gmail.nossr50.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.player.UserManager;

public class McnotifyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());

                if (mcMMOPlayer.useChatNotifications()) {
                    sender.sendMessage(LocaleLoader.getString("Commands.Notifications.Off"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Notifications.On"));
                }

                mcMMOPlayer.toggleChatNotifications();
                return true;

            default:
                return false;
        }
    }
}
