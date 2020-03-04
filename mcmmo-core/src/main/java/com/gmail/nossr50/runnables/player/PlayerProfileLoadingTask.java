package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;
import com.gmail.nossr50.runnables.commands.ScoreboardKeepTask;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class PlayerProfileLoadingTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private final Player player;
    private int attempt = 0;

    public PlayerProfileLoadingTask(mcMMO pluginRef, Player player) {
        this.pluginRef = pluginRef;
        this.player = player;
    }

    private PlayerProfileLoadingTask(mcMMO pluginRef, Player player, int attempt) {
        this.pluginRef = pluginRef;
        this.player = player;
        this.attempt = attempt;
    }

    // WARNING: ASYNC TASK
    // DO NOT MODIFY THE McMMOPLAYER FROM THIS CODE
    @Override
    public void accept(Task task) {
        if (pluginRef.getMiscTools().isNPCIncludingVillagers(player)) {
            return;
        }

        // Quit if they logged out
        if (!player.isOnline()) {
            pluginRef.getLogger().info("Aborting profile loading recovery for " + player.getName() + " - player logged out");
            return;
        }

        PlayerProfile profile = pluginRef.getDatabaseManager().loadPlayerProfile(player.getName(), player.getUniqueId(), true);
        // If successful, schedule the apply
        if (profile.isLoaded()) {
            pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                    .setTask(new ApplySuccessfulProfile(new BukkitMMOPlayer(player, profile, pluginRef)))
                    .schedule();
            pluginRef.getEventManager().callPlayerProfileLoadEvent(player, profile);
            return;
        }

        // Print errors to console/logs if we're failing at least 2 times in a row to load the profile
        if (attempt >= 3) {
            //Log the error
            pluginRef.getLogger().severe(pluginRef.getLocaleManager().getString("Profile.Loading.FailureNotice",
                    player.getName(), String.valueOf(attempt)));

            //Notify the admins
            Bukkit.getServer().broadcast(pluginRef.getLocaleManager().getString("Profile.Loading.FailureNotice", player.getName()), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

            //Notify the player
            player.sendMessage(pluginRef.getLocaleManager().getString("Profile.Loading.FailurePlayer", String.valueOf(attempt)).split("\n"));
        }

        // Increment attempt counter and try
        attempt++;
        pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                .setAsync(true)
                .setDelay((long) (100 + (attempt * 100)))
                .setTask(new PlayerProfileLoadingTask(pluginRef, player, attempt))
                .schedule();
    }

    private class ApplySuccessfulProfile implements Consumer<Task>  {
        private final BukkitMMOPlayer mcMMOPlayer;

        private ApplySuccessfulProfile(BukkitMMOPlayer mcMMOPlayer) {
            this.mcMMOPlayer = mcMMOPlayer;
        }

        // Synchronized task
        // No database access permitted
        @Override
        public void accept(Task task) {
            if (!player.isOnline()) {
                pluginRef.getLogger().info("Aborting profile loading recovery for " + player.getName() + " - player logged out");
                return;
            }

            mcMMOPlayer.setupPartyData();
            pluginRef.getUserManager().track(mcMMOPlayer);
            mcMMOPlayer.actualizeRespawnATS();

            if (pluginRef.getScoreboardSettings().getScoreboardsEnabled()) {
                pluginRef.getScoreboardManager().setupPlayer(player);

                if (pluginRef.getScoreboardSettings().getShowStatsAfterLogin()) {
                    pluginRef.getScoreboardManager().enablePlayerStatsScoreboard(player);
                    pluginRef.getPlatformProvider().getScheduler().getTaskBuilder()
                            .setDelay(pluginRef.getMiscTools().TICK_CONVERSION_FACTOR)
                            .setTask(new ScoreboardKeepTask(pluginRef, player))
                            .schedule();
                }
            }

            if (pluginRef.getConfigManager().getConfigNotifications().isShowProfileLoadedMessage()) {
                player.sendMessage(pluginRef.getLocaleManager().getString("Profile.Loading.Success"));
            }

        }
    }
}
