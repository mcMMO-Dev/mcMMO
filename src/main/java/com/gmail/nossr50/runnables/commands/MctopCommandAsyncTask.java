package com.gmail.nossr50.runnables.commands;

import com.gmail.nossr50.datatypes.database.PlayerStat;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.skill.RootSkill;
import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MctopCommandAsyncTask extends BukkitRunnable {
    private final @NotNull CommandSender sender;
    private final @Nullable RootSkill rootSkill;
    private final int page;
    private final boolean useBoard, useChat;

    public MctopCommandAsyncTask(int page, @Nullable RootSkill rootSkill, @NotNull CommandSender sender, boolean useBoard, boolean useChat) {
        Validate.isTrue(useBoard || useChat, "Attempted to start a rank retrieval with both board and chat off");
        Validate.notNull(sender, "Attempted to start a rank retrieval with no recipient");

        if (useBoard) {
            Validate.isTrue(sender instanceof Player, "Attempted to start a rank retrieval displaying scoreboard to a non-player");
        }

        this.page = page;
        this.rootSkill = rootSkill;
        this.sender = sender;
        this.useBoard = useBoard;
        this.useChat = useChat;
    }

    @Override
    public void run() {
        final List<PlayerStat> userStats = mcMMO.getDatabaseManager().readLeaderboard(rootSkill, page, 10);

        new MctopCommandDisplayTask(userStats, page, rootSkill, sender, useBoard, useChat).runTaskLater(mcMMO.p, 1);
    }
}
