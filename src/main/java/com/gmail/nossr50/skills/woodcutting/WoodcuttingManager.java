package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.woodcutting.Woodcutting.ExperienceGainMethod;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class WoodcuttingManager extends SkillManager {
    protected static boolean treeFellerReachedThreshold = false;

    public WoodcuttingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.WOODCUTTING);
    }

    public boolean canUseLeafBlower(ItemStack heldItem) {
        return getSkillLevel() >= Woodcutting.leafBlowerUnlockLevel && ItemUtils.isAxe(heldItem);
    }

    public boolean canUseTreeFeller(ItemStack heldItem) {
        return mcMMOPlayer.getAbilityMode(AbilityType.TREE_FELLER) && Permissions.treeFeller(getPlayer()) && ItemUtils.isAxe(heldItem);
    }

    protected boolean canGetDoubleDrops() {
        return Permissions.doubleDrops(getPlayer(), skill) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Woodcutting.doubleDropsMaxChance, Woodcutting.doubleDropsMaxLevel);
    }

    /**
     * Begins Woodcutting
     *
     * @param blockState Block being broken
     */
    public void woodcuttingBlockCheck(BlockState blockState) {
        int xp = Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.DEFAULT);

        switch (blockState.getType()) {
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                break;

            default:
                if (canGetDoubleDrops()) {
                    Woodcutting.checkForDoubleDrop(blockState);
                }
        }

        applyXpGain(xp);
    }

    /**
     * Begins Tree Feller
     *
     * @param blockState Block being broken
     */
    public void processTreeFeller(BlockState blockState) {
        Player player = getPlayer();
        List<BlockState> treeFellerBlocks = new ArrayList<BlockState>();

        switch (blockState.getType()) {
            case LOG:
            case HUGE_MUSHROOM_1:
                Woodcutting.processRegularTrees(blockState, treeFellerBlocks);
                break;

            case HUGE_MUSHROOM_2:
                Woodcutting.processRedMushroomTrees(blockState, treeFellerBlocks);
                break;

            default:
                if (ModUtils.isCustomLogBlock(blockState)) {
                    Woodcutting.processRegularTrees(blockState, treeFellerBlocks);
                }
                break;
        }

        // If the player is trying to break too many blocks
        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return;
        }

        // If the tool can't sustain the durability loss
        if (!Woodcutting.handleDurabilityLoss(treeFellerBlocks, player.getItemInHand())) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

            int health = player.getHealth();

            if (health > 1) {
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt(health - 1));
            }

            return;
        }

        dropBlocks(treeFellerBlocks);
        treeFellerReachedThreshold = false; // Reset the value after we're done with Tree Feller each time.
    }

    /**
     * Handles the dropping of blocks
     *
     * @param treeFellerBlocks List of blocks to be dropped
     * @param player Player using the ability
     */
    private void dropBlocks(List<BlockState> treeFellerBlocks) {
        Player player = getPlayer();
        int xp = 0;

        for (BlockState blockState : treeFellerBlocks) {
            if (!SkillUtils.blockBreakSimulate(blockState.getBlock(), player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            Material material = blockState.getType();

            if (material == Material.HUGE_MUSHROOM_1 || material == Material.HUGE_MUSHROOM_2) {
                xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);

                for (ItemStack drop : blockState.getBlock().getDrops()) {
                    Misc.dropItem(blockState.getLocation(), drop);
                }
            }
            else if (ModUtils.isCustomLogBlock(blockState)) {
                if (canGetDoubleDrops()) {
                    Woodcutting.checkForDoubleDrop(blockState);
                }

                CustomBlock customBlock = ModUtils.getCustomBlock(blockState);
                xp = customBlock.getXpGain();
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();
                Location location = blockState.getLocation();
                ItemStack item = customBlock.getItemDrop();;

                Misc.dropItems(location, item, minimumDropAmount);

                if (minimumDropAmount < maximumDropAmount) {
                    Misc.randomDropItems(location, item, maximumDropAmount - minimumDropAmount);
                }
            }
            else if (ModUtils.isCustomLeafBlock(blockState)) {
                Misc.randomDropItem(blockState.getLocation(), ModUtils.getCustomBlock(blockState).getItemDrop(), 10);
            }
            else {
                Tree tree = (Tree) blockState.getData();
                switch (material) {
                    case LOG:
                        if (canGetDoubleDrops()) {
                            Woodcutting.checkForDoubleDrop(blockState);
                        }
                        xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                        Misc.dropItem(blockState.getLocation(), new ItemStack(Material.LOG, 1, tree.getSpecies().getData()));
                        break;

                    case LEAVES:
                        Misc.randomDropItem(blockState.getLocation(), new ItemStack(Material.SAPLING, 1, tree.getSpecies().getData()), 10);
                        break;

                    default:
                        break;
                }
            }

            blockState.setRawData((byte) 0x0);
            blockState.setType(Material.AIR);
            blockState.update(true);
        }

        applyXpGain(xp);
    }

}
