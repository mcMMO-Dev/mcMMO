package com.gmail.nossr50.runnables;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.skills.Skills;

public class GainXp implements Runnable {
    private Player player = null;
    private PlayerProfile PP = null;
    private double baseXp = 0;
    private SkillType skillType = null;
    private LivingEntity target = null;
    private int baseHealth = 0;

    public GainXp(Player player, PlayerProfile PP, SkillType skillType, double baseXp, LivingEntity target) {
        this.player = player;
        this.PP = PP;
        this.skillType = skillType;
        this.baseXp = baseXp;
        this.target = target;
        baseHealth = target.getHealth();
    }

    @Override
    public void run() {
        int health = target.getHealth();
        int damage =  baseHealth - health;

        //May avoid negative xp, we don't know what other plugins do with the entity health
        if (damage <= 0) {
            return;
        }

        //Don't reward the player for overkills
        if (health < 0) {
            damage += health;
        }

        PP.addXP(skillType, (int) (damage * baseXp));
        Skills.XpCheckSkill(skillType, player);
    }
}
