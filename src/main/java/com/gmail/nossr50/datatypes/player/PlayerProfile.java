package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SuperAbility;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.runnables.player.PlayerProfileSaveTask;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.player.UserManager;

import com.google.common.collect.ImmutableMap;

public class PlayerProfile {
    private final String playerName;
    private UUID uuid;
    private boolean loaded;
    private volatile boolean changed;

    /* HUDs */
    private MobHealthbarType mobHealthbarType;
    private int scoreboardTipsShown;

    /* Skill Data */
    private final Map<PrimarySkill, Integer>   skills     = new HashMap<PrimarySkill, Integer>();   // Skill & Level
    private final Map<PrimarySkill, Float>     skillsXp   = new HashMap<PrimarySkill, Float>();     // Skill & XP
    private final Map<SuperAbility, Integer> abilityDATS = new HashMap<SuperAbility, Integer>(); // Ability & Cooldown
    private final Map<UniqueDataType, Integer> uniquePlayerData = new HashMap<>(); //Misc data that doesn't fit into other categories (chimaera wing, etc..)

    // Store previous XP gains for deminished returns
    private DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<SkillXpGain>();
    private HashMap<PrimarySkill, Float> rollingSkillsXp = new HashMap<PrimarySkill, Float>();

    @Deprecated
    public PlayerProfile(String playerName) {
        this(playerName, null);
    }

    public PlayerProfile(String playerName, UUID uuid) {
        this.uuid = uuid;
        this.playerName = playerName;

        mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        scoreboardTipsShown = 0;

        for (SuperAbility superAbility : SuperAbility.values()) {
            abilityDATS.put(superAbility, 0);
        }

        for (PrimarySkill primarySkill : PrimarySkill.NON_CHILD_SKILLS) {
            skills.put(primarySkill, 0);
            skillsXp.put(primarySkill, 0F);
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

    public PlayerProfile(String playerName, UUID uuid, Map<PrimarySkill, Integer> levelData, Map<PrimarySkill, Float> xpData, Map<SuperAbility, Integer> cooldownData, MobHealthbarType mobHealthbarType, int scoreboardTipsShown, Map<UniqueDataType, Integer> uniqueProfileData) {
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
        new PlayerProfileSaveTask(this).runTaskAsynchronously(mcMMO.p);
    }

    public void save() {
        if (!changed || !loaded) {
            return;
        }

        // TODO should this part be synchronized?
        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, ImmutableMap.copyOf(skills), ImmutableMap.copyOf(skillsXp), ImmutableMap.copyOf(abilityDATS), mobHealthbarType, scoreboardTipsShown, ImmutableMap.copyOf(uniquePlayerData));
        changed = !mcMMO.getDatabaseManager().saveUser(profileCopy);

        if (changed) {
            mcMMO.p.getLogger().warning("PlayerProfile saving failed for player: " + playerName + " " + uuid);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUniqueId(UUID uuid) {
        changed = true;

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
        changed = true;

        this.mobHealthbarType = mobHealthbarType;
    }

    public int getScoreboardTipsShown() {
        return scoreboardTipsShown;
    }

    public void setScoreboardTipsShown(int scoreboardTipsShown) {
        changed = true;

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
        changed = true;
        uniquePlayerData.put(UniqueDataType.CHIMAERA_WING_DATS, DATS);
    }

    public void setUniqueData(UniqueDataType uniqueDataType, int newData) {
        changed = true;
        uniquePlayerData.put(uniqueDataType, newData);
    }

    public long getUniqueData(UniqueDataType uniqueDataType) { return uniquePlayerData.get(uniqueDataType); }

    /**
     * Get the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbility} to get the DATS for
     * @return the deactivation timestamp for the ability
     */
    public long getAbilityDATS(SuperAbility ability) {
        return abilityDATS.get(ability);
    }

    /**
     * Set the current deactivation timestamp of an ability.
     *
     * @param ability The {@link SuperAbility} to set the DATS for
     * @param DATS the DATS of the ability
     */
    protected void setAbilityDATS(SuperAbility ability, long DATS) {
        changed = true;

        abilityDATS.put(ability, (int) (DATS * .001D));
    }

    /**
     * Reset all ability cooldowns.
     */
    protected void resetCooldowns() {
        changed = true;

        for (SuperAbility ability : abilityDATS.keySet()) {
            abilityDATS.put(ability, 0);
        }
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(PrimarySkill skill) {
        return skill.isChildSkill() ? getChildSkillLevel(skill) : skills.get(skill);
    }

    public float getSkillXpLevelRaw(PrimarySkill skill) {
        return skillsXp.get(skill);
    }

    public int getSkillXpLevel(PrimarySkill skill) {
        return (int) Math.floor(getSkillXpLevelRaw(skill));
    }

    public void setSkillXpLevel(PrimarySkill skill, float xpLevel) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skillsXp.put(skill, xpLevel);
    }

    protected float levelUp(PrimarySkill skill) {
        float xpRemoved = getXpToLevel(skill);

        changed = true;

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
    public void removeXp(PrimarySkill skill, int xp) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    public void removeXp(PrimarySkill skill, float xp) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param skill Type of skill to modify
     * @param level New level value for the skill
     */
    public void modifySkill(PrimarySkill skill, int level) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skills.put(skill, level);
        skillsXp.put(skill, 0F);
    }

    /**
     * Add levels to a skill.
     *
     * @param skill Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(PrimarySkill skill, int levels) {
        modifySkill(skill, skills.get(skill) + levels);
    }

    /**
     * Add Experience to a skill.
     *
     * @param skill Type of skill to add experience to
     * @param xp Number of experience to add
     */
    public void addXp(PrimarySkill skill, float xp) {
        changed = true;

        if (skill.isChildSkill()) {
            Set<PrimarySkill> parentSkills = FamilyTree.getParents(skill);
            float dividedXP = (xp / parentSkills.size());

            for (PrimarySkill parentSkill : parentSkills) {
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
    public float getRegisteredXpGain(PrimarySkill primarySkill) {
        float xp = 0F;

        if (rollingSkillsXp.get(primarySkill) != null) {
            xp = rollingSkillsXp.get(primarySkill);
        }

        return xp;
    }

    /**
     * Register an experience gain
     * This is used for diminished XP returns
     *
     * @param primarySkill Skill being used
     * @param xp Experience amount to add
     */
    public void registerXpGain(PrimarySkill primarySkill, float xp) {
        gainedSkillsXp.add(new SkillXpGain(primarySkill, xp));
        rollingSkillsXp.put(primarySkill, getRegisteredXpGain(primarySkill) + xp);
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
     * @param primarySkill Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getXpToLevel(PrimarySkill primarySkill) {
        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? UserManager.getPlayer(playerName).getPowerLevel() : skills.get(primarySkill);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getCachedXpToLevel(level, formulaType);
    }

    private int getChildSkillLevel(PrimarySkill primarySkill) {
        Set<PrimarySkill> parents = FamilyTree.getParents(primarySkill);
        int sum = 0;

        for (PrimarySkill parent : parents) {
            sum += Math.min(getSkillLevel(parent), parent.getMaxLevel());
        }

        return sum / parents.size();
    }
}
