package com.gmail.nossr50.runnables;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.SkillTools;

public class GainXp implements Runnable {
    private Player player;
    private PlayerProfile profile;
    private double baseXp;
    private SkillType skillType;
    private LivingEntity target;
    private int baseHealth;

    public GainXp(Player player, PlayerProfile profile, SkillType skillType, double baseXp, LivingEntity target) {
        this.player = player;
        this.profile = profile;
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

        SkillTools.xpProcessing(player, profile, skillType, (int) (damage * baseXp));
    }
}
