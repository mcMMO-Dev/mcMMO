package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;

public class McscoreboardCommand implements TabExecutor {
    private static final List<String> FIRST_ARGS = ImmutableList.of("keep", "time", "clear", "reset");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }
        if (args.length == 0) {
            help(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("reset")) {
            ScoreboardManager.clearBoard(sender.getName());
            sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Clear"));
        }
        else if (args[0].equalsIgnoreCase("keep")) {
            if (!Config.getInstance().getAllowKeepBoard()) {
                sender.sendMessage(LocaleLoader.getString("Commands.Disabled"));
                return true;
            }
            if (!ScoreboardManager.isBoardShown(sender.getName())) {
                sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.NoBoard"));
                return true;
            }
            ScoreboardManager.keepBoard(sender.getName());
            sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Keep"));
        }
        else if (args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("timer")) {
            if (args.length == 1) {
                help(sender);
                return true;
            }
            if (CommandUtils.isInvalidInteger(sender, args[1])) {
                return true;
            }
            ScoreboardManager.setRevertTimer(sender.getName(), Math.abs(Integer.parseInt(args[1])));
        }
        else {
            help(sender);
        }
        return true;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.0"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.1"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.2"));
        sender.sendMessage(LocaleLoader.getString("Commands.Scoreboard.Help.3"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], FIRST_ARGS, new ArrayList<String>(FIRST_ARGS.size()));
            default:
                break;
        }
        return ImmutableList.of();
    }
}
