package com.gmail.nossr50.skills.mining;

import org.bukkit.event.entity.ExplosionPrimeEvent;

public class BiggerBombsEventHandler {
    private int skillLevel;

    private ExplosionPrimeEvent event;
    private float radius;
    private float radiusModifier; 

    protected BiggerBombsEventHandler(MiningManager manager, ExplosionPrimeEvent event) {
        this.skillLevel = manager.getSkillLevel();

        this.event = event;
        this.radius = event.getRadius();
    }

    protected void calculateRadiusIncrease() {
        if (skillLevel < BlastMining.BLAST_MINING_RANK_2) {
            return;
        }

        if (skillLevel >= BlastMining.BLAST_MINING_RANK_8) {
            radiusModifier = 4.0f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_6) {
            radiusModifier = 3.0f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_4) {
            radiusModifier = 2.0f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_2) {
            radiusModifier = 1.0f;
        }
    }

    protected void modifyBlastRadius() {
        radius = radius + radiusModifier;
        event.setRadius(radius);
    }
}
