package com.gmail.nossr50.skills.gathering;

import java.util.Random;

import org.bukkit.CoalType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.sound.SoundEffect;
import org.bukkit.enchantments.Enchantment;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;

public class Mining {

    private static Random random = new Random();

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
                Misc.mcDropItem(loc, item);
            }
            break;

        case DIAMOND_ORE:
            if (configInstance.getDiamondDoubleDropsEnabled()) {
                item = new ItemStack(Material.DIAMOND);
                Misc.mcDropItem(loc, item);
            }
            break;

        case ENDER_STONE:
            if (configInstance.getEndStoneDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case GLOWING_REDSTONE_ORE:
        case REDSTONE_ORE:
            if (configInstance.getRedstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.REDSTONE);
                Misc.mcDropItems(loc, item, 4);
                Misc.mcRandomDropItem(loc, item, 50);
            }
            break;

        case GLOWSTONE:
            if (configInstance.getGlowstoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.GLOWSTONE_DUST);
                Misc.mcDropItems(loc, item, 2);
                Misc.mcRandomDropItems(loc, item, 50, 2);
            }
            break;

        case GOLD_ORE:
            if (configInstance.getGoldDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case IRON_ORE:
            if (configInstance.getIronDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case LAPIS_ORE:
            if (configInstance.getLapisDoubleDropsEnabled()) {
                item = new ItemStack(Material.INK_SACK, 1, (short) 0, (byte) 0x4);
                Misc.mcDropItems(loc, item, 4);
                Misc.mcRandomDropItems(loc, item, 50, 4);
            }
            break;

        case MOSSY_COBBLESTONE:
            if (configInstance.getMossyCobblestoneDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case NETHERRACK:
            if (configInstance.getNetherrackDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case OBSIDIAN:
            if (configInstance.getObsidianDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case SANDSTONE:
            if (configInstance.getSandstoneDoubleDropsEnabled()) {
                Misc.mcDropItem(loc, item);
            }
            break;

        case STONE:
            if (configInstance.getStoneDoubleDropsEnabled()) {
                item = new ItemStack(Material.COBBLESTONE);
                Misc.mcDropItem(loc, item);
            }
            break;

        default:
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
        if (mcMMO.placeStore.isTrue(block) || player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        miningXP(player, block);

        if (BlockChecks.canBeSuperBroken(block.getType())) {
            final int MAX_BONUS_LEVEL = 1000;

            int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.MINING);

            if ((skillLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= skillLevel) && Permissions.getInstance().miningDoubleDrops(player)) {
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
    public static void SuperBreakerBlockCheck(Player player, Block block) {
        Material type = block.getType();
        int tier = Misc.getTier(player.getItemInHand());
        int durabilityLoss = Config.getInstance().getAbilityToolDamage();
        FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);

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

            if (Config.getInstance().spoutEnabled) {
                SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
            }
        }
    }
}
