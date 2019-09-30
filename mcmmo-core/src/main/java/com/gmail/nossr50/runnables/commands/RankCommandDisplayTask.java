package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * Display the results of RankCommandAsyncTask to the sender.
 */
public class RankCommandDisplayTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final Map<PrimarySkillType, Integer> skills;
    private final CommandSender sender;
    private final String playerName;
    private final boolean useBoard, useChat;

    RankCommandDisplayTask(mcMMO pluginRef, Map<PrimarySkillType, Integer> skills, CommandSender sender, String playerName, boolean useBoard, boolean useChat) {
        this.pluginRef = pluginRef;
        this.skills = skills;
        this.sender = sender;
        this.playerName = playerName;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard && pluginRef.getScoreboardSettings().getScoreboardsEnabled()) {
            displayBoard();
        }

        if (useChat) {
            displayChat();
        }
        ((Player) sender).removeMetadata(MetadataConstants.DATABASE_PROCESSING_COMMAND_METAKEY, pluginRef);
    }

    private void displayChat() {
//        Player player = mcMMO.p.getServer().getPlayerExact(playerName);
        Integer rank;

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcrank.Heading"));
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcrank.Player", playerName));

        for (PrimarySkillType skill : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
//            if (!skill.getPermissions(player)) {
//                continue;
//            }

            rank = skills.get(skill);
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcrank.Skill", pluginRef.getSkillTools().getLocalizedSkillName(skill), (rank == null ? pluginRef.getLocaleManager().getString("Commands.mcrank.Unranked") : rank)));
        }

        rank = skills.get(null);
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcrank.Overall", (rank == null ? pluginRef.getLocaleManager().getString("Commands.mcrank.Unranked") : rank)));
    }

    public void displayBoard() {
        if (sender.getName().equalsIgnoreCase(playerName)) {
            pluginRef.getScoreboardManager().showPlayerRankScoreboard((Player) sender, skills);
        } else {
            pluginRef.getScoreboardManager().showPlayerRankScoreboardOthers((Player) sender, playerName, skills);
        }
    }
}
