package com.gmail.nossr50.skills;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Tree;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.Bukkit;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.spout.SpoutSounds;

import org.getspout.spoutapi.sound.SoundEffect;

public class WoodCutting {

    /**
     * Handle the Tree Feller ability.
     *
     * @param event Event to modify
     */
    public static void treeFeller(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block firstBlock = event.getBlock();
        PlayerProfile PP = Users.getProfile(player);
        ArrayList<Block> toBeFelled = new ArrayList<Block>();

        /* NOTE: Tree Feller will cut upwards like how you actually fell trees */
        processTreeFelling(firstBlock, toBeFelled);
        removeBlocks(toBeFelled, player, PP);
    }

    /**
     * Handles removing & dropping the blocks from Tree Feller.
     *
     * @param toBeFelled List of Blocks to be removed from the tree
     * @param player The player using the ability
     * @param PP The PlayerProfile of the player
     */
    private static void removeBlocks(ArrayList<Block> toBeFelled, Player player, PlayerProfile PP) {
        if (toBeFelled.size() > LoadProperties.treeFellerThreshold) {
            player.sendMessage(mcLocale.getString("Skills.Woodcutting.TreeFellerThreshold"));
            return;
        }

        int durabilityLoss = toBeFelled.size();
        int xp = 0;
        ItemStack inHand = player.getItemInHand();

        /* Damage the tool */
        inHand.setDurability((short) (inHand.getDurability() + durabilityLoss));

        /* This is to prevent using wood axes everytime you tree fell */
        if ((inHand.getDurability() + durabilityLoss >= inHand.getType().getMaxDurability()) || inHand.getType().equals(Material.AIR)) {
            player.sendMessage(mcLocale.getString("TreeFeller.AxeSplinters"));

            int health = player.getHealth();

            if (health >= 2) {
                Combat.dealDamage(player, (int)(Math.random() * (health - 1)));
            }
            return;
        }

        //Prepare ItemStacks
        ItemStack item = null;
        ItemStack oak = new ItemStack(Material.LOG, 1, (short) 0, TreeSpecies.GENERIC.getData());
        ItemStack spruce = new ItemStack(Material.LOG, 1, (short) 0, TreeSpecies.REDWOOD.getData());
        ItemStack birch = new ItemStack(Material.LOG, 1, (short) 0, TreeSpecies.BIRCH.getData());
        ItemStack jungle = new ItemStack(Material.LOG, 1, (short) 0, TreeSpecies.JUNGLE.getData());
        
        for (Block x : toBeFelled) {
            if (m.blockBreakSimulate(x, player, true)) {
                if (x.getType() == Material.LOG) {
                    Tree tree = (Tree) x.getState().getData();
                    TreeSpecies species = tree.getSpecies();

                    switch (species) {
                    case GENERIC:
                        item = oak;
                        break;

                    case REDWOOD:
                        item = spruce;
                        break;

                    case BIRCH:
                        item = birch;
                        break;

                    case JUNGLE:
                        item = jungle;
                        break;

                    default:
                        break;
                    }

                    if (!x.hasMetadata("mcmmoPlacedBlock")) {
                        WoodCutting.woodCuttingProcCheck(player, x);

                        switch (species) {
                            case GENERIC:
                                xp += LoadProperties.moak;
                                break;

                            case REDWOOD:
                                xp += LoadProperties.mspruce;
                                break;

                            case BIRCH:
                                xp += LoadProperties.mbirch;
                                break;

                            case JUNGLE:
                                xp += LoadProperties.mjungle / 4; //Nerf XP from Jungle Trees when using Tree Feller
                                break;

                            default:
                                break;
                        }
                    }

                    /* Remove the block */
                    x.setData((byte) 0x0);
                    x.setType(Material.AIR);

                    /* Drop the block */
                    m.mcDropItem(x.getLocation(), item);
                }
                else if (x.getType() == Material.LEAVES) {
                    final int SAPLING_DROP_CHANCE = 10;

                    item = new ItemStack(Material.SAPLING, 1, (short) 0, (byte) (x.getData() & 3)); //Drop the right type of sapling
                    m.mcRandomDropItem(x.getLocation(), item, SAPLING_DROP_CHANCE);

                    //Remove the block
                    x.setData((byte) 0);
                    x.setType(Material.AIR);
                }
            }
        }

        PP.addXP(SkillType.WOODCUTTING, xp, player); //Tree Feller gives nerf'd XP
        Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
    }

