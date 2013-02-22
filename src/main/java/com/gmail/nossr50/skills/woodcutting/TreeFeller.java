package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.skills.woodcutting.Woodcutting.ExperienceGainMethod;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public final class TreeFeller {
    private static boolean treeFellerReachedThreshold = false;

    private TreeFeller() {}

    /**
     * Begins Tree Feller
     *
     * @param mcMMOPlayer Player using Tree Feller
     * @param blockState Block being broken
     */
    protected static void process(BlockState blockState, Player player) {
        List<BlockState> treeFellerBlocks = new ArrayList<BlockState>();

        processRecursively(blockState, treeFellerBlocks);

        // If the player is trying to break too many blocks
        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return;
        }

        // If the tool can't sustain the durability loss
        if (!handleDurabilityLoss(treeFellerBlocks, player.getItemInHand())) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

            int health = player.getHealth();

            if (health > 1) {
                CombatTools.dealDamage(player, Misc.getRandom().nextInt(health - 1));
            }

            return;
        }

        dropBlocks(treeFellerBlocks, player);
    }

    /**
     * Processes Tree Feller
     *
     * @param blockState Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void processRecursively(BlockState blockState, List<BlockState> treeFellerBlocks) {
        if (!BlockChecks.isLog(blockState)) {
            return;
        }

        List<BlockState> futureCenterBlocks = new ArrayList<BlockState>();
        World world = blockState.getWorld();

        // Handle the blocks around 'block'
        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState nextBlock = world.getBlockAt(blockState.getLocation().add(x, y, z)).getState();

                    handleBlock(nextBlock, futureCenterBlocks, treeFellerBlocks);

                    if (treeFellerReachedThreshold) {
                        return;
                    }
                }
            }
        }

        // Recursive call for each log found
        for (BlockState futureCenterBlock : futureCenterBlocks) {
            if (treeFellerReachedThreshold) {
                return;
            }

            processRecursively(futureCenterBlock, treeFellerBlocks);
        }
    }

    /**
     * Handle a block addition to the list of blocks to be removed
     * and to the list of blocks used for future recursive calls of 'processRecursively()'
     *
     * @param blockState Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call 'processRecursively()'
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void handleBlock(BlockState blockState, List<BlockState> futureCenterBlocks, List<BlockState> treeFellerBlocks) {
        if (!BlockChecks.affectedByTreeFeller(blockState) || mcMMO.placeStore.isTrue(blockState) || treeFellerBlocks.contains(blockState)) {
            return;
        }

        treeFellerBlocks.add(blockState);

        if (treeFellerBlocks.size() > Config.getInstance().getTreeFellerThreshold()) {
            treeFellerReachedThreshold = true;
            return;
        }

        futureCenterBlocks.add(blockState);
    }

    /**
     * Handles the durability loss
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param inHand tool being used
     * @return True if the tool can sustain the durability loss
     */
    private static boolean handleDurabilityLoss(List<BlockState> treeFellerBlocks, ItemStack inHand) {
        Material inHandMaterial = inHand.getType();

        if (inHandMaterial != Material.AIR) {
            short durabilityLoss = 0;
            int unbreakingLevel = inHand.getEnchantmentLevel(Enchantment.DURABILITY);

            for (BlockState blockState : treeFellerBlocks) {
                if (BlockChecks.isLog(blockState) && Misc.getRandom().nextInt(unbreakingLevel + 1) == 0) {
                     durabilityLoss += Config.getInstance().getAbilityToolDamage();
                }
            }

            short finalDurability = (short) (inHand.getDurability() + durabilityLoss);
            short maxDurability = ModChecks.isCustomTool(inHand) ? ModChecks.getToolFromItemStack(inHand).getDurability() : inHandMaterial.getMaxDurability();

            if (finalDurability >= maxDurability) {
                inHand.setDurability(maxDurability);
                return false;
            }
    
            inHand.setDurability(finalDurability);
        }

        return true;
    }

    /**
     * Handles the dropping of blocks
     *
     * @param treeFellerBlocks List of blocks to be dropped
     * @param player Player using the ability
     */
    private static void dropBlocks(List<BlockState> treeFellerBlocks, Player player) {
        int xp = 0;

        for (BlockState blockState : treeFellerBlocks) {
            if (!SkillTools.blockBreakSimulate(blockState.getBlock(), player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            Material material = blockState.getType();

            switch (material) {
            case HUGE_MUSHROOM_1:
            case HUGE_MUSHROOM_2:
                xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);

                for (ItemStack drop : blockState.getBlock().getDrops()) {
                    Misc.dropItem(blockState.getLocation(), drop);
                }

                break;

            default:
                break;
            }

            if (ModChecks.isCustomLogBlock(blockState)) {
                Woodcutting.checkForDoubleDrop(player, blockState);

                CustomBlock customBlock = ModChecks.getCustomBlock(blockState);
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
            else if (ModChecks.isCustomLeafBlock(blockState)) {
                Misc.randomDropItem(blockState.getLocation(), ModChecks.getCustomBlock(blockState).getItemDrop(), 10);
            }

            Tree tree = (Tree) blockState.getData();
            switch (material) {
            case LOG:
                Woodcutting.checkForDoubleDrop(player, blockState);
                xp += Woodcutting.getExperienceFromLog(blockState, ExperienceGainMethod.TREE_FELLER);
                Misc.dropItem(blockState.getLocation(), new ItemStack(Material.LOG, 1, tree.getSpecies().getData()));
                break;

            case LEAVES:
                Misc.randomDropItem(blockState.getLocation(), new ItemStack(Material.SAPLING, 1, tree.getSpecies().getData()), 10);
                break;

            default:
                break;
            }

            blockState.setRawData((byte) 0x0);
            blockState.setType(Material.AIR);
            blockState.update();
        }

        Users.getPlayer(player).beginXpGain(SkillType.WOODCUTTING, xp);
    }
}
