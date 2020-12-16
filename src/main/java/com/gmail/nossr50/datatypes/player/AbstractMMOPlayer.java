package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.experience.ExperienceManager;
import com.neetgames.mcmmo.player.MMOPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class AbstractMMOPlayer implements MMOPlayer {
    /* All of the persistent data for a player that gets saved and loaded from DB */
    protected final @NotNull PersistentPlayerData persistentPlayerData; //All persistent data is kept here

    /* Managers */
    protected final @NotNull ExperienceManager experienceManager;
    protected final @NotNull CooldownManager cooldownManager;

    /**
     * Create a new AbstractMMOPlayer for a {@link Player} with default values
     *
     * @param player target player
     */
    public AbstractMMOPlayer(@NotNull Player player) {
        /* New Data */
        this(player.getUniqueId(), player.getName());
    }

    /**
     * Create a new AbstractMMOPlayer for a {@link Player} with default values
     *
     * @param playerUUID target player's UUID
     * @param playerName target player's name
     */
    public AbstractMMOPlayer(@NotNull UUID playerUUID, @NotNull String playerName) {
        /* New Data */
        this.persistentPlayerData = new PersistentPlayerData(playerUUID, playerName);
        this.experienceManager = new ExperienceManager(persistentPlayerData);
        this.cooldownManager = new CooldownManager(persistentPlayerData);
    }

    /**
     * Initialize an AbstractMMOPlayer for {@link PersistentPlayerData}
     * This will be used for existing data
     *
     * @param persistentPlayerData target persistent player data
     */
    public AbstractMMOPlayer(@NotNull PersistentPlayerData persistentPlayerData) {
        this.persistentPlayerData = persistentPlayerData;
        this.experienceManager = new ExperienceManager(persistentPlayerData);
        this.cooldownManager = new CooldownManager(persistentPlayerData);
    }
}
