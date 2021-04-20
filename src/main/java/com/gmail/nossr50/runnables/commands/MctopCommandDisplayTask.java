package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Display the results of {@link MctopCommandAsyncTask} to the sender.
 */
public class MctopCommandDisplayTask extends BukkitRunnable {
    private final List<PlayerStat> userStats;
    private final CommandSender sender;
    private final @Nullable PrimarySkillType primarySkillType;
    private final int page;
    private final boolean useBoard, useChat;

    MctopCommandDisplayTask(@NotNull List<PlayerStat> userStats, int page, @Nullable PrimarySkillType primarySkillType, @NotNull CommandSender sender, boolean useBoard, boolean useChat) {
        this.userStats = userStats;
        this.page = page;
        this.primarySkillType = primarySkillType;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard && mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            displayBoard();
        }

        if (useChat) {
            displayChat();
        }

        if (sender instanceof Player) {
            ((Player) sender).removeMetadata(mcMMO.databaseCommandKey, mcMMO.p);
        }
        if(sender instanceof Player)
            sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
    }

    private void displayChat() {
        if (primarySkillType == null) {
            if(sender instanceof Player) {
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
            }
            else {
                sender.sendMessage(ChatColor.stripColor(LocaleLoader.getString("Commands.PowerLevel.Leaderboard")));
            }
        }
        else {
            if(sender instanceof Player) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", mcMMO.p.getSkillTools().getLocalizedSkillName(skill)));
            }
            else {
                sender.sendMessage(ChatColor.stripColor(LocaleLoader.getString("Commands.Skill.Leaderboard", mcMMO.p.getSkillTools().getLocalizedSkillName(skill))));
            }
        }

        int place = (page * 10) - 9;

        for (PlayerStat stat : userStats) {
            // Format:
            // 01. Playername - skill value
            // 12. Playername - skill value
            if(sender instanceof Player) {
                sender.sendMessage(String.format("%2d. %s%s - %s%s", place, ChatColor.GREEN, stat.name, ChatColor.WHITE, stat.statVal));
            }
            else {
                sender.sendMessage(String.format("%2d. %s - %s", place, stat.name, stat.statVal));
            }
            
            place++;
        }
    }

    private void displayBoard() {
        if (primarySkillType == null) {
            ScoreboardManager.showTopPowerScoreboard((Player) sender, page, userStats);
        }
        else {
            ScoreboardManager.showTopScoreboard((Player) sender, primarySkillType, page, userStats);
        }
    }
}
