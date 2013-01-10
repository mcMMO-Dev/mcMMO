package com.gmail.nossr50.skills.gathering;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.mods.CustomBlock;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class WoodCutting {

    static AdvancedConfig advancedConfig = AdvancedConfig.getInstance();

    /**
     * Handle the Tree Feller ability.
     *
     * @param event Event to modify
     */
    public static void treeFeller(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block firstBlock = event.getBlock();
        PlayerProfile profile = Users.getProfile(player);
        ArrayList<Block> toBeFelled = new ArrayList<Block>();

        /* NOTE: Tree Feller will cut upwards like how you actually fell trees */
        processTreeFelling(firstBlock, toBeFelled);
        removeBlocks(toBeFelled, player, profile);
    }

    /**
     * Handles removing & dropping the blocks from Tree Feller.
     *
     * @param toBeFelled List of Blocks to be removed from the tree
     * @param player The player using the ability
     * @param profile The PlayerProfile of the player
     */
    private static void removeBlocks(ArrayList<Block> toBeFelled, Player player, PlayerProfile profile) {
        if (toBeFelled.size() >= Config.getInstance().getTreeFellerThreshold()) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return;
        }

        int xp = 0;
        ItemStack inHand = player.getItemInHand();
        int level = 0;
        if (inHand.containsEnchantment(Enchantment.DURABILITY)) {
            level = inHand.getEnchantmentLevel(Enchantment.DURABILITY);
        }
        int durabilityLoss = durabilityLossCalulate(toBeFelled, level);

        /* This is to prevent using wood axes everytime you tree fell */
        if (ModChecks.isCustomTool(inHand)) {
            if (inHand.getDurability() + durabilityLoss >= ModChecks.getToolFromItemStack(inHand).getDurability()) {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

                int health = player.getHealth();

                if (health >= 2) {
                    Combat.dealDamage(player, Misc.getRandom().nextInt(health - 1));
                }
                inHand.setDurability(inHand.getType().getMaxDurability());
                return;
            }
        }
        else if ((inHand.getDurability() + durabilityLoss >= inHand.getType().getMaxDurability()) || inHand.getType().equals(Material.AIR)) {
            player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

            int health = player.getHealth();

            if (health >= 2) {
                Combat.dealDamage(player, Misc.getRandom().nextInt(health - 1));
            }
            inHand.setDurability(inHand.getType().getMaxDurability());
            return;
        }

        /* Damage the tool */
        inHand.setDurability((short) (inHand.getDurability() + durabilityLoss));

        //Prepare ItemStacks
        ItemStack item = null;
        ItemStack oak = (new MaterialData(Material.LOG, TreeSpecies.GENERIC.getData())).toItemStack(1);
        ItemStack spruce = (new MaterialData(Material.LOG, TreeSpecies.REDWOOD.getData())).toItemStack(1);
        ItemStack birch = (new MaterialData(Material.LOG, TreeSpecies.BIRCH.getData())).toItemStack(1);
        ItemStack jungle = (new MaterialData(Material.LOG, TreeSpecies.JUNGLE.getData())).toItemStack(1);

        for (Block x : toBeFelled) {
            if (Misc.blockBreakSimulate(x, player, true)) {
                if (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(x)) {
                    if (ModChecks.isCustomLogBlock(x)) {
                        CustomBlock block = ModChecks.getCustomBlock(x);
                        item = block.getItemDrop();

                        if (!mcMMO.placeStore.isTrue(x)) {
                            WoodCutting.woodCuttingProcCheck(player, x);
                            xp = block.getXpGain();
                        }

                        /* Remove the block */
                        x.setData((byte) 0x0);
                        x.setType(Material.AIR);

                        int minimumDropAmount = block.getMinimumDropAmount();
                        int maximumDropAmount = block.getMaximumDropAmount();

                        item = block.getItemDrop();

                        if (minimumDropAmount != maximumDropAmount) {
                            Misc.dropItems(x.getLocation(), item, minimumDropAmount);
                            Misc.randomDropItems(x.getLocation(), item, 50, maximumDropAmount - minimumDropAmount);
                        }
                        else {
                            Misc.dropItems(x.getLocation(), item, minimumDropAmount);
                        }
                    }
                    else if (ModChecks.isCustomLeafBlock(x)) {
                        CustomBlock block = ModChecks.getCustomBlock(x);
                        item = block.getItemDrop();

                        final int SAPLING_DROP_CHANCE = 10;

                        /* Remove the block */
                        x.setData((byte) 0x0);
                        x.setType(Material.AIR);

                        Misc.randomDropItem(x.getLocation(), item, SAPLING_DROP_CHANCE);
                    }
                }
                else if (x.getType() == Material.LOG) {
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

                    if (!mcMMO.placeStore.isTrue(x)) {
                        WoodCutting.woodCuttingProcCheck(player, x);

                        switch (species) {
                        case GENERIC:
                            xp += Config.getInstance().getWoodcuttingXPOak();
                            break;

                        case REDWOOD:
                            xp += Config.getInstance().getWoodcuttingXPSpruce();
                            break;

                        case BIRCH:
                            xp += Config.getInstance().getWoodcuttingXPBirch();
                            break;

                        case JUNGLE:
                            xp += Config.getInstance().getWoodcuttingXPJungle() / 2; //Nerf XP from Jungle Trees when using Tree Feller
                            break;

                        default:
                            break;
                        }
                    }

                    /* Remove the block */
                    x.setData((byte) 0x0);
                    x.setType(Material.AIR);

                    /* Drop the block */
                    Misc.dropItem(x.getLocation(), item);
                }
                else if (x.getType() == Material.LEAVES) {
                    final int SAPLING_DROP_CHANCE = 10;

                    //Drop the right type of sapling    
                    item = (new MaterialData(Material.SAPLING, (byte) (x.getData() & 3))).toItemStack(1); 

                    Misc.randomDropItem(x.getLocation(), item, SAPLING_DROP_CHANCE);

                    //Remove the block
                    x.setData((byte) 0);
                    x.setType(Material.AIR);
                }
            }
        }

        if (Permissions.woodcutting(player)) {
            Skills.xpProcessing(player, profile, SkillType.WOODCUTTING, xp);
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

        if (toBeFelled.size() >= Config.getInstance().getTreeFellerThreshold()) {
            return;
        }

        if (type.equals(Material.LOG) || type.equals(Material.LEAVES)) {
            toBeFelled.add(currentBlock);
        }
        else if (Config.getInstance().getBlockModsEnabled() && (ModChecks.isCustomLogBlock(currentBlock) || ModChecks.isCustomLeafBlock(currentBlock))) {
            toBeFelled.add(currentBlock);
        }

        Block xPositive = currentBlock.getRelative(1, 0, 0);
        Block xNegative = currentBlock.getRelative(-1, 0, 0);
        Block zPositive = currentBlock.getRelative(0, 0, 1);
        Block zNegative = currentBlock.getRelative(0, 0, -1);
        Block yPositive = currentBlock.getRelative(0, 1, 0);

        if (!mcMMO.placeStore.isTrue(currentBlock)) {
            if (!isTooAggressive(currentBlock, xPositive) && BlockChecks.treeFellerCompatible(xPositive) && !toBeFelled.contains(xPositive)) {
                processTreeFelling(xPositive, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, xNegative) && BlockChecks.treeFellerCompatible(xNegative) && !toBeFelled.contains(xNegative)) {
                processTreeFelling(xNegative, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, zPositive) && BlockChecks.treeFellerCompatible(zPositive) && !toBeFelled.contains(zPositive)) {
                processTreeFelling(zPositive, toBeFelled);
            }

            if (!isTooAggressive(currentBlock, zNegative) && BlockChecks.treeFellerCompatible(zNegative) && !toBeFelled.contains(zNegative)) {
                processTreeFelling(zNegative, toBeFelled);
            }
        }

        byte data = currentBlock.getData();

        if ((data & 0x4) == 0x4)
            data ^= 0x4;

        if ((data & 0x8) == 0x8)
            data ^= 0x8;

        if (TreeSpecies.getByData(data) == TreeSpecies.JUNGLE) {
            Block corner1 = currentBlock.getRelative(1, 0, 1);
            Block corner2 = currentBlock.getRelative(1, 0, -1);
            Block corner3 = currentBlock.getRelative(-1, 0, 1);
            Block corner4 = currentBlock.getRelative(-1, 0, -1);

            if (!mcMMO.placeStore.isTrue(currentBlock)) {
                if (!isTooAggressive(currentBlock, corner1) && BlockChecks.treeFellerCompatible(corner1) && !toBeFelled.contains(corner1)) {
                    processTreeFelling(corner1, toBeFelled);
                }

                if (!isTooAggressive(currentBlock, corner2) && BlockChecks.treeFellerCompatible(corner2) && !toBeFelled.contains(corner2)) {
                    processTreeFelling(corner2, toBeFelled);
                }

                if (!isTooAggressive(currentBlock, corner3) && BlockChecks.treeFellerCompatible(corner3) && !toBeFelled.contains(corner3)) {
                    processTreeFelling(corner3, toBeFelled);
                }

                if (!isTooAggressive(currentBlock, corner4) && BlockChecks.treeFellerCompatible(corner4) && !toBeFelled.contains(corner4)) {
                    processTreeFelling(corner4, toBeFelled);
                }
            }
        }

        if (BlockChecks.treeFellerCompatible(yPositive)) {
            if (!mcMMO.placeStore.isTrue(currentBlock) && !toBeFelled.contains(yPositive)) {
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
        Material newType = newBlock.getType();

        if ((currentType.equals(Material.LEAVES) || currentType.equals(Material.AIR) || (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLeafBlock(currentBlock))) && (newType.equals(Material.LEAVES) || newType.equals(Material.AIR) || (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLeafBlock(currentBlock)))) {
            return true;
        }

        return false;
    }

    /**
     * Check for double drops.
     *
     * @param player Player breaking the block
     * @param block The block being broken
     */
    private static void woodCuttingProcCheck(Player player, Block block) {

        final int MAX_CHANCE = advancedConfig.getMiningDoubleDropChance();
        final int MAX_BONUS_LEVEL = advancedConfig.getMiningDoubleDropMaxLevel();

        int skillLevel = Users.getProfile(player).getSkillLevel(SkillType.WOODCUTTING);
        byte type = block.getData();

        if ((type & 0x4) == 0x4)
            type ^= 0x4;

        if ((type & 0x8) == 0x8)
            type ^= 0x8;

        Material mat = Material.getMaterial(block.getTypeId());

        int randomChance = 100;
        int chance = (int) (((double) MAX_CHANCE / (double) MAX_BONUS_LEVEL) * skillLevel);
        if (chance > MAX_CHANCE) chance = MAX_CHANCE;

        if (Permissions.luckyWoodcutting(player)) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (chance > Misc.getRandom().nextInt(randomChance) && Permissions.woodcuttingDoubleDrops(player)) {
            Config configInstance = Config.getInstance();
            ItemStack item;
            Location location;

            if (configInstance.getBlockModsEnabled() && ModChecks.isCustomLogBlock(block)) {
                CustomBlock customBlock = ModChecks.getCustomBlock(block);
                int minimumDropAmount = customBlock.getMinimumDropAmount();
                int maximumDropAmount = customBlock.getMaximumDropAmount();

                item = customBlock.getItemDrop();
                location = block.getLocation();

                if (minimumDropAmount != maximumDropAmount) {
                    Misc.dropItems(location, item, minimumDropAmount);
                    Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
                }
                else {
                    Misc.dropItems(location, item, minimumDropAmount);
                }
            }
            else {
                item = (new MaterialData(mat, type)).toItemStack(1);

                location = block.getLocation();

                TreeSpecies species = TreeSpecies.getByData(type);

                /* Drop the block */
                switch (species) {
                case GENERIC:
                    if (configInstance.getOakDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    break;

                case REDWOOD:
                    if (configInstance.getSpruceDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    break;

                case BIRCH:
                    if (configInstance.getBirchDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    break;

                case JUNGLE:
                    if (configInstance.getJungleDoubleDropsEnabled()) {
                        Misc.dropItem(location, item);
                    }
                    break;

                default:
                    break;
                }
            }
        }
    }

    /**
     * Check XP gain for woodcutting.
     *
     * @param player The player breaking the block
     * @param block The block being broken
     */
    public static void woodcuttingBlockCheck(Player player, Block block) {
        PlayerProfile profile = Users.getProfile(player);
        int xp = 0;

        if (mcMMO.placeStore.isTrue(block)) {
            return;
        }

        if (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(block)) {
            xp = ModChecks.getCustomBlock(block).getXpGain();
        }
        else {
            byte type = block.getData();

            if ((type & 0x4) == 0x4)
                type ^= 0x4;

            if ((type & 0x8) == 0x8)
                type ^= 0x8;

            TreeSpecies species = TreeSpecies.getByData(type);

            //Apparently species can be null in certain cases (custom server mods?)
            //https://github.com/mcMMO-Dev/mcMMO/issues/229
            if (species == null)
                return;

            switch (species) {
            case GENERIC:
                xp += Config.getInstance().getWoodcuttingXPOak();
                break;

            case REDWOOD:
                xp += Config.getInstance().getWoodcuttingXPSpruce();
                break;

            case BIRCH:
                xp += Config.getInstance().getWoodcuttingXPBirch();
                break;

            case JUNGLE:
                xp += Config.getInstance().getWoodcuttingXPJungle();
                break;

            default:
                break;
            }
        }

        WoodCutting.woodCuttingProcCheck(player, block);
        Skills.xpProcessing(player, profile, SkillType.WOODCUTTING, xp);
    }

    /**
     * Handle the Leaf Blower ability.
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void leafBlower(Player player, Block block) {
        FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
        mcMMO.p.getServer().getPluginManager().callEvent(armswing);

        if (Config.getInstance().getWoodcuttingRequiresTool()) {
            Skills.abilityDurabilityLoss(player.getItemInHand(), Config.getInstance().getAbilityToolDamage());
        }

        if (mcMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }

    private static int durabilityLossCalulate(ArrayList<Block> toBeFelled, int level) {
        int durabilityLoss = 0;
        for (Block x : toBeFelled) {
        	if (Misc.getRandom().nextInt(level + 1) > 0) {}//Don't add durabilityLoss, because Unbreaking enchantment does it's work.
        	else if (x.getType().equals(Material.LOG) || (Config.getInstance().getBlockModsEnabled() && ModChecks.isCustomLogBlock(x))) {
                durabilityLoss = durabilityLoss + Config.getInstance().getAbilityToolDamage();
            }
        }

        return durabilityLoss;
    }
}
