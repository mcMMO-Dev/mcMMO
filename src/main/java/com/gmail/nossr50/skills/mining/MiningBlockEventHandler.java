package com.gmail.nossr50.skills.mining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;

import com.gmail.nossr50.skills.utilities.SkillTools;

public class MiningBlockEventHandler {
    private MiningManager manager;
    private Block block;
    private Location blockLocation;
    private Material blockType;
    protected int skillModifier;

    protected MiningBlockEventHandler(MiningManager manager, Block block) {
        this.manager = manager;
        this.block = block;
        this.blockLocation = block.getLocation();
        this.blockType = block.getType();

        calculateSkillModifier();
    }

    private void calculateSkillModifier() {
        this.skillModifier = SkillTools.skillCheck(manager.getSkillLevel(), Mining.doubleDropsMaxLevel);
    }

    /**
     * Process Mining block drops.
     */
    protected void processDrops() {
        if (manager.getMcMMOPlayer().getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            Mining.silkTouchDrops(block, blockLocation, blockType);
        }
        else {
            Mining.miningDrops(block, blockLocation, blockType);
        }
    }

    protected void processXPGain() {
        Mining.miningXP(manager.getMcMMOPlayer(), block, blockType);
    }
}
