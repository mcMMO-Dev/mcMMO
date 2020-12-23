package com.gmail.nossr50.datatypes.player;

import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.experience.ExperienceHandler;
import com.neetgames.mcmmo.player.MMOPlayerData;
import com.neetgames.mcmmo.skill.RootSkill;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerProfile extends AbstractMMOPlayer {

    /**
     * Initialize an {@link PlayerProfile} for {@link PersistentPlayerData}
     * This will be used for existing data
     *
     * @param mmoPlayerData target persistent player data
     */
    public PlayerProfile(@NotNull MMOPlayerData mmoPlayerData) {
        super(mmoPlayerData);
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
        return experienceHandler.getPowerLevel();
    }

    @Override
    public int getSkillLevel(@NotNull RootSkill rootSkill) throws UnknownSkillException {
        return experienceHandler.getSkillLevel(rootSkill);
    }

    @Override
    public int getSkillExperience(@NotNull RootSkill rootSkill) throws UnknownSkillException {
        return experienceHandler.getSkillXpValue(rootSkill);
    }

    @Override
    public int getExperienceToNextLevel(@NotNull RootSkill rootSkill) throws UnknownSkillException {
        return experienceHandler.getExperienceToNextLevel(rootSkill);
    }

    @Override
    public double getProgressInCurrentSkillLevel(@NotNull RootSkill rootSkill) throws UnknownSkillException {
        return experienceHandler.getProgressInCurrentSkillLevel(rootSkill);
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public @NotNull ExperienceHandler getExperienceHandler() {
        return experienceHandler;
    }

    @Override
    public @NotNull MMOPlayerData getMMOPlayerData() {
        return mmoPlayerData;
    }
}
