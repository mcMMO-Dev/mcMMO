package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableList;

public abstract class ToggleCommand implements TabExecutor {
    protected McMMOPlayer mcMMOPlayer;
    protected Player player;

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

                mcMMOPlayer = UserManager.getPlayer(sender.getName());
                player = mcMMOPlayer.getPlayer();

                applyCommandAction();
                return true;

            case 1:
                if (!hasOtherPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                mcMMOPlayer = UserManager.getPlayer(args[0]);

                if (!CommandUtils.checkPlayerExistence(sender, args[0], mcMMOPlayer)) {
                    return true;
                }

                player = mcMMOPlayer.getPlayer();

                if (CommandUtils.isOffline(sender, player)) {
                    return true;
                }

                applyCommandAction();
                sendSuccessMessage(sender);
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

    protected abstract boolean hasOtherPermission(CommandSender sender);
    protected abstract boolean hasSelfPermission(CommandSender sender);
    protected abstract void applyCommandAction();
    protected abstract void sendSuccessMessage(CommandSender sender);
}
