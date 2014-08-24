package com.gmail.nossr50.runnables.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;

/**
 * Display the results of {@link MctopCommandAsyncTask} to the sender.
 */
public class MctopCommandDisplayTask extends BukkitRunnable {
    private final List<PlayerStat> userStats;
    private final CommandSender sender;
    private final SkillType skill;
    private final int page;
    private final boolean useBoard, useChat;

    MctopCommandDisplayTask(List<PlayerStat> userStats, int page, SkillType skill, CommandSender sender, boolean useBoard, boolean useChat) {
        this.userStats = userStats;
        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard) {
            displayBoard();
        }

        if (useChat) {
            displayChat();
        }

        if (sender instanceof Player) {
            ((Player) sender).removeMetadata(mcMMO.databaseCommandKey, mcMMO.p);
        }
        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }

    private void displayChat() {
        if (skill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", skill.getName()));
        }

        int place = (page * 10) - 9;

        for (PlayerStat stat : userStats) {
            // Format:
            // 01. Playername - skill value
            // 12. Playername - skill value
            sender.sendMessage(String.format("%2d. %s%s - %s%s", place, ChatColor.GREEN, stat.name, ChatColor.WHITE, stat.statVal));
            place++;
        }
    }

    private void displayBoard() {
        if (skill == null) {
            ScoreboardManager.showTopPowerScoreboard((Player) sender, page, userStats);
        }
        else {
            ScoreboardManager.showTopScoreboard((Player) sender, skill, page, userStats);
        }
    }
}
