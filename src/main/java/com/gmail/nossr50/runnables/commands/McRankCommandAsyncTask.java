package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class McRankCommandAsyncTask extends CancellableRunnable {
    private final String playerName;
    private final CommandSender sender;
    private final boolean useBoard, useChat;

    public McRankCommandAsyncTask(String playerName, CommandSender sender, boolean useBoard, boolean useChat) {
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
        Map<PrimarySkillType, Integer> skills = mcMMO.getDatabaseManager().readRank(playerName);

        mcMMO.p.getFoliaLib().getScheduler().runNextTick(new McRankCommandDisplayTask(skills, sender, playerName, useBoard, useChat));
    }
}

