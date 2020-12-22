package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.party.Party;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.player.PersistentPlayerData;
import com.gmail.nossr50.datatypes.skills.CoreSkillConstants;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.experience.ExperienceUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.skill.SkillIdentity;
import org.bukkit.GameMode;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ExperienceManager {

    private boolean isUsingUnarmed = false;

    private final @NotNull PersistentPlayerData persistentPlayerDataRef;
    private @Nullable OnlineMMOPlayer mmoPlayer;

    public ExperienceManager(@NotNull OnlineMMOPlayer mmoPlayer) {
        this.mmoPlayer = mmoPlayer;
        this.persistentPlayerDataRef = mmoPlayer.getPersistentPlayerData();
    }

    public ExperienceManager(@NotNull PersistentPlayerData persistentPlayerData) {
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
    public float getSkillXpLevelRaw(@NotNull PrimarySkillType primarySkillType) {
        return persistentPlayerDataRef.getSkillsExperienceMap().get(primarySkillType);
    }

    /**
     * Get the value of XP a player has accumulated in target skill
     * Child Skills will return 0 (Child Skills will be removed in a future update)
     * @param skillIdentity target skill
     * @return the value for XP the player has accumulated in target skill
     */
    public int getSkillXpValue(@NotNull SkillIdentity skillIdentity) {
        if(CoreSkillConstants.isChildSkill(skillIdentity)) {
            return 0;
        }

        return (int) Math.floor(getSkillXpLevelRaw(skillIdentity));
    }

    public void setSkillXpValue(@NotNull SkillIdentity skillIdentity, float xpLevel) {
        if (CoreSkillConstants.isChildSkill(skillIdentity)) {
            return;
        }

        persistentPlayerDataRef.getSkillsExperienceMap().put(skillIdentity, xpLevel);
    }

    public float levelUp(@NotNull SkillIdentity skillIdentity) {
        float xpRemoved = getExperienceToNextLevel(skillIdentity);

        setSkillLevel(skillIdentity, getSkillLevel(skillIdentity) + 1);
        setSkillXpValue(skillIdentity, getSkillXpValue(skillIdentity) - xpRemoved);

        return xpRemoved;
    }

    /**
     * Whether or not a player is level capped
     * If they are at the power level cap, this will return true, otherwise it checks their skill level
     *
     * @param skillIdentity
     * @return
     */
    public boolean hasReachedLevelCap(@NotNull SkillIdentity skillIdentity) {
        if(hasReachedPowerLevelCap())
            return true;

        return getSkillLevel(skillIdentity) >= Config.getInstance().getLevelCap(skillIdentity);
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
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate, perks, and may be shared with the party
     *
     * @param skillIdentity Skill being used
     * @param xp Experience amount to process
     */
    public void beginXpGain(@NotNull SkillIdentity skillIdentity, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if (xp <= 0.0) {
            return;
        }

        if (CoreSkillConstants.isChildSkill(skillIdentity)) {
            Set<SkillIdentity> parentSkills = FamilyTree.getParentSkills(skillIdentity);
            float splitXp = xp / parentSkills.size();

            for (SkillIdentity parentSkill : parentSkills) {
                beginXpGain(parentSkill, splitXp, xpGainReason, xpGainSource);
            }

            return;
        }

        //TODO: The logic here is so stupid... rewrite later

        // Return if the experience has been shared
        if (mmoPlayer.getParty() != null && ShareHandler.handleXpShare(xp, mmoPlayer, mmoPlayer.getParty(), skillIdentity, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(skillIdentity, xp, xpGainReason, xpGainSource);
    }

    /**
     * Begins an experience gain. The amount will be affected by skill modifiers, global rate and perks
     *
     * @param skillIdentity Skill being used
     * @param xp Experience amount to process
     */
    public void beginUnsharedXpGain(@NotNull SkillIdentity skillIdentity, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(mmoPlayer.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        ExperienceUtils.applyXpGain(mmoPlayer, skillIdentity, modifyXpGain(skillIdentity, xp), xpGainReason, xpGainSource);

        Party party = mmoPlayer.getParty();

        if (party != null) {
            if (!Config.getInstance().getPartyXpNearMembersNeeded() || !mcMMO.getPartyManager().getNearMembers(mmoPlayer).isEmpty()) {
                party.getPartyExperienceManager().applyXpGain(modifyXpGain(skillIdentity, xp));
            }
        }
    }

    public int getSkillLevel(@NotNull SkillIdentity skillIdentity) {
        return CoreSkillConstants.isChildSkill(skillIdentity) ? getChildSkillLevel(skillIdentity) : getSkillLevel(skillIdentity);
    }

    /**
     * Get the amount of Xp remaining before the next level.
     *
     * @param skillIdentity Type of skill to check
     * @return the total amount of Xp until next level
     */
    public int getExperienceToNextLevel(@NotNull SkillIdentity skillIdentity) {
        if(CoreSkillConstants.isChildSkill(skillIdentity)) {
            return 0;
        }

        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? getPowerLevel() : getSkillLevel(skillIdentity);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType);
    }

    private int getChildSkillLevel(@NotNull SkillIdentity skillIdentity) {
        Set<SkillIdentity> parents = FamilyTree.getParentSkills(skillIdentity);
        int sum = 0;

        for (SkillIdentity parentIdentity : parents) {
            sum += getSkillLevel(parentIdentity);
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
    public void removeXp(@NotNull PrimarySkillType skill, int xp) {
        if (skill.isChildSkill()) {
            return;
        }

        setSkillXpValue(skill, getSkillXpValue(skill) - xp);
    }

    public void removeXp(PrimarySkillType skill, float xp) {
        if (skill.isChildSkill()) {
            return;
        }

        setSkillXpValue(skill, getSkillXpValue(skill) - xp);
    }

    /**
     * Modify a primarySkillType level.
     *
     * @param primarySkillType Type of primarySkillType to modify
     * @param level New level value for the primarySkillType
     */
    public void setSkillLevel(@NotNull PrimarySkillType primarySkillType, int level) {
        if (primarySkillType.isChildSkill()) {
            return;
        }

        //Don't allow levels to be negative
        if(level < 0)
            level = 0;

        setSkillLevel(primarySkillType, level);
        setSkillXpValue(primarySkillType, 0F);
    }

    /**
     * Add levels to a primarySkillType.
     *
     * @param primarySkillType Type of primarySkillType to add levels to
     * @param levels Number of levels to add
     */
    public void addLevels(@NotNull PrimarySkillType primarySkillType, int levels) {
        setSkillLevel(primarySkillType, getSkillLevel(primarySkillType) + levels);
    }

    /**
     * Add Experience to a primarySkillType.
     *
     * @param primarySkillType Type of primarySkillType to add experience to
     * @param xp Number of experience to add
     */
    public void addXp(@NotNull PrimarySkillType primarySkillType, float xp) {
        if (primarySkillType.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);
            float dividedXP = (xp / parentSkills.size());

            for (PrimarySkillType parentSkill : parentSkills) {
                setSkillXpValue(parentSkill, getSkillXpValue(parentSkill) + dividedXP);
            }
        }
        else {
            setSkillXpValue(primarySkillType, getSkillXpValue(primarySkillType) + xp);
        }
    }

    /**
     * Get the registered amount of experience gained
     * This is used for diminished XP returns
     *
     * @return xp Experience amount registered
     */
    public float getRegisteredXpGain(@NotNull PrimarySkillType primarySkillType) {
        float xp = 0F;

        if (get(primarySkillType) != null) { //??
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
    public void registerXpGain(@NotNull PrimarySkillType primarySkillType, float xp) {
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
     * Modifies an experience gain using skill modifiers, global rate and perks
     *
     * @param skillIdentity Skill being used
     * @param xp Experience amount to process
     * @return Modified experience
     */
    private float modifyXpGain(@NotNull SkillIdentity skillIdentity, float xp) {
        if ((skillIdentity.getMaxLevel() <= getSkillLevel(skillIdentity)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / skillIdentity.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        return PerksUtils.handleXpPerks(mmoPlayer.getPlayer(), xp, skillIdentity);
    }

    public double getProgressInCurrentSkillLevel(@NotNull SkillIdentity skillIdentity) throws UnknownSkillException
    {
        if(CoreSkillConstants.isChildSkill(skillIdentity)) {
            return 1.0D;
        }

        double currentXP = getSkillXpValue(skillIdentity);
        double maxXP = getExperienceToNextLevel(skillIdentity);

        return (currentXP / maxXP);
    }

    public void setUsingUnarmed(boolean bool) {
        isUsingUnarmed = bool;
    }

    /**
     * Applies an experience gain
     *
     * @param primarySkillType Skill being used
     * @param xp Experience amount to add
     */
    public void applyXpGain(@NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        //Only check for permissions if the player is online, otherwise just assume a command is being executed by an admin or some other means and add the XP
        if(mmoPlayer != null) {
            if (!primarySkillType.getPermissions(mmoPlayer.getPlayer())) {
                return;
            }
        }

        if (primarySkillType.isChildSkill()) {
            Set<PrimarySkillType> parentSkills = FamilyTree.getParents(primarySkillType);

            for (PrimarySkillType parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(mmoPlayer.getPlayer(), primarySkillType, xp, xpGainReason)) {
            return;
        }

        setUsingUnarmed(primarySkillType == PrimarySkillType.UNARMED);
        updateLevelStats(primarySkillType, xpGainReason, xpGainSource);
    }

    public void processPostXpEvent(@NotNull PrimarySkillType primarySkillType, @NotNull Plugin plugin, @NotNull XPGainSource xpGainSource)
    {
        /*
         * Everything in this method requires an online player, so if they aren't online we don't waste our time
         */
        if(mmoPlayer == null)
            return;

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

    /**
     * Updates a players level
     *
     * @param primarySkillType The skill to check
     */
    public void updateLevelStats(@NotNull PrimarySkillType primarySkillType, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(hasReachedLevelCap(primarySkillType))
            return;

        if (getSkillXpLevelRaw(primarySkillType) < getExperienceToNextLevel(primarySkillType)) {
            processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(primarySkillType) >= getExperienceToNextLevel(primarySkillType)) {
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

        NotificationManager.sendPlayerLevelUpNotification(mmoPlayer, primarySkillType, levelsGained, getSkillLevel(primarySkillType));

        //UPDATE XP BARS
        processPostXpEvent(primarySkillType, mcMMO.p, xpGainSource);
    }


}
