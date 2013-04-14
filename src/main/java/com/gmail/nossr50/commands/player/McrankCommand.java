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
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.runnables.commands.McrankCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
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

                if (Config.getInstance().getMcrankScoreboardEnabled()) {
                    ScoreboardManager.setupPlayerScoreboard(sender.getName());
                    ScoreboardManager.enablePlayerRankScoreboard((Player) sender);
                }
                else {
                    display(sender, sender.getName());
                }

                return true;

            case 1:
                if (!Permissions.mcrankOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = args[0];
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);

                if (mcMMOPlayer != null) {
                    playerName = mcMMOPlayer.getPlayer().getName();

                    if (CommandUtils.tooFar(sender, mcMMOPlayer.getPlayer(), Permissions.mcrankFar(sender))) {
                        return true;
                    }
                }
                else if (CommandUtils.inspectOffline(sender, new PlayerProfile(playerName, false), Permissions.mcrankOffline(sender))) {
                    return true;
                }

                if (sender instanceof Player && Config.getInstance().getMcrankScoreboardEnabled()) {
                    ScoreboardManager.setupPlayerScoreboard(sender.getName());
                    ScoreboardManager.enablePlayerRankScoreboardOthers((Player) sender, playerName);
                }
                else {
                    display(sender, playerName);
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

    private void display(CommandSender sender, String playerName) {
        new McrankCommandAsyncTask(playerName, sender).runTaskAsynchronously(mcMMO.p);
    }
}
