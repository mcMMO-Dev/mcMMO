package com.gmail.nossr50.datatypes.experience;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.neetgames.mcmmo.party.Party;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.neetgames.mcmmo.experience.ExperienceHandler;
import com.neetgames.mcmmo.experience.XPGainReason;
import com.neetgames.mcmmo.experience.XPGainSource;
import com.neetgames.mcmmo.player.MMOPlayer;
import com.neetgames.mcmmo.player.MMOPlayerData;
import com.gmail.nossr50.datatypes.skills.CoreSkills;
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
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class OnlineExperienceProcessor implements ExperienceHandler {

    private boolean isUsingUnarmed = false;

    private final @NotNull MMOPlayerData mmoPlayerData;
    private final @NotNull MMOPlayer mmoPlayer;
    private final @NotNull Player playerRef;

    public OnlineExperienceProcessor(@NotNull MMOPlayer mmoPlayer, @NotNull Player playerRef) {
        this.mmoPlayer = mmoPlayer;
        this.playerRef = playerRef;
        this.mmoPlayerData = mmoPlayer.getMMOPlayerData();
    }

    @Override
    public int getPowerLevel() {
        int powerLevel = 0;

        Map<RootSkill, Integer> rootSkillLevelMap = mmoPlayerData.getDirtySkillLevelMap().unwrapMap();

        for (RootSkill rootSkill : rootSkillLevelMap.keySet()) {
            powerLevel += rootSkillLevelMap.get(rootSkill);
        }

        return powerLevel;
    }

    @Override
    public float getSkillXpLevelRaw(@NotNull RootSkill rootSkill) {
        return mmoPlayerData.getSkillsExperienceMap().get(rootSkill);
    }

    @Override
    public int getSkillXpValue(@NotNull RootSkill rootSkill) {
        if(CoreSkills.isChildSkill(rootSkill)) {
            return 0;
        }

        return (int) Math.floor(getSkillXpLevelRaw(rootSkill));
    }

    @Override
    public void setSkillXpValue(@NotNull RootSkill rootSkill, float xpLevel) {
        if (CoreSkills.isChildSkill(rootSkill)) {
            return;
        }

        mmoPlayerData.getSkillsExperienceMap().put(rootSkill, xpLevel);
    }

    @Override
    public float levelUp(@NotNull RootSkill rootSkill) {
        float xpRemoved = getExperienceToNextLevel(rootSkill);

        setSkillLevel(rootSkill, getSkillLevel(rootSkill) + 1);
        setSkillXpValue(rootSkill, getSkillXpValue(rootSkill) - xpRemoved);

        return xpRemoved;
    }

    @Override
    public boolean hasReachedLevelCap(@NotNull RootSkill rootSkill) {
        if(hasReachedPowerLevelCap())
            return true;

        return getSkillLevel(rootSkill) >= Config.getInstance().getLevelCap(rootSkill);
    }

    @Override
    public boolean hasReachedPowerLevelCap() {
        return this.getPowerLevel() >= Config.getInstance().getPowerLevelCap();
    }

    @Override
    public void beginXpGain(@NotNull RootSkill rootSkill, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if (xp <= 0.0) {
            return;
        }

        if (CoreSkills.isChildSkill(rootSkill)) {
            Set<RootSkill> parentSkills = FamilyTree.getParentSkills(rootSkill);
            float splitXp = xp / parentSkills.size();

            for (RootSkill parentSkill : parentSkills) {
                beginXpGain(parentSkill, splitXp, xpGainReason, xpGainSource);
            }

            return;
        }

        //TODO: The logic here is so stupid... rewrite later

        // Return if the experience has been shared
        if (mmoPlayer.getParty() != null && ShareHandler.handleXpShare(xp, mmoPlayer, mmoPlayer.getParty(), rootSkill, ShareHandler.getSharedXpGainReason(xpGainReason))) {
            return;
        }

        beginUnsharedXpGain(rootSkill, xp, xpGainReason, xpGainSource);
    }

    @Override
    public void beginUnsharedXpGain(@NotNull RootSkill rootSkill, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(Misc.adaptPlayer(mmoPlayer).getGameMode() == GameMode.CREATIVE)
            return;

        ExperienceUtils.applyXpGain(mmoPlayer, rootSkill, modifyXpGain(rootSkill, xp), xpGainReason, xpGainSource);

        Party party = mmoPlayer.getParty();

        if (party != null) {
            if (!Config.getInstance().getPartyXpNearMembersNeeded() || !mcMMO.getPartyManager().getNearMembers(mmoPlayer).isEmpty()) {
                party.getPartyExperienceManager().applyXpGain(modifyXpGain(rootSkill, xp));
            }
        }
    }

    @Override
    public int getSkillLevel(@NotNull RootSkill rootSkill) {
        return CoreSkills.isChildSkill(rootSkill) ? getChildSkillLevel(rootSkill) : getSkillLevel(rootSkill);
    }

    @Override
    public int getExperienceToNextLevel(@NotNull RootSkill rootSkill) {
        if(CoreSkills.isChildSkill(rootSkill)) {
            return 0;
        }

        int level = (ExperienceConfig.getInstance().getCumulativeCurveEnabled()) ? getPowerLevel() : getSkillLevel(rootSkill);
        FormulaType formulaType = ExperienceConfig.getInstance().getFormulaType();

        return mcMMO.getFormulaManager().getXPtoNextLevel(level, formulaType);
    }

    @Override
    public int getChildSkillLevel(@NotNull RootSkill rootSkill) {
        Set<RootSkill> parents = FamilyTree.getParentSkills(rootSkill);
        int sum = 0;

        for (RootSkill parentIdentity : parents) {
            sum += getSkillLevel(parentIdentity);
        }

        return sum / parents.size();
    }

    @Override
    public void removeXp(@NotNull RootSkill skill, int xp) {
        if (skill.isChildSkill()) {
            return;
        }

        setSkillXpValue(skill, getSkillXpValue(skill) - xp);
    }

    @Override
    public void removeXp(RootSkill skill, float xp) {
        if (skill.isChildSkill()) {
            return;
        }

        setSkillXpValue(skill, getSkillXpValue(skill) - xp);
    }

    @Override
    public void setSkillLevel(@NotNull RootSkill rootSkill, int level) {
        if (rootSkill.isChildSkill()) {
            return;
        }

        //Don't allow levels to be negative
        if(level < 0)
            level = 0;

        setSkillLevel(rootSkill, level);
        setSkillXpValue(rootSkill, 0F);
    }

    @Override
    public void addLevels(@NotNull RootSkill rootSkill, int levels) {
        setSkillLevel(rootSkill, getSkillLevel(rootSkill) + levels);
    }

    @Override
    public void addXp(@NotNull RootSkill rootSkill, float xp) {
        if (rootSkill.isChildSkill()) {
            Set<RootSkill> parentSkills = FamilyTree.getParents(rootSkill);
            float dividedXP = (xp / parentSkills.size());

            for (RootSkill parentSkill : parentSkills) {
                setSkillXpValue(parentSkill, getSkillXpValue(parentSkill) + dividedXP);
            }
        }
        else {
            setSkillXpValue(rootSkill, getSkillXpValue(rootSkill) + xp);
        }
    }

    @Override
    public float getRegisteredXpGain(@NotNull RootSkill rootSkill) {
        float xp = 0F;

        if (get(rootSkill) != null) { //??
            xp = rollingSkillsXp.get(rootSkill);
        }

        return xp;
    }

    @Override
    public void registerXpGain(@NotNull RootSkill rootSkill, float xp) {
        gainedSkillsXp.add(new SkillXpGain(rootSkill, xp));
        rollingSkillsXp.put(rootSkill, getRegisteredXpGain(rootSkill) + xp);
    }

    @Override
    public void purgeExpiredXpGains() {
        SkillXpGain gain;
        while ((gain = gainedSkillsXp.poll()) != null) {
            rollingSkillsXp.put(gain.getSkill(), getRegisteredXpGain(gain.getSkill()) - gain.getXp());
        }
    }

    @Override
    private float modifyXpGain(@NotNull RootSkill rootSkill, float xp) {
        if ((rootSkill.getMaxLevel() <= getSkillLevel(rootSkill)) || (Config.getInstance().getPowerLevelCap() <= getPowerLevel())) {
            return 0;
        }

        xp = (float) (xp / rootSkill.getXpModifier() * ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier());

        return PerksUtils.handleXpPerks(Misc.adaptPlayer(mmoPlayer), xp, rootSkill);
    }

    @Override
    public double getProgressInCurrentSkillLevel(@NotNull RootSkill rootSkill) throws UnknownSkillException
    {
        if(CoreSkills.isChildSkill(rootSkill)) {
            return 1.0D;
        }

        double currentXP = getSkillXpValue(rootSkill);
        double maxXP = getExperienceToNextLevel(rootSkill);

        return (currentXP / maxXP);
    }

    @Override
    public void setUsingUnarmed(boolean bool) {
        isUsingUnarmed = bool;
    }

    @Override
    public void applyXpGain(@NotNull RootSkill rootSkill, float xp, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        //Only check for permissions if the player is online, otherwise just assume a command is being executed by an admin or some other means and add the XP
        if (!Permissions.skillEnabled(mmoPlayer.getPlayer(), CoreSkills.getSkill(rootSkill))) {
            return;
        }

        if (CoreSkills.isChildSkill(rootSkill)) {
            Set<RootSkill> parentSkills = FamilyTree.getParentSkills(rootSkill);

            for (RootSkill parentSkill : parentSkills) {
                applyXpGain(parentSkill, xp / parentSkills.size(), xpGainReason, xpGainSource);
            }

            return;
        }

        if (!EventUtils.handleXpGainEvent(Misc.adaptPlayer(mmoPlayer), rootSkill, xp, xpGainReason)) {
            return;
        }

        setUsingUnarmed(rootSkill == CoreSkills.UNARMED);
        updateLevelStats(rootSkill, xpGainReason, xpGainSource);
    }

    @Override
    public void processPostXpEvent(@NotNull RootSkill rootSkill, @NotNull XPGainSource xpGainSource)
    {
        /*
         * Everything in this method requires an online player, so if they aren't online we don't waste our time
         */
        if(mmoPlayer == null)
            return;

        //Check if they've reached the power level cap just now
        if(hasReachedPowerLevelCap()) {
            NotificationManager.sendPlayerInformationChatOnly(Misc.adaptPlayer(mmoPlayer), "LevelCap.PowerLevel", String.valueOf(Config.getInstance().getPowerLevelCap()));
        } else if(hasReachedLevelCap(rootSkill)) {
            NotificationManager.sendPlayerInformationChatOnly(Misc.adaptPlayer(mmoPlayer), "LevelCap.Skill", String.valueOf(Config.getInstance().getLevelCap(rootSkill)), rootSkill.getRawSkillName());
        }

        //Updates from Party sources
        if(xpGainSource == XPGainSource.PARTY_MEMBERS && !ExperienceConfig.getInstance().isPartyExperienceBarsEnabled())
            return;

        //Updates from passive sources (Alchemy, Smelting, etc...)
        if(xpGainSource == XPGainSource.PASSIVE && !ExperienceConfig.getInstance().isPassiveGainsExperienceBarsEnabled())
            return;

        mmoPlayer.updateXPBar(rootSkill);
    }

    @Override
    public void updateLevelStats(@NotNull RootSkill rootSkill, @NotNull XPGainReason xpGainReason, @NotNull XPGainSource xpGainSource) {
        if(hasReachedLevelCap(rootSkill))
            return;

        if (getSkillXpLevelRaw(rootSkill) < getExperienceToNextLevel(rootSkill)) {
            processPostXpEvent(rootSkill, xpGainSource);
            return;
        }

        int levelsGained = 0;
        float xpRemoved = 0;

        while (getSkillXpLevelRaw(rootSkill) >= getExperienceToNextLevel(rootSkill)) {
            if (hasReachedLevelCap(rootSkill)) {
                setSkillXpValue(rootSkill, 0);
                break;
            }

            xpRemoved += levelUp(rootSkill);
            levelsGained++;
        }

        if (EventUtils.tryLevelChangeEvent(Misc.adaptPlayer(mmoPlayer), rootSkill, levelsGained, xpRemoved, true, xpGainReason)) {
            return;
        }

        if (Config.getInstance().getLevelUpSoundsEnabled()) {
            SoundManager.sendSound(Misc.adaptPlayer(mmoPlayer), Misc.adaptPlayer(mmoPlayer).getLocation(), SoundType.LEVEL_UP);
        }

        /*
         * Check to see if the player unlocked any new skills
         */

        NotificationManager.sendPlayerLevelUpNotification(mmoPlayer, rootSkill, levelsGained, getSkillLevel(rootSkill));

        //UPDATE XP BARS
        processPostXpEvent(rootSkill, xpGainSource);
    }
}
