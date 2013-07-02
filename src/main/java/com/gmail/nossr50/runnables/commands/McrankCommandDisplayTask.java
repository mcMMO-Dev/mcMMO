package com.gmail.nossr50.runnables.commands;

import java.util.HashMap;
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
    private final Map<String, Integer> skills;
    private final CommandSender sender;
    private final String playerName;
    private final boolean board;
    private final boolean chat;

    public McrankCommandDisplayTask(Map<String, Integer> skills, CommandSender sender, String playerName, boolean board, boolean chat) {
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

            rank = skills.get(skill.name());
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", SkillUtils.getSkillName(skill), (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
        }

        rank = skills.get("ALL");
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
    }

    public void displayBoard() {
        // XXX Conversion code, remove ASAP!
        Map<SkillType, Integer> rankMap = new HashMap<SkillType, Integer>();
        Integer rank;
        for (SkillType skill : SkillType.nonChildSkills()) {
            rank = skills.get(skill.name());
            if (rank != null) {
                rankMap.put(skill, rank);
            }
        }
        rank = skills.get("ALL");
        rankMap.put(null, rank);

        if (playerName == null || sender.getName().equals(playerName)) {
            ScoreboardManager.showPlayerRankScoreboard((Player) sender, rankMap);
        }
        else {
            ScoreboardManager.showPlayerRankScoreboardOthers((Player) sender, playerName, rankMap);
        }
    }
}
