package com.gmail.nossr50.skills.mining;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
    private List<BlockState> ores = new ArrayList<BlockState>();
    private List<BlockState> debris = new ArrayList<BlockState>();
    private List<BlockState> droppedOres = new ArrayList<BlockState>();
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
            BlockState blockState = block.getState();

            if (BlockChecks.isOre(blockState)) {
                ores.add(blockState);
            }
            else {
                debris.add(blockState);
            }
        }
    }

    protected void processXPGain() {
        for (BlockState blockState : droppedOres) {
            if (!mcMMO.placeStore.isTrue(blockState)) {
                Mining.awardMiningXp(blockState, manager.getMcMMOPlayer().getPlayer());
            }
        }
    }

    protected void processDroppedBlocks() {
        for (BlockState blockState : ores) {
            if (Misc.getRandom().nextFloat() < (yield + oreBonus)) {
                droppedOres.add(blockState);
                Mining.handleMiningDrops(blockState);

                if (!mcMMO.placeStore.isTrue(blockState)) {
                    for (int i = 1 ; i < dropMultiplier ; i++) {
                        droppedOres.add(blockState);
                        Mining.handleMiningDrops(blockState);
                    }
                }
            }
        }

        float debrisYield  = yield - debrisReduction;

        if (debrisYield > 0) {
            for (BlockState blockState : debris) {
                Location location = blockState.getLocation();
                Material type = blockState.getType();

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
        if (skillLevel >= BlastMining.rank8) {
            oreBonus = .70f;
        }
        else if (skillLevel >= BlastMining.rank7) {
            oreBonus = .65f;
        }
        else if (skillLevel >= BlastMining.rank6) {
            oreBonus = .60f;
        }
        else if (skillLevel >= BlastMining.rank5) {
            oreBonus = .55f;
        }
        else if (skillLevel >= BlastMining.rank4) {
            oreBonus = .50f;
        }
        else if (skillLevel >= BlastMining.rank3) {
            oreBonus = .45f;
        }
        else if (skillLevel >= BlastMining.rank2) {
            oreBonus = .40f;
        }
        else {
            debrisReduction = .35f;
        }
    }

    private void calculateDebrisReduction() {
        if (skillLevel >= BlastMining.rank3) {
            debrisReduction = .30f;
        }
        else if (skillLevel >= BlastMining.rank2) {
            debrisReduction = .20f;
        }
        else {
            debrisReduction = .10f;
        }
    }

    private void calculateDropMultiplier() {
        if (skillLevel >= BlastMining.rank7) {
            dropMultiplier = 3;
        }
        else if (skillLevel >= BlastMining.rank5) {
            dropMultiplier = 2;
        }
        else {
            dropMultiplier = 1;
        }
    }
}
