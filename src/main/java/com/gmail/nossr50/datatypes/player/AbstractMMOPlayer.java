package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.experience.OfflineExperienceProcessor;
import com.gmail.nossr50.datatypes.experience.OnlineExperienceProcessor;
import com.neetgames.mcmmo.experience.ExperienceProcessor;
import com.neetgames.mcmmo.player.MMOPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMMOPlayer implements MMOPlayer {
    /* All of the persistent data for a player that gets saved and loaded from DB */
    protected final @NotNull PlayerData playerData; //All persistent data is kept here
    protected @Nullable Player player = null;

    /* Managers */
    protected final @Nullable ExperienceProcessor experienceProcessor;
    protected final @NotNull CooldownManager cooldownManager;
    protected boolean isLoaded;

    /**
     * Init for online players
     * This will be used for existing data
     *
     * @param playerData player data
     */
    public AbstractMMOPlayer(@Nullable Player player, @NotNull PlayerData playerData, boolean isLoaded) {
        this.playerData = playerData;

        if(player != null)
            this.player = player;

        this.isLoaded = isLoaded;

        if(isLoaded) {
            if(player != null && player.isOnline()) {
                //Online Player
                this.experienceProcessor = new OnlineExperienceProcessor(player, playerData);
            } else {
                //Offline Player
                this.experienceProcessor = new OfflineExperienceProcessor(player, playerData);
            }
        } else {
            //Invalid Player (no loaded data) so experience operations are pointless
            this.experienceProcessor = null;
        }

        this.cooldownManager = new CooldownManager(playerData);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
