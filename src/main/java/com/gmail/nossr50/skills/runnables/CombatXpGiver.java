package com.gmail.nossr50.skills.runnables;

import org.bukkit.entity.LivingEntity;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.skills.utilities.SkillType;

public class CombatXpGiver implements Runnable {
    private McMMOPlayer mcMMOPlayer;
    private double baseXp;
    private SkillType skillType;
    private LivingEntity target;
    private int baseHealth;

    public CombatXpGiver(McMMOPlayer mcMMOPlayer, SkillType skillType, double baseXp, LivingEntity target) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.skillType = skillType;
        this.baseXp = baseXp;
        this.target = target;
        baseHealth = target.getHealth();
    }

    @Override
    public void run() {
        int health = target.getHealth();
        int damage = baseHealth - health;

        //May avoid negative xp, we don't know what other plugins do with the entity health
        if (damage <= 0) {
            return;
        }

        //Don't reward the player for overkills
        if (health < 0) {
            damage += health;
        }

        mcMMOPlayer.beginXpGain(skillType, (int) (damage * baseXp));
    }
}
