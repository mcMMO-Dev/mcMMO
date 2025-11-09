package com.gmail.nossr50.skills;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class SkillManager {
    protected McMMOPlayer mmoPlayer;
    protected PrimarySkillType skill;

    public SkillManager(McMMOPlayer mmoPlayer, PrimarySkillType skill) {
        this.mmoPlayer = mmoPlayer;
        this.skill = skill;
    }

    public Player getPlayer() {
        return mmoPlayer.getPlayer();
    }

    public int getSkillLevel() {
        return mmoPlayer.getSkillLevel(skill);
    }

    /**
     * Applies XP to a player, provides SELF as an XpGainSource source
     *
     * @param xp amount of XP to apply
     * @param xpGainReason the reason for the XP gain
     * @deprecated use applyXpGain(float, XPGainReason, XPGainSource)
     */
    @Deprecated(forRemoval = true)
    public void applyXpGain(float xp, XPGainReason xpGainReason) {
        mmoPlayer.beginXpGain(skill, xp, xpGainReason, XPGainSource.SELF);
    }

    /**
     * Applies XP to a player
     *
     * @param xp amount of XP to apply
     * @param xpGainReason the reason for the XP gain
     * @param xpGainSource the source of the XP
     */
    public void applyXpGain(float xp, XPGainReason xpGainReason, XPGainSource xpGainSource) {
        mmoPlayer.beginXpGain(skill, xp, xpGainReason, xpGainSource);
    }

    public XPGainReason getXPGainReason(LivingEntity target, Entity damager) {
        return (damager instanceof Player && target instanceof Player) ? XPGainReason.PVP
                : XPGainReason.PVE;
    }
}
