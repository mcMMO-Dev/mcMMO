package com.gmail.nossr50.listeners;

import static com.gmail.nossr50.util.MetadataConstants.METADATA_KEY_BONUS_DROPS;

import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakeEvent;
import com.gmail.nossr50.events.items.McMMOModifyBlockDropItemEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ContainerMetadataUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class BlockListener implements Listener {
    private final mcMMO plugin;

    public BlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
        //Make sure we clean up metadata on these blocks
        final Block block = event.getBlock();
        if (event.isCancelled()) {
            if (block.hasMetadata(METADATA_KEY_BONUS_DROPS)) {
                block.removeMetadata(METADATA_KEY_BONUS_DROPS, plugin);
            }
            return;
        }

        try {
            int tileEntityTolerance = 1;

            // beetroot hotfix, potentially other plants may need this fix
            final Material blockType = block.getType();
            if (blockType == Material.BEETROOTS) {
                tileEntityTolerance = 2;
            }

            //Track how many "things" are being dropped
            final Set<Material> uniqueMaterials = new HashSet<>();
            boolean dontRewardTE = false; //If we suspect TEs are mixed in with other things don't reward bonus drops for anything that isn't a block
            int blockCount = 0;

            final List<Item> eventItems = event.getItems();
            for (Item item : eventItems) {
                //Track unique materials
                uniqueMaterials.add(item.getItemStack().getType());

                //Count blocks as a second failsafe
                if (item.getItemStack().getType().isBlock()) {
                    blockCount++;
                }
            }

            if (uniqueMaterials.size() > tileEntityTolerance) {
                // Too many things are dropping, assume tile entities might be duped
                // Technically this would also prevent something like coal from being bonus dropped
                // if you placed a TE above a coal ore when mining it but that's pretty edge case
                // and this is a good solution for now
                dontRewardTE = true;
            }

            //If there are more than one block in the item list we can't really trust it
            // and will back out of rewarding bonus drops
            if (!block.getMetadata(METADATA_KEY_BONUS_DROPS).isEmpty()) {
                final MetadataValue bonusDropMeta = block
                        .getMetadata(METADATA_KEY_BONUS_DROPS).get(0);
                if (blockCount <= 1) {
                    for (final Item item : eventItems) {
                        final ItemStack eventItemStack = item.getItemStack();
                        int originalAmount = eventItemStack.getAmount();

                        if (eventItemStack.getAmount() <= 0) {
                            continue;
                        }

                        final Material itemType = eventItemStack.getType();
                        if (!mcMMO.p.getGeneralConfig()
                                .getDoubleDropsEnabled(PrimarySkillType.MINING, itemType)
                                && !mcMMO.p.getGeneralConfig()
                                .getDoubleDropsEnabled(PrimarySkillType.HERBALISM, itemType)
                                && !mcMMO.p.getGeneralConfig()
                                .getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, itemType)) {
                            continue;
                        }

                        //If we suspect TEs might be duped only reward block
                        if (dontRewardTE) {
                            if (!itemType.isBlock()) {
                                continue;
                            }
                        }

                        int amountToAddFromBonus = bonusDropMeta.asInt();
                        final McMMOModifyBlockDropItemEvent modifyDropEvent
                                = new McMMOModifyBlockDropItemEvent(event, item, amountToAddFromBonus);
                        plugin.getServer().getPluginManager().callEvent(modifyDropEvent);
                        if (!modifyDropEvent.isCancelled()
                                && modifyDropEvent.getModifiedItemStackQuantity() > originalAmount) {
                            eventItemStack.setAmount(
                                    Math.min(modifyDropEvent.getModifiedItemStackQuantity(),
                                            item.getItemStack().getMaxStackSize()));
                        }
                    }
                }
            }
        } finally {
            if (block.hasMetadata(METADATA_KEY_BONUS_DROPS)) {
                block.removeMetadata(METADATA_KEY_BONUS_DROPS, plugin);
            }
        }
    }

    /**
     * Monitor BlockPistonExtend events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        if (!ExperienceConfig.getInstance().isPistonCheatingPrevented()) {
            return;
        }

        final BlockFace direction = event.getDirection();

        for (final Block block : event.getBlocks()) {
            mcMMO.p.getFoliaLib().getScheduler().runAtLocation(block.getLocation(), t -> {
                final Block movedBlock = block.getRelative(direction);

                if (BlockUtils.isWithinWorldBounds(movedBlock)) {
                    BlockUtils.setUnnaturalBlock(movedBlock);
                }
            });
        }
    }

    /**
     * Monitor BlockPistonRetract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        if (!ExperienceConfig.getInstance().isPistonCheatingPrevented()) {
            return;
        }

        // Get opposite direction so we get correct block
        BlockFace direction = event.getDirection();
        Block movedBlock = event.getBlock().getRelative(direction);

        //Spigot makes bad things happen in its API
        if (BlockUtils.isWithinWorldBounds(movedBlock)) {
            BlockUtils.setUnnaturalBlock(movedBlock);
        }

        for (Block block : event.getBlocks()) {
            if (BlockUtils.isWithinWorldBounds(block) && BlockUtils.isWithinWorldBounds(
                    block.getRelative(direction))) {
                Block relativeBlock = block.getRelative(direction);
                BlockUtils.setUnnaturalBlock(relativeBlock);
            }
        }
    }

    /**
     * Monitor blocks formed by entities (snowmen) Does not seem to monitor stuff like a falling
     * block creating a new block
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBlockFormEvent(EntityBlockFormEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        BlockState blockState = event.getNewState();

        if (ExperienceConfig.getInstance().isSnowExploitPrevented() && BlockUtils.shouldBeWatched(
                blockState)) {
            Block block = blockState.getBlock();

            if (BlockUtils.isWithinWorldBounds(block)) {
                BlockUtils.setUnnaturalBlock(block);
            }
        }
    }

    /*
     * Does not monitor stuff like a falling block replacing a liquid
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event) {
        World world = event.getBlock().getWorld();

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(world)) {
            return;
        }

        if (ExperienceConfig.getInstance().preventStoneLavaFarming()) {
            BlockState newState = event.getNewState();

            if (newState.getType() != Material.OBSIDIAN
                    && ExperienceConfig.getInstance().doesBlockGiveSkillXP(
                    PrimarySkillType.MINING, newState.getType())) {
                Block block = newState.getBlock();
                if (BlockUtils.isWithinWorldBounds(block)) {
                    BlockUtils.setUnnaturalBlock(block);
                }
            }
        }
    }

    /**
     * Monitor BlockPlace events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        BlockState blockState = event.getBlock().getState();
        Block block = blockState.getBlock();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
//      if (!Tag.LOGS.isTagged(event.getBlockReplacedState().getType()) || !Tag.LOGS.isTagged(event.getBlockPlaced().getType()))

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(block.getWorld())) {
            return;
        }

        if (BlockUtils.isWithinWorldBounds(block)) {
            //NOTE: BlockMultiPlace has its own logic so don't handle anything that would overlap
            if (!(event instanceof BlockMultiPlaceEvent)) {
                BlockUtils.setUnnaturalBlock(block);
            }
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return;
        }

        if (blockState.getType() == Repair.anvilMaterial && mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.REPAIR)) {
            mmoPlayer.getRepairManager().placedAnvilCheck();
        } else if (blockState.getType() == Salvage.anvilMaterial && mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.SALVAGE)) {
            mmoPlayer.getSalvageManager().placedAnvilCheck();
        }
    }

    /**
     * Monitor BlockMultiPlace events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        for (BlockState replacedBlockState : event.getReplacedBlockStates()) {
            BlockState blockState = replacedBlockState.getBlock().getState();
            Block block = blockState.getBlock();

            /* Check if the blocks placed should be monitored so they do not give out XP in the future */
            if (BlockUtils.isWithinWorldBounds(block)) {
                //Updated: 10/5/2021
                //Note: For some reason Azalea trees trigger this event but no other tree does (as of 10/5/2021) but if this changes in the future we may need to update this
                if (BlockUtils.isPartOfTree(event.getBlockPlaced())) {
                    return;
                }

                //Track unnatural blocks
                for (BlockState replacedState : event.getReplacedBlockStates()) {
                    BlockUtils.setUnnaturalBlock(replacedState.getBlock());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(world)) {
            return;
        }

        // Minecraft is dumb, the events still throw when a plant "grows" higher than the max block height.  Even though no new block is created
        if (BlockUtils.isWithinWorldBounds(block)) {
            mcMMO.getUserBlockTracker().setEligible(block);
        }
    }

    /**
     * Monitor BlockBreak events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        /* WORLD BLACKLIST CHECK */
        final Block block = event.getBlock();

        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        if (WorldBlacklist.isWorldBlacklisted(block.getWorld())) {
            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                BlockUtils.cleanupBlockMetadata(block);
                return;
            }
        }

        final Location location = block.getLocation();

        /* ALCHEMY - Cancel any brew in progress for that BrewingStand */
        if (block.getType() == Material.BREWING_STAND) {
            final BlockState blockState = block.getState();
            if (blockState instanceof BrewingStand && Alchemy.brewingStandMap.containsKey(
                    location)) {
                Alchemy.brewingStandMap.get(location).cancelBrew();
            }
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Check if profile is loaded
        if (mmoPlayer == null) {
            /* Remove metadata from placed watched blocks */

            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        /* HERBALISM */
        if (BlockUtils.affectedByGreenTerra(block)) {
            HerbalismManager herbalismManager = mmoPlayer.getHerbalismManager();

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (mcMMO.p.getSkillTools()
                    .doesPlayerHaveSkillPermission(player, PrimarySkillType.HERBALISM)) {
                herbalismManager.processHerbalismBlockBreakEvent(event);
            }
            /*
             * We return here so that we don't unmark any affected blocks
             * due to special checks managing this on their own:
             */
            return;
        }

        /* MINING */
        else if (BlockUtils.affectedBySuperBreaker(block)
                && (ItemUtils.isPickaxe(heldItem) || ItemUtils.isHoe(heldItem))
                && mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.MINING)
                && !mcMMO.getUserBlockTracker().isIneligible(block)) {
            MiningManager miningManager = mmoPlayer.getMiningManager();
            miningManager.miningBlockCheck(block);
        }

        /* WOOD CUTTING */
        else if (BlockUtils.hasWoodcuttingXP(block) && ItemUtils.isAxe(heldItem)
                && mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.WOODCUTTING)
                && !mcMMO.getUserBlockTracker().isIneligible(block)) {
            WoodcuttingManager woodcuttingManager = mmoPlayer.getWoodcuttingManager();
            if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                woodcuttingManager.processTreeFeller(block);
            } else {
                //Check for XP
                woodcuttingManager.processWoodcuttingBlockXP(block);

                //Check for bonus drops
                woodcuttingManager.processBonusDropCheck(block);
            }
        }

        /* EXCAVATION */
        else if (BlockUtils.affectedByGigaDrillBreaker(block)
                && ItemUtils.isShovel(heldItem)
                && mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.EXCAVATION)
                && !mcMMO.getUserBlockTracker().isIneligible(block)) {
            final ExcavationManager excavationManager = mmoPlayer.getExcavationManager();
            excavationManager.excavationBlockCheck(block);

            if (mmoPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(block);
            }
        }

        /* Remove metadata from placed watched blocks */
        BlockUtils.cleanupBlockMetadata(block);
    }

    /**
     * Handle BlockBreak events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakHigher(BlockBreakEvent event) {
        if (event instanceof FakeEvent) {
            return;
        }

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                return;
            }
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        BlockState blockState = event.getBlock().getState();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (ItemUtils.isSword(heldItem)) {
            HerbalismManager herbalismManager = UserManager.getPlayer(player).getHerbalismManager();

            if (herbalismManager.canUseHylianLuck()) {
                if (herbalismManager.processHylianLuck(blockState)) {
                    blockState.update(true);
                    event.setCancelled(true);
                } else if (blockState.getType() == Material.FLOWER_POT) {
                    blockState.setType(Material.AIR);
                    blockState.update(true);
                    event.setCancelled(true);
                }
            }
        }
        /*else if (!heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
            SmeltingManager smeltingManager = UserManager.getPlayer(player).getSmeltingManager();

            if (smeltingManager.canUseFluxMining(blockState)) {
                if (smeltingManager.processFluxMining(blockState)) {
                    blockState.update(true);
                    event.setCancelled(true);
                }
            }
        }*/
    }

    /**
     * Monitor BlockDamage events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                return;
            }
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }
        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (BlockUtils.canActivateAbilities(block)) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (mmoPlayer.getToolPreparationMode(ToolType.HOE)
                    && ItemUtils.isHoe(heldItem)
                    && (BlockUtils.affectedByGreenTerra(block)
                    || BlockUtils.canMakeMossy(block))
                    && Permissions.greenTerra(player)) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            } else if (mmoPlayer.getToolPreparationMode(ToolType.AXE) && ItemUtils.isAxe(heldItem)
                    && BlockUtils.hasWoodcuttingXP(block) && Permissions.treeFeller(player)) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.WOODCUTTING);
            } else if (mmoPlayer.getToolPreparationMode(ToolType.PICKAXE) && ItemUtils.isPickaxe(
                    heldItem) && BlockUtils.affectedBySuperBreaker(block)
                    && Permissions.superBreaker(player)) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.MINING);
            } else if (mmoPlayer.getToolPreparationMode(ToolType.SHOVEL) && ItemUtils.isShovel(
                    heldItem) && BlockUtils.affectedByGigaDrillBreaker(block)
                    && Permissions.gigaDrillBreaker(player)) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.EXCAVATION);
            } else if (mmoPlayer.getToolPreparationMode(ToolType.FISTS)
                    && heldItem.getType() == Material.AIR && (
                    BlockUtils.affectedByGigaDrillBreaker(block)
                            || mcMMO.getMaterialMapStore().isGlass(block.getType())
                            || block.getType() == Material.SNOW
                            || BlockUtils.affectedByBlockCracker(block) && Permissions.berserk(
                            player))) {
                mmoPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);

                if (mmoPlayer.getAbilityMode(SuperAbilityType.BERSERK)) {
                    if (SuperAbilityType.BERSERK.blockCheck(block) && EventUtils.simulateBlockBreak(
                            block, player)) {
                        event.setInstaBreak(true);

                        if (block.getType().getKey().getKey().contains("glass")) {
                            SoundManager.worldSendSound(player.getWorld(), block.getLocation(),
                                    SoundType.GLASS);
                        } else {
                            SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
                        }
                    }
                }
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mmoPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER) && BlockUtils.hasWoodcuttingXP(
                block) && mcMMO.p.getGeneralConfig().getTreeFellerSoundsEnabled()) {
            SoundManager.sendSound(player, block.getLocation(), SoundType.FIZZ);
        }
    }

    /**
     * Handle BlockDamage events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamageHigher(BlockDamageEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld())) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                return;
            }
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        if (mmoPlayer == null) {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mmoPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA) && BlockUtils.canMakeMossy(
                block)) {
            mmoPlayer.getHerbalismManager().processGreenTerraBlockConversion(block);
        } else if (mmoPlayer.getAbilityMode(SuperAbilityType.BERSERK) && (
                heldItem.getType() == Material.AIR || mcMMO.p.getGeneralConfig()
                        .getUnarmedItemsAsUnarmed())) {
            if (mmoPlayer.getUnarmedManager().canUseBlockCracker()
                    && BlockUtils.affectedByBlockCracker(block)) {
                if (EventUtils.simulateBlockBreak(block, player)) {
                    mmoPlayer.getUnarmedManager().blockCrackerCheck(block);
                }
            } else if (!event.getInstaBreak() && SuperAbilityType.BERSERK.blockCheck(block)
                    && EventUtils.simulateBlockBreak(block, player)) {
                event.setInstaBreak(true);

                if (block.getType().getKey().getKey().contains("glass")) {
                    SoundManager.worldSendSound(player.getWorld(), block.getLocation(),
                            SoundType.GLASS);
                } else {
                    SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
                }
            }
        } else if (mmoPlayer.getWoodcuttingManager().canUseLeafBlower(heldItem)
                && BlockUtils.isNonWoodPartOfTree(block) && EventUtils.simulateBlockBreak(block,
                player)) {
            event.setInstaBreak(true);
            SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamageCleanup(BlockDamageEvent event) {
        Player player = event.getPlayer();
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        cleanupAbilityTools(mmoPlayer, event.getBlock(), heldItem);

        debugStickDump(player, event.getBlock());
    }

    //TODO: Rewrite this
    //TODO: Convert into locale strings
    private void debugStickDump(Player player, Block block) {
        //Profile not loaded
        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return;
        }

        final BlockState blockState = block.getState();

        if (mmoPlayer.isDebugMode()) {
            if (mcMMO.getUserBlockTracker().isIneligible(blockState)) {
                player.sendMessage(
                        "[mcMMO DEBUG] This block is not natural and does not reward treasures/XP");
            } else {
                player.sendMessage("[mcMMO DEBUG] This block is considered natural by mcMMO");
                mmoPlayer.getExcavationManager().printExcavationDebug(player, block);
            }

            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (WorldGuardManager.getInstance().hasMainFlag(player)) {
                    player.sendMessage(
                            "[mcMMO DEBUG] World Guard main flag is permitted for this player in this region");
                } else {
                    player.sendMessage(
                            "[mcMMO DEBUG] World Guard main flag is DENIED for this player in this region");
                }

                if (WorldGuardManager.getInstance().hasXPFlag(player)) {
                    player.sendMessage(
                            "[mcMMO DEBUG] World Guard xp flag is permitted for this player in this region");
                } else {
                    player.sendMessage(
                            "[mcMMO DEBUG] World Guard xp flag is not permitted for this player in this region");
                }
            }

            if (blockState instanceof Furnace || blockState instanceof BrewingStand) {
                if (ContainerMetadataUtils.isContainerOwned(blockState)) {
                    player.sendMessage("[mcMMO DEBUG] This container has a registered owner");
                    final OfflinePlayer furnacePlayer = ContainerMetadataUtils.getContainerOwner(
                            blockState);
                    if (furnacePlayer != null) {
                        player.sendMessage("[mcMMO DEBUG] This container is owned by player "
                                + furnacePlayer.getName());
                    }
                } else {
                    player.sendMessage(
                            "[mcMMO DEBUG] This container does not have a registered owner");
                }
            }

            if (ExperienceConfig.getInstance().isExperienceBarsEnabled()) {
                player.sendMessage(
                        "[mcMMO DEBUG] XP bars are enabled, however you should check per-skill settings to make sure those are enabled.");
            }

            player.sendMessage(
                    ChatColor.RED + "You can turn this debug info off by typing " + ChatColor.GOLD
                            + "/mmodebug");
        }
    }

    /**
     * Clean up ability tools after a block break event.
     *
     * @param mmoPlayer The player
     * @param block The block
     * @param heldItem The item in the player's hand
     */
    private void cleanupAbilityTools(McMMOPlayer mmoPlayer, Block block, ItemStack heldItem) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            if ((ItemUtils.isPickaxe(heldItem)
                    && !mmoPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER))
                    || (ItemUtils.isShovel(heldItem)
                    && !mmoPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER))) {
                SkillUtils.removeAbilityBuff(heldItem);
            }
        } else {
            if ((mmoPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER)
                    && !BlockUtils.affectedBySuperBreaker(block))
                    || (mmoPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER)
                    && !BlockUtils.affectedByGigaDrillBreaker(block))) {
                SkillUtils.removeAbilityBoostsFromInventory(mmoPlayer.getPlayer());
            }
        }
    }

}
