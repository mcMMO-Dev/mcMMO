package com.gmail.nossr50.skills;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.runnables.GreenThumbTimer;

public class Herbalism {

    private static Random random = new Random();

    /**
     * Activate the Green Terra ability.
     *
     * @param player The player activating the ability
     * @param block The block to be changed by Green Terra
     */
    public static void greenTerra(Player player, Block block) {
        PlayerInventory inventory = player.getInventory();
        boolean hasSeeds = inventory.contains(Material.SEEDS);
        Material type = block.getType();

        if (!hasSeeds) {
            player.sendMessage("You need more seeds to spread Green Terra");  //TODO: Needs more locale.
        }
        else if (hasSeeds && !block.getType().equals(Material.WHEAT)) {
            inventory.removeItem(new ItemStack(Material.SEEDS));
            player.updateInventory();

            if (m.blockBreakSimulate(block, player, false)) {
                if (LoadProperties.enableSmoothToMossy && type.equals(Material.SMOOTH_BRICK)) {
                    block.setData((byte) 0x1); //Set type of the brick to mossy
                }
                else if (LoadProperties.enableDirtToGrass && type.equals(Material.DIRT)) {
                    block.setType(Material.GRASS);
                }
                else if (LoadProperties.enableCobbleToMossy && type.equals(Material.COBBLESTONE)) {
                    block.setType(Material.MOSSY_COBBLESTONE);
                }
            }
        }
    }

    /**
     * Check if a block can be made mossy.
     *
     * @param material The type of Block to check
     * @return true if the block can be made mossy, false otherwise
     */
    public static Boolean makeMossy(Material type) {
        switch (type) {
        case COBBLESTONE:
        case DIRT:
        case SMOOTH_BRICK:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check if a block is affected by Herbalism abilities.
     *
     * @param type The type of Block to check
     * @return true if the block is affected, false otherwise
     */
    public static Boolean canBeGreenTerra(Material type){
        switch (type) {
        case BROWN_MUSHROOM:
        case CACTUS:
        case CROPS:
        case JACK_O_LANTERN:
        case MELON_BLOCK:
        case PUMPKIN:
        case RED_MUSHROOM:
        case RED_ROSE:
        case SUGAR_CANE_BLOCK:
        case VINE:
        case WATER_LILY:
        case YELLOW_FLOWER:
            return true;

        default:
            return false;
        }
    }

    /**
     * Check for extra Herbalism drops.
     *
     * @param block The block to check for extra drops
     * @param player The player getting extra drops
     * @param event The event to use for Green Thumb
     * @param plugin mcMMO plugin instance
     */
    public static void herbalismProcCheck(final Block block, Player player, BlockBreakEvent event, mcMMO plugin) {
        final PlayerProfile PP = Users.getProfile(player);
        final int MAX_BONUS_LEVEL = 1000;

        int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
        int id = block.getTypeId();
        Material type = block.getType();

        Byte data = block.getData();
        Location loc = block.getLocation();
        Material mat = null;
        int xp = 0;
        int catciDrops = 0;
        int caneDrops = 0;

        switch (type) {
        case BROWN_MUSHROOM:
        case RED_MUSHROOM:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = Material.getMaterial(id);
                xp = LoadProperties.mmushroom;
            }
            break;

        case CACTUS:
            for (int y = 0;  y <= 2; y++) {
                Block b = block.getRelative(0, y, 0);
                if (b.getType().equals(Material.CACTUS)) {
                    mat = Material.CACTUS;
                    if (!b.hasMetadata("mcmmoPlacedBlock")) {
                        if (herbLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= herbLevel) {
                            catciDrops++;
                        }
                        xp += LoadProperties.mcactus;
                    }
                }
            }
            break;

        case CROPS:
            if (data == CropState.RIPE.getData()) {
                mat = Material.WHEAT;
                xp = LoadProperties.mwheat;

                if (LoadProperties.wheatRegrowth && mcPermissions.getInstance().greenThumbWheat(player)) {
                    greenThumbWheat(block, player, event, plugin);
                }
            }
            break;

        case MELON_BLOCK:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = Material.MELON;
                xp = LoadProperties.mmelon;
            }
            break;

        case NETHER_WARTS:
            if (data == (byte) 0x3) {
                mat = Material.NETHER_STALK;
                xp = LoadProperties.mnetherwart;
            }
            break;

        case PUMPKIN:
        case JACK_O_LANTERN:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = Material.getMaterial(id);
                xp = LoadProperties.mpumpkin;
            }
            break;

        case RED_ROSE:
        case YELLOW_FLOWER:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = Material.getMaterial(id);
                xp = LoadProperties.mflower;
            }
            break;

