package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class AwardCombatXpTask extends BukkitRunnable {
    private final McMMOPlayer mcMMOPlayer;
    private final double baseXp;
    private final PrimarySkillType primarySkillType;
    private final LivingEntity target;
    private final XPGainReason xpGainReason;
    private final double baseHealth;

    public AwardCombatXpTask(McMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType, double baseXp, LivingEntity target, XPGainReason xpGainReason) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.primarySkillType = primarySkillType;
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

        if(ExperienceConfig.getInstance().useCombatHPCeiling()) {
            damage = Math.min(damage, ExperienceConfig.getInstance().getCombatHPCeiling());
        }

        mcMMOPlayer.beginXpGain(primarySkillType, (int) (damage * baseXp), xpGainReason, XPGainSource.SELF);
    }
}
