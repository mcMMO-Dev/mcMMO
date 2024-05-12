package com.gmail.nossr50.commands;

import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class McscoreboardCommand implements TabExecutor {
    private static final List<String> FIRST_ARGS = ImmutableList.of("keep", "time", "clear");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        if (!mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            sender.sendMessage(LocaleLoader.getString("Scoreboard.Disabled"));
            return true;
        }

        if (!ScoreboardManager.isPlayerBoardSetup(sender.getName())) {
            sender.sendMessage(LocaleLoader.getString("Scoreboard.NotSetupYet"));
            return true;
        }

        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("reset")) {
                    ScoreboardManager.clearBoard(sender.getName());
                    sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Clear"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("keep")) {
                    if (!mcMMO.p.getGeneralConfig().getAllowKeepBoard() || !mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                        sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
                        return true;
                    }

                    if (!ScoreboardManager.isBoardShown(sender.getName())) {
                        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.NoBoard"));
                        return true;
                    }

                    ScoreboardManager.keepBoard(sender.getName());
                    sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Keep"));
                    return true;
                }

                return help(sender);

            case 2:
                if (args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("timer")) {
                    if (CommandUtils.isInvalidInteger(sender, args[1])) {
                        return true;
                    }

                    int time = Math.abs(Integer.parseInt(args[1]));

                    ScoreboardManager.setRevertTimer(sender.getName(), time);
                    sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Timer", time));
                    return true;
                }

                return help(sender);

            default:
                return help(sender);
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], FIRST_ARGS, new ArrayList<>(FIRST_ARGS.size()));
        }
        return ImmutableList.of();
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.0"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.1"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.2"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.3"));
        return true;
    }
}
