package com.gmail.nossr50.skills.mining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Misc;

public class MiningBlockEventHandler {
    private MiningManager manager;
    private Player player;

    private Block block;
    private Location blockLocation;
    private Material blockType;

    protected int skillModifier;

    protected MiningBlockEventHandler(MiningManager manager, Block block) {
        this.manager = manager;
        this.player = manager.getPlayer();

        this.block = block;
        this.blockLocation = block.getLocation();
        this.blockType = block.getType();

        calculateSkillModifier();
    }

    private void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Mining.DOUBLE_DROPS_MAX_BONUS_LEVEL);
    }

    /**
     * Process Mining block drops.
     *
     * @param player The player mining the block
     * @param block The block being broken
     */
    protected void processDrops() {
        if (player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            Mining.silkTouchDrops(block, blockLocation, blockType);
        }
        else {
            Mining.miningDrops(block, blockLocation, blockType);
        }
    }

    protected void processXPGain() {
        Mining.miningXP(player, manager.getProfile(), block, blockType);
    }
}
