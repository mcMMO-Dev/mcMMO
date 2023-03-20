package com.gmail.nossr50.listeners;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakeEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;

public class BlockListener implements Listener {
    private final mcMMO plugin;

    public BlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockDropItemEvent(BlockDropItemEvent event)
    {
        //Make sure we clean up metadata on these blocks
        if(event.isCancelled()) {
            if(event.getBlock().hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS))
                event.getBlock().removeMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS, plugin);
            return;
        }

        int tileEntityTolerance = 1;

        // beetroot hotfix, potentially other plants may need this fix
        if(event.getBlockState().getType() == Material.BEETROOTS)
            tileEntityTolerance = 2;

        //Track how many "things" are being dropped
        HashSet<Material> uniqueMaterials = new HashSet<>();
        boolean dontRewardTE = false; //If we suspect TEs are mixed in with other things don't reward bonus drops for anything that isn't a block
        int blockCount = 0;

        for(Item item : event.getItems()) {
            //Track unique materials
            uniqueMaterials.add(item.getItemStack().getType());

            //Count blocks as a second failsafe
            if(item.getItemStack().getType().isBlock())
                blockCount++;
        }

        if(uniqueMaterials.size() > tileEntityTolerance) {
            //Too many things are dropping, assume tile entities might be duped
            //Technically this would also prevent something like coal from being bonus dropped if you placed a TE above a coal ore when mining it but that's pretty edge case and this is a good solution for now
            dontRewardTE = true;
        }

        //If there are more than one block in the item list we can't really trust it and will back out of rewarding bonus drops
        if(blockCount <= 1) {
            for(Item item : event.getItems())
            {
                ItemStack is = new ItemStack(item.getItemStack());

                if(is.getAmount() <= 0)
                    continue;

                //TODO: Ignore this abomination its rewritten in 2.2
                if(!mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.MINING, is.getType())
                        && !mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.HERBALISM, is.getType())
                        && !mcMMO.p.getGeneralConfig().getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, is.getType()))
                    continue;

                //If we suspect TEs might be duped only reward block
                if(dontRewardTE) {
                    if(!is.getType().isBlock()) {
                        continue;
                    }
                }

                if (event.getBlock().getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).size() > 0) {
                    BonusDropMeta bonusDropMeta = (BonusDropMeta) event.getBlock().getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).get(0);
                    int bonusCount = bonusDropMeta.asInt();

                    for (int i = 0; i < bonusCount; i++) {
                        Misc.spawnItemNaturally(event.getPlayer(), event.getBlockState().getLocation(), is, ItemSpawnReason.BONUS_DROPS);
                    }
                }
            }
        }

        if(event.getBlock().hasMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS))
            event.getBlock().removeMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS, plugin);
    }

    /**
     * Monitor BlockPistonExtend events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        if(!ExperienceConfig.getInstance().isPistonCheatingPrevented()) {
            return;
        }

        BlockFace direction = event.getDirection();
        Block movedBlock;
        for (Block block : event.getBlocks()) {
            movedBlock = block.getRelative(direction);

            if(BlockUtils.isWithinWorldBounds(movedBlock)) {
                mcMMO.getPlaceStore().setTrue(movedBlock);
            }
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
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        if(!ExperienceConfig.getInstance().isPistonCheatingPrevented()) {
            return;
        }

        // Get opposite direction so we get correct block
        BlockFace direction = event.getDirection();
        Block movedBlock = event.getBlock().getRelative(direction);

        //Spigot makes bad things happen in its API
        if(BlockUtils.isWithinWorldBounds(movedBlock)) {
            mcMMO.getPlaceStore().setTrue(movedBlock);
        }

        for (Block block : event.getBlocks()) {
            if(BlockUtils.isWithinWorldBounds(block)) {
                mcMMO.getPlaceStore().setTrue(block.getRelative(direction));
            }
        }
    }

    /**
     * Monitor blocks formed by entities (snowmen)
     * Does not seem to monitor stuff like a falling block creating a new block
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBlockFormEvent(EntityBlockFormEvent event)
    {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;


        BlockState blockState = event.getNewState();

        if(ExperienceConfig.getInstance().isSnowExploitPrevented() && BlockUtils.shouldBeWatched(blockState)) {
            Block block = blockState.getBlock();

            if(BlockUtils.isWithinWorldBounds(block)) {
                mcMMO.getPlaceStore().setTrue(block);
            }
        }
    }

    /*
     * Does not monitor stuff like a falling block replacing a liquid
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event)
    {
        World world = event.getBlock().getWorld();

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(world))
            return;

        if(ExperienceConfig.getInstance().preventStoneLavaFarming()) {
            BlockState newState = event.getNewState();

            if(newState.getType() != Material.OBSIDIAN && ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.MINING, newState.getBlockData())) {
                if(BlockUtils.isWithinWorldBounds(newState.getBlock())) {
                    mcMMO.getPlaceStore().setTrue(newState);
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
        if(WorldBlacklist.isWorldBlacklisted(block.getWorld())) {
            return;
        }

        if(BlockUtils.isWithinWorldBounds(block)) {
            //NOTE: BlockMultiPlace has its own logic so don't handle anything that would overlap
            if (!(event instanceof BlockMultiPlaceEvent)) {
                mcMMO.getPlaceStore().setTrue(blockState);
            }
        }


        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if(mcMMOPlayer == null)
            return;

        if (blockState.getType() == Repair.anvilMaterial && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.REPAIR)) {
            mcMMOPlayer.getRepairManager().placedAnvilCheck();
        }
        else if (blockState.getType() == Salvage.anvilMaterial && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.SALVAGE)) {
            mcMMOPlayer.getSalvageManager().placedAnvilCheck();
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
            if(BlockUtils.isWithinWorldBounds(block)) {
                //Updated: 10/5/2021
                //Note: For some reason Azalea trees trigger this event but no other tree does (as of 10/5/2021) but if this changes in the future we may need to update this
                if(BlockUtils.isPartOfTree(event.getBlockPlaced())) {
                    return;
                }

                //Track unnatural blocks
                for(BlockState replacedStates : event.getReplacedBlockStates()) {
                    mcMMO.getPlaceStore().setTrue(replacedStates);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(world))
            return;

        // Minecraft is dumb, the events still throw when a plant "grows" higher than the max block height.  Even though no new block is created
        if(BlockUtils.isWithinWorldBounds(block)) {
            mcMMO.getPlaceStore().setFalse(block);
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
        Block block = event.getBlock();

        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        if(WorldBlacklist.isWorldBlacklisted(block.getWorld())) {
            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                BlockUtils.cleanupBlockMetadata(block);
                return;
            }
        }

        BlockState blockState = block.getState();
        Location location = blockState.getLocation();

//        if (!BlockUtils.shouldBeWatched(blockState)) {
//            return;
//        }

        /* ALCHEMY - Cancel any brew in progress for that BrewingStand */
        if (blockState instanceof BrewingStand && Alchemy.brewingStandMap.containsKey(location)) {
            Alchemy.brewingStandMap.get(location).cancelBrew();
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Check if profile is loaded
        if(mcMMOPlayer == null) {
            /* Remove metadata from placed watched blocks */

            BlockUtils.cleanupBlockMetadata(block);
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        /* HERBALISM */
        if (BlockUtils.affectedByGreenTerra(blockState)) {
            HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.HERBALISM)) {
                herbalismManager.processHerbalismBlockBreakEvent(event);
            }
            /*
             * We return here so that we don't unmark any affected blocks
             * due to special checks managing this on their own:
             */
            return;
        }

        /* MINING */
        else if (BlockUtils.affectedBySuperBreaker(blockState)
                && (ItemUtils.isPickaxe(heldItem) || ItemUtils.isHoe(heldItem))
                && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.MINING)
                && !mcMMO.getPlaceStore().isTrue(blockState)) {
            MiningManager miningManager = mcMMOPlayer.getMiningManager();
            miningManager.miningBlockCheck(blockState);
        }

        /* WOOD CUTTING */
        else if (BlockUtils.hasWoodcuttingXP(blockState) && ItemUtils.isAxe(heldItem)
                && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.WOODCUTTING) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            WoodcuttingManager woodcuttingManager = mcMMOPlayer.getWoodcuttingManager();
            if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                woodcuttingManager.processTreeFeller(blockState);
            }
            else {
                //Check for XP
                woodcuttingManager.processWoodcuttingBlockXP(blockState);

                //Check for bonus drops
                woodcuttingManager.processHarvestLumber(blockState);
            }
        }

        /* EXCAVATION */
        else if (BlockUtils.affectedByGigaDrillBreaker(blockState) && ItemUtils.isShovel(heldItem) && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.EXCAVATION) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            ExcavationManager excavationManager = mcMMOPlayer.getExcavationManager();
            excavationManager.excavationBlockCheck(blockState);

            if (mcMMOPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(blockState);
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
        if(event instanceof FakeEvent)
            return;

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
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
                }
                else if (blockState.getType() == Material.FLOWER_POT) {
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
        Player player = event.getPlayer();
        BlockState blockState = event.getBlock().getState();

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }
        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if(mcMMOPlayer == null)
        {
            return;
        }

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (BlockUtils.canActivateAbilities(blockState)) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (mcMMOPlayer.getToolPreparationMode(ToolType.HOE) && ItemUtils.isHoe(heldItem) && (BlockUtils.affectedByGreenTerra(blockState) || BlockUtils.canMakeMossy(blockState)) && Permissions.greenTerra(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && ItemUtils.isAxe(heldItem) && BlockUtils.hasWoodcuttingXP(blockState) && Permissions.treeFeller(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.WOODCUTTING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.PICKAXE) && ItemUtils.isPickaxe(heldItem) && BlockUtils.affectedBySuperBreaker(blockState) && Permissions.superBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.MINING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.SHOVEL) && ItemUtils.isShovel(heldItem) && BlockUtils.affectedByGigaDrillBreaker(blockState) && Permissions.gigaDrillBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.EXCAVATION);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockUtils.affectedByGigaDrillBreaker(blockState)
                    || mcMMO.getMaterialMapStore().isGlass(blockState.getType())
                    || blockState.getType() == Material.SNOW
                    || BlockUtils.affectedByBlockCracker(blockState) && Permissions.berserk(player))) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);

                if(mcMMOPlayer.getAbilityMode(SuperAbilityType.BERSERK)) {
                    if (SuperAbilityType.BERSERK.blockCheck(blockState) && EventUtils.simulateBlockBreak(blockState.getBlock(), player, true)) {
                        event.setInstaBreak(true);

                        if(blockState.getType().getKey().getKey().contains("glass")) {
                            SoundManager.worldSendSound(player.getWorld(), blockState.getLocation(), SoundType.GLASS);
                        } else {
                            SoundManager.sendSound(player, blockState.getLocation(), SoundType.POP);
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
        if (mcMMOPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER) && BlockUtils.hasWoodcuttingXP(blockState) && mcMMO.p.getGeneralConfig().getTreeFellerSoundsEnabled()) {
            SoundManager.sendSound(player, blockState.getLocation(), SoundType.FIZZ);
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
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        BlockState blockState = block.getState();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mcMMOPlayer.getAbilityMode(SuperAbilityType.GREEN_TERRA) && BlockUtils.canMakeMossy(blockState)) {
            if (mcMMOPlayer.getHerbalismManager().processGreenTerraBlockConversion(blockState)) {
                blockState.update(true);
            }
        }
        else if (mcMMOPlayer.getAbilityMode(SuperAbilityType.BERSERK) && (heldItem.getType() == Material.AIR || mcMMO.p.getGeneralConfig().getUnarmedItemsAsUnarmed())) {
            if (mcMMOPlayer.getUnarmedManager().canUseBlockCracker() && BlockUtils.affectedByBlockCracker(blockState)) {
                if (EventUtils.simulateBlockBreak(block, player, true) && mcMMOPlayer.getUnarmedManager().blockCrackerCheck(blockState)) {
                    blockState.update();
                }
            }
            else if (!event.getInstaBreak() && SuperAbilityType.BERSERK.blockCheck(blockState) && EventUtils.simulateBlockBreak(block, player, true)) {
                event.setInstaBreak(true);

                if(blockState.getType().getKey().getKey().contains("glass")) {
                    SoundManager.worldSendSound(player.getWorld(), block.getLocation(), SoundType.GLASS);
                } else {
                    SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
                }
            }
        }
        else if (mcMMOPlayer.getWoodcuttingManager().canUseLeafBlower(heldItem) && BlockUtils.isNonWoodPartOfTree(blockState) && EventUtils.simulateBlockBreak(block, player, true)) {
            event.setInstaBreak(true);
            SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamageCleanup(BlockDamageEvent event) {
        Player player = event.getPlayer();
        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        BlockState blockState = event.getBlock().getState();

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        cleanupAbilityTools(player, mcMMOPlayer, blockState, heldItem);

        debugStickDump(player, blockState);
    }

    //TODO: Rewrite this
    //TODO: Convert into locale strings
    private void debugStickDump(Player player, BlockState blockState) {
        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        if(UserManager.getPlayer(player).isDebugMode())
        {
            if(mcMMO.getPlaceStore().isTrue(blockState))
                player.sendMessage("[mcMMO DEBUG] This block is not natural and does not reward treasures/XP");
            else
            {
                player.sendMessage("[mcMMO DEBUG] This block is considered natural by mcMMO");
                UserManager.getPlayer(player).getExcavationManager().printExcavationDebug(player, blockState);
            }

            if(WorldGuardUtils.isWorldGuardLoaded())
            {
                if(WorldGuardManager.getInstance().hasMainFlag(player))
                    player.sendMessage("[mcMMO DEBUG] World Guard main flag is permitted for this player in this region");
                else
                    player.sendMessage("[mcMMO DEBUG] World Guard main flag is DENIED for this player in this region");

                if(WorldGuardManager.getInstance().hasXPFlag(player))
                    player.sendMessage("[mcMMO DEBUG] World Guard xp flag is permitted for this player in this region");
                else
                    player.sendMessage("[mcMMO DEBUG] World Guard xp flag is not permitted for this player in this region");
            }

            if(blockState instanceof Furnace furnace)
            {
                if(mcMMO.getSmeltingTracker().isFurnaceOwned(furnace))
                {
                    player.sendMessage("[mcMMO DEBUG] This furnace has a registered owner");
                    OfflinePlayer furnacePlayer = mcMMO.getSmeltingTracker().getFurnaceOwner(furnace);
                    if(furnacePlayer != null)
                    {
                        player.sendMessage("[mcMMO DEBUG] This furnace is owned by player "+furnacePlayer.getName());
                    }
                }
                else
                    player.sendMessage("[mcMMO DEBUG] This furnace does not have a registered owner");
            }

            if(ExperienceConfig.getInstance().isExperienceBarsEnabled())
                player.sendMessage("[mcMMO DEBUG] XP bars are enabled, however you should check per-skill settings to make sure those are enabled.");

            player.sendMessage(ChatColor.RED+"You can turn this debug info off by typing "+ChatColor.GOLD+"/mmodebug");
        }
    }

    private void cleanupAbilityTools(Player player, McMMOPlayer mcMMOPlayer, BlockState blockState, ItemStack heldItem) {
        if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
            if ((ItemUtils.isPickaxe(heldItem) && !mcMMOPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER)) || (ItemUtils.isShovel(heldItem) && !mcMMOPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER))) {
                SkillUtils.removeAbilityBuff(heldItem);
            }
        } else {
            if ((mcMMOPlayer.getAbilityMode(SuperAbilityType.SUPER_BREAKER) && !BlockUtils.affectedBySuperBreaker(blockState)) || (mcMMOPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER) && !BlockUtils.affectedByGigaDrillBreaker(blockState))) {
                SkillUtils.removeAbilityBoostsFromInventory(player);
            }
        }
    }

}
