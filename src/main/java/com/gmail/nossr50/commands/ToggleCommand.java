package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public abstract class ToggleCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                if (CommandUtils.noConsoleUsage(sender)) {
                    return true;
                }

                if (!hasSelfPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                if (!CommandUtils.hasPlayerDataKey(sender)) {
                    return true;
                }

                applyCommandAction(UserManager.getPlayer(sender.getName()));
                return true;

            case 1:
                if (!hasOtherPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                McMMOPlayer mcMMOPlayer = UserManager.getPlayer(playerName);

                if (!CommandUtils.checkPlayerExistence(sender, playerName, mcMMOPlayer)) {
                    return true;
                }

                if (CommandUtils.isOffline(sender, mcMMOPlayer.getPlayer())) {
                    return true;
                }

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
                List<String> playerNames = CommandUtils.getOnlinePlayerNames(sender);
                return StringUtil.copyPartialMatches(args[0], playerNames, new ArrayList<String>(playerNames.size()));
            default:
                return ImmutableList.of();
        }
    }

    protected abstract boolean hasOtherPermission(CommandSender sender);
    protected abstract boolean hasSelfPermission(CommandSender sender);
    protected abstract void applyCommandAction(McMMOPlayer mcMMOPlayer);
    protected abstract void sendSuccessMessage(CommandSender sender, String playerName);
}
