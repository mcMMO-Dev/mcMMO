package com.gmail.nossr50.runnables.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class MctopCommandDisplayTask extends BukkitRunnable {
    private final List<PlayerStat> userStats;
    private final CommandSender sender;
    private final SkillType skill;
    private final int page;
    private final boolean board;
    private final boolean chat;

    public MctopCommandDisplayTask(List<PlayerStat> userStats, int page, SkillType skill, CommandSender sender, boolean board, boolean chat) {
        this.userStats = userStats;
        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.board = board && (sender instanceof Player) && ((Player)sender).isValid();
        this.chat = chat;
    }

    @Override
    public void run() {
        sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
        if (chat) displayChat();
        if (board) displayBoard();
    }

    public void displayChat() {
        if (skill == null) {
            sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
        }
        else {
            sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", SkillUtils.getSkillName(skill)));
        }

        int place = (page * 10) - 9;

        for (PlayerStat stat : userStats) {
            String digit = ((place < 10) ? "0" : "") + String.valueOf(place);

            // Format: 1. Playername - skill value
            sender.sendMessage(digit + ". " + ChatColor.GREEN + stat.name + " - " + ChatColor.WHITE + stat.statVal);
            place++;
        }
    }

    public void displayBoard() {
        if (skill == null) {
            ScoreboardManager.showTopPowerScoreboard((Player) sender, page, userStats);
        }
        else {
            ScoreboardManager.showTopScoreboard((Player) sender, skill, page, userStats);
        }
    }
}
