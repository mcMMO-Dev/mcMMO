package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class McnotifyCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        switch (args.length) {
            case 0:
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer((Player) sender);

                //Not Loaded yet
                if(mcMMOPlayer == null)
                    sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));

                sender.sendMessage(LocaleLoader.getString("Commands.Notifications." + (mcMMOPlayer.useChatNotifications() ? "Off" : "On")));
                mcMMOPlayer.toggleChatNotifications();
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ImmutableList.of();
    }
}
