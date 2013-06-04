package com.gmail.nossr50.runnables.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.database.PlayerStat;

public class MctopCommandAsyncTask extends BukkitRunnable {
    private CommandSender sender;
    private String skill;
    private int page;

    public MctopCommandAsyncTask(int page, String skill, CommandSender sender) {
        this.page = page;
        this.skill = skill;
        this.sender = sender;
    }

    @Override
    public void run() {
        final List<PlayerStat> userStats = mcMMO.getDatabaseManager().readLeaderboard(skill, page, 10);

        new MctopCommandDisplayTask(userStats, page, skill, sender).runTaskLater(mcMMO.p, 1);
    }
}
