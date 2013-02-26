package com.gmail.nossr50.skills.herbalism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.AbilityType;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
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
    public static int hylianLuckMaxLevel = AdvancedConfig.getInstance().getHylianLuckMaxLevel();

    public static double shroomThumbMaxChance = AdvancedConfig.getInstance().getShroomThumbChanceMax();
    public static int shroomThumbMaxLevel = AdvancedConfig.getInstance().getShroomThumbMaxLevel();

    /**
     * Handle the farmers diet skill.
     *
     * @param player The player to activate the skill for
     * @param rankChange The # of levels to change rank for the food
     * @param event The actual FoodLevelChange event
     */
    public static int farmersDiet(Player player, int rankChange, int eventFoodLevel) {
        return SkillTools.handleFoodSkills(player, SkillType.HERBALISM, eventFoodLevel, farmersDietRankLevel1, farmersDietMaxLevel, rankChange);
    }

    /**
     * Process the Green Terra ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static boolean processGreenTerra(BlockState blockState, Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = new ItemStack(Material.SEEDS);

        if (!playerInventory.containsAtLeast(seed, 1)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.NeedMore"));
            return false;
        }

        playerInventory.removeItem(seed);
        player.updateInventory(); // Needed until replacement available

        return convertGreenTerraBlocks(blockState, player);
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    private static boolean convertGreenTerraBlocks(BlockState blockState, Player player) {
        Material blockType = blockState.getType();

        if (!Permissions.greenThumbBlock(player, blockType)) {
            return false;
        }

        switch (blockType) {
        case COBBLE_WALL:
        case SMOOTH_BRICK:
            blockState.setRawData((byte) 0x1);
            return true;

        case DIRT:
            blockState.setType(Material.GRASS);
            return true;

        case COBBLESTONE:
            blockState.setType(Material.MOSSY_COBBLESTONE);
            return true;

        default:
            return false;
        }
    }

    /**
     * Calculate the drop amounts for cacti & sugar cane based on the blocks above them.
     *
     * @param blockState The {@link BlockState} of the bottom block of the plant
     * @return the number of bonus drops to award from the blocks in this plant
     */
    private static int calculateCatciAndSugarDrops(BlockState blockState) {
        Block block = blockState.getBlock();
        Material blockType = blockState.getType();
        int dropAmount = 0;

        // Handle the original block
        if (!mcMMO.placeStore.isTrue(blockState)) {
            dropAmount++;
        }

        // Handle the two blocks above it - cacti & sugar cane can only grow 3 high naturally
        for (int y = 1;  y < 3; y++) {
            Block relativeBlock = block.getRelative(BlockFace.UP, y);
            Material relativeBlockType = relativeBlock.getType();

            // If the first one is air, so is the next one
            if (relativeBlockType == Material.AIR) {
                break;
            }

            if (relativeBlockType == blockType && !mcMMO.placeStore.isTrue(relativeBlock)) {
                dropAmount++;
            }
        }

        return dropAmount;
    }

    /**
     * Process double drops & XP gain for Herbalism.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static void herbalismBlockCheck(BlockState blockState, Player player) {
        if (Config.getInstance().getHerbalismAFKDisabled() && player.isInsideVehicle()) {
            return;
        }

        Material blockType = blockState.getType();

        HerbalismBlock herbalismBlock = HerbalismBlock.getHerbalismBlock(blockType);
        CustomBlock customBlock = null;

        int xp = 0;
        int dropAmount = 1;
        ItemStack dropItem = null;

        if (herbalismBlock != null) {
            if (blockType == Material.CACTUS || blockType == Material.SUGAR_CANE_BLOCK) {
                dropItem = herbalismBlock.getDropItem();
                dropAmount = calculateCatciAndSugarDrops(blockState);
                xp = herbalismBlock.getXpGain() * dropAmount;
            }
            else if (herbalismBlock.hasGreenThumbPermission(player)){
                dropItem = herbalismBlock.getDropItem();
                xp = herbalismBlock.getXpGain();
                processGreenThumbPlants(blockState, player);
            }
            else {
                if (!mcMMO.placeStore.isTrue(blockState)) {
                    dropItem = herbalismBlock.getDropItem();
                    xp = herbalismBlock.getXpGain();
                }
            }
        }
        else {
            customBlock = ModChecks.getCustomBlock(blockState);
            dropItem = customBlock.getItemDrop();
            xp = customBlock.getXpGain();
        }

        if (Permissions.doubleDrops(player, SkillType.HERBALISM) && SkillTools.activationSuccessful(player, SkillType.HERBALISM, doubleDropsMaxChance, doubleDropsMaxLevel)) {
            Location location = blockState.getLocation();

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

        Users.getPlayer(player).beginXpGain(SkillType.HERBALISM, xp);
    }

    /**
     * Process the Green Thumb ability for blocks.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static boolean processGreenThumbBlocks(BlockState blockState, Player player) {
        if (!SkillTools.activationSuccessful(player, SkillType.HERBALISM, greenThumbMaxChance, greenThumbMaxLevel)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Fail"));
            return false;
        }

        return convertGreenTerraBlocks(blockState, player);
    }

    /**
     * Process the Green Thumb ability for plants.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     */
    private static void processGreenThumbPlants(BlockState blockState, Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = HerbalismBlock.getHerbalismBlock(blockState.getType()).getDropItem();

        if (!playerInventory.containsAtLeast(seed, 1)) {
            return;
        }

        PlayerProfile playerProfile = Users.getPlayer(player).getProfile();

        if (playerProfile.getAbilityMode(AbilityType.GREEN_TERRA)) {
            playerInventory.removeItem(seed);
            player.updateInventory(); // Needed until replacement available

            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GreenTerraTimer(blockState), 0);
            return;
        }
        else if (SkillTools.activationSuccessful(player, SkillType.HERBALISM, greenThumbMaxChance, greenThumbMaxLevel)) {
            playerInventory.removeItem(seed);
            player.updateInventory(); // Needed until replacement available

            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GreenThumbTimer(blockState, playerProfile.getSkillLevel(SkillType.HERBALISM)), 0);
            return;
        }
    }

    /**
     * Process the Hylian Luck ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static boolean processHylianLuck(BlockState blockState, Player player) {
        if (!SkillTools.activationSuccessful(player, SkillType.HERBALISM, hylianLuckMaxChance, hylianLuckMaxLevel)) {
            return false;
        }

        List<HylianTreasure> treasures = new ArrayList<HylianTreasure>();

        switch (blockState.getType()) {
        case DEAD_BUSH:
        case LONG_GRASS:
        case SAPLING:
            treasures = TreasuresConfig.getInstance().hylianFromBushes;
            break;

        case RED_ROSE:
        case YELLOW_FLOWER:
            if (mcMMO.placeStore.isTrue(blockState)) {
                mcMMO.placeStore.setFalse(blockState);
                return false;
            }

            treasures = TreasuresConfig.getInstance().hylianFromFlowers;
            break;

        case FLOWER_POT:
            treasures = TreasuresConfig.getInstance().hylianFromPots;
            break;

        default:
            return false;
        }

        if (treasures.isEmpty()) {
            return false;
        }

        blockState.setRawData((byte) 0x0);
        blockState.setType(Material.AIR);

        Misc.dropItem(blockState.getLocation(), treasures.get(Misc.getRandom().nextInt(treasures.size())).getDrop());
        player.sendMessage(LocaleLoader.getString("Herbalism.HylianLuck"));
        return true;
    }

    /**
     * Process the Shroom Thumb ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    public static boolean processShroomThumb(BlockState blockState, Player player) {
        PlayerInventory playerInventory = player.getInventory();

        if (!playerInventory.contains(Material.BROWN_MUSHROOM)) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(Material.BROWN_MUSHROOM)));
            return false;
        }

        if (!playerInventory.contains(Material.RED_MUSHROOM)) {
            player.sendMessage(LocaleLoader.getString("Skills.NeedMore", StringUtils.getPrettyItemString(Material.RED_MUSHROOM)));
            return false;
        }

        playerInventory.removeItem(new ItemStack(Material.BROWN_MUSHROOM));
        playerInventory.removeItem(new ItemStack(Material.RED_MUSHROOM));
        player.updateInventory();

        if (!SkillTools.activationSuccessful(player, SkillType.HERBALISM, shroomThumbMaxChance, shroomThumbMaxLevel)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.ShroomThumb.Fail"));
            return false;
        }

        return convertShroomThumb(blockState, player);
    }

    /**
     * Convert blocks affected by the Green Thumb & Green Terra abilities.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     * @return true if the ability was successful, false otherwise
     */
    private static boolean convertShroomThumb(BlockState blockState, Player player) {
        if (!Permissions.shroomThumb(player)) {
            return false;
        }

        switch (blockState.getType()){
        case DIRT:
        case GRASS:
            blockState.setType(Material.MYCEL);
            return true;

        default:
            return false;
        }
    }
}
