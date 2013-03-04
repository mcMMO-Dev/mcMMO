package com.gmail.nossr50.skills.herbalism;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
import com.gmail.nossr50.runnables.skills.herbalism.GreenTerraTimerTask;
import com.gmail.nossr50.runnables.skills.herbalism.GreenThumbTimerTask;
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
        return SkillUtils.handleFoodSkills(getPlayer(), SkillType.HERBALISM, eventFoodLevel, Herbalism.farmersDietRankLevel1, Herbalism.farmersDietMaxLevel, rankChange);
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
     * Process double drops & XP gain for Herbalism.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @return true if the ability was successful, false otherwise
     */
    public void herbalismBlockCheck(BlockState blockState) {
        Player player = getPlayer();
        Material blockType = blockState.getType();

        HerbalismBlock herbalismBlock = HerbalismBlock.getHerbalismBlock(blockType);
        CustomBlock customBlock = null;

        int xp = 0;
        int dropAmount = 1;
        ItemStack dropItem = null;

        if (herbalismBlock != null) {
            if (blockType == Material.CACTUS || blockType == Material.SUGAR_CANE_BLOCK) {
                dropItem = herbalismBlock.getDropItem();
                dropAmount = Herbalism.calculateCatciAndSugarDrops(blockState);
                xp = herbalismBlock.getXpGain() * dropAmount;
            }
            else if (herbalismBlock.hasGreenThumbPermission(player)) {
                dropItem = herbalismBlock.getDropItem();
                xp = herbalismBlock.getXpGain();
                processGreenThumbPlants(blockState);
            }
            else {
                if (!mcMMO.placeStore.isTrue(blockState)) {
                    dropItem = herbalismBlock.getDropItem();
                    xp = herbalismBlock.getXpGain();
                }
            }
        }
        else {
            customBlock = ModUtils.getCustomBlock(blockState);
            dropItem = customBlock.getItemDrop();
            xp = customBlock.getXpGain();
        }

        if (Permissions.doubleDrops(player, skill) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.doubleDropsMaxChance, Herbalism.doubleDropsMaxLevel)) {
            Location location = blockState.getLocation();

            if (dropItem != null && herbalismBlock != null && herbalismBlock.canDoubleDrop()) {
                Misc.dropItems(location, dropItem, dropAmount);
            }
            else if (customBlock != null) {
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();

                if (minimumDropAmount != maximumDropAmount) {
                    Misc.randomDropItems(location, dropItem, maximumDropAmount - minimumDropAmount);
                }

                Misc.dropItems(location, dropItem, minimumDropAmount);
            }
        }

        applyXpGain(xp);
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
     */
    private void processGreenThumbPlants(BlockState blockState) {
        Player player = getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack seed = HerbalismBlock.getHerbalismBlock(blockState.getType()).getDropItem();

        if (!playerInventory.containsAtLeast(seed, 1)) {
            return;
        }

        if (mcMMOPlayer.getAbilityMode(AbilityType.GREEN_TERRA)) {
            playerInventory.removeItem(seed);
            player.updateInventory(); // Needed until replacement available

            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GreenTerraTimerTask(blockState), 0);
            return;
        }
        else if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Herbalism.greenThumbMaxChance, Herbalism.greenThumbMaxLevel)) {
            playerInventory.removeItem(seed);
            player.updateInventory(); // Needed until replacement available

            mcMMO.p.getServer().getScheduler().scheduleSyncDelayedTask(mcMMO.p, new GreenThumbTimerTask(blockState, getSkillLevel()), 0);
            return;
        }
    }
}
