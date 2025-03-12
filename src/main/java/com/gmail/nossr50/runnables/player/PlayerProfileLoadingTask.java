package com.gmail.nossr50.runnables.player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.commands.McScoreboardKeepTask;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class PlayerProfileLoadingTask extends CancellableRunnable {
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
            LogUtils.debug(mcMMO.p.getLogger(), "Aborting profile loading recovery for " + player.getName() + " - player logged out");
            return;
        }

        PlayerProfile profile = mcMMO.getDatabaseManager().loadPlayerProfile(player);

        if (!profile.isLoaded()) {
            LogUtils.debug(mcMMO.p.getLogger(), "Creating new data for player: "+player.getName());
            //Profile isn't loaded so add as new user
            profile = mcMMO.getDatabaseManager().newUser(player);
        }

        // If successful, schedule the apply
        if (profile.isLoaded()) {
            mcMMO.p.getFoliaLib().getScheduler().runAtEntity(player, new ApplySuccessfulProfile(new McMMOPlayer(player, profile)));
            EventUtils.callPlayerProfileLoadEvent(player, profile);
            return;
        }

        // Print errors to console/logs if we're failing at least 2 times in a row to load the profile
        if (attempt >= 3) {
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

        mcMMO.p.getFoliaLib().getScheduler().runLaterAsync(new PlayerProfileLoadingTask(player, attempt), (100 + (attempt * 100L)));
    }

    private class ApplySuccessfulProfile extends CancellableRunnable {
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

            mcMMOPlayer.getProfile().updateLastLogin();

            mcMMOPlayer.setupPartyData();
            UserManager.track(mcMMOPlayer);
            mcMMOPlayer.actualizeRespawnATS();

            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.setupPlayer(player);

                if (mcMMO.p.getGeneralConfig().getShowStatsAfterLogin()) {
                    ScoreboardManager.enablePlayerStatsScoreboard(player);
                    mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(player, new McScoreboardKeepTask(player), Misc.TICK_CONVERSION_FACTOR);
                }
            }

            if (mcMMO.p.getGeneralConfig().getShowProfileLoadedMessage()) {
                player.sendMessage(LocaleLoader.getString("Profile.Loading.Success"));
            }

        }
    }
}
