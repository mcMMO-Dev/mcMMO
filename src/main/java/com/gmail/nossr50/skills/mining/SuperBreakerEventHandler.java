package com.gmail.nossr50.skills.mining;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Skills;

public class SuperBreakerEventHandler {
    private MiningManager manager;
    private Player player;

    private Block block;
    private Material blockType;
    private boolean customBlock;

    private ItemStack heldItem;
    private int tier;

    private int durabilityLoss;
    private FakePlayerAnimationEvent armswing;

    protected SuperBreakerEventHandler (MiningManager manager, Block block) {
        this.manager = manager;
        this.player = manager.getPlayer();

        this.block = block;
        this.blockType = block.getType();
        this.customBlock = ModChecks.isCustomMiningBlock(block);

        this.heldItem = player.getItemInHand();
        this.tier = Misc.getTier(heldItem);

        this.armswing = new FakePlayerAnimationEvent(player);

        calculateDurabilityLoss();
    }

    protected void callFakeArmswing() {
        mcMMO.p.getServer().getPluginManager().callEvent(armswing);
    }

    protected void processDurabilityLoss() {
        Skills.abilityDurabilityLoss(heldItem, durabilityLoss);
    }

    protected void processDropsAndXP() {
        manager.miningBlockCheck(block);
        manager.miningBlockCheck(block); //Triple drops
    }

    protected void playSpoutSound() {
        if (mcMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }

    /**
     * Check for the proper tier of item for use with Super Breaker.
     *
     * @return True if the item is the required tier or higher, false otherwise
     */
    protected boolean tierCheck() {
        if (customBlock) {
            if (ModChecks.getCustomBlock(block).getTier() < tier) {
                return false;
            }

            return true;
        }

        switch (blockType) {
        case OBSIDIAN:
            if (tier < Mining.DIAMOND_TOOL_TIER) {
                return false;
            }
            /* FALL THROUGH */

        case DIAMOND_ORE:
        case GLOWING_REDSTONE_ORE:
        case GOLD_ORE:
        case LAPIS_ORE:
        case REDSTONE_ORE:
        case EMERALD_ORE:
            if (tier < Mining.IRON_TOOL_TIER) {
                return false;
            }
            /* FALL THROUGH */

        case IRON_ORE:
            if (tier < Mining.STONE_TOOL_TIER) {
                return false;
            }
            /* FALL THROUGH */

        case COAL_ORE:
        case ENDER_STONE:
        case GLOWSTONE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case SANDSTONE:
        case STONE:
            return true;

        default:
            return false;
        }
    }

    private void calculateDurabilityLoss() {
        this.durabilityLoss = Misc.TOOL_DURABILITY_LOSS;

        if (blockType.equals(Material.OBSIDIAN)) {
            durabilityLoss = durabilityLoss * 5;
        }
    }
}
