package com.gmail.nossr50.commands.database;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class McremoveCommand implements TabExecutor {

    private mcMMO pluginRef;

    public McremoveCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String playerName = CommandUtils.getMatchedPlayerName(args[0]);

            if (UserManager.getOfflinePlayer(playerName) == null && CommandUtils.unloadedProfile(sender, pluginRef.getDatabaseManager().loadPlayerProfile(playerName, false))) {
                return true;
            }

            UUID uuid = null;

            if (Bukkit.getPlayer(playerName) != null) {
                uuid = Bukkit.getPlayer(playerName).getUniqueId();
            }

            if (pluginRef.getDatabaseManager().removeUser(playerName, uuid)) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcremove.Success", playerName));
            } else {
                sender.sendMessage(playerName + " could not be removed from the database."); // Pretty sure this should NEVER happen.
            }

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }
}
