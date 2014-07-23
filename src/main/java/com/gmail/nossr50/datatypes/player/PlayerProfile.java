package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
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

    /* Skill Data */
    private final Map<SkillType, Integer>   skills     = new HashMap<SkillType, Integer>();   // Skill & Level
    private final Map<SkillType, Float>     skillsXp   = new HashMap<SkillType, Float>();     // Skill & XP
    private final Map<AbilityType, Integer> abilityDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown

    @Deprecated
    public PlayerProfile(String playerName) {
        this(playerName, null);
    }

    public PlayerProfile(String playerName, UUID uuid) {
        this.uuid = uuid;
        this.playerName = playerName;

        mobHealthbarType = Config.getInstance().getMobHealthbarDefault();

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

    public PlayerProfile(String playerName, UUID uuid, Map<SkillType, Integer> levelData, Map<SkillType, Float> xpData, Map<AbilityType, Integer> cooldownData, MobHealthbarType mobHealthbarType) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.mobHealthbarType = mobHealthbarType;

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
        PlayerProfile profileCopy = new PlayerProfile(playerName, uuid, ImmutableMap.copyOf(skills), ImmutableMap.copyOf(skillsXp), ImmutableMap.copyOf(abilityDATS), mobHealthbarType);
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
        this.mobHealthbarType = mobHealthbarType;
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
     * Get the total amount of Xp before the next level.
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
