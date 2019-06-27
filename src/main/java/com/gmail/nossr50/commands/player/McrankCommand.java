package com.gmail.nossr50.commands.player;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McrankCommandAsyncTask;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class McrankCommand implements TabExecutor {

    private mcMMO pluginRef;

    public McrankCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }

    private void display(CommandSender sender, String playerName) {
        if (sender instanceof Player) {

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(sender.getName());

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
                && (pluginRef.getScoreboardSettings().isScoreboardEnabled(ScoreboardManager.SidebarType.RANK_BOARD));
        boolean useChat = !useBoard || pluginRef.getScoreboardSettings().isScoreboardPrinting(ScoreboardManager.SidebarType.RANK_BOARD);

        new McrankCommandAsyncTask(playerName, sender, useBoard, useChat).runTaskAsynchronously(pluginRef);
    }

    private long getCDSeconds(McMMOPlayer mcMMOPlayer, long cooldownMillis) {
        return ((mcMMOPlayer.getDatabaseATS() + cooldownMillis) - System.currentTimeMillis());
    }
}
