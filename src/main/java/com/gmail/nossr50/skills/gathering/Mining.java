package com.gmail.nossr50.skills.gathering;

import java.util.Random;

import org.bukkit.CoalType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.SpoutConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;

public class Mining {
    private static Random random = new Random();

    /**
     * Handle double drops when using Silk Touch.
     *
     * @param block The block to process drops for
     */
    private static void silkTouchDrops(Block block) {
        Location loc = block.getLocation();
        Material type = block.getType();
        ItemStack item = new ItemStack(type);
        Config configInstance = Config.getInstance();

        switch (type) {
        case ENDER_STONE:
        case GOLD_ORE:
        case IRON_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case SANDSTONE:
            miningDrops(block);
            break;

        case COAL_ORE:
            if (configInstance.getCoalDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case DIAMOND_ORE:
            if (configInstance.getDiamondDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (configInstance.getRedstoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case GLOWSTONE:
            if (configInstance.getGlowstoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case LAPIS_ORE:
            if (configInstance.getLapisDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case STONE:
            if (configInstance.getStoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                Misc.dropItem(loc, item);
            }
            break;
        }

    }

    /**
     * Drop items from Mining & Blast Mining skills.
     *
     * @param block The block to process drops for
     */
    public static void miningDrops(Block block) {
        Location loc = block.getLocation();
        Material type = block.getType();
        ItemStack item = new ItemStack(type);
        Config configInstance = Config.getInstance();

        switch (type) {
        case COAL_ORE:
            if (configInstance.getCoalDoubleDropsEnabled()) {
                item = new ItemStack(Material.COAL, 1, (short) 0, CoalType.COAL.getData());
                Misc.dropItem(loc, item);
            }
            break;

        case DIAMOND_ORE:
            if (configInstance.getDiamondDoubleDropsEnabled()) {
                item = new ItemStack(Material.DIAMOND);
                Misc.dropItem(loc, item);
            }
            break;

        case ENDER_STONE:
            if (configInstance.getEndStoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (configInstance.getRedstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.REDSTONE);
                Misc.dropItems(loc, item, 4);
                Misc.randomDropItem(loc, item, 50);
            }
            break;

        case GLOWSTONE:
            if (configInstance.getGlowstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                Misc.dropItems(loc, item, 2);
                Misc.randomDropItems(loc, item, 50, 2);
            }
            break;

        case GOLD_ORE:
            if (configInstance.getGoldDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case IRON_ORE:
            if (configInstance.getIronDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case LAPIS_ORE:
            if (configInstance.getLapisDoubleDropsEnabled()) {
                item = new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x4);
                Misc.dropItems(loc, item, 4);
                Misc.randomDropItems(loc, item, 50, 4);
            }
            break;

        case MOSSY_COBBLESTONE:
            if (configInstance.getMossyCobblestoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case NETHERRACK:
            if (configInstance.getNetherrackDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case OBSIDIAN:
            if (configInstance.getObsidianDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case SANDSTONE:
            if (configInstance.getSandstoneDoubleDropsEnabled()) {
                Misc.dropItem(loc, item);
            }
            break;

        case STONE:
            if (configInstance.getStoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.COBBLESTONE);
                Misc.dropItem(loc, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                item = ModChecks.getCustomBlock(block).getItemDrop();
                Misc.dropItem(loc, item);
            }
            break;
        }
    }

    /**
     * Award XP for Mining blocks.
     *
     * @param player The player to award XP to
     * @param block The block to award XP for
     */
    public static void miningXP(Player player, Block block) {
        PlayerProfile PP = Users.getProfile(player);
        Material type = block.getType();
        int xp = 0;
        
        switch (type) {
        case COAL_ORE:
            xp += Config.getInstance().getMiningXPCoalOre();
            break;

        case DIAMOND_ORE:
            xp += Config.getInstance().getMiningXPDiamondOre();
            break;

        case ENDER_STONE:
            xp += Config.getInstance().getMiningXPEndStone();
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            xp += Config.getInstance().getMiningXPRedstoneOre();
            break;

        case GLOWSTONE:
            xp += Config.getInstance().getMiningXPGlowstone();
            break;

        case GOLD_ORE:
            xp += Config.getInstance().getMiningXPGoldOre();
            break;

        case IRON_ORE:
            xp += Config.getInstance().getMiningXPIronOre();
            break;

        case LAPIS_ORE:
            xp += Config.getInstance().getMiningXPLapisOre();
            break;

        case MOSSY_COBBLESTONE:
            xp += Config.getInstance().getMiningXPMossyStone();
            break;

        case NETHERRACK:
            xp += Config.getInstance().getMiningXPNetherrack();
            break;

        case OBSIDIAN:
            xp += Config.getInstance().getMiningXPObsidian();
            break;

        case SANDSTONE:
            xp += Config.getInstance().getMiningXPSandstone();
            break;

        case STONE:
            xp += Config.getInstance().getMiningXPStone();
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                xp += ModChecks.getCustomBlock(block).getXpGain();
            }
            break;
        }

        PP.addXP(player, SkillType.MINING, xp);
        Skills.XpCheckSkill(SkillType.MINING, player);
    }

    /**
     * Process Mining block drops.
     *
     * @param player The player mining the block
     * @param block The block being broken
     */
    public static void miningBlockCheck(Player player, Block block) {
        if (mcMMO.placeStore.isTrue(block)) {
            return;
        }

        miningXP(player, block);

        final int MAX_BONUS_LEVEL = 1000;
        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(1000) <= skillCheck && Permissions.getInstance().miningDoubleDrops(player)) {
            if (player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                silkTouchDrops(block);
            }
            else {
                miningDrops(block);
            }
        }
    }

    /**
     * Handle the Super Breaker ability.
     *
     * @param player The player using the ability
     * @param block The block being affected
     */
    public static void superBreakerBlockCheck(Player player, Block block) {
        Material type = block.getType();
        int tier = Misc.getTier(player.getItemInHand());
        int durabilityLoss = Config.getInstance().getAbilityToolDamage();
        FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);

        if (ModChecks.isCustomMiningBlock(block)) {
            if (ModChecks.getCustomBlock(block).getTier() < tier) {
                return;
            }

            if (mcMMO.placeStore.isTrue(block)) {
                return;
            }

            mcMMO.p.getServer().getPluginManager().callEvent(armswing);
            Skills.abilityDurabilityLoss(player.getItemInHand(), durabilityLoss);

            miningBlockCheck(player, block);

            if (SpoutConfig.getInstance().spoutEnabled) {
                SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
            }
        }
        else {
            switch (type) {
            case OBSIDIAN:
                if (tier < 4) {
                    return;
                }
                durabilityLoss = durabilityLoss * 5; //Obsidian needs to do more damage than normal
                /* FALL THROUGH */

            case DIAMOND_ORE:
            case GLOWING_REDSTONE_ORE:
            case GOLD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
                if (tier < 3) {
                    return;
                }
                /* FALL THROUGH */

            case IRON_ORE:
                if (tier < 2) {
                    return;
                }
                /* FALL THROUGH */

            case COAL_ORE:
            case ENDER_STONE:
            case GLOWSTONE:
            case MOSSY_COBBLESTONE:
            case NETHERRACK:
            case SANDSTONE:
            case STONE:
                if (mcMMO.placeStore.isTrue(block)) {
                    return;
                }

                mcMMO.p.getServer().getPluginManager().callEvent(armswing);
                Skills.abilityDurabilityLoss(player.getItemInHand(), durabilityLoss);

                miningBlockCheck(player, block);

                if (SpoutConfig.getInstance().spoutEnabled) {
                    SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
                }
            }
        }
    }
}
