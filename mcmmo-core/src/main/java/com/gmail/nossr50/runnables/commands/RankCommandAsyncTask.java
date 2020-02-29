package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class RankCommandAsyncTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final String playerName;
    private final CommandSender sender;
    private final boolean useBoard, useChat;

    public RankCommandAsyncTask(mcMMO pluginRef, String playerName, CommandSender sender, boolean useBoard, boolean useChat) {
        this.pluginRef = pluginRef;
        Validate.isTrue(useBoard || useChat, "Attempted to start a rank retrieval with both board and chat off");
        Validate.notNull(sender, "Attempted to start a rank retrieval with no recipient");

        if (useBoard) {
            Validate.isTrue(sender instanceof Player, "Attempted to start a rank retrieval displaying scoreboard to a non-player");
        }

        this.playerName = playerName;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        Map<PrimarySkillType, Integer> skills = pluginRef.getDatabaseManager().readRank(playerName);

        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                .setDelay(1L)
                .setTask(new RankCommandDisplayTask(pluginRef, skills, sender, playerName, useBoard, useChat))
                .schedule();
    }
}

