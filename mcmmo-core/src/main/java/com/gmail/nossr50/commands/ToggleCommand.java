package com.gmail.nossr50.commands;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class ToggleCommand implements TabExecutor {

    protected mcMMO pluginRef;

    public ToggleCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (pluginRef.getCommandTools().noConsoleUsage(sender)) {
                    return true;
                }

                if (!hasSelfPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!pluginRef.getCommandTools().hasPlayerDataKey(sender)) {
                    return true;
                }

                applyCommandAction(pluginRef.getUserManager().getPlayer(sender.getName()));
                return true;

            case 1:
                if (!hasOtherPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = pluginRef.getCommandTools().getMatchedPlayerName(args[0]);
                BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(playerName);

                if (!pluginRef.getCommandTools().checkPlayerExistence(sender, playerName, mcMMOPlayer)) {
                    return true;
                }

                //TODO: Does it matter if they are offline?
                /*if (pluginRef.getCommandTools().isOffline(sender, mcMMOPlayer.getPlayer())) {
                    return true;
                }*/

                applyCommandAction(mcMMOPlayer);
                sendSuccessMessage(sender, playerName);
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

    protected abstract boolean hasOtherPermission(CommandSender sender);

    protected abstract boolean hasSelfPermission(CommandSender sender);

    protected abstract void applyCommandAction(BukkitMMOPlayer mcMMOPlayer);

    protected abstract void sendSuccessMessage(CommandSender sender, String playerName);
}
