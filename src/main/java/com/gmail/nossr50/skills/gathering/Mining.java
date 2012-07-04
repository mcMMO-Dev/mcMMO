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
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class Mining {
    private static Random random = new Random();

    /**
     * Handle double drops when using Silk Touch.
     *
     * @param block The block to process drops for
     */
    private static void silkTouchDrops(Block block) {
        Location location = block.getLocation();
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
                Misc.dropItem(location, item);
            }
            break;

        case DIAMOND_ORE:
            if (configInstance.getDiamondDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (configInstance.getRedstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWSTONE:
            if (configInstance.getGlowstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case LAPIS_ORE:
            if (configInstance.getLapisDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case STONE:
            if (configInstance.getStoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                Misc.dropItem(location, new ItemStack(block.getTypeId(), 1, (short) 0, block.getData()));
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
        Location location = block.getLocation();
        Material type = block.getType();
        ItemStack item = new ItemStack(type);
        Config configInstance = Config.getInstance();

        switch (type) {
        case COAL_ORE:
            if (configInstance.getCoalDoubleDropsEnabled()) {
                item = new ItemStack(Material.COAL, 1, (short) 0, CoalType.COAL.getData());
                Misc.dropItem(location, item);
            }
            break;

        case DIAMOND_ORE:
            if (configInstance.getDiamondDoubleDropsEnabled()) {
                item = new ItemStack(Material.DIAMOND);
                Misc.dropItem(location, item);
            }
            break;

        case ENDER_STONE:
            if (configInstance.getEndStoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (configInstance.getRedstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.REDSTONE);
                Misc.dropItems(location, item, 4);
                Misc.randomDropItem(location, item, 50);
            }
            break;

        case GLOWSTONE:
            if (configInstance.getGlowstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                Misc.dropItems(location, item, 2);
                Misc.randomDropItems(location, item, 50, 2);
            }
            break;

        case GOLD_ORE:
            if (configInstance.getGoldDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case IRON_ORE:
            if (configInstance.getIronDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case LAPIS_ORE:
            if (configInstance.getLapisDoubleDropsEnabled()) {
                item = new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x4);
                Misc.dropItems(location, item, 4);
                Misc.randomDropItems(location, item, 50, 4);
            }
            break;

        case MOSSY_COBBLESTONE:
            if (configInstance.getMossyCobblestoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case NETHERRACK:
            if (configInstance.getNetherrackDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case OBSIDIAN:
            if (configInstance.getObsidianDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case SANDSTONE:
            if (configInstance.getSandstoneDoubleDropsEnabled()) {
                Misc.dropItem(location, item);
            }
            break;

        case STONE:
            if (configInstance.getStoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.COBBLESTONE);
                Misc.dropItem(location, item);
            }
            break;

        default:
            if (ModChecks.isCustomMiningBlock(block)) {
                CustomBlock customBlock = ModChecks.getCustomBlock(block);
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();

                item = ModChecks.getCustomBlock(block).getItemDrop();

                if (minimumDropAmount != maximumDropAmount) {
                    Misc.dropItems(location, item, minimumDropAmount);
                    Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
                }
                else {
                    Misc.dropItems(location, item, minimumDropAmount);
                }
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
        PlayerProfile profile = Users.getProfile(player);
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

        Skills.xpProcessing(player, profile, SkillType.MINING, xp);
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

        int randomChance = 1000;

        if (player.hasPermission("mcmmo.perks.lucky.mining")) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (random.nextInt(randomChance) <= skillCheck && Permissions.getInstance().miningDoubleDrops(player)) {
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

            if (mcMMO.spoutEnabled) {
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

                if (mcMMO.spoutEnabled) {
                    SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
                }

            default:
                return;
            }
        }
    }
}
