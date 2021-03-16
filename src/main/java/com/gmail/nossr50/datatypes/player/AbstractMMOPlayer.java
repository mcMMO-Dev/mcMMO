package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.experience.OfflineExperienceProcessor;
import com.gmail.nossr50.datatypes.experience.OnlineExperienceProcessor;
import com.neetgames.mcmmo.experience.ExperienceHandler;
import com.neetgames.mcmmo.player.MMOPlayer;
import com.neetgames.mcmmo.player.MMOPlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractMMOPlayer implements MMOPlayer {
    /* All of the persistent data for a player that gets saved and loaded from DB */
    protected final @NotNull MMOPlayerData mmoPlayerData; //All persistent data is kept here

    /* Managers */
    protected final @NotNull ExperienceHandler experienceHandler;
    protected final @NotNull CooldownManager cooldownManager;

    /**
     * Init for online players
     * This will be used for existing data
     *
     * @param mmoPlayerData player data
     */
    public AbstractMMOPlayer(@NotNull Player player, @NotNull MMOPlayerDataImpl mmoPlayerData) {
        this.mmoPlayerData = mmoPlayerData;
        this.experienceHandler = new OnlineExperienceProcessor(mmoPlayerData);
        this.cooldownManager = new CooldownManager(mmoPlayerData);
    }

    /**
     * Init for offline players
     *
     * @param mmoPlayerData player data
     */
    public AbstractMMOPlayer(@NotNull MMOPlayerDataImpl mmoPlayerData) {
        this.mmoPlayerData = mmoPlayerData;
        this.experienceHandler = new OfflineExperienceProcessor(mmoPlayerData);
        this.cooldownManager = new CooldownManager(mmoPlayerData);
    }
}
