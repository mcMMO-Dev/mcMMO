package com.gmail.nossr50.skills.herbalism;

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
import org.bukkit.material.MaterialData;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.GreenThumbTimer;
import com.gmail.nossr50.skills.AbilityType;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.SkillTools;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
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
    public static boolean doubleDropsDisabled = Config.getInstance().herbalismDoubleDropsDisabled();

    public static double hylianLuckMaxChance = AdvancedConfig.getInstance().getHylianLuckChanceMax();
    public static int hylianLuckMaxLevel = AdvancedConfig.getInstance().getHylianLucksMaxLevel();

    public static boolean greenTerraWalls = Config.getInstance().getHerbalismGreenThumbCobbleWallToMossyWall();
    public static boolean greenTerraSmoothBrick = Config.getInstance().getHerbalismGreenThumbSmoothbrickToMossy();
    public static boolean greenTerraDirt = Config.getInstance().getHerbalismGreenThumbDirtToGrass();
    public static boolean greenTerraCobble = Config.getInstance().getHerbalismGreenThumbCobbleToMossy();

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

        if (!block.getType().equals(Material.WHEAT)) {
            inventory.removeItem(new ItemStack(Material.SEEDS));
            player.updateInventory();   // Needed until replacement available
            greenTerraConvert(player, block);
        }
    }

    public static void greenTerraConvert(Player player, Block block) {
        if (Misc.blockBreakSimulate(block, player, false)) {
            Material type = block.getType();

            switch (type) {
            case SMOOTH_BRICK:
                if (greenTerraSmoothBrick && block.getData() == 0x0) {
                    block.setData((byte) 0x1);
                }
                return;

            case DIRT:
                if (greenTerraDirt) {
                    block.setType(Material.GRASS);
                }
                return;

            case COBBLESTONE:
                if (greenTerraCobble) {
                    block.setType(Material.MOSSY_COBBLESTONE);
                }
                return;

            case COBBLE_WALL:
                if (greenTerraWalls && block.getData() == 0x0) {
                    block.setData((byte) 0x1);
                }
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
            if (relativeBlock.getType().equals(blockType) && !mcMMO.placeStore.isTrue(relativeBlock)) {
                dropAmount++;
            }
        }

        return dropAmount;
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
        if (Config.getInstance().getHerbalismAFKDisabled() && player.isInsideVehicle()) {
            return;
        }

        PlayerProfile profile = Users.getProfile(player);
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

                greenThumbWheat(block, player, event, plugin);
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

        if (Permissions.herbalismDoubleDrops(player)) {
            int activationChance = Misc.calculateActivationChance(Permissions.luckyHerbalism(player));
            double chance = (doubleDropsMaxChance / doubleDropsMaxLevel) * Misc.skillCheck(herbLevel, doubleDropsMaxLevel);

            if (chance > Misc.getRandom().nextInt(activationChance)) {
                Location location = block.getLocation();

                if (herbalismBlock != null && herbalismBlock.canDoubleDrop()) {
                    Misc.dropItems(location, dropItem, dropAmount);
                }
                else if (customBlock != null){
                    int minimumDropAmount = customBlock.getMinimumDropAmount();
                    int maximumDropAmount = customBlock.getMaximumDropAmount();

                    if (minimumDropAmount != maximumDropAmount) {
                        Misc.randomDropItems(location, dropItem, 50, maximumDropAmount - minimumDropAmount);
                    }

                    Misc.dropItems(location, dropItem, minimumDropAmount);
                }
            }
        }

        SkillTools.xpProcessing(player, profile, SkillType.HERBALISM, xp);
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
        PlayerProfile profile = Users.getProfile(player);
        int herbLevel = profile.getSkillLevel(SkillType.HERBALISM);
        PlayerInventory inventory = player.getInventory();
        boolean hasSeeds = false;
        Location location = block.getLocation();
        Material type = block.getType();

        switch(type) {
        case CROPS:
            hasSeeds = inventory.contains(Material.SEEDS);
            break;
        case COCOA:
            try {
                hasSeeds = inventory.containsAtLeast(new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()), 1);
            }
            catch(Exception e) {
                hasSeeds = inventory.containsAtLeast(new ItemStack(Material.INK_SACK, 1, (short) 3), 1);
            }
            catch(NoSuchMethodError e) {
                hasSeeds = inventory.containsAtLeast(new ItemStack(Material.INK_SACK, 1, (short) 3), 1);
            }
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

        int activationChance = Misc.calculateActivationChance(Permissions.luckyHerbalism(player));

        float chance = (float) ((greenThumbMaxChance / greenThumbMaxLevel) * herbLevel);
        if (chance > greenThumbMaxChance) chance = (float) greenThumbMaxChance;

        if (hasSeeds && profile.getAbilityMode(AbilityType.GREEN_TERRA) || hasSeeds && (chance > Misc.getRandom().nextInt(activationChance))) {
            event.setCancelled(true);

            switch(type) {
            case CROPS:
                Misc.dropItem(location, new ItemStack(Material.WHEAT));
                Misc.randomDropItems(location, new ItemStack(Material.SEEDS), 50, 3);
                inventory.removeItem(new ItemStack(Material.SEEDS));
                break;
            case COCOA:
                try {
                    Misc.dropItems(location, new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()), 3);
                    inventory.removeItem(new ItemStack(Material.INK_SACK, 1, DyeColor.BROWN.getDyeData()));
                }
                catch(Exception e) {
                    Misc.dropItems(location, new ItemStack(Material.INK_SACK, 1, (short) 3), 3);
                    inventory.removeItem(new ItemStack(Material.INK_SACK, 1, (short) 3));
                }
                catch(NoSuchMethodError e) {
                    Misc.dropItems(location, new ItemStack(Material.INK_SACK, 1, (short) 3), 3);
                    inventory.removeItem(new ItemStack(Material.INK_SACK, 1, (short) 3));
                }
                break;
            case CARROT:
                Misc.dropItem(location, new ItemStack(Material.CARROT_ITEM));
                Misc.randomDropItems(location, new ItemStack(Material.CARROT_ITEM), 50, 3);
                inventory.removeItem(new ItemStack(Material.CARROT_ITEM));
                break;
            case POTATO:
                Misc.dropItem(location, new ItemStack(Material.POTATO_ITEM));
                Misc.randomDropItems(location, new ItemStack(Material.POTATO_ITEM), 50, 3);
                Misc.randomDropItem(location, new ItemStack(Material.POISONOUS_POTATO), 2);
                inventory.removeItem(new ItemStack(Material.POTATO_ITEM));
                break;
            case NETHER_WARTS:
                Misc.dropItems(location, new ItemStack(Material.NETHER_STALK), 2);
                Misc.randomDropItems(location, new ItemStack(Material.NETHER_STALK), 50, 2);
                inventory.removeItem(new ItemStack(Material.NETHER_STALK));
                break;
            default:
                break;
            }

            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new GreenThumbTimer(block, profile, type), 1);
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
        PlayerProfile profile = Users.getProfile(player);
        int skillLevel = profile.getSkillLevel(SkillType.HERBALISM);
        int seeds = is.getAmount();

        player.setItemInHand(new ItemStack(Material.SEEDS, seeds - 1));

        int activationChance = Misc.calculateActivationChance(Permissions.luckyHerbalism(player));

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
        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.HERBALISM);

        float chance = (float) ((hylianLuckMaxChance / hylianLuckMaxLevel) * skillLevel);
        if (chance > hylianLuckMaxChance) chance = (float) hylianLuckMaxChance;

        int activationChance = Misc.calculateActivationChance(Permissions.luckyHerbalism(player));

        if (chance > Misc.getRandom().nextInt(activationChance)) {
            Location location = block.getLocation();
            int dropNumber = Misc.getRandom().nextInt(3);
            ItemStack item;

            switch (block.getType()) {
            case DEAD_BUSH:
            case LONG_GRASS:
            case SAPLING:
                if (dropNumber == 0) {
                    item = new ItemStack(Material.MELON_SEEDS);
                }
                else if (dropNumber == 1) {
                    item = new ItemStack(Material.PUMPKIN_SEEDS);
                }
                else {
                    try {
                        item = (new MaterialData(Material.INK_SACK, DyeColor.BROWN.getDyeData())).toItemStack(1);
                    }
                    catch (Exception e) {
                        item = (new MaterialData(Material.INK_SACK, (byte) 0x3)).toItemStack(1);
                    }
                    catch (NoSuchMethodError e) {
                        item = (new MaterialData(Material.INK_SACK, (byte) 0x3)).toItemStack(1);
                    }
                }
                break;

            case RED_ROSE:
            case YELLOW_FLOWER:
                if (mcMMO.placeStore.isTrue(block)) {
                    mcMMO.placeStore.setFalse(block);
                    return;
                }

                if (dropNumber == 0) {
                    item = new ItemStack(Material.POTATO);
                }
                else if (dropNumber == 1) {
                    item = new ItemStack(Material.CARROT);
                }
                else {
                    item = new ItemStack(Material.APPLE);
                }

                break;

            case FLOWER_POT:
                if (dropNumber == 0) {
                    item = new ItemStack(Material.EMERALD);
                }
                else if (dropNumber == 1) {
                    item = new ItemStack(Material.DIAMOND);
                }
                else {
                    item = new ItemStack(Material.GOLD_NUGGET);
                }
                break;

            default:
                return;
            }

            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            Misc.dropItem(location, item);
            player.sendMessage(LocaleLoader.getString("Herbalism.HylianLuck"));
        }
    }
}
