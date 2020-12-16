package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.google.common.collect.ImmutableMap;
import com.neetgames.mcmmo.MobHealthBarType;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MMODataSnapshot {
    /* Player Stuff */
    private final @NotNull String playerName;
    private final @NotNull UUID playerUUID;

    /* Records */
    private final @NotNull Long lastLogin;

    /* HUDs */
    private final @NotNull MobHealthBarType mobHealthBarType;

    /* Skill Data */
    private final @NotNull ImmutableMap<RootSkill, Integer> skillLevelValues;
    private final @NotNull ImmutableMap<RootSkill, Float> skillExperienceValues;
    private final @NotNull ImmutableMap<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull ImmutableMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull ImmutableMap<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private final @NotNull Boolean partyChatSpying;
    private final @NotNull Boolean leaderBoardExclusion;

    /* Scoreboards */
    private final @NotNull Integer scoreboardTipsShown;


    public MMODataSnapshot(@NotNull PersistentPlayerData persistentPlayerData) {
        playerName = persistentPlayerData.getPlayerName();
        playerUUID = persistentPlayerData.getPlayerUUID();
        lastLogin = persistentPlayerData.getLastLogin();

        mobHealthBarType = persistentPlayerData.getMobHealthBarType();

        skillLevelValues = ImmutableMap.copyOf(persistentPlayerData.getSkillLevelsMap());
        skillExperienceValues = ImmutableMap.copyOf(persistentPlayerData.getSkillsExperienceMap());
        abilityDeactivationTimestamps = ImmutableMap.copyOf(persistentPlayerData.getAbilityDeactivationTimestamps());
        uniquePlayerData = ImmutableMap.copyOf(persistentPlayerData.getUniquePlayerData());
        barStateMap = ImmutableMap.copyOf(persistentPlayerData.getBarStateMap());

        partyChatSpying = persistentPlayerData.isPartyChatSpying();
        leaderBoardExclusion = persistentPlayerData.isLeaderBoardExcluded();

        scoreboardTipsShown = persistentPlayerData.getScoreboardTipsShown();
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

    public @NotNull MobHealthBarType getMobHealthBarType() {
        return mobHealthBarType;
    }

    public @NotNull ImmutableMap<RootSkill, Integer> getSkillLevelValues() {
        return skillLevelValues;
    }

    public @NotNull ImmutableMap<RootSkill, Float> getSkillExperienceValues() {
        return skillExperienceValues;
    }

    public @NotNull ImmutableMap<SuperAbilityType, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull ImmutableMap<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public @NotNull ImmutableMap<RootSkill, SkillBossBarState> getBarStateMap() {
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
