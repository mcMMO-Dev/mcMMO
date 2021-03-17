package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.google.common.collect.ImmutableMap;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.player.MMOPlayerDataImpl;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import com.neetgames.mcmmo.skill.SuperSkill;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MMODataSnapshot {
    /* Player Stuff */
    private final @NotNull String playerName;
    private final @NotNull UUID playerUUID;

    /* Records */
    private final long lastLogin;

    /* Skill Data */
    private final @NotNull ImmutableMap<PrimarySkillType, Integer> skillLevelValues;
    private final @NotNull ImmutableMap<PrimarySkillType, Float> skillExperienceValues;
    private final @NotNull ImmutableMap<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull ImmutableMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull ImmutableMap<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private final boolean partyChatSpying;
    private final boolean leaderBoardExclusion;

    /* Scoreboards */
    private final int scoreboardTipsShown;


    public MMODataSnapshot(@NotNull PlayerData mmoPlayerData) {
        playerName = mmoPlayerData.getPlayerName();
        playerUUID = mmoPlayerData.getPlayerUUID();
        lastLogin = mmoPlayerData.getLastLogin();

        skillLevelValues = ImmutableMap.copyOf(mmoPlayerData.getSkillLevelsMap());
        skillExperienceValues = ImmutableMap.copyOf(mmoPlayerData.getSkillsExperienceMap());
        abilityDeactivationTimestamps = ImmutableMap.copyOf(mmoPlayerData.getAbilityDeactivationTimestamps());
        uniquePlayerData = ImmutableMap.copyOf(mmoPlayerData.getUniquePlayerData());
        barStateMap = ImmutableMap.copyOf(mmoPlayerData.getBarStateMap());

        partyChatSpying = mmoPlayerData.isPartyChatSpying();
        leaderBoardExclusion = mmoPlayerData.isLeaderBoardExcluded();

        scoreboardTipsShown = mmoPlayerData.getScoreboardTipsShown();
    }

    public @NotNull String getPlayerName() {
        return playerName;
    }

    public @NotNull UUID getPlayerUUID() {
        return playerUUID;
    }

    public @NotNull Long getLastLogin() {
        return lastLogin;
    }


    public @NotNull ImmutableMap<PrimarySkillType, Integer> getSkillLevelValues() {
        return skillLevelValues;
    }

    public @NotNull ImmutableMap<PrimarySkillType, Float> getSkillExperienceValues() {
        return skillExperienceValues;
    }

    public @NotNull ImmutableMap<SuperAbilityType, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull ImmutableMap<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public @NotNull ImmutableMap<PrimarySkillType, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull Boolean getPartyChatSpying() {
        return partyChatSpying;
    }

    public @NotNull Integer getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public int getSkillLevel(@NotNull PrimarySkillType primarySkillType) {
        return skillLevelValues.getOrDefault(primarySkillType, 0);
    }

    public int getSkillXpLevel(@NotNull PrimarySkillType primarySkillType) {
        return (skillExperienceValues.getOrDefault(primarySkillType, 0F)).intValue();
    }

    public long getAbilityDATS(@NotNull SuperAbilityType superAbilityType) {
        return abilityDeactivationTimestamps.getOrDefault(superAbilityType, 0);
    }

    public long getUniqueData(@NotNull UniqueDataType uniqueDataType) {
        return uniquePlayerData.getOrDefault(uniqueDataType, 0);
    }

    public boolean isLeaderBoardExcluded() {
        return leaderBoardExclusion;
    }
}
