package com.gmail.nossr50.skills.woodcutting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.Combat;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public abstract class TreeFeller {
    private static boolean treeFellerReachedThreshold = false;

    /**
     * Handle the Tree Feller ability.
     *
     * @param event Event to process
     */
    public static void process(BlockBreakEvent event) {
        List<Block> treeFellerBlocks = new ArrayList<Block>();
        Player player = event.getPlayer();

        processRecursively(event.getBlock(), treeFellerBlocks);

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

        removeBlocks(treeFellerBlocks, player);
    }

    /**
     * Process Tree Feller
     *
     * @param block Point of origin of the layer
     * @param treeFellerBlocks List of blocks to be removed
     */
    private static void processRecursively(Block block, List<Block> treeFellerBlocks) {
        List<Block> futureCenterBlocks = new ArrayList<Block>();
        boolean centerIsLog = BlockChecks.isLog(block);
        Block nextBlock = block.getRelative(BlockFace.UP);

        // Handle the block above 'block'
        if (addBlock(nextBlock, treeFellerBlocks)) {
            if (treeFellerReachedThreshold) {
                return;
            }

            if (centerIsLog) {
                futureCenterBlocks.add(nextBlock);
            }
        }

        World world = block.getWorld();

        // Handle the blocks around 'block'
        for (int x = -1 ; x <= 1 ; x++) {
            for (int z = -1 ; z <= 1 ; z++) {
                nextBlock = world.getBlockAt(block.getLocation().add(x, 0, z));

                if (addBlock(nextBlock, treeFellerBlocks)) {
                    if (treeFellerReachedThreshold) {
                        return;
                    }

                    if (centerIsLog) {
                        futureCenterBlocks.add(nextBlock);
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
     * Add a block to the block list
     *
     * @param block Block to be added
     * @param treeFellerBlocks List of blocks to be removed
     * @return True if block was added
     */
    private static boolean addBlock(Block block, List<Block> treeFellerBlocks) {
        if (BlockChecks.treeFellerCompatible(block) && !treeFellerBlocks.contains(block) && !mcMMO.placeStore.isTrue(block)) {
            treeFellerBlocks.add(block);

            if (treeFellerBlocks.size() >= Config.getInstance().getTreeFellerThreshold()) {
                treeFellerReachedThreshold = true;
            }

            return true;
        }

        return false;
    }

    /**
     * Handle the durability loss
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

            for (Block block : treeFellerBlocks) {
                if (BlockChecks.isLog(block)) {
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
     * Handles removing & dropping the blocks
     *
     * @param treeFellerBlocks List of blocks to be removed
     * @param player Player using the ability
     */
    private static void removeBlocks(List<Block> treeFellerBlocks, Player player) {
        int xp = 0;

        for (Block block : treeFellerBlocks) {
            if (!Misc.blockBreakSimulate(block, player, true)) {
                break; // TODO: Shouldn't we use continue instead?
            }

            switch (block.getType()) {
            case LOG:
                Woodcutting.checkDoubleDrop(player, block);

                try {
                    xp += Woodcutting.getExperienceFromLog(block);
                }
                catch (IllegalArgumentException exception) {
                    break;
                }

                // TODO: Nerf XP from jungle trees, as it was done previously

                Misc.dropItem(block.getLocation(), new ItemStack(Material.LOG, 1, block.getData()));
                break;
            case LEAVES:
                Misc.randomDropItem(block.getLocation(), new ItemStack(Material.SAPLING, 1, (short) (block.getData() & 3)), 10);
                break;
            default:
                if (ModChecks.isCustomLogBlock(block)) {
                    Woodcutting.checkDoubleDrop(player, block);

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

        // Do we really have to check the permission here?
        if (Permissions.woodcutting(player)) {
            Skills.xpProcessing(player, Users.getProfile(player), SkillType.WOODCUTTING, xp);
        }
    }
}
