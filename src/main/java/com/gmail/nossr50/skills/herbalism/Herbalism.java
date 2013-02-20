package com.gmail.nossr50.skills.herbalism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.AbilityType;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Herbalism {
    public static int farmersDietRankLevel1 = AdvancedConfig.getInstance().getFarmerDietRankChange();
    public static int farmersDietRankLevel2 = farmersDietRankLevel1 * 2;
    public static int farmersDietMaxLevel = farmersDietRankLevel1 * 5;

    public static int greenThumbStageChangeLevel = AdvancedConfig.getInstance().getGreenThumbStageChange();
    public static int greenThumbStageMaxLevel = greenThumbStageChangeLevel * 4;

    public static double greenThumbMaxChance = AdvancedConfig.getInstance().getGreenThumbChanceMax();
    public static int greenThumbMaxLevel = AdvancedConfig.getInstance().getGreenThumbMaxLevel();

    public static double doubleDropsMaxChance = AdvancedConfig.getInstance().getHerbalismDoubleDropsChanceMax();
    public static int doubleDropsMaxLevel = AdvancedConfig.getInstance().getHerbalismDoubleDropsMaxLevel();

    public static double hylianLuckMaxChance = AdvancedConfig.getInstance().getHylianLuckChanceMax();
    public static int hylianLuckMaxLevel = AdvancedConfig.getInstance().getHylianLucksMaxLevel();

    /**
     * Handle the farmers diet skill.
     *
     * @param player The player to activate the skill for
     * @param rankChange The # of levels to change rank for the food
     * @param event The actual FoodLevelChange event
     */
    public static void farmersDiet(Player player, int rankChange, FoodLevelChangeEvent event) {
        if (!Permissions.farmersDiet(player)) {
            return;
        }

        SkillTools.handleFoodSkills(player, SkillType.HERBALISM, event, farmersDietRankLevel1, farmersDietMaxLevel, rankChange);
    }

    /**
     * Activate the Green Terra ability.
     *
     * @param player The player activating the ability
     * @param block The block to be changed by Green Terra
     */
    public static void greenTerra(Player player, Block block) {
        PlayerInventory inventory = player.getInventory();
        boolean hasSeeds = inventory.contains(Material.SEEDS);

        if (!hasSeeds) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.NeedMore"));
            return;
        }

        inventory.removeItem(new ItemStack(Material.SEEDS));
        player.updateInventory();   // Needed until replacement available
        greenTerraConvert(player, block);
    }

    public static void greenTerraConvert(Player player, Block block) {
        if (SkillTools.blockBreakSimulate(block, player, false)) {
            Material type = block.getType();

            if (!Permissions.greenThumbBlock(player, type)) {
                return;
            }

            switch (type) {
            case SMOOTH_BRICK:
                block.setData((byte) 0x1);
                return;

            case DIRT:
                block.setType(Material.GRASS);
                return;

            case COBBLESTONE:
                block.setType(Material.MOSSY_COBBLESTONE);
                return;

            case COBBLE_WALL:
                block.setData((byte) 0x1);
                return;

            default:
                return;
            }
        }
    }

    private static int calculateCatciAndSugarDrops(Block block) {
        Material blockType = block.getType();
        int dropAmount = 0;

        for (int y = 0;  y <= 2; y++) {
            Block relativeBlock = block.getRelative(BlockFace.UP, y);
            if (relativeBlock.getType() == blockType && !mcMMO.placeStore.isTrue(relativeBlock)) {
                dropAmount++;
            }
        }

        return dropAmount;
    }

    /**
     * Check for extra Herbalism drops.
     *
     * @param block The block to check for extra drops
     * @param mcMMOPlayer The player getting extra drops
     * @param event The event to use for Green Thumb
     * @param plugin mcMMO plugin instance
     */
    public static void herbalismProcCheck(final Block block, McMMOPlayer mcMMOPlayer, mcMMO plugin) {
        Player player = mcMMOPlayer.getPlayer();

        if (Config.getInstance().getHerbalismAFKDisabled() && player.isInsideVehicle()) {
            return;
        }

        PlayerProfile profile = mcMMOPlayer.getProfile();
        int herbLevel = profile.getSkillLevel(SkillType.HERBALISM);
        Material blockType = block.getType();

        HerbalismBlock herbalismBlock = HerbalismBlock.getHerbalismBlock(blockType);
        CustomBlock customBlock = null;

        int xp = 0;
        int dropAmount = 1;
        ItemStack dropItem = null;

        if (herbalismBlock != null) {
            if (blockType == Material.CACTUS || blockType == Material.SUGAR_CANE_BLOCK) {
                dropItem = herbalismBlock.getDropItem();
                dropAmount = calculateCatciAndSugarDrops(block);
                xp = herbalismBlock.getXpGain() * dropAmount;
            }
            else if (herbalismBlock.hasGreenThumbPermission(player)){
                dropItem = herbalismBlock.getDropItem();
                xp = herbalismBlock.getXpGain();

                greenThumbWheat(block, player, plugin);
            }
            else {
                if (!mcMMO.placeStore.isTrue(block)) {
                    dropItem = herbalismBlock.getDropItem();
                    xp = herbalismBlock.getXpGain();
                }
            }
        }
        else {
            customBlock = ModChecks.getCustomBlock(block);
            dropItem = customBlock.getItemDrop();
            xp = customBlock.getXpGain();
        }

        if (Permissions.doubleDrops(player, SkillType.HERBALISM)) {
            int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.HERBALISM);
            double chance = (doubleDropsMaxChance / doubleDropsMaxLevel) * SkillTools.skillCheck(herbLevel, doubleDropsMaxLevel);

            if (chance > Misc.getRandom().nextInt(activationChance)) {
                Location location = block.getLocation();

                if (dropItem != null && herbalismBlock != null && herbalismBlock.canDoubleDrop()) {
                    Misc.dropItems(location, dropItem, dropAmount);
                }
                else if (customBlock != null){
                    int minimumDropAmount = customBlock.getMinimumDropAmount();
                    int maximumDropAmount = customBlock.getMaximumDropAmount();

                    if (minimumDropAmount != maximumDropAmount) {
                        Misc.randomDropItems(location, dropItem, maximumDropAmount - minimumDropAmount);
                    }

                    Misc.dropItems(location, dropItem, minimumDropAmount);
                }
            }
        }

        mcMMOPlayer.beginXpGain(SkillType.HERBALISM, xp);
    }

    /**
     * Apply the Green Thumb ability to crops.
     *
     * @param block The block to apply the ability to
     * @param player The player using the ability
     * @param event The event triggering the ability
     * @param plugin mcMMO plugin instance
     */
    private static void greenThumbWheat(Block block, Player player, mcMMO plugin) {
        PlayerProfile profile = Users.getPlayer(player).getProfile();
        int herbLevel = profile.getSkillLevel(SkillType.HERBALISM);
        PlayerInventory inventory = player.getInventory();
        boolean hasSeeds = false;
        Material type = block.getType();

        switch(type) {
        case CROPS:
            hasSeeds = inventory.contains(Material.SEEDS);
            break;
        case COCOA:
            hasSeeds = inventory.containsAtLeast(new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()), 1);
            break;
        case CARROT:
            hasSeeds = inventory.contains(Material.CARROT_ITEM);
            break;
        case POTATO:
            hasSeeds = inventory.contains(Material.POTATO_ITEM);
            break;
        case NETHER_WARTS:
            hasSeeds = inventory.contains(Material.NETHER_STALK);
            break;
        default:
            break;
        }

        if (!hasSeeds) {
            return;
        }

        int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.HERBALISM);
        float chance = (float) (greenThumbMaxChance / greenThumbMaxLevel * herbLevel);

        if (chance > greenThumbMaxChance) {
            chance = (float) greenThumbMaxChance;
        }

        if (profile.getAbilityMode(AbilityType.GREEN_TERRA) || chance > Misc.getRandom().nextInt(activationChance)) {
            switch(type) {
            case CROPS:
                inventory.removeItem(new ItemStack(Material.SEEDS));
                break;
            case COCOA:
                inventory.removeItem(new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()));
                break;
            case CARROT:
                inventory.removeItem(new ItemStack(Material.CARROT_ITEM));
                break;
            case POTATO:
                inventory.removeItem(new ItemStack(Material.POTATO_ITEM));
                break;
            case NETHER_WARTS:
                inventory.removeItem(new ItemStack(Material.NETHER_STALK));
                break;
            default:
                break;
            }

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new GreenThumbTimer(block, profile, type), 0);
            player.updateInventory();   // Needed until replacement available
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
        PlayerProfile profile = Users.getPlayer(player).getProfile();
        int skillLevel = profile.getSkillLevel(SkillType.HERBALISM);
        int seeds = is.getAmount();

        player.setItemInHand(new ItemStack(Material.SEEDS, seeds - 1));

        int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.HERBALISM);

        float chance = (float) ((greenThumbMaxChance / greenThumbMaxLevel) * skillLevel);
        if (chance > greenThumbMaxChance) chance = (float) greenThumbMaxChance;

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            greenTerraConvert(player, block);
        }
        else {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Fail"));
        }
    }

    public static void hylianLuck(Block block, Player player, BlockBreakEvent event) {
        int skillLevel = Users.getPlayer(player).getProfile().getSkillLevel(SkillType.HERBALISM);

        double chance = (hylianLuckMaxChance / hylianLuckMaxLevel) * SkillTools.skillCheck(skillLevel, hylianLuckMaxLevel);
        int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.HERBALISM);

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            List<HylianTreasure> treasures = new ArrayList<HylianTreasure>();

            switch (block.getType()) {
            case DEAD_BUSH:
            case LONG_GRASS:
            case SAPLING:
                treasures = TreasuresConfig.getInstance().hylianFromBushes;
                break;

            case RED_ROSE:
            case YELLOW_FLOWER:
                if (mcMMO.placeStore.isTrue(block)) {
                    mcMMO.placeStore.setFalse(block);
                    return;
                }

                treasures = TreasuresConfig.getInstance().hylianFromFlowers;
                break;

            case FLOWER_POT:
                treasures = TreasuresConfig.getInstance().hylianFromPots;
                break;

            default:
                return;
            }

            if (treasures.isEmpty()) {
                return;
            }

            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            Misc.dropItem(block.getLocation(), treasures.get(Misc.getRandom().nextInt(treasures.size())).getDrop());
            player.sendMessage(LocaleLoader.getString("Herbalism.HylianLuck"));
        }
    }
}
