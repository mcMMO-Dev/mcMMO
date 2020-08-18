package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.MobHealthBarType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.google.common.collect.ImmutableMap;
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
    private final @NotNull ImmutableMap<PrimarySkillType, Integer> skillLevelValues;
    private final @NotNull ImmutableMap<PrimarySkillType, Float> skillExperienceValues;
    private final @NotNull ImmutableMap<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull ImmutableMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull ImmutableMap<PrimarySkillType, MMOExperienceBarManager.BarState> barStateMap;

    /* Special Flags */
    private final @NotNull Boolean partyChatSpying;

    /* Scoreboards */
    private final @NotNull Integer scoreboardTipsShown;


    public MMODataSnapshot(PersistentPlayerData persistentPlayerData) {
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

    public @NotNull ImmutableMap<PrimarySkillType, MMOExperienceBarManager.BarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull Boolean getPartyChatSpying() {
        return partyChatSpying;
    }

    public @NotNull Integer getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public int getSkillLevel(PrimarySkillType primarySkillType) {
        return skillLevelValues.getOrDefault(primarySkillType, 0);
    }

    public int getSkillXpLevel(PrimarySkillType primarySkillType) {
        return (skillExperienceValues.getOrDefault(primarySkillType, 0F)).intValue();
    }

    public long getAbilityDATS(SuperAbilityType superAbilityType) {
        return abilityDeactivationTimestamps.getOrDefault(superAbilityType, 0);
    }

    public long getUniqueData(UniqueDataType uniqueDataType) {
        return uniquePlayerData.getOrDefault(uniqueDataType, 0);
    }
}
