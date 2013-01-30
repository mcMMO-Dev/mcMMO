package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.mods.datatypes.CustomBlock;
import com.gmail.nossr50.skills.Combat;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.SkillTools;
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
     * @param player Player using Tree Feller
     * @param block Block being broken
     */
    public static void process(Player player, Block block) {
        List<Block> treeFellerBlocks = new ArrayList<Block>();

        processRecursively(block, treeFellerBlocks);

        // If the player is trying to break to many block
        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return;
        }

        // If the tool can't sustain the durability loss
        if (!handleDurabilityLoss(treeFellerBlocks, player)) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

            int health = player.getHealth();

            if (health > 1) {
                Combat.dealDamage(player, Misc.getRandom().nextInt(health - 1));
            }

            return;
        }

        dropBlocks(treeFellerBlocks, player);
    }

    /**
     * Processes Tree Feller
     *
     * @param block Block being checked
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void processRecursively(Block block, List<Block> treeFellerBlocks) {
        if (!BlockChecks.isLog(block)) {
            return;
        }

        List<Block> futureCenterBlocks = new ArrayList<Block>();
        World world = block.getWorld();

        // Handle the blocks around 'block'
        for (int y = 0 ; y <= 1 ; y++) {
            for (int x = -1 ; x <= 1 ; x++) {
                for (int z = -1 ; z <= 1 ; z++) {
                    Block nextBlock = world.getBlockAt(block.getLocation().add(x, y, z));

                    handleBlock(nextBlock, futureCenterBlocks, treeFellerBlocks);

                    if (treeFellerReachedThreshold) {
                        return;
                    }
                }
            }
        }

        // Recursive call for each log found
        for (Block futurCenterBlock : futureCenterBlocks) {
            if (treeFellerReachedThreshold) {
                return;
            }

            processRecursively(futurCenterBlock, treeFellerBlocks);
        }
    }

    /**
     * Handle a block addition to the list of blocks to be removed
     * and to the list of blocks used for future recursive calls of 'processRecursively()'
     *
     * @param block Block to be added
     * @param futureCenterBlocks List of blocks that will be used to call 'processRecursively()'
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void handleBlock(Block block, List<Block> futureCenterBlocks, List<Block> treeFellerBlocks) {
        if (!BlockChecks.treeFellerCompatible(block) || mcMMO.placeStore.isTrue(block) || treeFellerBlocks.contains(block)) {
            return;
        }

        treeFellerBlocks.add(block);

        if (treeFellerBlocks.size() > Woodcutting.CONFIG.getTreeFellerThreshold()) {
            treeFellerReachedThreshold = true;
            return;
        }

        futureCenterBlocks.add(block);
    }

    /**
     * Handles the durability loss
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param Player Player using the ability
     * @return True if the tool can sustain the durability loss
     */
    private static boolean handleDurabilityLoss(List<Block> treeFellerBlocks, Player player) {
        ItemStack inHand = player.getItemInHand();
        Material inHandMaterial = inHand.getType();

        if (inHandMaterial != Material.AIR) {
            short durabilityLoss = 0;
            int unbreakingLevel = inHand.getEnchantmentLevel(Enchantment.DURABILITY);

            for (Block block : treeFellerBlocks) {
                if (BlockChecks.isLog(block) && Misc.getRandom().nextInt(unbreakingLevel + 1) == 0) {
                     durabilityLoss += Misc.toolDurabilityLoss;
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
    private static void dropBlocks(List<Block> treeFellerBlocks, Player player) {
        int xp = 0;

        for (Block block : treeFellerBlocks) {
            if (!Misc.blockBreakSimulate(block, player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            switch (block.getType()) {
            case LOG:
                Woodcutting.checkForDoubleDrop(player, block);

                try {
                    xp += Woodcutting.getExperienceFromLog(block, ExperienceGainMethod.TREE_FELLER);
                }
                catch (IllegalArgumentException exception) {
                    break;
                }

                Misc.dropItem(block.getLocation(), new ItemStack(Material.LOG, 1, Woodcutting.extractLogItemData(block.getData())));
                break;
            case LEAVES:
                Misc.randomDropItem(block.getLocation(), new ItemStack(Material.SAPLING, 1, Woodcutting.extractLogItemData(block.getData())), 10);
                break;
            default:
                if (ModChecks.isCustomLogBlock(block)) {
                    Woodcutting.checkForDoubleDrop(player, block);

                    CustomBlock customBlock = ModChecks.getCustomBlock(block);
                    xp = customBlock.getXpGain();
                    int minimumDropAmount = customBlock.getMinimumDropAmount();
                    int maximumDropAmount = customBlock.getMaximumDropAmount();
                    Location location = block.getLocation();
                    ItemStack item = customBlock.getItemDrop();;

                    Misc.dropItems(location, item, minimumDropAmount);

                    if (minimumDropAmount < maximumDropAmount) {
                        Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
                    }
                }
                else if (ModChecks.isCustomLeafBlock(block)) {
                    CustomBlock customBlock = ModChecks.getCustomBlock(block);

                    Misc.randomDropItem(block.getLocation(), customBlock.getItemDrop(), 10);
                }

                break;
            }

            block.setData((byte) 0);
            block.setType(Material.AIR);
        }

        SkillTools.xpProcessing(player, Users.getProfile(player), SkillType.WOODCUTTING, xp);
    }
}
