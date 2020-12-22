package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.neetgames.mcmmo.MobHealthBarType;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.api.SkillRegister;
import com.neetgames.mcmmo.skill.RootSkill;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import com.neetgames.mcmmo.skill.SkillIdentity;
import com.neetgames.mcmmo.skill.SuperSkill;
import it.unimi.dsi.fastutil.Hash;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
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
    private @Nullable Map<RootSkill, Integer> skillLevelValues;
    private @Nullable Map<RootSkill, Float> skillExperienceValues;
    private @Nullable Map<SuperSkill, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private @Nullable Map<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private @Nullable Map<RootSkill, SkillBossBarState> barStateMap;

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


        return new PersistentPlayerData(playerUUID, playerName, partyChatSpying, skillLevelValues, skillExperienceValues, abilityDeactivationTimestamps, uniquePlayerData, barStateMap, scoreboardTipsShown, lastLogin, leaderBoardExemption);
    }

    private void validateBarStateMapEntries(@NotNull Map<RootSkill, SkillBossBarState> map) {
        Map<RootSkill, SkillBossBarState> barMapDefaults = MMOExperienceBarManager.generateDefaultBarStateMap();

        for(RootSkill key : mcMMO.p.getSkillRegister().getRootSkills()) {
            map.putIfAbsent(key, barMapDefaults.get(key));
        }
    }

    private void validateExperienceValueMapEntries(@NotNull Map<RootSkill, Float> map) {
        for(RootSkill key : mcMMO.p.getSkillRegister().getRootSkills()) {
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

    private void validateAbilityCooldownMapEntries(@NotNull Map<SuperSkill, Integer> map) {
        for(SuperSkill key : mcMMO.p.getSkillRegister().getSuperSkills()) {
            map.putIfAbsent(key, 0);

            if(map.get(key) < 0) {
                mcMMO.p.getLogger().severe("found negative value for map entry of " + key.toString() + " for player " + playerName + " , resetting to 0");
                map.put(key, 0);
            }
        }
    }

    private void validateSkillLevelMapEntries(@NotNull Map<RootSkill, Integer> map) {
        for(RootSkill key : mcMMO.p.getSkillRegister().getRootSkills()) {
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

    public @Nullable Map<RootSkill, Integer> getSkillLevelValues() {
        return skillLevelValues;
    }

    public @NotNull PersistentPlayerDataBuilder setSkillLevelValues(@NotNull HashMap<RootSkill, Integer> skillLevelValues) {
        this.skillLevelValues = skillLevelValues;
        return this;
    }

    public @Nullable Map<RootSkill, Float> getSkillExperienceValues() {
        return skillExperienceValues;
    }

    public @NotNull PersistentPlayerDataBuilder setSkillExperienceValues(@NotNull HashMap<RootSkill, Float> skillExperienceValues) {
        this.skillExperienceValues = skillExperienceValues;
        return this;
    }

    public @Nullable Map<SuperSkill, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull PersistentPlayerDataBuilder setAbilityDeactivationTimestamps(@NotNull HashMap<SuperSkill, Integer> abilityDeactivationTimestamps) {
        this.abilityDeactivationTimestamps = abilityDeactivationTimestamps;
        return this;
    }

    public @Nullable Map<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public @NotNull PersistentPlayerDataBuilder setUniquePlayerData(@NotNull EnumMap<UniqueDataType, Integer> uniquePlayerData) {
        this.uniquePlayerData = uniquePlayerData;
        return this;
    }

    public @Nullable Map<PrimarySkillType, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull PersistentPlayerDataBuilder setBarStateMap(@NotNull Map<RootSkill, SkillBossBarState> barStateMap) {
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
