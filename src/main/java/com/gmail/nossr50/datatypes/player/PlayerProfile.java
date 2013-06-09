package com.gmail.nossr50.datatypes.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.scoreboard.Scoreboard;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.datatypes.spout.huds.McMMOHud;
import com.gmail.nossr50.skills.child.FamilyTree;

public class PlayerProfile {
    private final String playerName;
    private boolean loaded;

    /* HUDs */
    private HudType hudType;
    private MobHealthbarType mobHealthbarType;
    private McMMOHud spoutHud;
    private Scoreboard playerStatsScoreboard;

    /* Skill Data */
    private final Map<SkillType, Integer>   skills     = new HashMap<SkillType, Integer>();   // Skill & Level
    private final Map<SkillType, Float>     skillsXp   = new HashMap<SkillType, Float>();     // Skill & XP
    private final Map<AbilityType, Integer> skillsDATS = new HashMap<AbilityType, Integer>(); // Ability & Cooldown

    public PlayerProfile(String playerName) {
        this.playerName = playerName;

        hudType = mcMMO.isSpoutEnabled() ? SpoutConfig.getInstance().getDefaultHudType() : HudType.DISABLED;
        mobHealthbarType = Config.getInstance().getMobHealthbarDefault();

        for (AbilityType abilityType : AbilityType.values()) {
            skillsDATS.put(abilityType, 0);
        }

        for (SkillType skillType : SkillType.nonChildSkills()) {
            skills.put(skillType, 0);
            skillsXp.put(skillType, 0F);
        }
    }

    public PlayerProfile(String playerName, boolean isLoaded) {
        this(playerName);
        this.loaded = isLoaded;
    }

    /**
     * Calling this constructor is considered loading the profile.
     */
    public PlayerProfile(String playerName, Map<SkillType, Integer> argSkills, Map<SkillType, Float> argSkillsXp, Map<AbilityType, Integer> argSkillsDats, HudType hudType, MobHealthbarType mobHealthbarType) {
        this(playerName, true);

        this.hudType = hudType;
        this.mobHealthbarType = mobHealthbarType;

        this.skills.putAll(argSkills);
        this.skillsXp.putAll(argSkillsXp);
        this.skillsDATS.putAll(argSkillsDats);

        loaded = true;
    }

    public void save() {
        mcMMO.getDatabaseManager().saveUser(this);
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isLoaded() {
        return loaded;
    }

    /*
     * HUD Stuff
     */

    public HudType getHudType() {
        return hudType;
    }

    public McMMOHud getSpoutHud() {
        return spoutHud;
    }

    public void setSpoutHud(McMMOHud spoutHud) {
        this.spoutHud = spoutHud;
    }

    public void setHudType(HudType hudType) {
        this.hudType = hudType;
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
     * Scoreboards
     */

    public Scoreboard getPlayerStatsScoreboard() {
        return playerStatsScoreboard;
    }

    public void setPlayerStatsScoreboard(Scoreboard statsScoreboard) {
        this.playerStatsScoreboard = statsScoreboard;
    }

    /*
     * Cooldowns
     */

    /**
     * Get the current DATS of a skill.
     *
     * @param abilityType Ability to get the DATS for
     * @return the DATS for the ability
     */
    public long getSkillDATS(AbilityType abilityType) {
        return skillsDATS.get(abilityType);
    }

    /**
     * Set the current DATS of a skill.
     *
     *  @param abilityType Ability to set the DATS for
     * @param DATS the DATS of the ability
     */
    public void setSkillDATS(AbilityType abilityType, long DATS) {
        int wearsOff = (int) (DATS * .001D);

        skillsDATS.put(abilityType, wearsOff);
    }

    /**
     * Reset all skill cooldowns.
     */
    public void resetCooldowns() {
        for (AbilityType ability : skillsDATS.keySet()) {
            skillsDATS.put(ability, 0);
        }
    }

    /*
     * Xp Functions
     */

    public int getSkillLevel(SkillType skillType) {
        if (skillType.isChildSkill()) {
            return getChildSkillLevel(skillType);
        }

        return skills.get(skillType);
    }

    public float getSkillXpLevelRaw(SkillType skillType) {
        return skillsXp.get(skillType);
    }

    public int getSkillXpLevel(SkillType skillType) {
        return (int) Math.floor(getSkillXpLevelRaw(skillType));
    }

    public void setSkillXpLevel(SkillType skillType, float newValue) {
        if (skillType.isChildSkill()) {
            return;
        }

        skillsXp.put(skillType, newValue);
    }

    public void skillUp(SkillType skillType, int newValue) {
        skills.put(skillType, skills.get(skillType) + newValue);
    }

    /**
     * Remove Xp from a skill.
     *
     * @param skillType Type of skill to modify
     * @param xp Amount of xp to remove
     */
    public void removeXp(SkillType skillType, int xp) {
        if (skillType.isChildSkill()) {
            return;
        }

        skillsXp.put(skillType, skillsXp.get(skillType) - xp);
    }

    /**
     * Modify a skill level.
     *
     * @param skillType Type of skill to modify
     * @param newValue New level value for the skill
     */
    public void modifySkill(SkillType skillType, int newValue) {
        if (skillType.isChildSkill()) {
            return;
        }

        skills.put(skillType, newValue);
        skillsXp.put(skillType, 0F);
    }

    /**
     * Add levels to a skill.
     *
     * @param skillType Type of skill to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(SkillType skillType, int levels) {
        if (skillType.isChildSkill()) {
            return;
        }

        skills.put(skillType, skills.get(skillType) + levels);
        skillsXp.put(skillType, 0F);
    }

    /**
     * Add Experience to a skill.
     *
     * @param skillType Type of skill to add experience to
     * @param experience Number of experience to add
     */
    public void addExperience(SkillType skillType, int experience) {
        if (skillType.isChildSkill()) {
            return;
        }

        skillsXp.put(skillType, skillsXp.get(skillType) + experience);
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param skillType Type of skill to check
     * @return the Xp remaining until next level
     */
    public int getXpToLevel(SkillType skillType) {
        return 1020 + (skills.get(skillType) * Config.getInstance().getFormulaMultiplierCurve());
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
