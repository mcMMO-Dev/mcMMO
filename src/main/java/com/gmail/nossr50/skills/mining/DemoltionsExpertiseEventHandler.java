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
        if (skillLevel < BlastMining.BLAST_MINING_RANK_4) {
            return;
        }

        if (skillLevel >= BlastMining.BLAST_MINING_RANK_8) {
            damageModifier = 0;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_6) {
            damageModifier = 0.5;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_4) {
            damageModifier = 0.25;
        }
    }

    protected void modifyEventDamage() {
        damage = (int) (damage * damageModifier);
        event.setDamage(damage);
    }
}
