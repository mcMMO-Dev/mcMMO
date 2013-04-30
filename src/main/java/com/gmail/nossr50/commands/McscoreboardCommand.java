package com.gmail.nossr50.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.google.common.collect.ImmutableList;

public class McscoreboardCommand implements TabExecutor {
    private static final List<String> SCOREBOARD_TYPES = ImmutableList.of("clear", "rank", "stats", "top");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (CommandUtils.noConsoleUsage(sender)) {
            return true;
        }

        Player player = (Player) sender;

        switch (args.length) {
            case 0:
                clearScoreboard(player);
                return true;

            case 1:
                if (args[0].equalsIgnoreCase("clear")) {
                    clearScoreboard(player);
                }
                else if (args[0].equalsIgnoreCase("rank")) {
                    if (!Config.getInstance().getMcrankScoreboardEnabled()) {
                        sender.sendMessage("This scoreboard is not enabled."); //TODO: Localize
                        return true;
                    }

                    ScoreboardManager.setupPlayerScoreboard(player.getName());
                    ScoreboardManager.enablePlayerRankScoreboard(player);
                }
                else if (args[0].equalsIgnoreCase("stats")) {
                    if (!Config.getInstance().getMcstatsScoreboardsEnabled()) {
                        sender.sendMessage("This scoreboard is not enabled."); //TODO: Localize
                        return true;
                    }

                    ScoreboardManager.setupPlayerScoreboard(player.getName());
                    ScoreboardManager.enablePlayerStatsScoreboard(UserManager.getPlayer(player));
                }
                else if (args[0].equalsIgnoreCase("top")) {
                    if (!Config.getInstance().getMctopScoreboardEnabled()) {
                        sender.sendMessage("This scoreboard is not enabled."); //TODO: Localize
                        return true;
                    }

                    ScoreboardManager.enableGlobalStatsScoreboard(player, "all", 1);
                }
                else {
                    return false;
                }

                return true;

            case 2:
                if (!args[0].equalsIgnoreCase("top")) {
                    return false;
                }

                if (!Config.getInstance().getMctopScoreboardEnabled()) {
                    sender.sendMessage("This scoreboard is not enabled."); //TODO: Localize
                    return true;
                }

                if (StringUtils.isInt(args[1])) {
                    ScoreboardManager.enableGlobalStatsScoreboard(player, "all", Math.abs(Integer.parseInt(args[1])));
                    return true;
                }

                if (CommandUtils.isInvalidSkill(sender, args[1])) {
                    return true;
                }

                ScoreboardManager.enableGlobalStatsScoreboard(player, args[1], 1);
                return true;

            case 3:
                if (!args[0].equalsIgnoreCase("top")) {
                    return false;
                }

                if (!Config.getInstance().getMctopScoreboardEnabled()) {
                    sender.sendMessage("This scoreboard is not enabled."); //TODO: Localize
                    return true;
                }

                if (CommandUtils.isInvalidSkill(sender, args[1])) {
                    return true;
                }

                if (CommandUtils.isInvalidInteger(sender, args[2])) {
                    return true;
                }

                ScoreboardManager.enableGlobalStatsScoreboard(player, args[1], Math.abs(Integer.parseInt(args[2])));
                return true;

            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], SCOREBOARD_TYPES, new ArrayList<String>(SCOREBOARD_TYPES.size()));
            case 2:
                if (args[0].equalsIgnoreCase("top")) {
                    return StringUtil.copyPartialMatches(args[1], SkillType.SKILL_NAMES, new ArrayList<String>(SkillType.SKILL_NAMES.size()));
                }
                // Fallthrough

            default:
                return ImmutableList.of();
        }
    }

    private void clearScoreboard(Player player) {
        player.setScoreboard(mcMMO.p.getServer().getScoreboardManager().getMainScoreboard());
        player.sendMessage("Your scoreboard has been cleared!"); //TODO: Locale
    }
}
