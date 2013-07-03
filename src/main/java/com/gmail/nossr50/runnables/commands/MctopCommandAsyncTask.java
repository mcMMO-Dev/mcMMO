package com.gmail.nossr50.runnables.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class MctopCommandAsyncTask extends BukkitRunnable {
    private final CommandSender sender;
    private final SkillType skill;
    private final int page;
    private final boolean board;
    private final boolean chat;

    public MctopCommandAsyncTask(int page, SkillType skill, CommandSender sender, boolean board, boolean chat) {
        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.board = board;
        this.chat = chat;
    }

    @Override
    public void run() {
        List<PlayerStat> userStats;
        if (skill == null) {
            userStats = mcMMO.getDatabaseManager().readLeaderboard("all", page, 10);
        }
        else {
            userStats = mcMMO.getDatabaseManager().readLeaderboard(skill.name(), page, 10);
        }

        new MctopCommandDisplayTask(userStats, page, skill, sender, board, chat).runTask(mcMMO.p);
    }
}
