package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.RankCommandAsyncTask;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements TabExecutor {

    private final mcMMO pluginRef;

    public RankCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
                    return true;
                }

                if (!pluginRef.getPermissionTools().mcrank(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
                    return true;
                }

                display(sender, sender.getName());

                return true;

            case 1:
                if (!pluginRef.getPermissionTools().mcrankOthers(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
                    return true;
                }

                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(playerName);

                if (mcMMOPlayer != null) {
                    Player player = mcMMOPlayer.getPlayer();
                    playerName = player.getName();

                    if (pluginRef.getCommandTools().tooFar(sender, player, pluginRef.getPermissionTools().mcrankFar(sender))) {
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = pluginRef.getCommandTools().getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void display(CommandSender sender, String playerName) {
        if (sender instanceof Player) {

            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(sender.getName());

            if (mcMMOPlayer == null) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Profile.PendingLoad"));
                return;
            }

            long cooldownMillis = 5000;

            if (mcMMOPlayer.getDatabaseATS() + cooldownMillis > System.currentTimeMillis()) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Database.CooldownMS", getCDSeconds(mcMMOPlayer, cooldownMillis)));
                return;
            }

            if (((Player) sender).hasMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY)) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Database.Processing"));
                return;
            } else {
                ((Player) sender).setMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY, new FixedMetadataValue(pluginRef, null));
            }

            mcMMOPlayer.actualizeDatabaseATS();
        }

        boolean useBoard = pluginRef.getScoreboardSettings().getScoreboardsEnabled() && (sender instanceof Player)
                && (pluginRef.getScoreboardSettings().isScoreboardEnabled(pluginRef.getScoreboardManager().SidebarType.RANK_BOARD));
        boolean useChat = !useBoard || pluginRef.getScoreboardSettings().isScoreboardPrinting(pluginRef.getScoreboardManager().SidebarType.RANK_BOARD);

        new RankCommandAsyncTask(playerName, sender, useBoard, useChat).runTaskAsynchronously(pluginRef);
    }

    private long getCDSeconds(McMMOPlayer mcMMOPlayer, long cooldownMillis) {
        return ((mcMMOPlayer.getDatabaseATS() + cooldownMillis) - System.currentTimeMillis());
    }
}
