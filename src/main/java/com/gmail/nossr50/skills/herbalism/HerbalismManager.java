package com.gmail.nossr50.skills.herbalism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.HerbalismBlockUpdaterTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class HerbalismManager extends SkillManager {
    public HerbalismManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.HERBALISM);
    }

    public boolean canBlockCheck() {
        return !(Config.getInstance().getHerbalismAFKDisabled() && getPlayer().isInsideVehicle());
    }

    public boolean canGreenThumbBlock(BlockState blockState) {
        Player player = getPlayer();

        return player.getItemInHand().getType() == Material.SEEDS && BlockUtils.canMakeMossy(blockState) && Permissions.greenThumbBlock(player, blockState.getType());
    }

    public boolean canUseShroomThumb(BlockState blockState) {
        Player player = getPlayer();
        Material itemType = player.getItemInHand().getType();

        return (itemType == Material.RED_MUSHROOM || itemType == Material.BROWN_MUSHROOM) && BlockUtils.canMakeShroomy(blockState) && Permissions.shroomThumb(player);
    }

    public boolean canUseHylianLuck() {
        return Permissions.hylianLuck(getPlayer());
    }

    public boolean canGreenTerraBlock(BlockState blockState) {
        return mcMMOPlayer.getAbilityMode(AbilityType.GREEN_TERRA) && BlockUtils.canMakeMossy(blockState);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.HOE) && Permissions.greenTerra(getPlayer());
    }

    public boolean canGreenTerraPlant() {
        return mcMMOPlayer.getAbilityMode(AbilityType.GREEN_TERRA);
    }

    /**
     * Handle the Farmer's Diet ability
     *
     * @param rankChange The # of levels to change rank for the food
     * @param eventFoodLevel The initial change in hunger from the event
     * @return the modified change in hunger for the event
     */
    public int farmersDiet(int rankChange, int eventFoodLevel) {
        return SkillUtils.handleFoodSkills(getPlayer(), skill, eventFoodLevel, Herbalism.farmersDietRankLevel1, Herbalism.farmersDietMaxLevel, rankChange);
    }

    /**
     * Process the Green Terra ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processGreenTerra(BlockState blockState) {
        Player player = getPlayer();

        if (!Permissions.greenThumbBlock(player, blockState.getType())) {
            return false;
        }

        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = new ItemStack(Material.SEEDS);

        if (!playerInventory.containsAtLeast(seed, 1)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.GTe.NeedMore"));
            return false;
        }

        playerInventory.removeItem(seed);
        player.updateInventory(); // Needed until replacement available

        return Herbalism.convertGreenTerraBlocks(blockState);
    }

    /**
     * 
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void herbalismBlockCheck(BlockState blockState) {
        Material material = blockState.getType();
        boolean oneBlockPlant = (material == Material.CACTUS || material == Material.SUGAR_CANE_BLOCK) ? false : true;

        if (oneBlockPlant && mcMMO.placeStore.isTrue(blockState)) {
            return;
        }

        HerbalismBlock herbalismBlock = HerbalismBlock.getHerbalismBlock(material);
        ItemStack drop = null;
        int amount = 1;
        int xp = 0;
        boolean greenTerra = mcMMOPlayer.getAbilityMode(skill.getAbility());

        if (herbalismBlock != null) {
            if (herbalismBlock.hasGreenThumbPermission(getPlayer())) {
                processGreenThumbPlants(blockState, greenTerra);
            }

            xp = herbalismBlock.getXpGain();

            if (herbalismBlock.canDoubleDrop() && Permissions.doubleDrops(getPlayer(), skill)) {
                drop = herbalismBlock.getDropItem();
            }

            if (!oneBlockPlant) {
                amount = Herbalism.calculateCatciAndSugarDrops(blockState);
                xp *= amount;
            }
        }
        else {
            CustomBlock customBlock = ModUtils.getCustomBlock(blockState);
            xp = customBlock.getXpGain();

            if (Permissions.doubleDrops(getPlayer(), skill)) {
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();
                drop = customBlock.getItemDrop();
                amount = Misc.getRandom().nextInt(maximumDropAmount - minimumDropAmount + 1) + minimumDropAmount;
            }
        }

        applyXpGain(xp);

        if (drop == null) {
            return;
        }

        for (int i = greenTerra ? 2 : 1; i != 0; i--) {
            if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.doubleDropsMaxChance, Herbalism.doubleDropsMaxLevel)) {
                Misc.dropItems(blockState.getLocation(), drop, amount);
            }
        }
    }

    /**
     * Process the Green Thumb ability for blocks.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processGreenThumbBlocks(BlockState blockState) {
        if (!SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.greenThumbMaxChance, Herbalism.greenThumbMaxLevel)) {
            getPlayer().sendMessage(LocaleLoader.getString("Herbalism.Ability.GTh.Fail"));
            return false;
        }

        return Herbalism.convertGreenTerraBlocks(blockState);
    }

    /**
     * Process the Hylian Luck ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processHylianLuck(BlockState blockState) {
        if (!SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.hylianLuckMaxChance, Herbalism.hylianLuckMaxLevel)) {
            return false;
        }

        List<HylianTreasure> treasures = new ArrayList<HylianTreasure>();

        switch (blockState.getType()) {
            case DEAD_BUSH:
            case LONG_GRASS:
            case SAPLING:
                treasures = TreasureConfig.getInstance().hylianFromBushes;
                break;

            case RED_ROSE:
            case YELLOW_FLOWER:
                if (mcMMO.placeStore.isTrue(blockState)) {
                    mcMMO.placeStore.setFalse(blockState);
                    return false;
                }

                treasures = TreasureConfig.getInstance().hylianFromFlowers;
                break;

            case FLOWER_POT:
                treasures = TreasureConfig.getInstance().hylianFromPots;
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
        getPlayer().sendMessage(LocaleLoader.getString("Herbalism.HylianLuck"));
        return true;
    }

    /**
     * Process the Shroom Thumb ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public boolean processShroomThumb(BlockState blockState) {
        Player player = getPlayer();
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

        if (!SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.shroomThumbMaxChance, Herbalism.shroomThumbMaxLevel)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.ShroomThumb.Fail"));
            return false;
        }

        return Herbalism.convertShroomThumb(blockState);
    }

    /**
     * Process the Green Thumb ability for plants.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param greenTerra
     */
    private void processGreenThumbPlants(BlockState blockState, boolean greenTerra) {
        Player player = getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = (blockState.getType() == Material.CROPS) ? new ItemStack(Material.SEEDS) : HerbalismBlock.getHerbalismBlock(blockState.getType()).getDropItem();

        if (!playerInventory.containsAtLeast(seed, 1)) {
            return;
        }

        if (!greenTerra && !SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.greenThumbMaxChance, Herbalism.greenThumbMaxLevel)) {
            return;
        }

        if (!handleBlockState(blockState, greenTerra)) {
            return;
        }

        playerInventory.removeItem(seed);
        player.updateInventory(); // Needed until replacement available
        new HerbalismBlockUpdaterTask(blockState).runTaskLater(mcMMO.p, 0);
    }

    private boolean handleBlockState(BlockState blockState, boolean greenTerra) {
        switch (blockState.getType()) {
            case CROPS:
            case CARROT:
            case POTATO:
                if (greenTerra) {
                    blockState.setRawData(CropState.MEDIUM.getData()); // 2
                }
                else {
                    blockState.setRawData(getGreenThumbStage());
                }

                return true;

            case NETHER_WARTS:
                if (greenTerra) {
                    blockState.setRawData((byte) 2);
                }
                else {
                    int greenThumbStage = getGreenThumbStage();

                    if (greenThumbStage > 2) {
                        blockState.setRawData((byte) 2);
                    }
                    else if (greenThumbStage == 2) {
                        blockState.setRawData((byte) 1);
                    }
                    else {
                        blockState.setRawData((byte) 0);
                    }
                }

                return true;

            case COCOA:
                CocoaPlant plant = (CocoaPlant) blockState.getData();

                if (greenTerra || getGreenThumbStage() > 1) {
                    plant.setSize(CocoaPlantSize.MEDIUM);
                }
                else {
                    plant.setSize(CocoaPlantSize.SMALL);
                }

                return true;

            default:
                return false;
        }
    }

    private byte getGreenThumbStage() {
        return (byte) Math.min(Math.min(getProfile().getSkillLevel(skill), Herbalism.greenThumbStageMaxLevel) / Herbalism.greenThumbStageChangeLevel, 4);
    }
}
