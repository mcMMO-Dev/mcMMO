package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.validation.NonNullRule;
import com.gmail.nossr50.datatypes.validation.PositiveIntegerRule;
import com.gmail.nossr50.datatypes.validation.Validator;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.google.common.collect.ImmutableMap;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.exceptions.UnexpectedValueException;
import com.neetgames.mcmmo.skill.*;
import com.neetgames.neetlib.dirtydata.DirtyData;
import com.neetgames.neetlib.dirtydata.DirtyMap;
import com.neetgames.neetlib.mutableprimitives.MutableBoolean;
import com.neetgames.neetlib.mutableprimitives.MutableInteger;
import com.neetgames.neetlib.mutableprimitives.MutableLong;
import com.neetgames.neetlib.mutableprimitives.MutableString;
import org.apache.commons.lang.NullArgumentException;
import org.jetbrains.annotations.NotNull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final @NotNull MutableBoolean dirtyFlag; //Dirty values in this class will change this flag as needed

    /* Player Stuff */
    private final @NotNull DirtyData<MutableString> playerName;
    private final @NotNull UUID playerUUID;

    /* Records */
    private final DirtyData<MutableLong> lastLogin;

    /* Skill Data */
    private final @NotNull DirtyMap<PrimarySkillType, Integer> skillLevelValues;
    private final @NotNull DirtyMap<PrimarySkillType, Float> skillExperienceValues;
    private final @NotNull DirtyMap<SuperAbilityType, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull DirtyMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull DirtyMap<PrimarySkillType, SkillBossBarState> barStateMap;

    /* Special Flags */
    private final @NotNull DirtyData<MutableBoolean> partyChatSpying;
    private final @NotNull DirtyData<MutableBoolean> leaderBoardExclusion;

    /* Scoreboards */
    private final @NotNull DirtyData<MutableInteger> scoreboardTipsShown;

    /**
     * Create new persistent player data for a player
     * Initialized with default values
     *
     * @param playerUUID target player's UUID
     * @param playerName target player's name
     * @throws NullArgumentException thrown when never null arguments are null
     */
    public PlayerData(@NotNull UUID playerUUID, @NotNull String playerName) throws NullArgumentException {
        /*
         * New Data
         */
        this.dirtyFlag = new MutableBoolean(false); //Set this one first
        this.playerUUID = playerUUID;
        this.playerName = new DirtyData<>(new MutableString(playerName), dirtyFlag);

        this.skillLevelValues = new DirtyMap<>(new HashMap<>(), dirtyFlag);
        this.skillExperienceValues = new DirtyMap<>(new HashMap<>(), dirtyFlag);
        this.abilityDeactivationTimestamps = new DirtyMap<>(new HashMap<>(), dirtyFlag);
        this.uniquePlayerData = new DirtyMap<>(new EnumMap<>(UniqueDataType.class), dirtyFlag);

        this.scoreboardTipsShown = new DirtyData<>(new MutableInteger(0), dirtyFlag);

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

        this.partyChatSpying = new DirtyData<>(new MutableBoolean(false), dirtyFlag);

        this.barStateMap = new DirtyMap<>(MMOExperienceBarManager.generateDefaultBarStateMap(), dirtyFlag);
        this.lastLogin = new DirtyData<>(new MutableLong(0), dirtyFlag); //Value of 0 will represent that the user hasn't been seen online
        this.leaderBoardExclusion = new DirtyData<>(new MutableBoolean(false), dirtyFlag);
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
        this.dirtyFlag = new MutableBoolean(false); //Set this one first

        validateRootSkillMap(skillLevelValues);
        this.skillLevelValues = new DirtyMap<>(skillLevelValues, dirtyFlag);

        validateRootSkillMap(skillExperienceValues);
        this.skillExperienceValues = new DirtyMap<>(skillExperienceValues, dirtyFlag);

        validateSuperSkillMap(abilityDeactivationTimestamps);
        this.abilityDeactivationTimestamps = new DirtyMap<>(abilityDeactivationTimestamps, dirtyFlag);

        this.uniquePlayerData = new DirtyMap<>(uniquePlayerData, dirtyFlag);

        this.scoreboardTipsShown = new DirtyData<>(new MutableInteger(scoreboardTipsShown), dirtyFlag);

        this.playerUUID = playerUUID;
        this.playerName = new DirtyData<>(new MutableString(playerName), dirtyFlag);
        this.barStateMap = new DirtyMap<>(barStateMap, dirtyFlag);

        this.partyChatSpying = new DirtyData<>(new MutableBoolean(partyChatSpying), dirtyFlag);
        this.lastLogin = new DirtyData<>(new MutableLong(lastLogin), dirtyFlag);

        this.leaderBoardExclusion = new DirtyData<>(new MutableBoolean(leaderBoardExclusion), dirtyFlag);
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

    public boolean isDirtyProfile() {
        return dirtyFlag.getImmutableCopy();
    }

    public void resetDirtyFlag() {
        dirtyFlag.setBoolean(false);
    }

    public @NotNull String getPlayerName() {
        return playerName.getData().getImmutableCopy();
    }

    public @NotNull UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isPartyChatSpying() { return partyChatSpying.getData().getImmutableCopy(); }

    public void togglePartyChatSpying() {
        partyChatSpying.getData().setBoolean(!partyChatSpying.getData().getImmutableCopy());
    }

    public void setPartyChatSpying(boolean bool) {
        this.partyChatSpying.getData().setBoolean(bool);
    }

    /*
     * Scoreboards
     */

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown.getData(false).getImmutableCopy();
    }

    public void setScoreboardTipsShown(int newValue) {
        scoreboardTipsShown.getData(true).setInt(newValue);
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

    public @NotNull DirtyMap<PrimarySkillType, SkillBossBarState> getDirtyBarStateMap() {
        return barStateMap;
    }

    public @NotNull DirtyMap<PrimarySkillType, Integer> getDirtySkillLevelMap() {
        return skillLevelValues;
    }

    public @NotNull DirtyMap<PrimarySkillType, Float> getDirtyExperienceValueMap() {
        return skillExperienceValues;
    }

    public @NotNull DirtyData<MutableBoolean> getDirtyPartyChatSpying() {
        return partyChatSpying;
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

    public void setDirtyProfile() {
        this.dirtyFlag.setBoolean(true);
    }

    public long getLastLogin() {
        return lastLogin.getData().getImmutableCopy();
    }

    public void setLastLogin(long newValue) {
        lastLogin.getData().setLong(newValue);
    }

    public boolean isLeaderBoardExcluded() {
        return leaderBoardExclusion.getData().getImmutableCopy();
    }

    public void setLeaderBoardExclusion(boolean bool) {
        leaderBoardExclusion.getData(true).setBoolean(bool);
    }

    public @NotNull ImmutableMap<PrimarySkillType, Integer> copyPrimarySkillLevelsMap() {
        return ImmutableMap.copyOf(getSkillLevelsMap());
    }

    public @NotNull ImmutableMap<PrimarySkillType, Float> copyPrimarySkillExperienceValuesMap() {
        return ImmutableMap.copyOf(getSkillsExperienceMap());
    }
}
