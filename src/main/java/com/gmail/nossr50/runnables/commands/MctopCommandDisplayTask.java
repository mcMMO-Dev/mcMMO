package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Display the results of {@link McTopCommandAsyncTask} to the sender.
 */
public class MctopCommandDisplayTask extends CancellableRunnable {
    private final List<PlayerStat> userStats;
    private final CommandSender sender;
    private final PrimarySkillType skill;
    private final int page;
    private final boolean useBoard, useChat;

    MctopCommandDisplayTask(List<PlayerStat> userStats, int page, PrimarySkillType skill,
            CommandSender sender, boolean useBoard, boolean useChat) {
        this.userStats = userStats;
        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        if (useBoard && mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            displayBoard();
        }

        if (useChat) {
            displayChat();
        }

        if (sender instanceof Player) {
            ((Player) sender).removeMetadata(MetadataConstants.METADATA_KEY_DATABASE_COMMAND,
                    mcMMO.p);
        }
        if (sender instanceof Player) {
            sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
        }
    }

    private void displayChat() {

        if (skill == null) {
            if (sender instanceof Player) {
                sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
            } else {
                sender.sendMessage(ChatColor.stripColor(
                        LocaleLoader.getString("Commands.PowerLevel.Leaderboard")));
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard",
                        mcMMO.p.getSkillTools().getLocalizedSkillName(skill)));
            } else {
                sender.sendMessage(ChatColor.stripColor(
                        LocaleLoader.getString("Commands.Skill.Leaderboard",
                                mcMMO.p.getSkillTools().getLocalizedSkillName(skill))));
            }
        }

        int place = (page * 10) - 9;

        for (PlayerStat stat : userStats) {
            // Format:
            // 01. Playername - skill value
            // 12. Playername - skill value
            if (sender instanceof Player) {
                sender.sendMessage(
                        String.format("%2d. %s%s - %s%s", place, ChatColor.GREEN, stat.name,
                                ChatColor.WHITE, stat.statVal));
            } else {
                sender.sendMessage(String.format("%2d. %s - %s", place, stat.name, stat.statVal));
            }

            place++;
        }
    }

    private void displayBoard() {
        if (skill == null) {
            ScoreboardManager.showTopPowerScoreboard((Player) sender, page, userStats);
        } else {
            ScoreboardManager.showTopScoreboard((Player) sender, skill, page, userStats);
        }
    }
}
