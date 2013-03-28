package com.gmail.nossr50.commands.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.LeaderboardManager;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public class McremoveCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.mcremove(sender)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        switch (args.length) {
            case 1:
                if (UserManager.getPlayer(args[0]) == null && CommandUtils.unloadedProfile(sender, new PlayerProfile(args[0], false))) {
                    return true;
                }

                /* MySQL */
                if (Config.getInstance().getUseMySQL()) {
                    String tablePrefix = Config.getInstance().getMySQLTablePrefix();

                    if (DatabaseManager.update("DELETE FROM " + tablePrefix + "users WHERE " + tablePrefix + "users.user = '" + args[0] + "'") != 0) {
                        Misc.profileCleanup(args[0]);
                        sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                    }
                }
                else {
                    if (LeaderboardManager.removeFlatFileUser(args[0])) {
                        Misc.profileCleanup(args[0]);
                        sender.sendMessage(LocaleLoader.getString("Commands.mcremove.Success", args[0]));
                    }
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
