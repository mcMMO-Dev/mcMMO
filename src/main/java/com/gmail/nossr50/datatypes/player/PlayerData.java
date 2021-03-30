package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.validation.NonNullRule;
import com.gmail.nossr50.datatypes.validation.PositiveIntegerRule;
import com.gmail.nossr50.datatypes.validation.Validator;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.exceptions.UnexpectedValueException;
import com.neetgames.mcmmo.skill.SkillBossBarState;
import org.apache.commons.lang.NullArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    /* Player Stuff */
    private @NotNull String playerName;
    private final @Nullable UUID playerUUID;

    /* Records */
    private long lastLogin;

    /* Skill Data */
    private final @NotNull Map<PrimarySkillType, Integer> skillLevelValues;
    private final @NotNull Map<PrimarySkillType, Float> skillExperienceValues;
    private final @NotNull Map<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull Map<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull Map<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private boolean partyChatSpying;
    private boolean leaderBoardExclusion;

    /* Scoreboards */
    private int scoreboardTipsShown;

    private int lastUpdateHash;

    /**
     * Create new persistent player data for a player
     * Initialized with default values
     *
     * @param playerUUID target player's UUID
     * @param playerName target player's name
     * @throws NullArgumentException thrown when never null arguments are null
     */
    public PlayerData(@Nullable UUID playerUUID, @NotNull String playerName) throws NullArgumentException {
        /*
         * New Data
         */
        this.playerUUID = playerUUID;
        this.playerName = playerName;

        this.skillLevelValues = new HashMap<>();
        this.skillExperienceValues = new HashMap<>();
        this.abilityDeactivationTimestamps = new HashMap<>();
        this.uniquePlayerData = new EnumMap<>(UniqueDataType.class);

        this.scoreboardTipsShown = 0;

        for(SuperAbilityType superSkill : SuperAbilityType.values()) {
            abilityDeactivationTimestamps.put(superSkill, 0);
        }

        //Core skills
        //TODO: Don't store values for disabled skills
        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {

            if(primarySkillType.isChildSkill())
                continue;

            skillLevelValues.put(primarySkillType, AdvancedConfig.getInstance().getStartingLevel());
            skillExperienceValues.put(primarySkillType, 0F);
        }

        //Unique Player Data
        this.uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, 0);

        this.partyChatSpying = false;

        this.barStateMap = MMOExperienceBarManager.generateDefaultBarStateMap();
        this.lastLogin = 0L; //Value of 0 will represent that the user hasn't been seen online
        this.leaderBoardExclusion = false;

        this.lastUpdateHash = hashCode();
    }

    /**
     * Create persistent player data for a player using existing values
     * Typically this will be used when loading data
     * @param playerUUID target player's {@link UUID}
     * @param playerName target player's saved name
     * @param partyChatSpying target player's chat spy toggle
     * @param skillLevelValues target player's skill levels
     * @param skillExperienceValues target player's skill experience levels
     * @param abilityDeactivationTimestamps target player's ability deactivation time stamps
     * @param uniquePlayerData target player's misc unique data
     * @param barStateMap target player's XP bar state settings
     * @param scoreboardTipsShown target player's scoreboard tip view count
     * @param lastLogin target player's last login
     * @param leaderBoardExclusion target player's leaderboard exemption status
     */
    public PlayerData(@NotNull UUID playerUUID,
                      @NotNull String playerName,
                      boolean partyChatSpying,
                      @NotNull Map<PrimarySkillType, Integer> skillLevelValues,
                      @NotNull Map<PrimarySkillType, Float> skillExperienceValues,
                      @NotNull Map<SuperAbilityType, Integer> abilityDeactivationTimestamps,
                      @NotNull Map<UniqueDataType, Integer> uniquePlayerData,
                      @NotNull Map<PrimarySkillType, SkillBossBarState> barStateMap,
                      int scoreboardTipsShown,
                      long lastLogin,
                      boolean leaderBoardExclusion) throws Exception {

        /*
         * Skills Data
         */
        validateRootSkillMap(skillLevelValues);
        this.skillLevelValues = skillLevelValues;

        validateRootSkillMap(skillExperienceValues);
        this.skillExperienceValues = skillExperienceValues;

        validateSuperSkillMap(abilityDeactivationTimestamps);
        this.abilityDeactivationTimestamps = abilityDeactivationTimestamps;

        this.uniquePlayerData = uniquePlayerData;

        this.scoreboardTipsShown = scoreboardTipsShown;

        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.barStateMap = barStateMap;

        this.partyChatSpying = partyChatSpying;
        this.lastLogin = lastLogin;

        this.leaderBoardExclusion = leaderBoardExclusion;
    }

    /**
     * Checks the map for a few potential logic issues such as negative numbers, or null entries
     *
     * @param map target map
     * @throws UnexpectedValueException when values are outside of expected norms
     * @throws Exception when values are outside of expected norms
     */
    private void validateRootSkillMap(Map<PrimarySkillType, ? extends Number> map) throws UnexpectedValueException, Exception {
        //TODO: Check for missing/unregistered
        Validator<Number> validator = new Validator<>();

        validator.addRule(new PositiveIntegerRule<>());
        validator.addRule(new NonNullRule<>());

        for(PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            if(primarySkillType.isChildSkill())
                continue;
            
            validator.validate(map.get(primarySkillType));
        }
    }

    /**
     * Checks the map for a few potential logic issues such as negative numbers, or null entries
     *
     * @param map target map
     * @throws UnexpectedValueException when values are outside of expected norms
     * @throws Exception when values are outside of expected norms
     */
    private void validateSuperSkillMap(Map<? extends SuperAbilityType, ? extends Number> map) throws UnexpectedValueException, Exception {
        //TODO: Check for missing/unregistered
        Validator<Number> validator = new Validator<>();

        validator.addRule(new PositiveIntegerRule<>());
        validator.addRule(new NonNullRule<>());

        for(SuperAbilityType superSkill : SuperAbilityType.values()) {
            validator.validate(map.get(superSkill));
        }
    }
    
    public void setSkillLevel(@NotNull PrimarySkillType primarySkillType, int i) {
        skillLevelValues.put(primarySkillType, i);
    }

    
    public int getSkillLevel(@NotNull PrimarySkillType primarySkillType) {
        return 0;
    }

    public boolean isProfileDirty() {
        return lastUpdateHash != hashCode();
    }

    //TODO: T&C this needs to be called or pointless save operations won't be avoided
    public void resetDirtyFlag() {
        lastUpdateHash = hashCode();
    }

    public @NotNull String getPlayerName() {
        return playerName;
    }

    public @Nullable UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isPartyChatSpying() { return partyChatSpying; }

    public void togglePartyChatSpying() {
        partyChatSpying = !partyChatSpying;
    }

    public void setPartyChatSpying(boolean bool) {
        this.partyChatSpying = bool;
    }

    /*
     * Scoreboards
     */

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public void setScoreboardTipsShown(int newValue) {
        this.scoreboardTipsShown = newValue;
    }

    public int getChimaeraWingDATS() {
        return uniquePlayerData.get((UniqueDataType.CHIMAERA_WING_DATS));
    }

    public void setChimaeraWingDATS(int DATS) {
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    public void setUniqueData(@NotNull UniqueDataType uniqueDataType, int newData) {
        uniquePlayerData.put(uniqueDataType, newData);
    }

    
    public long getUniqueData(@NotNull UniqueDataType uniqueDataType) { return uniquePlayerData.get(uniqueDataType); }

    public long getAbilityDATS(@NotNull SuperAbilityType superSkill) {
        return abilityDeactivationTimestamps.get(superSkill);
    }

    public void setAbilityDATS(@NotNull SuperAbilityType superSkill, long DATS) {
        abilityDeactivationTimestamps.put(superSkill, (int) (DATS * .001D));
    }

    public void resetCooldowns() {
        abilityDeactivationTimestamps.replaceAll((a, v) -> 0);
    }

    public @NotNull Map<PrimarySkillType, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    public @NotNull Map<PrimarySkillType, Integer> getSkillLevelsMap() {
        return skillLevelValues;
    }

    public @NotNull Map<PrimarySkillType, Float> getSkillsExperienceMap() {
        return skillExperienceValues;
    }

    public @NotNull Map<SuperAbilityType, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    public @NotNull Map<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long newValue) {
        this.lastLogin = newValue;
    }

    public boolean isLeaderBoardExcluded() {
        return leaderBoardExclusion;
    }

    public void setLeaderBoardExclusion(boolean bool) {
        this.leaderBoardExclusion = bool;
    }

    public @NotNull ImmutableMap<PrimarySkillType, Integer> copyPrimarySkillLevelsMap() {
        return ImmutableMap.copyOf(getSkillLevelsMap());
    }

    public @NotNull ImmutableMap<PrimarySkillType, Float> copyPrimarySkillExperienceValuesMap() {
        return ImmutableMap.copyOf(getSkillsExperienceMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return lastLogin == that.lastLogin
                && partyChatSpying == that.partyChatSpying
                && leaderBoardExclusion == that.leaderBoardExclusion
                && scoreboardTipsShown == that.scoreboardTipsShown
                && Objects.equal(playerName, that.playerName)
                && Objects.equal(playerUUID, that.playerUUID)
                && Objects.equal(skillLevelValues, that.skillLevelValues)
                && Objects.equal(skillExperienceValues, that.skillExperienceValues)
                && Objects.equal(abilityDeactivationTimestamps, that.abilityDeactivationTimestamps)
                && Objects.equal(uniquePlayerData, that.uniquePlayerData) && Objects.equal(barStateMap, that.barStateMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerName, playerUUID, lastLogin, skillLevelValues, skillExperienceValues, abilityDeactivationTimestamps, uniquePlayerData, barStateMap, partyChatSpying, leaderBoardExclusion, scoreboardTipsShown);
    }
}
