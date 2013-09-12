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

/**
 * Display the results of McrankCommandAsyncTask to the sender.
 */
public class McrankCommandDisplayTask extends BukkitRunnable {
    private final Map<SkillType, Integer> skills;
    private final CommandSender sender;
    private final String playerName;
    private final boolean useBoard, useChat;

    /*package-private*/ McrankCommandDisplayTask(Map<SkillType, Integer> skills, CommandSender sender, String playerName, boolean useBoard, boolean useChat) {
        this.skills = skills;
        this.sender = sender;
        this.playerName = playerName;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard) {
            displayBoard();
        }
        if (useChat){
            displayChat();
        }
    }

    private void displayChat() {
        Player player = mcMMO.p.getServer().getPlayerExact(playerName);
        Integer rank;

        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Heading"));
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Player", playerName));

        for (SkillType skill : SkillType.NON_CHILD_SKILLS) {
            if (player != null && !Permissions.skillEnabled(player, skill)) {
                continue;
            }

            rank = skills.get(skill);
            sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Skill", skill.getSkillName(), (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
        }

        rank = skills.get(null);
        sender.sendMessage(LocaleLoader.getString("Commands.mcrank.Overall", (rank == null ? LocaleLoader.getString("Commands.mcrank.Unranked") : rank)));
    }

    public void displayBoard() {
        if (playerName == null || sender.getName().equalsIgnoreCase(playerName)) {
            ScoreboardManager.showPlayerRankScoreboard((Player) sender, skills);
        }
        else {
            ScoreboardManager.showPlayerRankScoreboardOthers((Player) sender, playerName, skills);
        }
    }
}
