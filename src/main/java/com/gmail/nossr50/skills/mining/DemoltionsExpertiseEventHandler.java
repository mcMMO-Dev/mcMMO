package com.gmail.nossr50.skills.mining;

import org.bukkit.event.entity.EntityDamageEvent;

public class DemoltionsExpertiseEventHandler {
    private int skillLevel;
    private EntityDamageEvent event;
    private int damage;
    private double damageModifier;

    public DemoltionsExpertiseEventHandler(MiningManager manager, EntityDamageEvent event) {
        this.skillLevel = manager.getSkillLevel();

        this.event = event;
        this.damage = event.getDamage();
    }

    protected void calculateDamageModifier() {
        if (skillLevel >= BlastMining.rank8) {
            damageModifier = 0.0;
        }
        else if (skillLevel >= BlastMining.rank6) {
            damageModifier = 0.25;
        }
        else if (skillLevel >= BlastMining.rank4) {
            damageModifier = 0.5;
        }
        else {
            damageModifier = 1.0;
        }
    }

    protected void modifyEventDamage() {
        damage = (int) (damage * damageModifier);
        event.setDamage(damage);
    }
}
