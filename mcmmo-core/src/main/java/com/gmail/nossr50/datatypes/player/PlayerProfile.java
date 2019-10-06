package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

public class PlayerProfile {
    private final String playerName;
    /* Skill Data */
    private final Map<PrimarySkillType, Integer> primarySkillLevelMap = new HashMap<>();   // Skill & Level
    private final Map<PrimarySkillType, Double> primarySkillXPMap = new HashMap<>();     // Skill & XP
    private final Map<SuperAbilityType, Integer> abilityDATS = new HashMap<>(); // Ability & Cooldown
    private final Map<UniqueDataType, Integer> uniquePlayerData = new HashMap<>(); //Misc data that doesn't fit into other categories (chimaera wing, etc..)
    private UUID uuid;
    private boolean loaded;
    private volatile boolean changed;
    /* HUDs */
    private MobHealthbarType mobHealthbarType;
    private int scoreboardTipsShown;
    private int saveAttempts = 0;
    // Store previous XP gains for diminished returns
    private DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<SkillXpGain>();
    private HashMap<PrimarySkillType, Double> rollingSkillsXp = new HashMap<PrimarySkillType, Double>();
    private final mcMMO pluginRef;

    @Deprecated
    public PlayerProfile(mcMMO pluginRef, String playerName) {
        this(pluginRef, playerName, null);
    }

    public PlayerProfile(mcMMO pluginRef, String playerName, UUID uuid) {
        this.pluginRef = pluginRef;
        this.uuid = uuid;
        this.playerName = playerName;

        mobHealthbarType = pluginRef.getConfigManager().getConfigMobs().getCombat().getHealthBars().getDisplayBarType();
        scoreboardTipsShown = 0;

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            abilityDATS.put(superAbilityType, 0);
        }

        for (PrimarySkillType primarySkillType : pluginRef.getSkillTools().NON_CHILD_SKILLS) {
            primarySkillLevelMap.put(primarySkillType, pluginRef.getPlayerLevelingSettings().getConfigSectionLevelingGeneral().getStartingLevel());
            primarySkillXPMap.put(primarySkillType, 0.0);
        }

