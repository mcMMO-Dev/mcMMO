package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseRemovePlayerCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            String playerName = CommandUtils.getMatchedPlayerName(args[0]);

            if (mcMMO.getUserManager().queryPlayer(playerName) == null
                    && CommandUtils.hasNoProfile(sender, mcMMO.getDatabaseManager().queryPlayerDataByUUID(playerName, false))) {
                sender.sendMessage(LocaleLoader.getString("Commands.Offline"));
                return true;
            }

            UUID uuid = null;

            Player targetPlayer = Bukkit.getPlayer(playerName);

            if (targetPlayer != null) {
                uuid = targetPlayer.getUniqueId();
            }

            if (mcMMO.getDatabaseManager().removeUser(playerName, uuid)) {
                sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", playerName));
            } else {
                sender.sendMessage(playerName + " could not be removed from the database."); // Pretty sure this should NEVER happen.
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
            return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
        }
        return ImmutableList.of();
    }
}
