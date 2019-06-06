package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

public class PlayerProfile {
    private final String playerName;
    private UUID uuid;
    private boolean loaded;
    private volatile boolean changed;

    /* HUDs */
    private MobHealthbarType mobHealthbarType;
    private int scoreboardTipsShown;
    private int saveAttempts = 0;

    /* Skill Data */
    private final Map<PrimarySkillType, Integer>   skills     = new HashMap<PrimarySkillType, Integer>();   // Skill & Level
    private final Map<PrimarySkillType, Float>     skillsXp   = new HashMap<PrimarySkillType, Float>();     // Skill & XP
    private final Map<SuperAbilityType, Integer> abilityDATS = new HashMap<SuperAbilityType, Integer>(); // Ability & Cooldown
    private final Map<UniqueDataType, Integer> uniquePlayerData = new HashMap<>(); //Misc data that doesn't fit into other categories (chimaera wing, etc..)

    // Store previous XP gains for diminished returns
    private DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<SkillXpGain>();
    private HashMap<PrimarySkillType, Float> rollingSkillsXp = new HashMap<PrimarySkillType, Float>();

    @Deprecated
    public PlayerProfile(String playerName) {
        this(playerName, null);
    }

    public PlayerProfile(String playerName, UUID uuid) {
        this.uuid = uuid;
        this.playerName = playerName;

        mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        scoreboardTipsShown = 0;

        for (SuperAbilityType superAbilityType : SuperAbilityType.values()) {
            abilityDATS.put(superAbilityType, 0);
        }

        for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
            skills.put(primarySkillType, AdvancedConfig.getInstance().getStartingLevel());
            skillsXp.put(primarySkillType, 0F);
        }

