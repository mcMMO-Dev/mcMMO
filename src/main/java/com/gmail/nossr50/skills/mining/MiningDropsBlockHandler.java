package com.gmail.nossr50.skills.mining;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;

import com.gmail.nossr50.util.Misc;

public class MiningDropsBlockHandler {
    private MiningManager manager;
    private Block block;

    protected int skillModifier;

    protected MiningDropsBlockHandler(MiningManager manager, Block block) {
        this.manager = manager;
        this.block = block;

        calculateSkillModifier();
    }

    protected void calculateSkillModifier() {
        this.skillModifier = Misc.skillCheck(manager.getSkillLevel(), Mining.DOUBLE_DROPS_MAX_BONUS_LEVEL);
    }

    /**
     * Process Mining block drops.
     *
     * @param player The player mining the block
     * @param block The block being broken
     */
    protected void processDrops() {
        if (manager.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            Mining.silkTouchDrops(block);
        }
        else {
            Mining.miningDrops(block);
        }
    }
}
