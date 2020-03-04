package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import org.bukkit.entity.LivingEntity;

import java.util.function.Consumer;

//TODO: Why is this a task? Investigate later.
public class AwardCombatXpTask implements Consumer<Task> {
    private BukkitMMOPlayer mcMMOPlayer;
    private double baseXp;
    private PrimarySkillType primarySkillType;
    private LivingEntity target;
    private XPGainReason xpGainReason;
    private double baseHealth;

    public AwardCombatXpTask(BukkitMMOPlayer mcMMOPlayer, PrimarySkillType primarySkillType, double baseXp, LivingEntity target, XPGainReason xpGainReason) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.primarySkillType = primarySkillType;
        this.baseXp = baseXp;
        this.target = target;
        this.xpGainReason = xpGainReason;
        baseHealth = target.getHealth();
    }

    @Override
    public void accept(Task task) {
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

        mcMMOPlayer.beginXpGain(primarySkillType, (int) (damage * baseXp), xpGainReason, XPGainSource.SELF);
    }
}
