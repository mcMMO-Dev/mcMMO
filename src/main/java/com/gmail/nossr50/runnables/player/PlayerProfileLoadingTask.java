package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McScoreboardKeepTask;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfileLoadingTask extends BukkitRunnable {
    private final Player player;
    private int attempt = 0;

    public PlayerProfileLoadingTask(Player player) {
        this.player = player;
    }

    private PlayerProfileLoadingTask(Player player, int attempt) {
        this.player = player;
        this.attempt = attempt;
    }

    // WARNING: ASYNC TASK
    // DO NOT MODIFY THE McMMOPLAYER FROM THIS CODE
    @Override
    public void run() {

        if (Misc.isNPCIncludingVillagers(player)) {
            return;
        }

        // Quit if they logged out
        if (!player.isOnline()) {
            mcMMO.p.getLogger().info("Aborting profile loading recovery for " + player.getName() + " - player logged out");
            return;
        }

        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(player.getUniqueId(), player.getName());

        if(!profile.isLoaded()) {
            mcMMO.p.getLogger().info("Creating new data for player: "+player.getName());
            //Profile isn't loaded so add as new user
            profile = mcMMO.getDatabaseManager().newUser(player);
        }

        // If successful, schedule the apply
        if (profile.isLoaded()) {
            new ApplySuccessfulProfile(new McMMOPlayer(player, profile)).runTask(mcMMO.p);
            EventUtils.callPlayerProfileLoadEvent(player, profile);
            return;
        }

        // Print errors to console/logs if we're failing at least 2 times in a row to load the profile
        if (attempt >= 3)
        {
            //Log the error
            mcMMO.p.getLogger().severe(LocaleLoader.getString("Profile.Loading.FailureNotice",
                    player.getName(), String.valueOf(attempt)));

            //Notify the admins
            mcMMO.p.getServer().broadcast(LocaleLoader.getString("Profile.Loading.FailureNotice", player.getName()), Server.BROADCAST_CHANNEL_ADMINISTRATIVE);

            //Notify the player
            player.sendMessage(LocaleLoader.getString("Profile.Loading.FailurePlayer", String.valueOf(attempt)).split("\n"));
        }

        // Increment attempt counter and try
        attempt++;

        new PlayerProfileLoadingTask(player, attempt).runTaskLaterAsynchronously(mcMMO.p, (100 + (attempt * 100)));
    }

    private class ApplySuccessfulProfile extends BukkitRunnable {
        private final McMMOPlayer mcMMOPlayer;

        private ApplySuccessfulProfile(McMMOPlayer mcMMOPlayer) {
            this.mcMMOPlayer = mcMMOPlayer;
        }

        // Synchronized task
        // No database access permitted
        @Override
        public void run() {
            if (!player.isOnline()) {
                mcMMO.p.getLogger().info("Aborting profile loading recovery for " + player.getName() + " - player logged out");
                return;
            }

            mcMMOPlayer.setupPartyData();
            UserManager.track(mcMMOPlayer);
            mcMMOPlayer.actualizeRespawnATS();

            if (Config.getInstance().getScoreboardsEnabled()) {
                ScoreboardManager.setupPlayer(player);

                if (Config.getInstance().getShowStatsAfterLogin()) {
                    ScoreboardManager.enablePlayerStatsScoreboard(player);
                    new McScoreboardKeepTask(player).runTaskLater(mcMMO.p, Misc.TICK_CONVERSION_FACTOR);
                }
            }

            if (Config.getInstance().getShowProfileLoadedMessage()) {
                player.sendMessage(LocaleLoader.getString("Profile.Loading.Success"));
            }

        }
    }
}
