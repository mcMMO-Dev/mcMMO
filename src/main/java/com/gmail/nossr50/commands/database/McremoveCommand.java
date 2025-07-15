package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

public class McremoveCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, String[] args) {
        if (args.length == 1) {
            String playerName = CommandUtils.getMatchedPlayerName(args[0]);

            if (UserManager.getOfflinePlayer(playerName) == null && CommandUtils.unloadedProfile(
                    sender, mcMMO.getDatabaseManager().loadPlayerProfile(playerName))) {
                return true;
            }

            UUID uuid = null;

            if (Bukkit.getPlayer(playerName) != null) {
                uuid = Bukkit.getPlayer(playerName).getUniqueId();
            }

            if (mcMMO.getDatabaseManager().removeUser(playerName, uuid)) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", playerName));
            } else {
                sender.sendMessage(playerName
                        + " could not be removed from the database."); // Pretty sure this should NEVER happen.
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
            return StringUtil.copyPartialMatches(args[0], playerNames,
                    new ArrayList<>(playerNames.size()));
        }
        return ImmutableList.of();
    }
}