        //Misc Cooldowns
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, 0); //Chimaera wing
    }

    public PlayerProfile(mcMMO pluginRef, String playerName, UUID uuid, boolean isLoaded) {
        this(pluginRef, playerName, uuid);
        this.loaded = isLoaded;
    }

    public PlayerProfile(mcMMO pluginRef, String playerName, UUID uuid, Map<PrimarySkillType, Integer> levelData, Map<PrimarySkillType, Double> xpData, Map<SuperAbilityType, Integer> cooldownData, MobHealthbarType mobHealthbarType, int scoreboardTipsShown, Map<UniqueDataType, Integer> uniqueProfileData) {
        this.pluginRef = pluginRef;
        this.playerName = playerName;
        this.uuid = uuid;
        this.mobHealthbarType = mobHealthbarType;
        this.scoreboardTipsShown = scoreboardTipsShown;

        primarySkillLevelMap.putAll(levelData);
        primarySkillXPMap.putAll(xpData);
        abilityDATS.putAll(cooldownData);
        uniquePlayerData.putAll(uniqueProfileData);

        loaded = true;
    }

    public void scheduleAsyncSave() {
        new PlayerProfileSaveTask(this, false).runTaskAsynchronously(pluginRef);
    }

    public void scheduleSyncSave() {
        new PlayerProfileSaveTask(this, true).runTask(pluginRef);
    }

    public void scheduleAsyncSaveDelay() {
        new PlayerProfileSaveTask(this, false).runTaskLaterAsynchronously(pluginRef, 20);
    }

    @Deprecated
    public void scheduleSyncSaveDelay() {
        new PlayerProfileSaveTask(this, true).runTaskLater(pluginRef, 20);
    }

    public void save(boolean useSync) {
        if (!changed || !loaded) {
            saveAttempts = 0;
            return;
        }

        // TODO should this part be synchronized?
        PlayerProfile profileCopy = new PlayerProfile(pluginRef, playerName, uuid, ImmutableMap.copyOf(primarySkillLevelMap), ImmutableMap.copyOf(primarySkillXPMap), ImmutableMap.copyOf(abilityDATS), mobHealthbarType, scoreboardTipsShown, ImmutableMap.copyOf(uniquePlayerData));
        changed = !pluginRef.getDatabaseManager().saveUser(profileCopy);

        if (changed) {
            pluginRef.getLogger().severe("PlayerProfile saving failed for player: " + playerName + " " + uuid);

            if (saveAttempts > 0) {
                pluginRef.getLogger().severe("Attempted to save profile for player " + getPlayerName()
                        + " resulted in failure. " + saveAttempts + " have been made so far.");
            }

            if (saveAttempts < 10) {
                saveAttempts++;

                if(useSync)
                    scheduleSyncSave(); //Execute sync saves immediately
                else
                    scheduleAsyncSaveDelay();

                return;
            } else {
                pluginRef.getLogger().severe("mcMMO has failed to save the profile for "
                        + getPlayerName() + " numerous times." +
                        " mcMMO will now stop attempting to save this profile." +
                        " Check your console for errors and inspect your DB for issues.");
            }

        } else {
            saveAttempts = 0;
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUniqueId(UUID uuid) {
        markProfileDirty();

        this.uuid = uuid;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /*
     * Mob Healthbars
     */

    public MobHealthbarType getMobHealthbarType() {
        return mobHealthbarType;
    }

    public void setMobHealthbarType(MobHealthbarType mobHealthbarType) {
        markProfileDirty();

        this.mobHealthbarType = mobHealthbarType;
    }

    /**
     * Marks the profile as "dirty" which flags a profile to be saved in the next save operation
     */
    public void markProfileDirty() {
        changed = true;
    }

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public void setScoreboardTipsShown(int scoreboardTipsShown) {
        markProfileDirty();

        this.scoreboardTipsShown = scoreboardTipsShown;
    }

    public void increaseTipsShown() {
        setScoreboardTipsShown(getScoreboardTipsShown() + 1);
    }

    /*
     * Cooldowns
     */

    public int getChimaerWingDATS() {
        return uniquePlayerData.get(UniqueDataType.CHIMAERA_WING_DATS);
    }

    protected void setChimaeraWingDATS(int DATS) {
        markProfileDirty();
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    public void setUniqueData(UniqueDataType uniqueDataType, int newData) {
        markProfileDirty();
        uniquePlayerData.put(uniqueDataType, newData);
    }

    public long getUniqueData(UniqueDataType uniqueDataType) {
        return uniquePlayerData.get(uniqueDataType);
    }

    /**
     * Get the current deactivation timestamp of an ability.
     *
     * @param superAbilityType The {@link SuperAbilityType} to get the DATS for
     * @return the deactivation timestamp for the ability
     */
    public long getAbilityDATS(SuperAbilityType superAbilityType) {
        return abilityDATS.get(superAbilityType);
    }

    /**
     * Set the current deactivation timestamp of an ability.
     *
     * @param superAbilityType The {@link SuperAbilityType} to set the DATS for
     * @param DATS    the DATS of the ability
     */
    protected void setAbilityDATS(SuperAbilityType superAbilityType, long DATS) {
        markProfileDirty();

        abilityDATS.put(superAbilityType, (int) (DATS * .001D));
    }

    /**
     * Reset all ability cooldowns.
     */
    protected void resetCooldowns() {
        markProfileDirty();

        for (SuperAbilityType ability : abilityDATS.keySet()) {
            abilityDATS.put(ability, 0);
        }
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(PrimarySkillType primarySkillType) {
        return pluginRef.getSkillTools().isChildSkill(primarySkillType) ? getChildSkillLevel(primarySkillType) : primarySkillLevelMap.get(primarySkillType);
    }

    public double getSkillXpLevelRaw(PrimarySkillType primarySkillType) {
        return primarySkillXPMap.get(primarySkillType);
    }

    public int getSkillXpLevel(PrimarySkillType primarySkillType) {
        return (int) Math.floor(getSkillXpLevelRaw(primarySkillType));
    }

    public void setSkillXpLevel(PrimarySkillType primarySkillType, double xpLevel) {
        if (pluginRef.getSkillTools().isChildSkill(primarySkillType)) {
            return;
        }

        markProfileDirty();

        primarySkillXPMap.put(primarySkillType, xpLevel);
    }

    protected double levelUp(PrimarySkillType skill) {
        double xpRemoved = getXpToLevel(skill);

        markProfileDirty();

        primarySkillLevelMap.put(skill, primarySkillLevelMap.get(skill) + 1);
        primarySkillXPMap.put(skill, primarySkillXPMap.get(skill) - xpRemoved);

        return xpRemoved;
    }

    /**
     * Remove Xp from a skill.
     *
     * @param primarySkillType Type of skill to modify
     * @param xp    Amount of xp to remove
     */
    public void removeXp(PrimarySkillType primarySkillType, int xp) {
        if (pluginRef.getSkillTools().isChildSkill(primarySkillType)) {
            return;
        }

        markProfileDirty();

        primarySkillXPMap.put(primarySkillType, primarySkillXPMap.get(primarySkillType) - xp);
    }

    public void removeXp(PrimarySkillType primarySkillType, double xp) {
        if (pluginRef.getSkillTools().isChildSkill(primarySkillType)) {
            return;
        }

        markProfileDirty();

        primarySkillXPMap.put(primarySkillType, primarySkillXPMap.get(primarySkillType) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param primarySkillType Type of skill to modify
     * @param level New level value for the skill
     */
    public void modifySkill(PrimarySkillType primarySkillType, int level) {
        if (pluginRef.getSkillTools().isChildSkill(primarySkillType)) {
            return;
        }

        markProfileDirty();

        //Don't allow levels to be negative
        if (level < 0)
            level = 0;

        primarySkillLevelMap.put(primarySkillType, level);
        primarySkillXPMap.put(primarySkillType, 0.0);
    }

    /**
     * Add levels to a skill.
     *
     * @param primarySkillType  Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(PrimarySkillType primarySkillType, int levels) {
        modifySkill(primarySkillType, primarySkillLevelMap.get(primarySkillType) + levels);
    }

    /**
     * Add Experience to a skill.
     *
     * @param primarySkillType Type of skill to add experience to
     * @param xp    Number of experience to add
     */
    public void addXp(PrimarySkillType primarySkillType, double xp) {
        markProfileDirty();

        if (pluginRef.getSkillTools().isChildSkill(primarySkillType)) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);
            double dividedXP = (xp / parentSkills.size());

            for (PrimarySkillType parentSkill : parentSkills) {
                primarySkillXPMap.put(parentSkill, primarySkillXPMap.get(parentSkill) + dividedXP);
            }
        } else {
            primarySkillXPMap.put(primarySkillType, primarySkillXPMap.get(primarySkillType) + xp);
        }
    }

    /**
     * Get the registered amount of experience gained
     * This is used for diminished XP returns
     *
     * @return xp Experience amount registered
     */
    public double getRegisteredXpGain(PrimarySkillType primarySkillType) {
        double xp = 0F;

        if (rollingSkillsXp.get(primarySkillType) != null) {
            xp = rollingSkillsXp.get(primarySkillType);
        }

        return xp;
    }

    /**
     * Register an experience gain
     * This is used for diminished XP returns
     *
     * @param primarySkillType Skill being used
     * @param xp               Experience amount to add
     */
    public void registerXpGain(PrimarySkillType primarySkillType, double xp) {
        gainedSkillsXp.add(new SkillXpGain(primarySkillType, xp, pluginRef.getConfigManager().getConfigLeveling().getDimishedReturnTimeInterval()));
        rollingSkillsXp.put(primarySkillType, getRegisteredXpGain(primarySkillType) + xp);
    }

    /**
     * Remove experience gains older than a given time
     * This is used for diminished XP returns
     */
    public void purgeExpiredXpGains() {
        SkillXpGain gain;
        while ((gain = gainedSkillsXp.poll()) != null) {
            rollingSkillsXp.put(gain.getSkill(), getRegisteredXpGain(gain.getSkill()) - gain.getXp());
        }
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param primarySkillType Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getXpToLevel(PrimarySkillType primarySkillType) {
        int level = (pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceFormula().isCumulativeCurveEnabled()) ? pluginRef.getUserManager().getPlayer(playerName).getPowerLevel() : primarySkillLevelMap.get(primarySkillType);

        return pluginRef.getFormulaManager().getXPtoNextLevel(level);
    }

    private int getChildSkillLevel(PrimarySkillType primarySkillType) {
        Set<PrimarySkillType> parents = FamilyTree.getParents(primarySkillType);
        int sum = 0;

        for (PrimarySkillType parentSkill : parents) {
            if (pluginRef.getPlayerLevelingSettings().isSkillLevelCapEnabled(parentSkill))
                sum += Math.min(getSkillLevel(parentSkill), pluginRef.getConfigManager().getConfigLeveling().getSkillLevelCap(parentSkill));
            else
                sum += getSkillLevel(parentSkill);
        }

        return sum / parents.size();
    }
}
