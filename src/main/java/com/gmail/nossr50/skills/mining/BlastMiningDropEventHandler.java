package com.gmail.nossr50.skills.mining;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Misc;

public class BlastMiningDropEventHandler {
    private MiningManager manager;
    private int skillLevel;

    private EntityExplodeEvent event;
    private float yield;
    private List<Block> blocks;

    private List<Block> ores = new ArrayList<Block>();
    private List<Block> debris = new ArrayList<Block>();
    private List<Block> droppedOres = new ArrayList<Block>();

    private float oreBonus;
    private float debrisReduction;
    private int dropMultiplier;

    public BlastMiningDropEventHandler(MiningManager manager, EntityExplodeEvent event) {
        this.manager = manager;
        this.skillLevel = manager.getSkillLevel();

        this.event = event;
        this.yield = event.getYield();
        this.blocks = event.blockList();

    }

    protected void sortExplosionBlocks() {
        for (Block block : blocks) {
            if (BlockChecks.isOre(block)) {
                ores.add(block);
            }
            else {
                debris.add(block);
            }
        }
    }

    protected void processXPGain() {
        for (Block block : droppedOres) {
            if (!mcMMO.placeStore.isTrue(block)) {
                Mining.miningXP(manager.getPlayer(), manager.getProfile(), block, block.getType());
            }
        }
    }

    protected void processDroppedBlocks() {
        for (Block block : ores) {
            Location location = block.getLocation();
            Material type = block.getType();

            if (Misc.getRandom().nextFloat() < (yield + oreBonus)) {
                droppedOres.add(block);
                Mining.miningDrops(block, location, type);

                if (!mcMMO.placeStore.isTrue(block)) {
                    for (int i = 1 ; i < dropMultiplier ; i++) {
                        droppedOres.add(block);
                        Mining.miningDrops(block, location, type);
                    }
                }
            }
        }

        float debrisYield  = yield - debrisReduction;

        if (debrisYield > 0) {
            for (Block block : debris) {
                Location location = block.getLocation();
                Material type = block.getType();

                if (Misc.getRandom().nextFloat() < debrisYield) {
                    Misc.dropItem(location, new ItemStack(type));
                }
            }
        }
    }

    protected void modifyEventYield() {
        event.setYield(0);
    }

    protected void calcuateDropModifiers() {
        calculateOreBonus();
        calculateDebrisReduction();
        calculateDropMultiplier();
    }

    private void calculateOreBonus() {
        if (skillLevel >= BlastMining.BLAST_MINING_RANK_8) {
            oreBonus = .70f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_7) {
            oreBonus = .65f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_6) {
            oreBonus = .60f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_5) {
            oreBonus = .55f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_4) {
            oreBonus = .50f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_3) {
            oreBonus = .45f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_2) {
            oreBonus = .40f;
        }
        else {
            debrisReduction = .35f;
        }
    }

    private void calculateDebrisReduction() {
        if (skillLevel >= BlastMining.BLAST_MINING_RANK_3) {
            debrisReduction = .30f;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_2) {
            debrisReduction = .20f;
        }
        else {
            debrisReduction = .10f;
        }
    }

    private void calculateDropMultiplier() {
        if (skillLevel >= BlastMining.BLAST_MINING_RANK_7) {
            dropMultiplier = 3;
        }
        else if (skillLevel >= BlastMining.BLAST_MINING_RANK_5) {
            dropMultiplier = 2;
        }
        else {
            dropMultiplier = 1;
        }
    }
}
