package com.gmail.nossr50.skills.woodcutting;

import java.util.LinkedHashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        LinkedHashSet<BlockState> treeFellerBlocks = new LinkedHashSet<BlockState>();

        Woodcutting.processTree(blockState, treeFellerBlocks);

        // If the player is trying to break too many blocks
        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return;
        }

        // If the tool can't sustain the durability loss
        if (!Woodcutting.handleDurabilityLoss(treeFellerBlocks, player.getItemInHand())) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

            double health = player.getHealth();

            if (health > 1) {
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (health - 1)));
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
     */
    private void dropBlocks(LinkedHashSet<BlockState> treeFellerBlocks) {
        Player player = getPlayer();
        int xp = 0;

        for (BlockState blockState : treeFellerBlocks) {
            Block block = blockState.getBlock();

            if (!SkillUtils.blockBreakSimulate(block, player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            Material material = blockState.getType();

            if (material == Material.HUGE_MUSHROOM_1 || material == Material.HUGE_MUSHROOM_2) {
                xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                Misc.dropItems(blockState.getLocation(), block.getDrops());
            }
            else if (ModUtils.isCustomLogBlock(blockState)) {
                if (canGetDoubleDrops()) {
                    Woodcutting.checkForDoubleDrop(blockState);
                }

                CustomBlock customBlock = ModUtils.getCustomBlock(blockState);
                xp = customBlock.getXpGain();

                Misc.dropItems(blockState.getLocation(), block.getDrops());
            }
            else if (ModUtils.isCustomLeafBlock(blockState)) {
                Misc.randomDropItems(blockState.getLocation(), block.getDrops(), 10.0);
            }
            else {
                Tree tree = (Tree) blockState.getData();
                tree.setDirection(BlockFace.UP);

                switch (material) {
                    case LOG:
                        if (canGetDoubleDrops()) {
                            Woodcutting.checkForDoubleDrop(blockState);
                        }
                        xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                        Misc.dropItems(blockState.getLocation(), block.getDrops());
                        break;

                    case LEAVES:
                        Misc.randomDropItems(blockState.getLocation(), block.getDrops(), 10.0);
                        break;

                    default:
                        break;
                }
            }

            blockState.setType(Material.AIR);
            blockState.update(true);
        }

        applyXpGain(xp);
    }

}
