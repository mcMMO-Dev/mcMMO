package com.gmail.nossr50.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Users;

public class McnotifyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
        case 0:
            PlayerProfile profile = Users.getPlayer((Player) sender).getProfile();

            if (profile.useChatNotifications()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Notifications.Off"));
            }
            else {
                sender.sendMessage(LocaleLoader.getString("Commands.Notifications.On"));
            }

            profile.toggleChatNotifications();
            return true;

        default:
            return false;
        }
    }
}
