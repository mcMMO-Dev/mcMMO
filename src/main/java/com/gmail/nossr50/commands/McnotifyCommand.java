package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class McnotifyCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (args.length == 0) {
            McMMOPlayer mmoPlayer = mcMMO.getUserManager().getPlayer((Player) sender);

            //Not Loaded yet
            if (mmoPlayer == null)
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));

            sender.sendMessage(LocaleLoader.getString("Commands.Notifications." + (mmoPlayer.hasSkillChatNotifications() ? "Off" : "On")));
            mmoPlayer.toggleSkillChatNotifications();
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return ImmutableList.of();
    }
}
