package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.experience.ExperienceManager;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import com.neetgames.mcmmo.MobHealthBarType;
import com.neetgames.mcmmo.player.MMOPlayerData;
import com.neetgames.mcmmo.player.OfflineMMOPlayer;
import com.neetgames.mcmmo.skill.Skill;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerProfile extends AbstractMMOPlayer {

    /**
     * Create a new {@link PlayerProfile} for a {@link Player} with default values
     *
     * @param player target player
     */
    public PlayerProfile(@NotNull Player player) {
        super(player);
    }

    /**
     * Create a new {@link PlayerProfile} for a {@link Player} with default values
     *
     * @param playerUUID target player's UUID
     * @param playerName target player's name
     */
    public PlayerProfile(@NotNull UUID playerUUID, @NotNull String playerName) {
        super(playerUUID, playerName);
    }

    /**
     * Initialize an {@link PlayerProfile} for {@link PersistentPlayerData}
     * This will be used for existing data
     *
     * @param persistentPlayerData target persistent player data
     */
    public PlayerProfile(@NotNull PersistentPlayerData persistentPlayerData) {
        super(persistentPlayerData);
    }

    /**
     * Get the saved player name for this profile
     * @return the saved player name for this profile
     */
    @Override
    public @NotNull String getPlayerName() {
        return getPersistentPlayerData().getPlayerName();
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

    /**
     * Attempt to get a party for this PlayerProfile
     * @return get a party for this PlayerProfile
     */
    public @Nullable Party getParty(){
        //TODO: This can be optimized
        return mcMMO.getPartyManager().queryParty(persistentPlayerData.getPlayerUUID());
    }

    /**
     * Whether or not this player is excluded from leaderboards
     * @return true if the player is excluded from leaderboards
     */
    public boolean isLeaderBoardExcluded() {
        return persistentPlayerData.isLeaderBoardExcluded();
    }


    /**
     * Get the saved {@link UUID} for this profile
     * @return the saved {@link UUID} for this profile
     */
    @Override
    public @NotNull UUID getUUID() {
        return getPersistentPlayerData().getPlayerUUID();
    }

    @Override
    public int getPowerLevel() {
        return experienceManager.getPowerLevel();
    }

    @Override
    public int getSkillLevel(@NotNull Skill skill) {
        return 0;
    }

    @Override
    public int getSkillExperience(@NotNull Skill skill) {
        return persistentPlayerData;
    }

    @Override
    public int getExperienceToNextLevel() {
        return 0;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public @NotNull MMOPlayerData getMMOPlayerData() {
        return persistentPlayerData;
    }
}
