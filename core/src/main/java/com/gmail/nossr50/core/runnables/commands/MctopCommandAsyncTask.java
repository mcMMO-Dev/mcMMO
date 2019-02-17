package com.gmail.nossr50.core.runnables.commands;

import com.gmail.nossr50.core.datatypes.database.PlayerStat;
import com.gmail.nossr50.core.mcmmo.commands.CommandSender;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.skills.PrimarySkillType;

import java.util.List;

public class MctopCommandAsyncTask extends BukkitRunnable {
    private final CommandSender sender;
    private final PrimarySkillType skill;
    private final int page;
    private final boolean useBoard, useChat;

    public MctopCommandAsyncTask(int page, PrimarySkillType skill, CommandSender sender, boolean useBoard, boolean useChat) {
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
        final List<PlayerStat> userStats = mcMMO.getDatabaseManager().readLeaderboard(skill, page, 10);

        new MctopCommandDisplayTask(userStats, page, skill, sender, useBoard, useChat).runTaskLater(mcMMO.p, 1);
    }
}
