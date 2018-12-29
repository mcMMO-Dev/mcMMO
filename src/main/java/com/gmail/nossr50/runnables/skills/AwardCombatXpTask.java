package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.XPGainReason;

public class AwardCombatXpTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private double baseXp;
    private PrimarySkill primarySkill;
    private LivingEntity target;
    private XPGainReason xpGainReason;
    private double baseHealth;

    public AwardCombatXpTask(McMMOPlayer mcMMOPlayer, PrimarySkill primarySkill, double baseXp, LivingEntity target, XPGainReason xpGainReason) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.primarySkill = primarySkill;
        this.baseXp = baseXp;
        this.target = target;
        this.xpGainReason = xpGainReason;
        baseHealth = target.getHealth();
    }

    @Override
    public void run() {
        double health = target.getHealth();
        double damage = baseHealth - health;

        // May avoid negative xp, we don't know what other plugins do with the entity health
        if (damage <= 0) {
            return;
        }

        // Don't reward the player for overkills
        if (health < 0) {
            damage += health;
        }

        mcMMOPlayer.beginXpGain(primarySkill, (int) (damage * baseXp), xpGainReason);
    }
}
