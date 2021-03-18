package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.experience.OfflineExperienceProcessor;
import com.gmail.nossr50.datatypes.experience.OnlineExperienceProcessor;
import com.neetgames.mcmmo.experience.ExperienceProcessor;
import com.neetgames.mcmmo.player.MMOPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMMOPlayer implements MMOPlayer {
    /* All of the persistent data for a player that gets saved and loaded from DB */
    protected final @NotNull PlayerData mmoPlayerData; //All persistent data is kept here

    /* Managers */
    protected final @NotNull ExperienceProcessor experienceProcessor;
    protected final @NotNull CooldownManager cooldownManager;
    protected boolean isLoaded;

    /**
     * Init for online players
     * This will be used for existing data
     *
     * @param mmoPlayerData player data
     */
    public AbstractMMOPlayer(@NotNull Player player, @NotNull PlayerData mmoPlayerData, boolean isLoaded) {
        this.mmoPlayerData = mmoPlayerData;
        this.experienceProcessor = new OnlineExperienceProcessor(mmoPlayerData);
        this.cooldownManager = new CooldownManager(mmoPlayerData);
        this.isLoaded = isLoaded;
    }

    /**
     * Init for offline players
     *
     * @param mmoPlayerData player data
     */
    public AbstractMMOPlayer(@NotNull PlayerData mmoPlayerData, boolean isLoaded) {
        this.mmoPlayerData = mmoPlayerData;
        this.experienceProcessor = new OfflineExperienceProcessor(mmoPlayerData);
        this.cooldownManager = new CooldownManager(mmoPlayerData);
        this.isLoaded = isLoaded;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }
}
