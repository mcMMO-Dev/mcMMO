package com.gmail.nossr50.skills.gathering;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
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
    private static final AdvancedConfig ADVANCED_CONFIG = AdvancedConfig.getInstance();
    private static boolean treeFellerReachedThreshold = false;

    /**
     * Handle the Tree Feller ability.
     *
     * @param event Event to process
     */
    public static void treeFeller(BlockBreakEvent event) {
        List<Block> toBeFelled = processTreeFeller(event);

        if (toBeFelled != null && !toBeFelled.isEmpty()) {
            removeBlocks(toBeFelled, event.getPlayer());
        }
    }

    /**
     * Handles removing & dropping the blocks from Tree Feller.
     *
     * @param toBeFelled List of blocks to be removed
     * @param player Player using the ability
     */
    private static void removeBlocks(List<Block> toBeFelled, Player player) {
        ItemStack inHand = player.getItemInHand();
        Material inHandMaterial = inHand.getType();
        short finalDurability = (short) (inHand.getDurability() + calulateDurabilityLossFromTreeFeller(toBeFelled));

        // Prevent the tree to be cut down if the tool doesn't have enough durability
        if (inHandMaterial != Material.AIR) {
            short maxDurability = ModChecks.isCustomTool(inHand) ? ModChecks.getToolFromItemStack(inHand).getDurability() : inHandMaterial.getMaxDurability();

            if (finalDurability >= maxDurability) {
                player.sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFeller.Splinter"));

                int health = player.getHealth();

                if (health >= 2) {
                    Combat.dealDamage(player, Misc.getRandom().nextInt(health - 1)); // Why not base the damage on the number of elements in toBeFelled?
                }

                inHand.setDurability(maxDurability);
                return;
            }
        }

        inHand.setDurability(finalDurability);

        int xp = 0;
        ItemStack item = null;

        for (Block block : toBeFelled) {
            if (!Misc.blockBreakSimulate(block, player, true)) {
                break;
            }

            if (block.getType() == Material.LOG) {
                WoodCutting.woodCuttingProcCheck(player, block);

                TreeSpecies species = ((Tree) block.getState().getData()).getSpecies();

                switch (species) {
                case GENERIC:
                    item = new MaterialData(Material.LOG, TreeSpecies.GENERIC.getData()).toItemStack(1);
                    xp += Config.getInstance().getWoodcuttingXPOak();
                    break;
                case REDWOOD:
                    item = new MaterialData(Material.LOG, TreeSpecies.REDWOOD.getData()).toItemStack(1);
                    xp += Config.getInstance().getWoodcuttingXPSpruce();
                    break;
                case BIRCH:
                    item = new MaterialData(Material.LOG, TreeSpecies.BIRCH.getData()).toItemStack(1);
                    xp += Config.getInstance().getWoodcuttingXPBirch();
                    break;
                case JUNGLE:
                    item = new MaterialData(Material.LOG, TreeSpecies.JUNGLE.getData()).toItemStack(1);
                    xp += Config.getInstance().getWoodcuttingXPJungle() / 2; // Nerf XP from Jungle Trees when using Tree Feller
                    break;
                default:
                    break;
                }

                Misc.dropItem(block.getLocation(), item);
            }
            else if (block.getType() == Material.LEAVES) {
                item = new MaterialData(Material.SAPLING, (byte) (block.getData() & 3)).toItemStack(1); 

                Misc.randomDropItem(block.getLocation(), item, 10);
            }
            else if (Config.getInstance().getBlockModsEnabled()) {
                if (ModChecks.isCustomLogBlock(block)) {
                    CustomBlock customBlock = ModChecks.getCustomBlock(block);

                    WoodCutting.woodCuttingProcCheck(player, block);

                    xp = customBlock.getXpGain();
                    int minimumDropAmount = customBlock.getMinimumDropAmount();
                    int maximumDropAmount = customBlock.getMaximumDropAmount();
                    Location location = block.getLocation();
                    item = customBlock.getItemDrop();

                    Misc.dropItems(location, item, minimumDropAmount);

                    if (minimumDropAmount < maximumDropAmount) {
                        Misc.randomDropItems(location, item, 50, maximumDropAmount - minimumDropAmount);
                    }
                }
                else if (ModChecks.isCustomLeafBlock(block)) {
                    CustomBlock customBlock = ModChecks.getCustomBlock(block);

                    Misc.randomDropItem(block.getLocation(), customBlock.getItemDrop(), 10);
                }
            }

            block.setData((byte) 0);
            block.setType(Material.AIR);
        }

        if (Permissions.woodcutting(player)) {
            Skills.xpProcessing(player, Users.getProfile(player), SkillType.WOODCUTTING, xp);
        }
    }

    /**
     * Process Tree Feller around a block.
     *
     * @param block Point of origin of the layer
     * @param toBeFelled List of blocks to be removed
     */
    private static void processTreeFellerAroundBlock(Block block, List<Block> toBeFelled) {
        // TODO: too much duplicate code here
        List<Block> futureCenterBlocks = new ArrayList<Block>();
        boolean centerIsLog = (block.getType() == Material.LOG); //TODO: custom blocks?
        
        // Handle the block above 'block'
        Block nextBlock = block.getRelative(BlockFace.UP);;

        if (BlockChecks.treeFellerCompatible(nextBlock) && !toBeFelled.contains(nextBlock) && !mcMMO.placeStore.isTrue(nextBlock)) {
            toBeFelled.add(nextBlock);

            if (centerIsLog) {
                futureCenterBlocks.add(nextBlock);
            }

            if (toBeFelled.size() >= Config.getInstance().getTreeFellerThreshold()) {
                treeFellerReachedThreshold = true;
                return;
            }
        }

        World world = block.getWorld();

        // Handle the blocks around 'block'
        for (int x = -1 ; x <= 1 ; x++) {
            for (int z = -1 ; z <= 1 ; z++) {
                nextBlock = world.getBlockAt(block.getLocation().add(x, 0, z));

                if (BlockChecks.treeFellerCompatible(nextBlock) && !toBeFelled.contains(nextBlock) && !mcMMO.placeStore.isTrue(nextBlock)) {
                    toBeFelled.add(nextBlock);

                    if (centerIsLog) {
                        futureCenterBlocks.add(nextBlock);
                    }

                    if (toBeFelled.size() >= Config.getInstance().getTreeFellerThreshold()) {
                        treeFellerReachedThreshold = true;
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

            processTreeFellerAroundBlock(futurCenterBlock, toBeFelled);
        }
    }

    /**
     * Process Tree Feller.
     *
     * @param event Event to process
     * @return List of blocks to be removed
     */
    private static List<Block> processTreeFeller(BlockBreakEvent event) {
        List<Block> toBeFelled = new ArrayList<Block>();

        processTreeFellerAroundBlock(event.getBlock(), toBeFelled);

        if (treeFellerReachedThreshold) {
            treeFellerReachedThreshold = false;

            event.getPlayer().sendMessage(LocaleLoader.getString("Woodcutting.Skills.TreeFellerThreshold"));
            return null;
        }

        return toBeFelled;
    }

    /**
     * Check for double drops.
     *
     * @param player Player breaking the block
     * @param block Block being broken
     */
    private static void woodCuttingProcCheck(Player player, Block block) {
        final int MAX_CHANCE = ADVANCED_CONFIG.getMiningDoubleDropChance();
        final int MAX_BONUS_LEVEL = ADVANCED_CONFIG.getMiningDoubleDropMaxLevel();
        byte type = block.getData();

        if ((type & 0x4) == 0x4)
            type ^= 0x4;

        if ((type & 0x8) == 0x8)
            type ^= 0x8;

<<<<<<< Upstream, based on origin/master
        Material mat = Material.getMaterial(block.getTypeId());

        int chance = (int) (((double) MAX_CHANCE / (double) MAX_BONUS_LEVEL) * skillLevel);
        if (chance > MAX_CHANCE) chance = MAX_CHANCE;
=======
        Material blockMaterial = block.getType();
        int randomChance = 100;
        int chance = (int) (((double) MAX_CHANCE / (double) MAX_BONUS_LEVEL) * Users.getProfile(player).getSkillLevel(SkillType.WOODCUTTING));
        
        if (chance > MAX_CHANCE) {
            chance = MAX_CHANCE;
        }
>>>>>>> f510cb2 Optimized Tree Feller And cleaned up WoodCutting a little

        int activationChance = Misc.calculateActivationChance(Permissions.luckyWoodcutting(player));

        if (chance > Misc.getRandom().nextInt(activationChance) && Permissions.woodcuttingDoubleDrops(player)) {
            Config configInstance = Config.getInstance();
            ItemStack item = null;
            Location location = null;

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
                item = (new MaterialData(blockMaterial, type)).toItemStack(1);
                location = block.getLocation();

                switch (TreeSpecies.getByData(type)) {
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
     * @param player Player breaking the block
     * @param block Block being broken
     */
    public static void woodcuttingBlockCheck(Player player, Block block) {
        if (mcMMO.placeStore.isTrue(block)) {
            return;
        }

        int xp = 0;

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

            // Apparently species can be null in certain cases (custom server mods?)
            // https://github.com/mcMMO-Dev/mcMMO/issues/229
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
        Skills.xpProcessing(player,  Users.getProfile(player), SkillType.WOODCUTTING, xp);
    }

    /**
     * Handle the Leaf Blower ability.
     *
     * @param player Player using the ability
     * @param block Block being broken
     */
    public static void leafBlower(Player player, Block block) {
        mcMMO.p.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));

        if (mcMMO.spoutEnabled) {
            SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
        }
    }

    /**
     * Calculate the durability loss from Tree Feller
     *
     * @param List<Block> Blocks to be felled
     * @return Durability loss
     */
    private static short calulateDurabilityLossFromTreeFeller(List<Block> toBeFelled) {
        short durabilityLoss = 0;
        boolean blockModsEnabled = Config.getInstance().getBlockModsEnabled();

        for (Block block : toBeFelled) {
            if (block.getType() == Material.LOG || (blockModsEnabled && ModChecks.isCustomLogBlock(block))) {
                durabilityLoss += Misc.toolDurabilityLoss;
            }
        }

        return durabilityLoss;
    }
}
