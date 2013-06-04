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

    public PlayerProfile(String playerName, boolean addNew) {
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

        if (!loadPlayer() && addNew) {
            mcMMO.getDatabaseManager().newUser(playerName);
            loaded = true;
        }
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

    private boolean loadPlayer() {
        List<String> playerData = mcMMO.getDatabaseManager().loadPlayerData(playerName);
        
        if (playerData == null || playerData.isEmpty()) {
            return false;
        }

        skills.put(SkillType.TAMING, Integer.valueOf(playerData.get(0)));
        skills.put(SkillType.MINING, Integer.valueOf(playerData.get(1)));
        skills.put(SkillType.REPAIR, Integer.valueOf(playerData.get(2)));
        skills.put(SkillType.WOODCUTTING, Integer.valueOf(playerData.get(3)));
        skills.put(SkillType.UNARMED, Integer.valueOf(playerData.get(4)));
        skills.put(SkillType.HERBALISM, Integer.valueOf(playerData.get(5)));
        skills.put(SkillType.EXCAVATION, Integer.valueOf(playerData.get(6)));
        skills.put(SkillType.ARCHERY, Integer.valueOf(playerData.get(7)));
        skills.put(SkillType.SWORDS, Integer.valueOf(playerData.get(8)));
        skills.put(SkillType.AXES, Integer.valueOf(playerData.get(9)));
        skills.put(SkillType.ACROBATICS, Integer.valueOf(playerData.get(10)));
        skills.put(SkillType.FISHING, Integer.valueOf(playerData.get(11)));

        skillsXp.put(SkillType.TAMING, (float) Integer.valueOf(playerData.get(12)));
        skillsXp.put(SkillType.MINING, (float) Integer.valueOf(playerData.get(13)));
        skillsXp.put(SkillType.REPAIR, (float) Integer.valueOf(playerData.get(14)));
        skillsXp.put(SkillType.WOODCUTTING, (float) Integer.valueOf(playerData.get(15)));
        skillsXp.put(SkillType.UNARMED, (float) Integer.valueOf(playerData.get(16)));
        skillsXp.put(SkillType.HERBALISM, (float) Integer.valueOf(playerData.get(17)));
        skillsXp.put(SkillType.EXCAVATION, (float) Integer.valueOf(playerData.get(18)));
        skillsXp.put(SkillType.ARCHERY, (float) Integer.valueOf(playerData.get(19)));
        skillsXp.put(SkillType.SWORDS, (float) Integer.valueOf(playerData.get(20)));
        skillsXp.put(SkillType.AXES, (float) Integer.valueOf(playerData.get(21)));
        skillsXp.put(SkillType.ACROBATICS, (float) Integer.valueOf(playerData.get(22)));
        skillsXp.put(SkillType.FISHING, (float) Integer.valueOf(playerData.get(23)));

        // Taming 24 - Unused
        skillsDATS.put(AbilityType.SUPER_BREAKER, Integer.valueOf(playerData.get(25)));
        // Repair 26 - Unused
        skillsDATS.put(AbilityType.TREE_FELLER, Integer.valueOf(playerData.get(27)));
        skillsDATS.put(AbilityType.BERSERK, Integer.valueOf(playerData.get(28)));
        skillsDATS.put(AbilityType.GREEN_TERRA, Integer.valueOf(playerData.get(29)));
        skillsDATS.put(AbilityType.GIGA_DRILL_BREAKER, Integer.valueOf(playerData.get(30)));
        // Archery 31 - Unused
        skillsDATS.put(AbilityType.SERRATED_STRIKES, Integer.valueOf(playerData.get(32)));
        skillsDATS.put(AbilityType.SKULL_SPLITTER, Integer.valueOf(playerData.get(33)));
        // Acrobatics 34 - Unused
        skillsDATS.put(AbilityType.BLAST_MINING, Integer.valueOf(playerData.get(35)));

        try {
            hudType = HudType.valueOf(playerData.get(36));
        }
        catch (Exception e) {
            hudType = HudType.STANDARD; // Shouldn't happen unless database is being tampered with
        }

        try {
            mobHealthbarType = MobHealthbarType.valueOf(playerData.get(37));
        }
        catch (Exception e) {
            mobHealthbarType = Config.getInstance().getMobHealthbarDefault();
        }

        loaded = true;
        return true;
    }
}
