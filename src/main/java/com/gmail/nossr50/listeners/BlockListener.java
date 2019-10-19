package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
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
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
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
import org.bukkit.metadata.MetadataValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockListener implements Listener {
    private final mcMMO plugin;

    public BlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItemEvent(BlockDropItemEvent event)
    {
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

        if(uniqueMaterials.size() > 1) {
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
                if(!Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.MINING, is.getType())
                        && !Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.HERBALISM, is.getType())
                        && !Config.getInstance().getDoubleDropsEnabled(PrimarySkillType.WOODCUTTING, is.getType()))
                    continue;

                //If we suspect TEs might be duped only reward block
                if(dontRewardTE) {
                    if(!is.getType().isBlock()) {
                        continue;
                    }
                }

                if (event.getBlock().getMetadata(mcMMO.BONUS_DROPS_METAKEY).size() > 0) {
                    BonusDropMeta bonusDropMeta = (BonusDropMeta) event.getBlock().getMetadata(mcMMO.BONUS_DROPS_METAKEY).get(0);
                    int bonusCount = bonusDropMeta.asInt();

                    for (int i = 0; i < bonusCount; i++) {
                        event.getBlock().getWorld().dropItemNaturally(event.getBlockState().getLocation(), is);
                    }
                }
            }
        }

        if(event.getBlock().hasMetadata(mcMMO.BONUS_DROPS_METAKEY))
            event.getBlock().removeMetadata(mcMMO.BONUS_DROPS_METAKEY, plugin);
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
        Block movedBlock = event.getBlock();
        movedBlock = movedBlock.getRelative(direction, 2);

        for (Block b : event.getBlocks()) {
            if (BlockUtils.shouldBeWatched(b.getState())) {
                movedBlock = b.getRelative(direction);
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
        mcMMO.getPlaceStore().setTrue(movedBlock);

        for (Block block : event.getBlocks()) {
            movedBlock = block.getRelative(direction);
            mcMMO.getPlaceStore().setTrue(movedBlock);
        }
    }

    /**
     * Monitor blocks formed by entities (snowmen)
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

        if(BlockUtils.shouldBeWatched(blockState))
        {
            mcMMO.getPlaceStore().setTrue(blockState.getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event)
    {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        if(ExperienceConfig.getInstance().preventStoneLavaFarming())
        {
            if(event.getNewState().getType() != Material.OBSIDIAN
                    && BlockUtils.shouldBeWatched(event.getNewState())
                    && ExperienceConfig.getInstance().doesBlockGiveSkillXP(PrimarySkillType.MINING, event.getNewState().getBlockData()))
            {
                mcMMO.getPlaceStore().setTrue(event.getNewState());
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
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        BlockState blockState = event.getBlock().getState();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockUtils.shouldBeWatched(blockState)) {
            // Don't count de-barking wood
            if (!Tag.LOGS.isTagged(event.getBlockReplacedState().getType()) || !Tag.LOGS.isTagged(event.getBlockPlaced().getType()))
                mcMMO.getPlaceStore().setTrue(blockState);
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if(mcMMOPlayer == null)
            return;

        if (blockState.getType() == Repair.anvilMaterial && PrimarySkillType.REPAIR.getPermissions(player)) {
            mcMMOPlayer.getRepairManager().placedAnvilCheck();
        }
        else if (blockState.getType() == Salvage.anvilMaterial && PrimarySkillType.SALVAGE.getPermissions(player)) {
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
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        for (BlockState replacedBlockState : event.getReplacedBlockStates())
        {
            BlockState blockState = replacedBlockState.getBlock().getState();

            /* Check if the blocks placed should be monitored so they do not give out XP in the future */
            if (BlockUtils.shouldBeWatched(blockState)) {
                mcMMO.getPlaceStore().setTrue(blockState);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event)
    {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        BlockState blockState = event.getBlock().getState();

        if (!BlockUtils.shouldBeWatched(blockState)) {
            return;
        }

        mcMMO.getPlaceStore().setFalse(blockState);
    }

    /**
     * Monitor BlockBreak events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        BlockState blockState = event.getBlock().getState();
        Location location = blockState.getLocation();

        if (!BlockUtils.shouldBeWatched(blockState)) {
            return;
        }

        /* ALCHEMY - Cancel any brew in progress for that BrewingStand */
        if (blockState instanceof BrewingStand && Alchemy.brewingStandMap.containsKey(location)) {
            Alchemy.brewingStandMap.get(location).cancelBrew();
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        //Check if profile is loaded
        if(mcMMOPlayer == null)
            return;

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
            if (PrimarySkillType.HERBALISM.getPermissions(player)) {
                herbalismManager.processHerbalismBlockBreakEvent(event);
            }
            /*
             * We return here so that we don't unmark any affected blocks
             * due to special checks managing this on their own:
             */
            return;
        }

        /* MINING */
        else if (BlockUtils.affectedBySuperBreaker(blockState) && ItemUtils.isPickaxe(heldItem) && PrimarySkillType.MINING.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            MiningManager miningManager = mcMMOPlayer.getMiningManager();
            miningManager.miningBlockCheck(blockState);
        }

        /* WOOD CUTTING */
        else if (BlockUtils.isLog(blockState) && ItemUtils.isAxe(heldItem) && PrimarySkillType.WOODCUTTING.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            WoodcuttingManager woodcuttingManager = mcMMOPlayer.getWoodcuttingManager();
            if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                woodcuttingManager.processTreeFeller(blockState);
            }
            else {
                woodcuttingManager.woodcuttingBlockCheck(blockState);
            }
        }

        /* EXCAVATION */
        else if (BlockUtils.affectedByGigaDrillBreaker(blockState) && ItemUtils.isShovel(heldItem) && PrimarySkillType.EXCAVATION.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            ExcavationManager excavationManager = mcMMOPlayer.getExcavationManager();
            excavationManager.excavationBlockCheck(blockState);

            if (mcMMOPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(blockState);
            }
        }

        /* Remove metadata from placed watched blocks */
        mcMMO.getPlaceStore().setFalse(blockState);
    }

    /**
     * Handle BlockBreak events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakHigher(BlockBreakEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getBlock().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockBreakEvent) {
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

        if (Herbalism.isRecentlyRegrown(blockState)) {
            event.setCancelled(true);
            return;
        }

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
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && ItemUtils.isAxe(heldItem) && BlockUtils.isLog(blockState) && Permissions.treeFeller(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.WOODCUTTING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.PICKAXE) && ItemUtils.isPickaxe(heldItem) && BlockUtils.affectedBySuperBreaker(blockState) && Permissions.superBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.MINING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.SHOVEL) && ItemUtils.isShovel(heldItem) && BlockUtils.affectedByGigaDrillBreaker(blockState) && Permissions.gigaDrillBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.EXCAVATION);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockUtils.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW || BlockUtils.affectedByBlockCracker(blockState) && Permissions.berserk(player))) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mcMMOPlayer.getAbilityMode(SuperAbilityType.TREE_FELLER) && BlockUtils.isLog(blockState) && Config.getInstance().getTreeFellerSoundsEnabled()) {
            SoundManager.sendSound(player, blockState.getLocation(), SoundType.FIZZ);
        }
    }

    private Player getPlayerFromFurnace(Block furnaceBlock) {
        List<MetadataValue> metadata = furnaceBlock.getMetadata(mcMMO.furnaceMetadataKey);

        if (metadata.isEmpty()) {
            return null;
        }

        return plugin.getServer().getPlayerExact(metadata.get(0).asString());
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
        else if (mcMMOPlayer.getAbilityMode(SuperAbilityType.BERSERK) && (heldItem.getType() == Material.AIR || Config.getInstance().getUnarmedItemsAsUnarmed())) {
            if (mcMMOPlayer.getUnarmedManager().canUseBlockCracker() && BlockUtils.affectedByBlockCracker(blockState)) {
                if (EventUtils.simulateBlockBreak(block, player, true) && mcMMOPlayer.getUnarmedManager().blockCrackerCheck(blockState)) {
                    blockState.update();
                }
            }
            else if (SuperAbilityType.BERSERK.blockCheck(block.getState()) && EventUtils.simulateBlockBreak(block, player, true)) {
                event.setInstaBreak(true);
                SoundManager.sendSound(player, block.getLocation(), SoundType.POP);
            }
        }
        else if (mcMMOPlayer.getWoodcuttingManager().canUseLeafBlower(heldItem) && BlockUtils.isLeaves(blockState) && EventUtils.simulateBlockBreak(block, player, true)) {
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

            if(blockState instanceof Furnace)
            {
                Furnace furnace = (Furnace) blockState;
                if(furnace.hasMetadata(mcMMO.furnaceMetadataKey))
                {
                    player.sendMessage("[mcMMO DEBUG] This furnace has a registered owner");
                    Player furnacePlayer = getPlayerFromFurnace(furnace.getBlock());
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
                SkillUtils.handleAbilitySpeedDecrease(player);
            }
        }
    }

}
