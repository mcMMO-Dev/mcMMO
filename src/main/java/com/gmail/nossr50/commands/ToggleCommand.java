package com.gmail.nossr50.commands;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.google.common.collect.ImmutableList;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class ToggleCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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

                OnlineMMOPlayer mmoPlayer = mcMMO.getUserManager().queryPlayer((Player) sender);

                if(mmoPlayer != null) {
                    applyCommandAction(mmoPlayer);
                } else {
                    Misc.adaptPlayer(mmoPlayer).sendMessage(LocaleLoader.getString("Commands.NotLoaded"));
                }

                return true;

            case 1:
                if (!hasOtherPermission(sender)) {
                    sender.sendMessage(command.getPermissionMessage());
                    return true;
                }

                String playerName = CommandUtils.getMatchedPlayerName(args[0]);
                Player otherPlayer = Bukkit.getPlayer(playerName);
                OnlineMMOPlayer mmoOther = mcMMO.getUserManager().queryPlayer(otherPlayer);

                if (!CommandUtils.checkPlayerExistence(sender, playerName, mmoOther)) {
                    return true;
                }

                if(mmoOther.getPlayer().isOnline()) {
                    return false;
                }

                applyCommandAction(mmoOther);
                sendSuccessMessage(sender, playerName);
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

    protected abstract boolean hasOtherPermission(@NotNull CommandSender sender);
    protected abstract boolean hasSelfPermission(@NotNull CommandSender sender);
    protected abstract void applyCommandAction(@NotNull OnlineMMOPlayer mmoPlayer);
    protected abstract void sendSuccessMessage(@NotNull CommandSender sender, @NotNull String playerName);
}
