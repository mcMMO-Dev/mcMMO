package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class MMODataBuilder {

    /* Player Stuff */
    private @Nullable String playerName;
    private @Nullable UUID playerUUID;

    /* Records */
    private long lastLogin;

    /* Skill Data */
    private @Nullable Map<PrimarySkillType, Integer> skillLevelValues;
    private @Nullable Map<PrimarySkillType, Float> skillExperienceValues;
    private @Nullable Map<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private @Nullable Map<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private @Nullable Map<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private boolean partyChatSpying;
    private boolean leaderBoardExemption = false;

    /* Scoreboards */
    private int scoreboardTipsShown;

    public @NotNull PlayerData buildNewPlayerData(@NotNull Player player) {
        /*
         * New Profile with default values
         */
        return buildNewPlayerData(player.getUniqueId(), player.getName());
    }

    public @Nullable PlayerData buildNewPlayerData(@NotNull OfflinePlayer offlinePlayer) {
        if(offlinePlayer.getName() != null)
            return buildNewPlayerData(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        else
            return null;
    }

    public @NotNull PlayerData buildNewPlayerData(@NotNull UUID playerUUID, @NotNull String playerName) {
        /*
         * New Profile with default values
         */
        return new PlayerData(playerUUID, playerName);
    }

    public @NotNull PlayerData build() throws Exception {
        if(playerUUID == null)
            throw new NullArgumentException("playerUUID");

        if(playerName == null)
            throw new NullArgumentException("player name");

        if(skillLevelValues == null)
            throw new NullArgumentException("skillLevelValues");

        validateSkillLevelMapEntries(skillLevelValues);

        if(skillExperienceValues == null)
            throw new NullArgumentException("skillExperienceValues");

        validateExperienceValueMapEntries(skillExperienceValues);

        if(abilityDeactivationTimestamps == null)
            throw new NullArgumentException("abilityDeactivationTimestamps");

        validateAbilityCooldownMapEntries(abilityDeactivationTimestamps);

        if(uniquePlayerData == null)
            throw new NullArgumentException("uniquePlayerData");

        validateUniquePlayerDataMapEntries(uniquePlayerData);

        if(barStateMap == null)
            throw new NullArgumentException("barStateMap");

        validateBarStateMapEntries(barStateMap);

        return new PlayerData(playerUUID, playerName, partyChatSpying, skillLevelValues, skillExperienceValues, abilityDeactivationTimestamps, uniquePlayerData, barStateMap, scoreboardTipsShown, lastLogin, leaderBoardExemption);
    }

    private void validateBarStateMapEntries(@NotNull Map<PrimarySkillType, SkillBossBarState> map) {
        Map<PrimarySkillType, SkillBossBarState> barMapDefaults = MMOExperienceBarManager.generateDefaultBarStateMap();

        for(PrimarySkillType key : PrimarySkillType.values()) {
            map.putIfAbsent(key, barMapDefaults.get(key));
        }
    }

    private void validateExperienceValueMapEntries(@NotNull Map<PrimarySkillType, Float> map) {
        for(PrimarySkillType key : PrimarySkillType.values()) {
            if(key.isChildSkill())
                continue;

            map.putIfAbsent(key, 0F);

            if(map.get(key) < 0F) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0F);
            }
        }
    }

    private void validateUniquePlayerDataMapEntries(@NotNull Map<UniqueDataType, Integer> map) {
        for(UniqueDataType key : UniqueDataType.values()) {
            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    private void validateAbilityCooldownMapEntries(@NotNull Map<SuperAbilityType, Integer> map) {
        for(SuperAbilityType key : SuperAbilityType.values()) {
            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    private void validateSkillLevelMapEntries(@NotNull Map<PrimarySkillType, Integer> map) {
        for(PrimarySkillType key : PrimarySkillType.values()) {

            if(key.isChildSkill())
                continue;

            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    public @Nullable String getPlayerName() {
        return playerName;
    }

    public @NotNull MMODataBuilder setPlayerName(@NotNull String playerName) {
        this.playerName = playerName;
        return this;
    }

    public @Nullable UUID getPlayerUUID() {
        return playerUUID;
    }

    public @NotNull MMODataBuilder setPlayerUUID(@NotNull UUID playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public @NotNull MMODataBuilder setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public @Nullable Map<PrimarySkillType, Integer> getSkillLevelValues() {
        return skillLevelValues;
    }

    public @NotNull MMODataBuilder setSkillLevelValues(@NotNull Map<PrimarySkillType, Integer> skillLevelValues) {
        this.skillLevelValues = skillLevelValues;
        return this;
    }

    public @Nullable Map<PrimarySkillType, Float> getSkillExperienceValues() {
        return skillExperienceValues;
    }

    public @NotNull MMODataBuilder setSkillExperienceValues(@NotNull Map<PrimarySkillType, Float> skillExperienceValues) {
        this.skillExperienceValues = skillExperienceValues;
        return this;
    }

    public @Nullable Map<SuperAbilityType, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull MMODataBuilder setAbilityDeactivationTimestamps(@NotNull Map<SuperAbilityType, Integer> abilityDeactivationTimestamps) {
        this.abilityDeactivationTimestamps = abilityDeactivationTimestamps;
        return this;
    }

    public @Nullable Map<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public @NotNull MMODataBuilder setUniquePlayerData(@NotNull Map<UniqueDataType, Integer> uniquePlayerData) {
        this.uniquePlayerData = uniquePlayerData;
        return this;
    }

    public @Nullable Map<PrimarySkillType, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull MMODataBuilder setBarStateMap(@NotNull Map<PrimarySkillType, SkillBossBarState> barStateMap) {
        this.barStateMap = barStateMap;
        return this;
    }

    public @NotNull MMODataBuilder setPartyChatSpying(boolean partyChatSpying) {
        this.partyChatSpying = partyChatSpying;
        return this;
    }

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public @NotNull MMODataBuilder setScoreboardTipsShown(int scoreboardTipsShown) {
        this.scoreboardTipsShown = scoreboardTipsShown;
        return this;
    }

    public boolean isPartyChatSpying() {
        return partyChatSpying;
    }

    public void setLeaderBoardExemption(boolean bool) {
        leaderBoardExemption = bool;
    }
}
