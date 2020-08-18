package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McrankCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class McrankCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!Permissions.mcrank(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!CommandUtils.hasPlayerDataKey(sender)) {
                    return true;
                }

                display(sender, sender.getName());

                return true;

            case 1:
                if (!Permissions.mcrankOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!CommandUtils.hasPlayerDataKey(sender)) {
                    return true;
                }

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                McMMOPlayer mmoPlayer = mcMMO.getUserManager().getOfflinePlayer(playerName);

                if (mmoPlayer != null) {
                    Player player = mmoPlayer.getPlayer();
                    playerName = player.getName();

                    if (CommandUtils.tooFar(sender, player, Permissions.mcrankFar(sender))) {
                        return true;
                    }
                }

                display(sender, playerName);
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
            return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
        }
        return ImmutableList.of();
    }

    private void display(CommandSender sender, String playerName) {
        if (sender instanceof Player) {
            McMMOPlayer mmoPlayer = mcMMO.getUserManager().getPlayer(sender.getName());

            if(mmoPlayer == null)
            {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return;
            }

            long cooldownMillis = Math.min(Config.getInstance().getDatabasePlayerCooldown(), 1750);

            if (mmoPlayer.getDatabaseCommandATS() + cooldownMillis > System.currentTimeMillis()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.CooldownMS", getCDSeconds(mmoPlayer, cooldownMillis)));
                return;
            }

            if (((Player) sender).hasMetadata(mcMMO.databaseCommandKey)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(mcMMO.databaseCommandKey, new FixedMetadataValue(mcMMO.p, null));
            }

            mmoPlayer.actualizeDatabaseCommandATS();
        }

        boolean useBoard = Config.getInstance().getScoreboardsEnabled() && (sender instanceof Player) && (Config.getInstance().getRankUseBoard());
        boolean useChat = !useBoard || Config.getInstance().getRankUseChat();

        new McrankCommandAsyncTask(playerName, sender, useBoard, useChat).runTaskAsynchronously(mcMMO.p);
    }

    private long getCDSeconds(McMMOPlayer mmoPlayer, long cooldownMillis) {
        return ((mmoPlayer.getDatabaseCommandATS() + cooldownMillis) - System.currentTimeMillis());
    }
}
