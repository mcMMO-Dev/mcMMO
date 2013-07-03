package com.gmail.nossr50.runnables.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class McrankCommandAsyncTask extends BukkitRunnable {
    private final String playerName;
    private final CommandSender sender;
    private final boolean board;
    private final boolean chat;

    public McrankCommandAsyncTask(String playerName, CommandSender sender, boolean board, boolean chat) {
        this.playerName = playerName;
        this.sender = sender;
        this.board = board;
        this.chat = chat;
    }

    @Override
    public void run() {
        Map<SkillType, Integer> skills = mcMMO.getDatabaseManager().readRank(playerName);

        new McrankCommandDisplayTask(skills, sender, playerName, board, chat).runTask(mcMMO.p);
    }
}

