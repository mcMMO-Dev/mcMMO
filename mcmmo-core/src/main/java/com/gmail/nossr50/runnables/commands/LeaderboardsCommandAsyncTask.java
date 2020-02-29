package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LeaderboardsCommandAsyncTask extends BukkitRunnable {
    private final mcMMO pluginRef;
    private final CommandSender sender;
    private final PrimarySkillType skill;
    private final int page;
    private final boolean useBoard, useChat;

    public LeaderboardsCommandAsyncTask(mcMMO pluginRef, int page, PrimarySkillType skill, CommandSender sender, boolean useBoard, boolean useChat) {
        this.pluginRef = pluginRef;

        Validate.isTrue(useBoard || useChat, "Attempted to start a rank retrieval with both board and chat off");
        Validate.notNull(sender, "Attempted to start a rank retrieval with no recipient");

        if (useBoard) {
            Validate.isTrue(sender instanceof Player, "Attempted to start a rank retrieval displaying scoreboard to a non-player");
        }

        this.page = page;
        this.skill = skill;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        final List<PlayerStat> userStats = pluginRef.getDatabaseManager().readLeaderboard(skill, page, 10);

        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                .setDelay(1L)
                .setTask(new LeaderboardsCommandDisplayTask(pluginRef, userStats, page, skill, sender, useBoard, useChat))
                .schedule();
    }
}
