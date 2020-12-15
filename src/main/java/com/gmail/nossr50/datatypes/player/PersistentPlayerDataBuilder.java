package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.neetgames.mcmmo.MobHealthBarType;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.UUID;

public class PersistentPlayerDataBuilder {

    /* Player Stuff */
    private @Nullable String playerName;
    private @Nullable UUID playerUUID;

    /* Records */
    private long lastLogin;

    /* HUDs */
    private @Nullable MobHealthBarType mobHealthBarType;

    /* Skill Data */
    private @Nullable EnumMap<PrimarySkillType, Integer> skillLevelValues;
    private @Nullable EnumMap<PrimarySkillType, Float> skillExperienceValues;
    private @Nullable EnumMap<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private @Nullable EnumMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private @Nullable EnumMap<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private boolean partyChatSpying;
    private boolean leaderBoardExemption = false;

    /* Scoreboards */
    private int scoreboardTipsShown;

    public @NotNull PersistentPlayerData buildNewPlayerData(@NotNull Player player) {
        /*
         * New Profile with default values
         */
        return buildNewPlayerData(player.getUniqueId(), player.getName());
    }

    public @Nullable PersistentPlayerData buildNewPlayerData(@NotNull OfflinePlayer offlinePlayer) {
        if(offlinePlayer.getName() != null)
            return buildNewPlayerData(offlinePlayer.getUniqueId(), offlinePlayer.getName());
        else
            return null;
    }

    public @NotNull PersistentPlayerData buildNewPlayerData(@NotNull UUID playerUUID, @NotNull String playerName) {
        /*
         * New Profile with default values
         */
        return new PersistentPlayerData(playerUUID, playerName);
    }

    public @NotNull PersistentPlayerData build() throws Exception {
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

        if(mobHealthBarType == null)
            throw new NullArgumentException("mobHealthBarType");


        return new PersistentPlayerData(playerUUID, playerName, partyChatSpying, skillLevelValues, skillExperienceValues, abilityDeactivationTimestamps, uniquePlayerData, barStateMap, scoreboardTipsShown, mobHealthBarType, lastLogin, leaderBoardExemption);
    }

    private void validateBarStateMapEntries(@NotNull EnumMap<PrimarySkillType, SkillBossBarState> map) {
        EnumMap<PrimarySkillType, SkillBossBarState> barMapDefaults = MMOExperienceBarManager.generateDefaultBarStateMap();
        
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            map.putIfAbsent(primarySkillType, barMapDefaults.get(primarySkillType));
        }
    }

    private void validateExperienceValueMapEntries(@NotNull EnumMap<PrimarySkillType, Float> map) {
        for(PrimarySkillType key : PrimarySkillType.values()) {
            map.putIfAbsent(key, 0F);

            if(map.get(key) < 0F) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0F);
            }
        }
    }

    private void validateUniquePlayerDataMapEntries(@NotNull EnumMap<UniqueDataType, Integer> map) {
        for(UniqueDataType key : UniqueDataType.values()) {
            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    private void validateAbilityCooldownMapEntries(@NotNull EnumMap<SuperAbilityType, Integer> map) {
        for(SuperAbilityType key : SuperAbilityType.values()) {
            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    private void validateSkillLevelMapEntries(@NotNull EnumMap<PrimarySkillType, Integer> map) {
        for(PrimarySkillType key : PrimarySkillType.values()) {
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

    public @NotNull PersistentPlayerDataBuilder setPlayerName(@NotNull String playerName) {
        this.playerName = playerName;
        return this;
    }

    public @Nullable UUID getPlayerUUID() {
        return playerUUID;
    }

    public @NotNull PersistentPlayerDataBuilder setPlayerUUID(@NotNull UUID playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public @NotNull PersistentPlayerDataBuilder setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public @Nullable MobHealthBarType getMobHealthBarType() {
        return mobHealthBarType;
    }

    public @NotNull PersistentPlayerDataBuilder setMobHealthBarType(@NotNull MobHealthBarType mobHealthBarType) {
        this.mobHealthBarType = mobHealthBarType;
        return this;
    }

    public @Nullable EnumMap<PrimarySkillType, Integer> getSkillLevelValues() {
        return skillLevelValues;
    }

    public @NotNull PersistentPlayerDataBuilder setSkillLevelValues(@NotNull EnumMap<PrimarySkillType, Integer> skillLevelValues) {
        this.skillLevelValues = skillLevelValues;
        return this;
    }

    public @Nullable EnumMap<PrimarySkillType, Float> getSkillExperienceValues() {
        return skillExperienceValues;
    }

    public @NotNull PersistentPlayerDataBuilder setSkillExperienceValues(@NotNull EnumMap<PrimarySkillType, Float> skillExperienceValues) {
        this.skillExperienceValues = skillExperienceValues;
        return this;
    }

    public @Nullable EnumMap<SuperAbilityType, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull PersistentPlayerDataBuilder setAbilityDeactivationTimestamps(@NotNull EnumMap<SuperAbilityType, Integer> abilityDeactivationTimestamps) {
        this.abilityDeactivationTimestamps = abilityDeactivationTimestamps;
        return this;
    }

    public @Nullable EnumMap<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public @NotNull PersistentPlayerDataBuilder setUniquePlayerData(@NotNull EnumMap<UniqueDataType, Integer> uniquePlayerData) {
        this.uniquePlayerData = uniquePlayerData;
        return this;
    }

    public @Nullable EnumMap<PrimarySkillType, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull PersistentPlayerDataBuilder setBarStateMap(@NotNull EnumMap<PrimarySkillType, SkillBossBarState> barStateMap) {
        this.barStateMap = barStateMap;
        return this;
    }

    public @NotNull PersistentPlayerDataBuilder setPartyChatSpying(boolean partyChatSpying) {
        this.partyChatSpying = partyChatSpying;
        return this;
    }

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public @NotNull PersistentPlayerDataBuilder setScoreboardTipsShown(int scoreboardTipsShown) {
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
