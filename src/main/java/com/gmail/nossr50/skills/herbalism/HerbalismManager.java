package com.gmail.nossr50.skills.herbalism;

import java.util.Collection;
import java.util.List;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.NetherWarts;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SecondaryAbility;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.datatypes.treasure.HylianTreasure;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.HerbalismBlockUpdaterTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class HerbalismManager extends SkillManager {
    public HerbalismManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.HERBALISM);
    }

    public boolean canBlockCheck() {
        return !(Config.getInstance().getHerbalismPreventAFK() && getPlayer().isInsideVehicle());
    }

    public boolean canGreenThumbBlock(BlockState blockState) {
        Player player = getPlayer();

        return player.getItemInHand().getType() == Material.SEEDS && BlockUtils.canMakeMossy(blockState) && Permissions.greenThumbBlock(player, blockState.getType());
    }

    public boolean canUseShroomThumb(BlockState blockState) {
        Player player = getPlayer();
        Material itemType = player.getItemInHand().getType();

        return (itemType == Material.RED_MUSHROOM || itemType == Material.BROWN_MUSHROOM) && BlockUtils.canMakeShroomy(blockState) && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.SHROOM_THUMB);
    }

    public boolean canUseHylianLuck() {
        return Permissions.secondaryAbilityEnabled(getPlayer(), SecondaryAbility.HYLIAN_LUCK);
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
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void herbalismBlockCheck(BlockState blockState) {
        Player player = getPlayer();
        Material material = blockState.getType();
        boolean oneBlockPlant = !(material == Material.CACTUS || material == Material.SUGAR_CANE_BLOCK);

        if (oneBlockPlant && mcMMO.getPlaceStore().isTrue(blockState)) {
            return;
        }

        if (!canBlockCheck()) {
            return;
        }

        Collection<ItemStack> drops = null;
        int amount = 1;
        int xp;
        boolean greenTerra = mcMMOPlayer.getAbilityMode(skill.getAbility());

        if (mcMMO.getModManager().isCustomHerbalismBlock(blockState)) {
            CustomBlock customBlock = mcMMO.getModManager().getBlock(blockState);
            xp = customBlock.getXpGain();

            if (Permissions.secondaryAbilityEnabled(player, SecondaryAbility.HERBALISM_DOUBLE_DROPS) && customBlock.isDoubleDropEnabled()) {
                drops = blockState.getBlock().getDrops();
            }
        }
        else {
            if (Permissions.greenThumbPlant(player, material)) {
                processGreenThumbPlants(blockState, greenTerra);
            }

            if (material == Material.DOUBLE_PLANT || material == Material.RED_ROSE || material == Material.LONG_GRASS) {
                xp = ExperienceConfig.getInstance().getFlowerAndGrassXp(blockState.getData());
            }
            else {
                xp = ExperienceConfig.getInstance().getXp(skill, material);
            }

            if (Config.getInstance().getDoubleDropsEnabled(skill, material) && Permissions.secondaryAbilityEnabled(player, SecondaryAbility.HERBALISM_DOUBLE_DROPS)) {
                drops = blockState.getBlock().getDrops();
            }

            if (!oneBlockPlant) {
                amount = Herbalism.calculateCatciAndSugarDrops(blockState);
                xp *= amount;
            }
        }

        applyXpGain(xp, XPGainReason.PVE);

        if (drops == null) {
            return;
        }

        for (int i = greenTerra ? 2 : 1; i != 0; i--) {
            if (SkillUtils.activationSuccessful(SecondaryAbility.HERBALISM_DOUBLE_DROPS, getPlayer(), getSkillLevel(), activationChance)) {
                for (ItemStack item : drops) {
                    Misc.dropItems(blockState.getLocation(), item, amount);
                }
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
        if (!SkillUtils.activationSuccessful(SecondaryAbility.GREEN_THUMB_BLOCK, getPlayer(), getSkillLevel(), activationChance)) {
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
        if (!SkillUtils.activationSuccessful(SecondaryAbility.HYLIAN_LUCK, getPlayer(), getSkillLevel(), activationChance)) {
            return false;
        }

        List<HylianTreasure> treasures;

        switch (blockState.getType()) {
            case DEAD_BUSH:
            case LONG_GRASS:
            case SAPLING:
                treasures = TreasureConfig.getInstance().hylianFromBushes;
                break;

            case RED_ROSE:
            case YELLOW_FLOWER:
                if (mcMMO.getPlaceStore().isTrue(blockState)) {
                    mcMMO.getPlaceStore().setFalse(blockState);
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

        Player player = getPlayer();

        if (treasures.isEmpty() || !EventUtils.simulateBlockBreak(blockState.getBlock(), player, false)) {
            return false;
        }

        blockState.setType(Material.AIR);

        Misc.dropItem(blockState.getLocation(), treasures.get(Misc.getRandom().nextInt(treasures.size())).getDrop());
        player.sendMessage(LocaleLoader.getString("Herbalism.HylianLuck"));
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

        if (!SkillUtils.activationSuccessful(SecondaryAbility.SHROOM_THUMB, getPlayer(), getSkillLevel(), activationChance)) {
            player.sendMessage(LocaleLoader.getString("Herbalism.Ability.ShroomThumb.Fail"));
            return false;
        }

        return Herbalism.convertShroomThumb(blockState);
    }

    /**
     * Process the Green Thumb ability for plants.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param greenTerra boolean to determine if greenTerra is active or not
     */
    private void processGreenThumbPlants(BlockState blockState, boolean greenTerra) {
        Player player = getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = null;

        switch (blockState.getType()) {
            case CARROT:
                seed = new ItemStack(Material.CARROT_ITEM);
                break;

            case CROPS:
                seed = new ItemStack(Material.SEEDS);
                break;

            case NETHER_WARTS:
                seed = new ItemStack(Material.NETHER_STALK);
                break;

            case POTATO:
                seed = new ItemStack(Material.POTATO_ITEM);
                break;

            default:
                break;
        }

        if (!playerInventory.containsAtLeast(seed, 1)) {
            return;
        }

        if (!greenTerra && !SkillUtils.activationSuccessful(SecondaryAbility.GREEN_THUMB_PLANT, getPlayer(), getSkillLevel(), activationChance)) {
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
        byte greenThumbStage = getGreenThumbStage();

        blockState.setMetadata(mcMMO.greenThumbDataKey, new FixedMetadataValue(mcMMO.p, (int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR)));

        switch (blockState.getType()) {
            case CROPS:
                Crops crops = (Crops) blockState.getData();

                if (greenTerra) {
                    crops.setState(CropState.MEDIUM);
                }
                else {
                    switch (greenThumbStage) {
                        case 4:
                            crops.setState(CropState.SMALL);
                            break;
                        case 3:
                            crops.setState(CropState.VERY_SMALL);
                            break;
                        case 2:
                            crops.setState(CropState.GERMINATED);
                            break;
                        default:
                            crops.setState(CropState.SEEDED);
                            break;
                    }
                }

                return true;

            case CARROT:
            case POTATO:
                if (greenTerra) {
                    blockState.setRawData(CropState.MEDIUM.getData());
                }
                else {
                    blockState.setRawData(greenThumbStage);
                }

                return true;

            case NETHER_WARTS:
                NetherWarts warts = (NetherWarts) blockState.getData();

                if (greenTerra || greenThumbStage > 2) {
                    warts.setState(NetherWartsState.STAGE_TWO);
                }
                else if (greenThumbStage == 2) {
                    warts.setState(NetherWartsState.STAGE_ONE);
                }
                else {
                    warts.setState(NetherWartsState.SEEDED);
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
        return (byte) Math.min(Math.min(getSkillLevel(), Herbalism.greenThumbStageMaxLevel) / Herbalism.greenThumbStageChangeLevel, 4);
    }
}
