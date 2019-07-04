package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Display the results of {@link LeaderboardsCommandAsyncTask} to the sender.
 */
public class LeaderboardsCommandDisplayTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final List<PlayerStat> userStats;
    private final CommandSender sender;
    private final PrimarySkillType skill;
    private final int page;
    private final boolean useBoard, useChat;

    LeaderboardsCommandDisplayTask(mcMMO pluginRef, List<PlayerStat> userStats, int page, PrimarySkillType skill, CommandSender sender, boolean useBoard, boolean useChat) {
        this.pluginRef = pluginRef;
        this.userStats = userStats;
        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard && pluginRef.getScoreboardSettings().getScoreboardsEnabled()) {
            displayBoard();
        }

        if (useChat) {
            displayChat();
        }

        if (sender instanceof Player) {
            ((Player) sender).removeMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY, pluginRef);
        }
        if (sender instanceof Player)
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mctop.Tip"));
    }

    private void displayChat() {

        if (skill == null) {
            if (sender instanceof Player) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.PowerLevel.Leaderboard"));
            } else {
                sender.sendMessage(ChatColor.stripColor(pluginRef.getLocaleManager().getString("Commands.PowerLevel.Leaderboard")));
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Skill.Leaderboard", pluginRef.getSkillTools().getLocalizedSkillName(skill)));
            } else {
                sender.sendMessage(ChatColor.stripColor(pluginRef.getLocaleManager().getString("Commands.Skill.Leaderboard", pluginRef.getSkillTools().getLocalizedSkillName(skill))));
            }
        }

        int place = (page * 10) - 9;

        for (PlayerStat stat : userStats) {
            // Format:
            // 01. Playername - skill value
            // 12. Playername - skill value
            if (sender instanceof Player) {
                sender.sendMessage(String.format("%2d. %s%s - %s%s", place, ChatColor.GREEN, stat.name, ChatColor.WHITE, stat.statVal));
            } else {
                sender.sendMessage(String.format("%2d. %s - %s", place, stat.name, stat.statVal));
            }

            place++;
        }
    }

    private void displayBoard() {
        if (skill == null) {
            pluginRef.getScoreboardManager().showTopPowerScoreboard((Player) sender, page, userStats);
        } else {
            pluginRef.getScoreboardManager().showTopScoreboard((Player) sender, skill, page, userStats);
        }
    }
}
