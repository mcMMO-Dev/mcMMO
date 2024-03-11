package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class McrankCommandAsyncTask extends CancellableRunnable {
    private final String playerName;
    private final CommandSender sender;
    private final boolean useBoard, useChat;

    public McrankCommandAsyncTask(String playerName, CommandSender sender, boolean useBoard, boolean useChat) {
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

        // If the sender is a player, actions on the player will be taken. Under folia, this needs to run on the
        // entity's scheduler.
        if (sender instanceof Player player) {
            mcMMO.p.getFoliaLib().getImpl().runAtEntityLater(
                    player,
                    new McrankCommandDisplayTask(skills, player, playerName, useBoard, useChat),
                    1
            );
        } else {
            McrankCommandDisplayTask task = new McrankCommandDisplayTask(skills, sender, playerName, useBoard, useChat);
            mcMMO.p.getFoliaLib().getImpl().runNextTick(task);
        }
    }
}

