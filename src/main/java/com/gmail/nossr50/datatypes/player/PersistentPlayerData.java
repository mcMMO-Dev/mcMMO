package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.CoreSkillConstants;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.validation.NonNullRule;
import com.gmail.nossr50.datatypes.validation.PositiveIntegerRule;
import com.gmail.nossr50.datatypes.validation.Validator;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.experience.MMOExperienceBarManager;
import com.google.common.collect.ImmutableMap;
import com.neetgames.mcmmo.UniqueDataType;
import com.neetgames.mcmmo.exceptions.UnexpectedValueException;
import com.neetgames.mcmmo.skill.*;
import com.neetgames.mcmmo.player.MMOPlayerData;
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

public class PersistentPlayerData implements MMOPlayerData {

    private final @NotNull MutableBoolean dirtyFlag; //Dirty values in this class will change this flag as needed

    /* Player Stuff */
    private final @NotNull DirtyData<MutableString> playerName;
    private final @NotNull UUID playerUUID;

    /* Records */
    private final DirtyData<MutableLong> lastLogin;

    /* Skill Data */
    private final @NotNull DirtyMap<RootSkill, Integer> skillLevelValues;
    private final @NotNull DirtyMap<RootSkill, Float> skillExperienceValues;
    private final @NotNull DirtyMap<SuperSkill, Integer> abilityDeactivationTimestamps; // Ability & Cooldown
    private final @NotNull DirtyMap<UniqueDataType, Integer> uniquePlayerData; //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private final @NotNull DirtyMap<RootSkill, SkillBossBarState> barStateMap;

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
    public PersistentPlayerData(@NotNull UUID playerUUID, @NotNull String playerName) throws NullArgumentException {
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

        for(SuperSkill superSkill : mcMMO.p.getSkillRegister().getSuperSkills()) {
            abilityDeactivationTimestamps.put(superSkill, 0);
        }

        //Core skills
        //TODO: Don't store values for disabled skills
        for(RootSkill rootSkill : CoreSkillConstants.getImmutableCoreRootSkillSet()) {
            skillLevelValues.put(rootSkill, AdvancedConfig.getInstance().getStartingLevel());
            skillExperienceValues.put(rootSkill, 0F);
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
    public PersistentPlayerData(@NotNull UUID playerUUID,
                                @NotNull String playerName,
                                boolean partyChatSpying,
                                @NotNull Map<RootSkill, Integer> skillLevelValues,
                                @NotNull Map<RootSkill, Float> skillExperienceValues,
                                @NotNull Map<SuperSkill, Integer> abilityDeactivationTimestamps,
                                @NotNull Map<UniqueDataType, Integer> uniquePlayerData,
                                @NotNull Map<RootSkill, SkillBossBarState> barStateMap,
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
    private void validateRootSkillMap(Map<? extends RootSkill, ? extends Number> map) throws UnexpectedValueException, Exception {
        //TODO: Check for missing/unregistered
        Validator<Number> validator = new Validator<>();

        validator.addRule(new PositiveIntegerRule<>());
        validator.addRule(new NonNullRule<>());

        for(RootSkill rootSkill : mcMMO.p.getSkillRegister().getRootSkills()) {
            validator.validate(map.get(rootSkill));
        }
    }

    /**
     * Checks the map for a few potential logic issues such as negative numbers, or null entries
     *
     * @param map target map
     * @throws UnexpectedValueException when values are outside of expected norms
     * @throws Exception when values are outside of expected norms
     */
    private void validateSuperSkillMap(Map<? extends SuperSkill, ? extends Number> map) throws UnexpectedValueException, Exception {
        //TODO: Check for missing/unregistered
        Validator<Number> validator = new Validator<>();

        validator.addRule(new PositiveIntegerRule<>());
        validator.addRule(new NonNullRule<>());

        for(SuperSkill superSkill : mcMMO.p.getSkillRegister().getSuperSkills()) {
            validator.validate(map.get(superSkill));
        }
    }
    @Override
    public void setSkillLevel(@NotNull RootSkill rootSkill, int i) {
        skillLevelValues.put(rootSkill, i);
    }

    @Override
    public int getSkillLevel(@NotNull RootSkill rootSkill) {
        return 0;
    }

    @Override
    public boolean isDirtyProfile() {
        return dirtyFlag.getImmutableCopy();
    }

    @Override
    public void resetDirtyFlag() {
        dirtyFlag.setBoolean(false);
    }

    @Override
    public @NotNull String getPlayerName() {
        return playerName.getData().getImmutableCopy();
    }

    @Override
    public @NotNull UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public boolean isPartyChatSpying() { return partyChatSpying.getData().getImmutableCopy(); }

    @Override
    public void togglePartyChatSpying() {
        partyChatSpying.getData().setBoolean(!partyChatSpying.getData().getImmutableCopy());
    }

    @Override
    public void setPartyChatSpying(boolean bool) {
        this.partyChatSpying.getData().setBoolean(bool);
    }

    /*
     * Scoreboards
     */

    @Override
    public int getScoreboardTipsShown() {
        return scoreboardTipsShown.getData(false).getImmutableCopy();
    }

    @Override
    public void setScoreboardTipsShown(int newValue) {
        scoreboardTipsShown.getData(true).setInt(newValue);
    }

    @Override
    public int getChimaeraWingDATS() {
        return uniquePlayerData.get((UniqueDataType.CHIMAERA_WING_DATS));
    }

    @Override
    public void setChimaeraWingDATS(int DATS) {
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    @Override
    public void setUniqueData(@NotNull UniqueDataType uniqueDataType, int newData) {
        uniquePlayerData.put(uniqueDataType, newData);
    }

    @Override
    public long getUniqueData(@NotNull UniqueDataType uniqueDataType) { return uniquePlayerData.get(uniqueDataType); }

    @Override
    public long getAbilityDATS(@NotNull SuperSkill superSkill) {
        return abilityDeactivationTimestamps.get(superSkill);
    }

    public void setAbilityDATS(@NotNull SuperSkill superSkill, long DATS) {
        abilityDeactivationTimestamps.put(superSkill, (int) (DATS * .001D));
    }

    @Override
    public void resetCooldowns() {
        abilityDeactivationTimestamps.replaceAll((a, v) -> 0);
    }

    @Override
    public @NotNull Map<RootSkill, SkillBossBarState> getBarStateMap() {
        return barStateMap;
    }

    @Override
    public @NotNull DirtyMap<RootSkill, SkillBossBarState> getDirtyBarStateMap() {
        return barStateMap;
    }

    @Override
    public @NotNull DirtyMap<RootSkill, Integer> getDirtySkillLevelMap() {
        return skillLevelValues;
    }

    @Override
    public @NotNull DirtyMap<RootSkill, Float> getDirtyExperienceValueMap() {
        return skillExperienceValues;
    }

    @Override
    public @NotNull DirtyData<MutableBoolean> getDirtyPartyChatSpying() {
        return partyChatSpying;
    }

    @Override
    public @NotNull Map<RootSkill, Integer> getSkillLevelsMap() {
        return skillLevelValues;
    }

    @Override
    public @NotNull Map<RootSkill, Float> getSkillsExperienceMap() {
        return skillExperienceValues;
    }

    @Override
    public @NotNull Map<SuperSkill, Integer> getAbilityDeactivationTimestamps() {
        return abilityDeactivationTimestamps;
    }

    @Override
    public @NotNull Map<UniqueDataType, Integer> getUniquePlayerData() {
        return uniquePlayerData;
    }

    @Override
    public void setDirtyProfile() {
        this.dirtyFlag.setBoolean(true);
    }

    @Override
    public long getLastLogin() {
        return lastLogin.getData().getImmutableCopy();
    }

    @Override
    public void setLastLogin(long newValue) {
        lastLogin.getData().setLong(newValue);
    }

    @Override
    public boolean isLeaderBoardExcluded() {
        return leaderBoardExclusion.getData().getImmutableCopy();
    }

    @Override
    public void setLeaderBoardExclusion(boolean bool) {
        leaderBoardExclusion.getData(true).setBoolean(bool);
    }

    @Override
    public @NotNull ImmutableMap<RootSkill, Integer> copyPrimarySkillLevelsMap() {
        return ImmutableMap.copyOf(getSkillLevelsMap());
    }

    @Override
    public @NotNull ImmutableMap<RootSkill, Float> copyPrimarySkillExperienceValuesMap() {
        return ImmutableMap.copyOf(getSkillsExperienceMap());
    }
}