    /**
     * Checks if the block is affected by Tree Feller.
     *
     * @param block Block to check
     * @return true if the block is affected by Tree Feller, false otherwise
     */
    private static boolean treeFellerCompatible(Block block) {
        switch (block.getType()) {
        case LOG:
        case LEAVES:
        case AIR:
            return true;

        default:
            return false;
        }
    }

    /**
     * Handle the calculations from Tree Feller.
     *
     * @param currentBlock The current block to be removed
     * @param toBeFelled The list of blocks left to be removed
     */
    private static void processTreeFelling(Block currentBlock, ArrayList<Block> toBeFelled) {
        Material type = currentBlock.getType();

        if (type.equals(Material.LOG) || type.equals(Material.LEAVES)) {
            toBeFelled.add(currentBlock);
        }

        Block xPositive = currentBlock.getRelative(1, 0, 0);
        Block xNegative = currentBlock.getRelative(-1, 0, 0);
        Block zPositive = currentBlock.getRelative(0, 0, 1);
        Block zNegative = currentBlock.getRelative(0, 0, -1);
        Block yPositive = currentBlock.getRelative(0, 1, 0);

        if (!currentBlock.hasMetadata("mcmmoPlacedBlock")) {
            if (!isTooAggressive(currentBlock, xPositive) && treeFellerCompatible(xPositive) && !toBeFelled.contains(xPositive)) {
                processTreeFelling(xPositive, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, xNegative) && treeFellerCompatible(xNegative) && !toBeFelled.contains(xNegative)) {
                processTreeFelling(xNegative, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, zPositive) && treeFellerCompatible(zPositive) && !toBeFelled.contains(zPositive)) {
                processTreeFelling(zPositive, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, zNegative) && treeFellerCompatible(zNegative) && !toBeFelled.contains(zNegative)) {
                processTreeFelling(zNegative, toBeFelled);
            }

            if (treeFellerCompatible(yPositive) && !toBeFelled.contains(yPositive)) {
                processTreeFelling(yPositive, toBeFelled);
            }
        }
    }

    /**
     * Check if Tree Feller is being too aggressive.
     *
     * @param currentBlock The current block being felled
     * @param newBlock The next block to be felled
     * @return true if Tree Feller is too aggressive, false otherwise
     */
    private static boolean isTooAggressive(Block currentBlock, Block newBlock) {
        Material currentType = currentBlock.getType();
        Material newType = currentBlock.getType();

        if ((currentType.equals(Material.LEAVES) || currentType.equals(Material.AIR)) && (newType.equals(Material.LEAVES) || newType.equals(Material.AIR))) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Check for double drops.
     *
     * @param player Player breaking the block
     * @param block The block being broken
     */
    private static void woodCuttingProcCheck(Player player, Block block) {
        final int MAX_SKILL_LEVEL = 1000;

        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.WOODCUTTING);
        byte type = block.getData();
        Material mat = Material.getMaterial(block.getTypeId());

        if ((skillLevel > MAX_SKILL_LEVEL || Math.random() * 1000 <= skillLevel) && mcPermissions.getInstance().woodcuttingDoubleDrops(player)) {
            ItemStack item = new ItemStack(mat, 1, (short) 0, type);
            m.mcDropItem(block.getLocation(), item);
        }
    }

    /**
     * Check XP gain for woodcutting.
     *
     * @param player The player breaking the block
     * @param block The block being broken
     */
    public static void woodcuttingBlockCheck(Player player, Block block) {
        PlayerProfile PP = Users.getProfile(player);
        int xp = 0;
        TreeSpecies species = TreeSpecies.getByData(block.getData());

        if (block.hasMetadata("mcmmoPlacedBlock")) {
            return;
        }

        switch (species) {
        case GENERIC:
            xp += LoadProperties.moak;
            break;

        case REDWOOD:
            xp += LoadProperties.mspruce;
            break;

        case BIRCH:
            xp += LoadProperties.mbirch;
            break;

        case JUNGLE:
            xp += LoadProperties.mjungle;
            break;

        default:
            break;
        }

        WoodCutting.woodCuttingProcCheck(player, block);
        PP.addXP(SkillType.WOODCUTTING, xp, player);
        Skills.XpCheckSkill(SkillType.WOODCUTTING, player);
    }

    /**
     * Handle the Leaf Blower ability.
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void leafBlower(Player player, Block block) {
        PlayerAnimationEvent armswing = new PlayerAnimationEvent(player);
        Bukkit.getPluginManager().callEvent(armswing);

        if (LoadProperties.woodcuttingrequiresaxe) {
            Skills.abilityDurabilityLoss(player.getItemInHand(), LoadProperties.abilityDurabilityLoss);
        }

        if (LoadProperties.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }
}
