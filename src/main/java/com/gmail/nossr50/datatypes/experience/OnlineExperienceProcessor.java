package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.PlayerData;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.skills.child.FamilyTree;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.experience.ExperienceUtils;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.neetgames.mcmmo.exceptions.UnknownSkillException;
import com.neetgames.mcmmo.experience.XPGainReason;
import com.neetgames.mcmmo.experience.XPGainSource;
import com.neetgames.mcmmo.party.Party;
import com.neetgames.mcmmo.player.MMOPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class OnlineExperienceProcessor {

    private boolean isUsingUnarmed = false;

    private final @NotNull PlayerData mmoPlayerData;
    private final @NotNull MMOPlayer mmoPlayer;
    private final @NotNull Player playerRef;

    public OnlineExperienceProcessor(@NotNull MMOPlayer mmoPlayer, @NotNull Player playerRef) {
        this.mmoPlayer = mmoPlayer;
        this.playerRef = playerRef;
        this.mmoPlayerData = mmoPlayer.getMMOPlayerDataImpl();
    }

    public int getPowerLevel() {
        int powerLevel = 0;

        Map<PrimarySkillType, Integer> primarySkillTypeLevelMap = mmoPlayerData.getDirtySkillLevelMap().unwrapMap();

        for (PrimarySkillType primarySkillType : primarySkillTypeLevelMap.keySet()) {
            powerLevel += primarySkillTypeLevelMap.get(primarySkillType);
        }

        return powerLevel;
    }

    public float getSkillXpLevelRaw(@NotNull PrimarySkillType primarySkillType) {
        return mmoPlayerData.getSkillsExperienceMap().get(primarySkillType);
    }

    public int getSkillXpValue(@NotNull PrimarySkillType primarySkillType) {
        if(PrimarySkillType.isChildSkill(primarySkillType)) {
            return 0;
        }

        return (int) Math.floor(getSkillXpLevelRaw(primarySkillType));
    }

    public void setSkillXpValue(@NotNull PrimarySkillType primarySkillType, float xpLevel) {
        if (PrimarySkillType.isChildSkill(primarySkillType)) {
            return;
        }

        mmoPlayerData.getSkillsExperienceMap().put(primarySkillType, xpLevel);
    }

    public float levelUp(@NotNull PrimarySkillType primarySkillType) {
        float xpRemoved = getExperienceToNextLevel(primarySkillType);

        setSkillLevel(primarySkillType, getSkillLevel(primarySkillType) + 1);
        setSkillXpValue(primarySkillType, getSkillXpValue(primarySkillType) - xpRemoved);

        return xpRemoved;
    }

    public boolean hasReachedLevelCap(@NotNull PrimarySkillType primarySkillType) {
        if(hasReachedPowerLevelCap())
            return true;

        return getSkillLevel(primarySkillType) >= Config.getInstance().getLevelCap(primarySkillType);
    }

    public boolean hasReachedPowerLevelCap() {
        return this.getPowerLevel() >= Config.getInstance().getPowerLevelCap();
    }

    public void beginXpGain(@NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if (xp <= 0.0) {
            return;
        }

        if (PrimarySkillType.isChildSkill(primarySkillType)) {
            Set<RootSkill> parentSkills = FamilyTree.getParentSkills(primarySkillType);
            float splitXp = xp / parentSkills.size();

            for (RootSkill parentSkill : parentSkills) {
                beginXpGain(parentSkill, splitXp, xpGainReason, xpGainSource);
            }

            return;
        }

        //TODO: The logic here is so stupid... rewrite later

        // Return if the experience has been shared
        if (mmoPlayer.getParty() != null && ShareHandler.handleXpShare(xp, mmoPlayer, mmoPlayer.getParty(), primarySkillType, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(primarySkillType, xp, xpGainReason, xpGainSource);
    }

    public void beginUnsharedXpGain(@NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(Misc.adaptPlayer(mmoPlayer).getGameMode() == GameMode.CREATIVE)
            return;

        ExperienceUtils.applyXpGain(mmoPlayer, primarySkillType, modifyXpGain(primarySkillType, xp), xpGainReason, xpGainSource);

        Party party = mmoPlayer.getParty();

        if (party != null) {
            if (!Config.getInstance().getPartyXpNearMembersNeeded() || !mcMMO.getPartyManager().getNearMembers(mmoPlayer).isEmpty()) {
                party.getPartyExperienceManager().applyXpGain(modifyXpGain(primarySkillType, xp));
            }
        }
    }

    public int getSkillLevel(@NotNull PrimarySkillType primarySkillType) {
        return PrimarySkillType.isChildSkill(primarySkillType) ? getChildSkillLevel(primarySkillType) : getSkillLevel(primarySkillType);
    }

    public int getExperienceToNextLevel(@NotNull PrimarySkillType primarySkillType) {
        if(PrimarySkillType.isChildSkill(primarySkillType)) {
            return 0;
        }

        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? getPowerLevel() : getSkillLevel(primarySkillType);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType);
    }

    public int getChildSkillLevel(@NotNull PrimarySkillType primarySkillType) {
        Set<RootSkill> parents = FamilyTree.getParentSkills(primarySkillType);
        int sum = 0;

        for (RootSkill parentIdentity : parents) {
            sum += getSkillLevel(parentIdentity);
        }

        return sum / parents.size();
    }

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

    public void addLevels(@NotNull PrimarySkillType primarySkillType, int levels) {
        setSkillLevel(primarySkillType, getSkillLevel(primarySkillType) + levels);
    }

    public void addXp(@NotNull PrimarySkillType primarySkillType, float xp) {
        if (primarySkillType.isChildSkill()) {
            Set<RootSkill> parentSkills = FamilyTree.getParents(primarySkillType);
            float dividedXP = (xp / parentSkills.size());

            for (RootSkill parentSkill : parentSkills) {
                setSkillXpValue(parentSkill, getSkillXpValue(parentSkill) + dividedXP);
            }
        }
        else {
            setSkillXpValue(primarySkillType, getSkillXpValue(primarySkillType) + xp);
        }
    }

    public float getRegisteredXpGain(@NotNull PrimarySkillType primarySkillType) {
        float xp = 0F;

        if (get(primarySkillType) != null) { //??
            xp = rollingSkillsXp.get(primarySkillType);
        }

        return xp;
    }

    public void registerXpGain(@NotNull PrimarySkillType primarySkillType, float xp) {
        gainedSkillsXp.add(new SkillXpGain(primarySkillType, xp));
        rollingSkillsXp.put(primarySkillType, getRegisteredXpGain(primarySkillType) + xp);
    }

    public void purgeExpiredXpGains() {
        SkillXpGain gain;
        while ((gain = gainedSkillsXp.poll()) != null) {
            rollingSkillsXp.put(gain.getSkill(), getRegisteredXpGain(gain.getSkill()) - gain.getXp());
        }
    }

    private float modifyXpGain(@NotNull PrimarySkillType primarySkillType, float xp) {
        if ((primarySkillType.getMaxLevel() <= getSkillLevel(primarySkillType)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / primarySkillType.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        return PerksUtils.handleXpPerks(Misc.adaptPlayer(mmoPlayer), xp, primarySkillType);
    }

    public double getProgressInCurrentSkillLevel(@NotNull PrimarySkillType primarySkillType) throws UnknownSkillException
    {
        if(PrimarySkillType.isChildSkill(primarySkillType)) {
            return 1.0D;
        }

        double currentXP = getSkillXpValue(primarySkillType);
        double maxXP = getExperienceToNextLevel(primarySkillType);

        return (currentXP / maxXP);
    }

    public void setUsingUnarmed(boolean bool) {
        isUsingUnarmed = bool;
    }

    public void applyXpGain(@NotNull PrimarySkillType primarySkillType, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        //Only check for permissions if the player is online, otherwise just assume a command is being executed by an admin or some other means and add the XP
        if (!Permissions.skillEnabled(mmoPlayer.getPlayer(), PrimarySkillType.getSkill(primarySkillType))) {
            return;
        }

        if (PrimarySkillType.isChildSkill(primarySkillType)) {
            Set<RootSkill> parentSkills = FamilyTree.getParentSkills(primarySkillType);

            for (RootSkill parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(Misc.adaptPlayer(mmoPlayer), primarySkillType, xp, xpGainReason)) {
            return;
        }

        setUsingUnarmed(primarySkillType == PrimarySkillType.UNARMED);
        updateLevelStats(primarySkillType, xpGainReason, xpGainSource);
    }

    public void processPostXpEvent(@NotNull PrimarySkillType primarySkillType, @NotNull XPGainSource xpGainSource)
    {
        /*
         * Everything in this method requires an online player, so if they aren't online we don't waste our time
         */
        if(mmoPlayer == null)
            return;

        //Check if they've reached the power level cap just now
        if(hasReachedPowerLevelCap()) {
            NotificationManager.sendPlayerInformationChatOnly(Misc.adaptPlayer(mmoPlayer), "LevelCap.PowerLevel", String.valueOf(Config.getInstance().getPowerLevelCap()));
        } else if(hasReachedLevelCap(primarySkillType)) {
            NotificationManager.sendPlayerInformationChatOnly(Misc.adaptPlayer(mmoPlayer), "LevelCap.Skill", String.valueOf(Config.getInstance().getLevelCap(primarySkillType)), primarySkillType.getRawSkillName());
        }

        //Updates from Party sources
        if(xpGainSource == XPGainSource.PARTY_MEMBERS && !ExperienceConfig.getInstance().isPartyExperienceBarsEnabled())
            return;

        //Updates from passive sources (Alchemy, Smelting, etc...)
        if(xpGainSource == XPGainSource.PASSIVE && !ExperienceConfig.getInstance().isPassiveGainsExperienceBarsEnabled())
            return;

        mmoPlayer.updateXPBar(primarySkillType);
    }

    public void updateLevelStats(@NotNull PrimarySkillType primarySkillType, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(hasReachedLevelCap(primarySkillType))
            return;

        if (getSkillXpLevelRaw(primarySkillType) < getExperienceToNextLevel(primarySkillType)) {
            processPostXpEvent(primarySkillType, xpGainSource);
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

        if (EventUtils.tryLevelChangeEvent(Misc.adaptPlayer(mmoPlayer), primarySkillType, levelsGained, xpRemoved, true, xpGainReason)) {
            return;
        }

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            SoundManager.sendSound(Misc.adaptPlayer(mmoPlayer), Misc.adaptPlayer(mmoPlayer).getLocation(), SoundType.LEVEL_UP);
        }

        /*
         * Check to see if the player unlocked any new skills
         */

        NotificationManager.sendPlayerLevelUpNotification(mmoPlayer, primarySkillType, levelsGained, getSkillLevel(primarySkillType));

        //UPDATE XP BARS
        processPostXpEvent(primarySkillType, xpGainSource);
    }
}
