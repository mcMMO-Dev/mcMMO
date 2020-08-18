package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.MobHealthBarType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerProfile {

    /* All of the persistent data for a player that gets saved and loaded from DB */
    private final @NotNull PersistentPlayerData persistentPlayerData; //All persistent data is kept here

    /* Managers */
    private final @NotNull ExperienceManager experienceManager;
    private final @NotNull CooldownManager cooldownManager;

    /**
     * Create a new PlayerProfile for a {@link Player}
     * @param player target player
     */
    public PlayerProfile(@NotNull Player player) {
        /* New Data */
        this(player.getUniqueId(), player.getName());
    }

    /**
     * Create a new PlayerProfile for a {@link Player}
     * @param playerUUID target player's UUID
     * @param playerName target player's name
     */
    public PlayerProfile(@NotNull UUID playerUUID, @NotNull String playerName) {
        /* New Data */
        this.persistentPlayerData = new PersistentPlayerData(playerUUID, playerName);
        this.experienceManager = new ExperienceManager(persistentPlayerData);
        this.cooldownManager = new CooldownManager(persistentPlayerData);
    }

    /**
     * Create a PlayerProfile for {@link PersistentPlayerData}
     * This will be used for existing data
     * @param persistentPlayerData target persistent player data
     */
    public PlayerProfile(@NotNull PersistentPlayerData persistentPlayerData) {
        this.persistentPlayerData = persistentPlayerData;
        this.experienceManager = new ExperienceManager(persistentPlayerData);
        this.cooldownManager = new CooldownManager(persistentPlayerData);
    }

    /**
     * Get the saved player name for this profile
     * @return the saved player name for this profile
     */
    public @NotNull String getPlayerName() {
        return getPersistentPlayerData().getPlayerName();
    }

    /**
     * Get the saved {@link UUID} for this profile
     * @return the saved {@link UUID} for this profile
     */
    public @NotNull UUID getUniqueId() {
        return getPersistentPlayerData().getPlayerUUID();
    }

    /**
     * Get the current {@link MobHealthBarType} for this profile
     * @return the mob health bar setting for this profile
     */
    public @NotNull MobHealthBarType getMobHealthBarType() {
        return getPersistentPlayerData().getMobHealthBarType();
    }

    /**
     * Set the {@link MobHealthBarType} for this profile
     * @param mobHealthbarType desired mob health bar type
     */
    public void setMobHealthBarType(@NotNull MobHealthBarType mobHealthbarType) {
        getPersistentPlayerData().setMobHealthBarType(mobHealthbarType);
    }

    /**
     * The number of times scoreboard tips have been shown to this profile
     * @return the scoreboard tips view count
     */
    public int getScoreboardTipsShown() {
        return getPersistentPlayerData().getScoreboardTipsShown();
    }

    /**
     * Replace the scoreboard view count
     * @param scoreboardTipsShown new value
     */
    public void setScoreboardTipsShown(int scoreboardTipsShown) {
        getPersistentPlayerData().setScoreboardTipsShown(scoreboardTipsShown);

    }

    /**
     * Increments the scoreboard tip view count by 1
     */
    public void increaseTipsShown() {
        setScoreboardTipsShown(getScoreboardTipsShown() + 1);
    }

    /**
     * Retrieves a reference to the {@link PersistentPlayerData} for this profile
     * @return the persistent data for this profile
     */
    public @NotNull PersistentPlayerData getPersistentPlayerData() {
        return persistentPlayerData;
    }

    /**
     * Invert the current value of the party chat spy toggle
     */
    public void togglePartyChatSpying() {
        persistentPlayerData.togglePartyChatSpying();
    }

    /**
     * Retrieve the {@link ExperienceManager} for this profile
     * @return the experience manager for this profile
     */
    public @NotNull ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    /**
     * Retrieve the {@link CooldownManager} for this profile
     * @return the cooldown manager for this profile
     */
    public @NotNull CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
