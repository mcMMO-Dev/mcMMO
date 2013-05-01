package com.gmail.nossr50.commands.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class McremoveCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                if (UserManager.getPlayer(args[0]) == null && CommandUtils.unloadedProfile(sender, new PlayerProfile(args[0], false))) {
                    return true;
                }

                if (mcMMO.getDatabaseManager().removeUser(args[0])) {
                    sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                }
                else {
                    sender.sendMessage(args[0] + " could not be removed from the database."); // Pretty sure this should NEVER happen.
                }

                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                Set<String> playerNames = UserManager.getPlayers().keySet();
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }
}
