package com.gmail.nossr50.commands.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.commands.McrankCommandAsyncTask;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;

public class McrankCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.mcrank(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                display(sender, sender.getName());

                return true;

            case 1:
                if (!Permissions.mcrankOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = Misc.getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName, true);

                if (mcMMOPlayer != null) {
                    playerName = mcMMOPlayer.getPlayer().getName();

                    if (CommandUtils.tooFar(sender, mcMMOPlayer.getPlayer(), Permissions.mcrankFar(sender))) {
                        return true;
                    }
                }
                else if (CommandUtils.inspectOffline(sender, mcMMO.getDatabaseManager().loadPlayerProfile(playerName, false), Permissions.mcrankOffline(sender))) {
                    return true;
                }

                display(sender, playerName);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                Set<String> playerNames = UserManager.getPlayerNames();
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void display(CommandSender sender, String playerName) {
        if (sender instanceof Player) {
            McMMOPlayer mcpl = UserManager.getPlayer(sender.getName());
            if (mcpl.getDatabaseATS() + Misc.PLAYER_DATABASE_COOLDOWN_MILLIS > System.currentTimeMillis()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Cooldown"));
                return;
            }
            mcpl.actualizeDatabaseATS();
        }

        boolean useBoard = (sender instanceof Player) && (Config.getInstance().getRankUseBoard());
        boolean useChat = useBoard ? Config.getInstance().getRankUseChat() : true;
        new McrankCommandAsyncTask(playerName, sender, useBoard, useChat).runTaskAsynchronously(mcMMO.p);
    }
}
