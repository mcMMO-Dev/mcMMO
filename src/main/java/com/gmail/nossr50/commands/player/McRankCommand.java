package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McRankCommandAsyncTask;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
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

public class McRankCommand implements TabExecutor {
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
                McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(playerName);

                if (mcMMOPlayer != null) {
                    Player player = mcMMOPlayer.getPlayer();
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
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());

            if (mcMMOPlayer == null) {
                sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
                return;
            }

            long cooldownMillis = Math.min(mcMMO.p.getGeneralConfig().getDatabasePlayerCooldown(), 1750);

            if (mcMMOPlayer.getDatabaseATS() + cooldownMillis > System.currentTimeMillis()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.CooldownMS", getCDSeconds(mcMMOPlayer, cooldownMillis)));
                return;
            }

            if (((Player) sender).hasMetadata(MetadataConstants.METADATA_KEY_DATABASE_COMMAND)) {
                sender.sendMessage(LocaleLoader.getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(MetadataConstants.METADATA_KEY_DATABASE_COMMAND, new FixedMetadataValue(mcMMO.p, null));
            }

            mcMMOPlayer.actualizeDatabaseATS();
        }

        boolean useBoard = mcMMO.p.getGeneralConfig().getScoreboardsEnabled() && (sender instanceof Player) && (mcMMO.p.getGeneralConfig().getRankUseBoard());
        boolean useChat = !useBoard || mcMMO.p.getGeneralConfig().getRankUseChat();

        mcMMO.p.getFoliaLib().getImpl().runAsync(new McRankCommandAsyncTask(playerName, sender, useBoard, useChat));
    }

    private long getCDSeconds(McMMOPlayer mcMMOPlayer, long cooldownMillis) {
        return ((mcMMOPlayer.getDatabaseATS() + cooldownMillis) - System.currentTimeMillis());
    }
}
