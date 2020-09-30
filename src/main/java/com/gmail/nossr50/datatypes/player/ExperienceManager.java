package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.experience.SkillXpGain;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class ExperienceManager {

    private boolean isUsingUnarmed = false;

    private final PersistentPlayerData persistentPlayerDataRef;

    public ExperienceManager(PersistentPlayerData persistentPlayerData) {
        this.persistentPlayerDataRef = persistentPlayerData;
    }

    /**
     * Gets the power level of this player.
     * A power level is the sum of all skill levels for this player
     *
     * @return the power level of the player
     */
    public int getPowerLevel() {
        int powerLevel = 0;

        for (PrimarySkillType primarySkillType : PrimarySkillType.NON_CHILD_SKILLS) {
            powerLevel += getSkillLevel(primarySkillType);
        }

        return powerLevel;
    }

    /**
     * Get the current value of raw XP for a skill
     * @param primarySkillType target skill
     * @return the value of raw XP for target skill
     */
    public float getSkillXpLevelRaw(PrimarySkillType primarySkillType) {
        return persistentPlayerDataRef.getSkillsExperienceMap().get(primarySkillType);
    }

    /**
     * Get the value of XP a player has accumulated in target skill
     * Child Skills will return 0 (Child Skills will be removed in a future update)
     * @param primarySkillType target skill
     * @return the value for XP the player has accumulated in target skill
     */
    public int getSkillXpValue(PrimarySkillType primarySkillType) {
        if(primarySkillType.isChildSkill()) {
            return 0;
        }

        return (int) Math.floor(getSkillXpLevelRaw(primarySkillType));
    }

    public void setSkillXpValue(PrimarySkillType skill, float xpLevel) {
        if (skill.isChildSkill()) {
            return;
        }

        persistentPlayerDataRef.getSkillsExperienceMap().put(skill, xpLevel);
    }

    public float levelUp(PrimarySkillType skill) {
        float xpRemoved = getXpToLevel(skill);

        markProfileDirty();

        skills.put(skill, skills.get(skill) + 1);
        skillsXp.put(skill, skillsXp.get(skill) - xpRemoved);

        return xpRemoved;
    }

    /**
     * Whether or not a player is level capped
     * If they are at the power level cap, this will return true, otherwise it checks their skill level
     * @param primarySkillType
     * @return
     */
    public boolean hasReachedLevelCap(PrimarySkillType primarySkillType) {
        if(hasReachedPowerLevelCap())
            return true;

        return playerDataRef.getSkillLevel(primarySkillType) >= Config.getInstance().getLevelCap(primarySkillType);
    }

    /**
     * Whether or not a player is power level capped
     * Compares their power level total to the current set limit
     * @return true if they have reached the power level cap
     */
    public boolean hasReachedPowerLevelCap() {
        return this.getPowerLevel() >= Config.getInstance().getPowerLevelCap();
    }

    /**
     * Begins an experience gain. The amount will be affected by primarySkillType modifiers, global rate, perks, and may be shared with the party
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(Player player, PrimarySkillType primarySkillType, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        Validate.isTrue(xp >= 0.0, "XP gained should be greater than or equal to zero.");

        if (xp <= 0.0) {
            return;
        }

        if (primarySkillType.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);
            float splitXp = xp / parentSkills.size();

            for (PrimarySkillType parentSkill : parentSkills) {
                beginXpGain(player, parentSkill, splitXp, xpGainReason, xpGainSource);
            }

            return;
        }

        // Return if the experience has been shared
        if (party != null && ShareHandler.handleXpShare(xp, this, primarySkillType, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(player, primarySkillType, xp, xpGainReason, xpGainSource);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skill Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(Player player, PrimarySkillType skill, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if(player.getGameMode() == GameMode.CREATIVE)
            return;

        applyXpGain(skill, modifyXpGain(player, skill, xp), xpGainReason, xpGainSource);

        if (party == null) {
            return;
        }

        if (!Config.getInstance().getPartyXpNearMembersNeeded() || !mcMMO.getPartyManager().getNearMembers(this).isEmpty()) {
            party.applyXpGain(modifyXpGain(player, skill, xp));
        }
    }

    /**
     * Applies an experience gain
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(Player player, PrimarySkillType primarySkillType, float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if (!primarySkillType.getPermissions(player)) {
            return;
        }

        if (primarySkillType.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);

            for (PrimarySkillType parentSkill : parentSkills) {
                applyXpGain(player, parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(player, primarySkillType, xp, xpGainReason)) {
            return;
        }

        isUsingUnarmed = (primarySkillType == PrimarySkillType.UNARMED);
        checkXp(primarySkillType, xpGainReason, xpGainSource);
    }

    /**
     * Check the XP of a skill.
     *
     * @param primarySkillType The skill to check
     */
    private void checkXp(McMMOPlayer mmoPlayer, PrimarySkillType primarySkillType, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        if(hasReachedLevelCap(primarySkillType))
            return;

        if (getSkillXpLevelRaw(primarySkillType) < getXpToLevel(primarySkillType)) {
            processPostXpEvent(mmoPlayer.getPlayer(), primarySkillType, mcMMO.p, xpGainSource);
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(primarySkillType) >= getXpToLevel(primarySkillType)) {
            if (hasReachedLevelCap(primarySkillType)) {
                setSkillXpValue(primarySkillType, 0);
                break;
            }

            xpRemoved += levelUp(primarySkillType);
            levelsGained++;
        }

        if (EventUtils.tryLevelChangeEvent(mmoPlayer.getPlayer(), primarySkillType, levelsGained, xpRemoved, true, xpGainReason)) {
            return;
        }

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            SoundManager.sendSound(mmoPlayer.getPlayer(), mmoPlayer.getPlayer().getLocation(), SoundType.LEVEL_UP);
        }

        /*
         * Check to see if the player unlocked any new skills
         */

        NotificationManager.sendPlayerLevelUpNotification(player, primarySkillType, levelsGained, getSkillLevel(primarySkillType));

        //UPDATE XP BARS
        processPostXpEvent(player, primarySkillType, mcMMO.p, xpGainSource);
    }

    public void processPostXpEvent(McMMOPlayer mmoPlayer, PrimarySkillType primarySkillType, Plugin plugin, XPGainSource xpGainSource)
    {
        //Check if they've reached the power level cap just now
        if(hasReachedPowerLevelCap()) {
            NotificationManager.sendPlayerInformationChatOnly(mmoPlayer.getPlayer(), "LevelCap.PowerLevel", String.valueOf(Config.getInstance().getPowerLevelCap()));
        } else if(hasReachedLevelCap(primarySkillType)) {
            NotificationManager.sendPlayerInformationChatOnly(mmoPlayer.getPlayer(), "LevelCap.Skill", String.valueOf(Config.getInstance().getLevelCap(primarySkillType)), primarySkillType.getName());
        }

        //Updates from Party sources
        if(xpGainSource == XPGainSource.PARTY_MEMBERS && !ExperienceConfig.getInstance().isPartyExperienceBarsEnabled())
            return;

        //Updates from passive sources (Alchemy, Smelting, etc...)
        if(xpGainSource == XPGainSource.PASSIVE && !ExperienceConfig.getInstance().isPassiveGainsExperienceBarsEnabled())
            return;

        mmoPlayer.updateXPBar(primarySkillType, plugin);
    }

    public int getSkillLevel(PrimarySkillType skill) {
        return skill.isChildSkill() ? getChildSkillLevel(skill) : skills.get(skill);
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param primarySkillType Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getXpToLevel(PrimarySkillType primarySkillType) {
        if(primarySkillType.isChildSkill()) {
            return 0;
        }

        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? getPowerLevel() : playerDataRef..get(primarySkillType);
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

    /*
     * Xp Functions
     */

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
    public void setSkillLevel(PrimarySkillType skill, int level) {
        if (skill.isChildSkill()) {
            return;
        }

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
        setSkillLevel(skill, skills.get(skill) + levels);
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

    public ImmutableMap<PrimarySkillType, Integer> copyPrimarySkillLevelsMap() {
        return ImmutableMap.copyOf(primarySkillLevelMap);
    }

    public ImmutableMap<PrimarySkillType, Float> copyPrimarySkillExperienceValuesMap() {
        return ImmutableMap.copyOf(primarySkillCurrentExpMap);
    }

    /**
     * Modifies an experience gain using skill modifiers, global rate and perks
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    private float modifyXpGain(Player player, PrimarySkillType primarySkillType, float xp) {
        if ((primarySkillType.getMaxLevel() <= getSkillLevel(primarySkillType)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / primarySkillType.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        return PerksUtils.handleXpPerks(player, xp, primarySkillType);
    }

    public double getProgressInCurrentSkillLevel(PrimarySkillType primarySkillType)
    {
        if(primarySkillType.isChildSkill()) {
            return 1.0D;
        }

        double currentXP = getSkillXpValue(primarySkillType);
        double maxXP = getXpToLevel(primarySkillType);

        return (currentXP / maxXP);
    }


}