        //Misc Cooldowns
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, 0); //Chimaera wing
    }

    @Deprecated
    public PlayerProfile(String playerName, boolean isLoaded) {
        this(playerName);
        this.loaded = isLoaded;
    }

    public PlayerProfile(String playerName, UUID uuid, boolean isLoaded) {
        this(playerName, uuid);
        this.loaded = isLoaded;
    }

    public PlayerProfile(String playerName, UUID uuid, Map<PrimarySkillType, Integer> levelData, Map<PrimarySkillType, Float> xpData, Map<SuperAbilityType, Integer> cooldownData, MobHealthbarType mobHealthbarType, int scoreboardTipsShown, Map<UniqueDataType, Integer> uniqueProfileData) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.mobHealthbarType = mobHealthbarType;
        this.scoreboardTipsShown = scoreboardTipsShown;

        skills.putAll(levelData);
        skillsXp.putAll(xpData);
        abilityDATS.putAll(cooldownData);
        uniquePlayerData.putAll(uniqueProfileData);

        loaded = true;
    }

    public void scheduleAsyncSave() {
        new PlayerProfileSaveTask(this, false).runTaskAsynchronously(mcMMO.p);
    }

    public void scheduleSyncSave() {
        new PlayerProfileSaveTask(this, true).runTask(mcMMO.p);
    }

    public void scheduleAsyncSaveDelay() {
        new PlayerProfileSaveTask(this, false).runTaskLaterAsynchronously(mcMMO.p, 20);
    }

    @Deprecated
    public void scheduleSyncSaveDelay() {
        new PlayerProfileSaveTask(this, true).runTaskLater(mcMMO.p, 20);
    }

    public void save(boolean useSync) {
        if (!changed || !loaded) {
            saveAttempts = 0;
            return;
        }

        // TODO should this part be synchronized?
        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, ImmutableMap.copyOf(skills), ImmutableMap.copyOf(skillsXp), ImmutableMap.copyOf(abilityDATS), mobHealthbarType, scoreboardTipsShown, ImmutableMap.copyOf(uniquePlayerData));
        changed = !mcMMO.getDatabaseManager().saveUser(profileCopy);

        if (changed) {
            mcMMO.p.getLogger().severe("PlayerProfile saving failed for player: " + playerName + " " + uuid);

            if(saveAttempts > 0)
            {
                mcMMO.p.getLogger().severe("Attempted to save profile for player "+getPlayerName()
                        + " resulted in failure. "+saveAttempts+" have been made so far.");
            }

            if(saveAttempts < 10)
            {
                saveAttempts++;

                if(useSync)
                    scheduleSyncSave(); //Execute sync saves immediately
                else
                    scheduleAsyncSaveDelay();

                return;
            } else {
                mcMMO.p.getLogger().severe("mcMMO has failed to save the profile for "
                        +getPlayerName()+" numerous times." +
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

    public int getChimaerWingDATS() { return uniquePlayerData.get(UniqueDataType.CHIMAERA_WING_DATS);}

    protected void setChimaeraWingDATS(int DATS) {
        markProfileDirty();
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    public void setUniqueData(UniqueDataType uniqueDataType, int newData) {
        markProfileDirty();
        uniquePlayerData.put(uniqueDataType, newData);
    }

    public long getUniqueData(UniqueDataType uniqueDataType) { return uniquePlayerData.get(uniqueDataType); }

    /**
     * Get the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbilityType} to get the DATS for
     * @return the deactivation timestamp for the ability
     */
    public long getAbilityDATS(SuperAbilityType ability) {
        return abilityDATS.get(ability);
    }

    /**
     * Set the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbilityType} to set the DATS for
     * @param DATS the DATS of the ability
     */
    protected void setAbilityDATS(SuperAbilityType ability, long DATS) {
        markProfileDirty();

        abilityDATS.put(ability, (int) (DATS * .001D));
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

    public int getSkillLevel(PrimarySkillType skill) {
        return skill.isChildSkill() ? getChildSkillLevel(skill) : skills.get(skill);
    }

    public float getSkillXpLevelRaw(PrimarySkillType skill) {
        return skillsXp.get(skill);
    }

    public int getSkillXpLevel(PrimarySkillType skill) {
        return (int) Math.floor(getSkillXpLevelRaw(skill));
    }

    public void setSkillXpLevel(PrimarySkillType skill, float xpLevel) {
        if (skill.isChildSkill()) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, xpLevel);
    }

    protected float levelUp(PrimarySkillType skill) {
        float xpRemoved = getXpToLevel(skill);

        markProfileDirty();

        skills.put(skill, skills.get(skill) + 1);
        skillsXp.put(skill, skillsXp.get(skill) - xpRemoved);

        return xpRemoved;
    }

    /**
     * Remove Xp from a skill.
     *
     * @param skill Type of skill to modify
     * @param xp Amount of xp to remove
     */
    public void removeXp(PrimarySkillType skill, int xp) {
        if (skill.isChildSkill()) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    public void removeXp(PrimarySkillType skill, float xp) {
        if (skill.isChildSkill()) {
            return;
        }

        markProfileDirty();

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param skill Type of skill to modify
     * @param level New level value for the skill
     */
    public void modifySkill(PrimarySkillType skill, int level) {
        if (skill.isChildSkill()) {
            return;
        }

        markProfileDirty();

        //Don't allow levels to be negative
        if(level < 0)
            level = 0;

        skills.put(skill, level);
        skillsXp.put(skill, 0F);
    }

    /**
     * Add levels to a skill.
     *
     * @param skill Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(PrimarySkillType skill, int levels) {
        modifySkill(skill, skills.get(skill) + levels);
    }

    /**
     * Add Experience to a skill.
     *
     * @param skill Type of skill to add experience to
     * @param xp Number of experience to add
     */
    public void addXp(PrimarySkillType skill, float xp) {
        markProfileDirty();

        if (skill.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(skill);
            float dividedXP = (xp / parentSkills.size());

            for (PrimarySkillType parentSkill : parentSkills) {
                skillsXp.put(parentSkill, skillsXp.get(parentSkill) + dividedXP);
            }
        }
        else {
            skillsXp.put(skill, skillsXp.get(skill) + xp);
        }
    }

    /**
     * Get the registered amount of experience gained
     * This is used for diminished XP returns
     *
     * @return xp Experience amount registered
     */
    public float getRegisteredXpGain(PrimarySkillType primarySkillType) {
        float xp = 0F;

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
     * @param xp Experience amount to add
     */
    public void registerXpGain(PrimarySkillType primarySkillType, float xp) {
        gainedSkillsXp.add(new SkillXpGain(primarySkillType, xp));
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
        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? UserManager.getPlayer(playerName).getPowerLevel() : skills.get(primarySkillType);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType);
    }

    private int getChildSkillLevel(PrimarySkillType primarySkillType) {
        Set<PrimarySkillType> parents = FamilyTree.getParents(primarySkillType);
        int sum = 0;

        for (PrimarySkillType parent : parents) {
            sum += Math.min(getSkillLevel(parent), parent.getMaxLevel());
        }

        return sum / parents.size();
    }
}
