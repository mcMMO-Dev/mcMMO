package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class McrankCommandDisplayTask extends BukkitRunnable {
    private final Map<SkillType, Integer> skills;
    private final CommandSender sender;
    private final String playerName;
    private final boolean board;
    private final boolean chat;

    public McrankCommandDisplayTask(Map<SkillType, Integer> skills, CommandSender sender, String playerName, boolean board, boolean chat) {
        this.skills = skills;
        this.sender = sender;
        this.playerName = playerName;
        this.board = board && (sender instanceof Player) && ((Player)sender).isValid();
        this.chat = chat;
    }

    @Override
    public void run() {
        if (chat) displayChat();
        if (board) displayBoard();
    }

    public void displayChat() {
        Player player = mcMMO.p.getServer().getPlayer(playerName);
        Integer rank;

        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skill : SkillType.nonChildSkills()) {
            if (player != null && !Permissions.skillEnabled(player, skill)) {
                continue;
            }

            rank = skills.get(skill);
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skill), (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
        }

        rank = skills.get(null);
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
    }

    public void displayBoard() {
        if (playerName == null || sender.getName().equals(playerName)) {
            ScoreboardManager.showPlayerRankScoreboard((Player) sender, skills);
        }
        else {
            ScoreboardManager.showPlayerRankScoreboardOthers((Player) sender, playerName, skills);
        }
    }
}
