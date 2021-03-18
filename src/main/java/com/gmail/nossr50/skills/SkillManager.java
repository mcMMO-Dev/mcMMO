package com.gmail.nossr50.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class SkillManager {
    protected OnlineMMOPlayer mmoPlayer;
    protected PrimarySkillType skill;

    public SkillManager(OnlineMMOPlayer mmoPlayer, PrimarySkillType skill) {
        this.mmoPlayer = mmoPlayer;
        this.skill = skill;
    }

    public Player getPlayer() {
        return Misc.adaptPlayer(mmoPlayer);
    }

    public int getSkillLevel() {
        return mmoPlayer.getExperienceHandler().getSkillLevel(skill);
    }

    /**
     * Applies XP to a player, provides SELF as an XpGainSource source
     * @param xp amount of XP to apply
     * @param xpGainReason the reason for the XP gain
     * @deprecated use applyXpGain(float, XPGainReason, XPGainSource)
     */
    @Deprecated
    public void applyXpGain(float xp, XPGainReason xpGainReason) {
        mmoPlayer.beginXpGain(skill, xp, xpGainReason, XPGainSource.SELF);
    }

    /**
     * Applies XP to a player
     * @param xp amount of XP to apply
     * @param xpGainReason the reason for the XP gain
     * @param xpGainSource the source of the XP
     */
    public void applyXpGain(float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        mmoPlayer.beginXpGain(skill, xp, xpGainReason, xpGainSource);
    }

    public XPGainReason getXPGainReason(LivingEntity target, Entity damager) {
        return (damager instanceof Player && target instanceof Player) ? XPGainReason.PVP : XPGainReason.PVE;
    }
}
