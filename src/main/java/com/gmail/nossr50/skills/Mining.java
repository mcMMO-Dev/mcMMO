package com.gmail.nossr50.skills;

import org.bukkit.Bukkit;
import org.bukkit.CoalType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerAnimationEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;


public class Mining
{

    /**
     * Drop items from Mining & Blast Mining skills.
     *
     * @param block The block to process drops for
     */
    public static void miningDrops(Block block) {
        Location loc = block.getLocation();
        Material type = block.getType();
        ItemStack item = new ItemStack(type);

        switch (type) {
        case COAL_ORE:
            item = new ItemStack(Material.COAL, 1, (short) 0, CoalType.COAL.getData());
            m.mcDropItem(loc, item);
            break;

        case DIAMOND_ORE:
            item = new ItemStack(Material.DIAMOND);
            m.mcDropItem(loc, item);
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            item = new ItemStack(Material.REDSTONE);
            m.mcDropItems(loc, item, 4);
            m.mcRandomDropItem(loc, item, 50);
            break;

        case GLOWSTONE:
            item = new ItemStack(Material.GLOWSTONE_DUST);
            m.mcDropItems(loc, item, 2);
            m.mcRandomDropItems(loc, item, 50, 2);
            break;

        case LAPIS_ORE:
            item = new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x4);
            m.mcDropItems(loc, item, 4);
            m.mcRandomDropItems(loc, item, 50, 4);
            break;

        case STONE:
            item = new ItemStack(Material.COBBLESTONE);
            m.mcDropItem(loc, item);
            break;

        default:
            m.mcDropItem(loc, item);
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
            xp += LoadProperties.mcoal;
            break;

        case DIAMOND_ORE:
            xp += LoadProperties.mdiamond;
            break;

        case ENDER_STONE:
            xp += LoadProperties.mendstone;
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            xp += LoadProperties.mredstone;
            break;

        case GLOWSTONE:
            xp += LoadProperties.mglowstone;
            break;

        case GOLD_ORE:
            xp += LoadProperties.mgold;
            break;

        case IRON_ORE:
            xp += LoadProperties.miron;
            break;

        case LAPIS_ORE:
            xp += LoadProperties.mlapis;
            break;

        case MOSSY_COBBLESTONE:
            xp += LoadProperties.mmossstone;
            break;

        case NETHERRACK:
            xp += LoadProperties.mnetherrack;
            break;

        case OBSIDIAN:
            xp += LoadProperties.mobsidian;
            break;

        case SANDSTONE:
            xp += LoadProperties.msandstone;
            break;

        case STONE:
            xp += LoadProperties.mstone;
            break;

        default:
            break;
        }

        PP.addXP(SkillType.MINING, xp, player);
        Skills.XpCheckSkill(SkillType.MINING, player);
    }

    /**
     * Process Mining block drops.
     *
     * @param player The player mining the block
     * @param block The block being broken
     */
    public static void miningBlockCheck(Player player, Block block) {
        if (block.hasMetadata("mcmmoPlacedBlock")) {
            return;
        }

        miningXP(player, block);

        if (canBeSuperBroken(block.getType())) {
            final int MAX_BONUS_LEVEL = 1000;

            int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);

            if ((skillLevel > MAX_BONUS_LEVEL || (Math.random() * 1000 <= skillLevel)) && mcPermissions.getInstance().miningDoubleDrops(player)) {
                if (player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                    m.mcDropItem(block.getLocation(), new ItemStack(block.getType()));
                }
                else {
                    miningDrops(block);
                }
            }
        }
    }

    /**
     * Check to see if a block is broken by Super Breaker.
     *
     * @param type The type of Block to check
     * @return true if the block would be broken by Super Breaker, false otherwise
     */
    public static Boolean canBeSuperBroken(Material type) {
        switch (type) {
        case COAL_ORE:
        case DIAMOND_ORE:
        case ENDER_STONE:
        case GLOWING_REDSTONE_ORE:
        case GLOWSTONE:
        case GOLD_ORE:
        case IRON_ORE:
        case LAPIS_ORE:
        case MOSSY_COBBLESTONE:
        case NETHERRACK:
        case OBSIDIAN:
        case REDSTONE_ORE:
        case SANDSTONE:
        case STONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Handle the Super Breaker ability.
     *
     * @param player The player using the ability
     * @param block The block being affected
     */
    public static void SuperBreakerBlockCheck(Player player, Block block) {
        Material type = block.getType();
        int tier = m.getTier(player.getItemInHand());
        int durabilityLoss = LoadProperties.abilityDurabilityLoss;
        PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);

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
            if (block.hasMetadata("mcmmoPlacedBlock")) {
                return;
            }

            Bukkit.getPluginManager().callEvent(armswing);
            Skills.abilityDurabilityLoss(player.getItemInHand(), durabilityLoss);

            miningBlockCheck(player, block);

            if (LoadProperties.spoutEnabled) {
                SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
            }
        }
    }
}
