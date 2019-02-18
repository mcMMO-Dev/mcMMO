package com.gmail.nossr50.skills.woodcutting;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.woodcutting.Woodcutting.ExperienceGainMethod;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class WoodcuttingManager extends SkillManager {

    public WoodcuttingManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.WOODCUTTING);
    }

    public boolean canUseLeafBlower(ItemStack heldItem) {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.WOODCUTTING_LEAF_BLOWER)
                && ItemUtils.isAxe(heldItem);
    }

    public boolean canUseTreeFeller(ItemStack heldItem) {
        return mcMMOPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER)
                && ItemUtils.isAxe(heldItem);
    }

    protected boolean canGetDoubleDrops() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RankUtils.hasReachedRank(1, getPlayer(), SubSkillType.WOODCUTTING_HARVEST_LUMBER)
                && RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.WOODCUTTING_HARVEST_LUMBER, getPlayer());
    }

    /**
     * Begins Woodcutting
     *
     * @param blockState Block being broken
     */
    public void woodcuttingBlockCheck(BlockState blockState) {
        int xp = Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.DEFAULT);

        switch (blockState.getType()) {
            case BROWN_MUSHROOM_BLOCK:
            case RED_MUSHROOM_BLOCK:
                break;

            default:
                if (canGetDoubleDrops()) {
                    Woodcutting.checkForDoubleDrop(blockState);
                }
        }

        applyXpGain(xp, XPGainReason.PVE);
    }

    /**
     * Begins Tree Feller
     *
     * @param blockState Block being broken
     */
    public void processTreeFeller(BlockState blockState) {
        Player player = getPlayer();
        Set<BlockState> treeFellerBlocks = new HashSet<BlockState>();

        Woodcutting.treeFellerReachedThreshold = false;

        Woodcutting.processTree(blockState, treeFellerBlocks);

        // If the player is trying to break too many blocks
        if (Woodcutting.treeFellerReachedThreshold) {
            Woodcutting.treeFellerReachedThreshold = false;

            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Woodcutting.Skills.TreeFeller.Threshold");
            return;
        }

        // If the tool can't sustain the durability loss
        if (!Woodcutting.handleDurabilityLoss(treeFellerBlocks, player.getInventory().getItemInMainHand())) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE_FAILED, "Woodcutting.Skills.TreeFeller.Splinter");

            double health = player.getHealth();

            if (health > 1) {
                CombatUtils.dealDamage(player, Misc.getRandom().nextInt((int) (health - 1)));
            }

            return;
        }

        dropBlocks(treeFellerBlocks);
        Woodcutting.treeFellerReachedThreshold = false; // Reset the value after we're done with Tree Feller each time.
    }

    /**
     * Handles the dropping of blocks
     *
     * @param treeFellerBlocks List of blocks to be dropped
     */
    private void dropBlocks(Set<BlockState> treeFellerBlocks) {
        Player player = getPlayer();
        int xp = 0;

        for (BlockState blockState : treeFellerBlocks) {
            Block block = blockState.getBlock();

            if (!EventUtils.simulateBlockBreak(block, player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            Material material = blockState.getType();

            if (material == Material.BROWN_MUSHROOM_BLOCK || material == Material.RED_MUSHROOM_BLOCK) {
                xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                Misc.dropItems(Misc.getBlockCenter(blockState), block.getDrops());
            }
            /*else if (mcMMO.getModManager().isCustomLog(blockState)) {
                if (canGetDoubleDrops()) {
                    Woodcutting.checkForDoubleDrop(blockState);
                }

                CustomBlock customBlock = mcMMO.getModManager().getBlock(blockState);
                xp = customBlock.getXpGain();

                Misc.dropItems(Misc.getBlockCenter(blockState), block.getDrops());
            }
            else if (mcMMO.getModManager().isCustomLeaf(blockState)) {
                Misc.dropItems(Misc.getBlockCenter(blockState), block.getDrops());
            }*/
            else {

                if (BlockUtils.isLog(blockState)) {
                    if (canGetDoubleDrops()) {
                        Woodcutting.checkForDoubleDrop(blockState);
                    }
                    xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                    Misc.dropItems(Misc.getBlockCenter(blockState), block.getDrops());
                }
                if (BlockUtils.isLeaves(blockState)) {
                    Misc.dropItems(Misc.getBlockCenter(blockState), block.getDrops());
                }
            }

            blockState.setType(Material.AIR);
            blockState.update(true);
        }

        applyXpGain(xp, XPGainReason.PVE);
    }
}
