package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerProfile extends AbstractMMOPlayer {

    /**
     * Initialize an {@link PlayerProfile} for {@link PlayerData}
     * This will be used for existing data
     *
     * @param mmoPlayerData target persistent player data
     */
    public PlayerProfile(@NotNull PlayerData mmoPlayerData, boolean isLoaded) {
        super(mmoPlayerData, isLoaded);
    }

    @Override
    public @NotNull UUID getUUID() {
        return mmoPlayerData.getPlayerUUID();
    }

    @Override
    public @NotNull String getPlayerName() {
        return mmoPlayerData.getPlayerName();
    }

    @Override
    public int getPowerLevel() {
        return experienceProcessor.getPowerLevel();
    }

    @Override
    public int getSkillLevel(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException {
        return experienceProcessor.getSkillLevel(primarySkillType);
    }

    @Override
    public int getSkillExperience(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException {
        return experienceProcessor.getSkillXpValue(primarySkillType);
    }

    @Override
    public int getExperienceToNextLevel(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException {
        return experienceProcessor.getExperienceToNextLevel(primarySkillType);
    }

    @Override
    public double getProgressInCurrentSkillLevel(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException {
        return experienceProcessor.getProgressInCurrentSkillLevel(primarySkillType);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public @NotNull ExperienceHandler getExperienceHandler() {
        return experienceProcessor;
    }

    public @NotNull PlayerData getPlayerData() {
        return mmoPlayerData;
    }
}