        case SUGAR_CANE_BLOCK:
            for (int y = 0;  y <= 2; y++) {
                Block b = block.getRelative(0, y, 0);
                if (b.getType().equals(Material.SUGAR_CANE_BLOCK)) {
                    mat = Material.SUGAR_CANE;
                    if (!b.hasMetadata("mcmmoPlacedBlock")) {
                        if (herbLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= herbLevel) {
                            caneDrops++;
                        }
                        xp += LoadProperties.msugar;
                    }
                }
            }
            break;

        case VINE:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = type;
                xp = LoadProperties.mvines;
            }
            break;

        case WATER_LILY:
            if (!block.hasMetadata("mcmmoPlacedBlock")) {
                mat = type;
                xp = LoadProperties.mlilypad;
            }
            break;

        default:
            break;
        }

        if (mat == null) {
            return;
        }
        else {
            ItemStack is = new ItemStack(mat);

            if (herbLevel > MAX_BONUS_LEVEL || random.nextInt(1000) <= herbLevel) {
                if (type.equals(Material.CACTUS)) {
                    m.mcDropItems(loc, is, catciDrops);
                }
                else if (type.equals(Material.MELON_BLOCK)) {
                    m.mcDropItems(loc, is, 3);
                    m.mcRandomDropItems(loc, is, 50, 4);
                }
                else if (type.equals(Material.NETHER_WARTS)) {
                    m.mcDropItems(loc, is, 2);
                    m.mcRandomDropItems(loc, is, 50, 3);
                }
                else if (type.equals(Material.SUGAR_CANE_BLOCK)) {
                    m.mcDropItems(loc, is, caneDrops);
                }
                else {
                    m.mcDropItem(loc, is);
                }
            }

            PP.addXP(SkillType.HERBALISM, xp);
            Skills.XpCheckSkill(SkillType.HERBALISM, player);
        }
    }

    /**
     * Apply the Green Thumb ability to crops.
     *
     * @param block The block to apply the ability to
     * @param player The player using the ability
     * @param event The event triggering the ability
     * @param plugin mcMMO plugin instance
     */
    private static void greenThumbWheat(Block block, Player player, BlockBreakEvent event, mcMMO plugin) {
        final int MAX_BONUS_LEVEL = 1500;

        PlayerProfile PP = Users.getProfile(player);
        int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
        PlayerInventory inventory = player.getInventory();
        boolean hasSeeds = inventory.contains(Material.SEEDS);
        Location loc = block.getLocation();

        if (hasSeeds && PP.getAbilityMode(AbilityType.GREEN_TERRA) || hasSeeds && (herbLevel > MAX_BONUS_LEVEL || random.nextInt(1500) <= herbLevel)) {
            event.setCancelled(true);

            m.mcDropItem(loc, new ItemStack(Material.WHEAT));
            m.mcRandomDropItems(loc, new ItemStack(Material.SEEDS), 50, 3);

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new GreenThumbTimer(block, PP), 1);

            inventory.removeItem(new ItemStack(Material.SEEDS));
            player.updateInventory();
        }
    }

    /**
     * Apply the Green Thumb ability to blocks.
     *
     * @param is The item in the player's hand
     * @param player The player activating the ability
     * @param block The block being used in the ability
     */
    public static void greenThumbBlocks(ItemStack is, Player player, Block block) {
        final int MAX_BONUS_LEVEL = 1500;

        PlayerProfile PP = Users.getProfile(player);
        int skillLevel = PP.getSkillLevel(SkillType.HERBALISM);
        int seeds = is.getAmount();

        player.setItemInHand(new ItemStack(Material.SEEDS, seeds - 1));

        if (skillLevel > MAX_BONUS_LEVEL || random.nextInt(1500) <= skillLevel) {
            greenTerra(player, block);
        }
        else {
            player.sendMessage(mcLocale.getString("mcPlayerListener.GreenThumbFail"));
        }
    }
}
