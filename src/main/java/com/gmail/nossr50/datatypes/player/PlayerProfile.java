package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
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
    private final Map<SkillType, Integer>   skills     = new HashMap<SkillType, Integer>();   // Skill & Level
    private final Map<SkillType, Float>     skillsXp   = new HashMap<SkillType, Float>();     // Skill & XP
    private final Map<AbilityType, Integer> abilityDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown

    // Store previous XP gains for deminished returns
    private DelayQueue<SkillXpGain> gainedSkillsXp = new DelayQueue<SkillXpGain>();
    private HashMap<SkillType, Float> rollingSkillsXp = new HashMap<SkillType, Float>();

    @Deprecated
    public PlayerProfile(String playerName) {
        this(playerName, null);
    }

    public PlayerProfile(String playerName, UUID uuid) {
        this.uuid = uuid;
        this.playerName = playerName;

        mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        scoreboardTipsShown = 0;

        for (AbilityType abilityType : AbilityType.values()) {
            abilityDATS.put(abilityType, 0);
        }

        for (SkillType skillType : SkillType.NON_CHILD_SKILLS) {
            skills.put(skillType, 0);
            skillsXp.put(skillType, 0F);
        }
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

    public PlayerProfile(String playerName, UUID uuid, Map<SkillType, Integer> levelData, Map<SkillType, Float> xpData, Map<AbilityType, Integer> cooldownData, MobHealthbarType mobHealthbarType, int scoreboardTipsShown) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.mobHealthbarType = mobHealthbarType;
        this.scoreboardTipsShown = scoreboardTipsShown;

        skills.putAll(levelData);
        skillsXp.putAll(xpData);
        abilityDATS.putAll(cooldownData);

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
        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, ImmutableMap.copyOf(skills), ImmutableMap.copyOf(skillsXp), ImmutableMap.copyOf(abilityDATS), mobHealthbarType, scoreboardTipsShown);
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

    /**
     * Get the current deactivation timestamp of an ability.
     *
     * @param ability The {@link AbilityType} to get the DATS for
     * @return the deactivation timestamp for the ability
     */
    public long getAbilityDATS(AbilityType ability) {
        return abilityDATS.get(ability);
    }

    /**
     * Set the current deactivation timestamp of an ability.
     *
     * @param ability The {@link AbilityType} to set the DATS for
     * @param DATS the DATS of the ability
     */
    protected void setAbilityDATS(AbilityType ability, long DATS) {
        changed = true;

        abilityDATS.put(ability, (int) (DATS * .001D));
    }

    /**
     * Reset all ability cooldowns.
     */
    protected void resetCooldowns() {
        changed = true;

        for (AbilityType ability : abilityDATS.keySet()) {
            abilityDATS.put(ability, 0);
        }
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(SkillType skill) {
        return skill.isChildSkill() ? getChildSkillLevel(skill) : skills.get(skill);
    }

    public float getSkillXpLevelRaw(SkillType skill) {
        return skillsXp.get(skill);
    }

    public int getSkillXpLevel(SkillType skill) {
        return (int) Math.floor(getSkillXpLevelRaw(skill));
    }

    public void setSkillXpLevel(SkillType skill, float xpLevel) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skillsXp.put(skill, xpLevel);
    }

    protected float levelUp(SkillType skill) {
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
    public void removeXp(SkillType skill, int xp) {
        if (skill.isChildSkill()) {
            return;
        }

        changed = true;

        skillsXp.put(skill, skillsXp.get(skill) - xp);
    }

    public void removeXp(SkillType skill, float xp) {
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
    public void modifySkill(SkillType skill, int level) {
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
    public void addLevels(SkillType skill, int levels) {
        modifySkill(skill, skills.get(skill) + levels);
    }

    /**
     * Add Experience to a skill.
     *
     * @param skill Type of skill to add experience to
     * @param xp Number of experience to add
     */
    public void addXp(SkillType skill, float xp) {
        changed = true;

        if (skill.isChildSkill()) {
            Set<SkillType> parentSkills = FamilyTree.getParents(skill);
            float dividedXP = (xp / parentSkills.size());

            for (SkillType parentSkill : parentSkills) {
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
    public float getRegisteredXpGain(SkillType skillType) {
        float xp = 0F;

        if (rollingSkillsXp.get(skillType) != null) {
            xp = rollingSkillsXp.get(skillType);
        }

        return xp;
    }

    /**
     * Register an experience gain
     * This is used for diminished XP returns
     *
     * @param skillType Skill being used
     * @param xp Experience amount to add
     */
    public void registerXpGain(SkillType skillType, float xp) {
        gainedSkillsXp.add(new SkillXpGain(skillType, xp));
        rollingSkillsXp.put(skillType, getRegisteredXpGain(skillType) + xp);
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
     * @param skillType Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getXpToLevel(SkillType skillType) {
        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? UserManager.getPlayer(playerName).getPowerLevel() : skills.get(skillType);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getCachedXpToLevel(level, formulaType);
    }

    private int getChildSkillLevel(SkillType skillType) {
        Set<SkillType> parents = FamilyTree.getParents(skillType);
        int sum = 0;

        for (SkillType parent : parents) {
            sum += Math.min(getSkillLevel(parent), 1000);
        }

        return sum / parents.size();
    }
}
