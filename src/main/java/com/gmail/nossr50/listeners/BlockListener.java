package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.runnables.PistonTrackerTask;
import com.gmail.nossr50.runnables.StickyPistonTrackerTask;
import com.gmail.nossr50.skills.alchemy.Alchemy;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class BlockListener implements Listener {
    private final mcMMO plugin;

    public BlockListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor BlockPistonExtend events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!EventUtils.shouldProcessEvent(event.getBlock(), true)) {
            return;
        }

        BlockFace direction = event.getDirection();
        Block futureEmptyBlock = event.getBlock().getRelative(direction); // Block that would be air after piston is finished

        if (futureEmptyBlock.getType() == Material.AIR) {
            return;
        }

        List<Block> blocks = event.getBlocks();

        for (Block b : blocks) {
            if (BlockUtils.shouldBeWatched(b.getState()) && mcMMO.getPlaceStore().isTrue(b)) {
                b.getRelative(direction).setMetadata(mcMMO.blockMetadataKey, mcMMO.metadataValue);
            }
        }

        // Needed because blocks sometimes don't move when two pistons push towards each other
        new PistonTrackerTask(blocks, direction, futureEmptyBlock).runTaskLater(plugin, 2);
    }

    /**
     * Monitor BlockPistonRetract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!EventUtils.shouldProcessEvent(event.getBlock(), false)) {
            return;
        }

        // Don't even work, return ALLWAYS false xD
        /*if (!event.isSticky()){
        	return;
        }*/      
        
        // Sticky piston return PISTON_MOVING_PIECE and normal piston PISTON_BASE, so mess stuff
        if (event.getBlock().getType() != Material.PISTON_MOVING_PIECE) {
            return;
        }

        // event.getRetractLocation() return wrong side and too far away
        // Get opposite direction so we get correct block, so mess stuff
        Block movedBlock = event.getBlock().getRelative(event.getDirection().getOppositeFace());

        // Needed only because under some circumstances Minecraft doesn't move the block
        // Opposite side here too
        new StickyPistonTrackerTask(event.getDirection().getOppositeFace(), event.getBlock(), movedBlock).runTaskLater(plugin, 2);
    }

    /**
     * Monitor BlockPlace events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        BlockState blockState = event.getBlock().getState();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockUtils.shouldBeWatched(blockState)) {
            mcMMO.getPlaceStore().setTrue(blockState);
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (blockState.getType() == Repair.anvilMaterial && SkillType.REPAIR.getPermissions(player)) {
            mcMMOPlayer.getRepairManager().placedAnvilCheck();
        }
        else if (blockState.getType() == Salvage.anvilMaterial && SkillType.SALVAGE.getPermissions(player)) {
            mcMMOPlayer.getSalvageManager().placedAnvilCheck();
        }
    }

    /**
     * Monitor BlockBreak events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
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
        ItemStack heldItem = player.getItemInHand();

        /* HERBALISM */
        if (BlockUtils.affectedByGreenTerra(blockState)) {
            HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                mcMMOPlayer.checkAbilityActivation(SkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (SkillType.HERBALISM.getPermissions(player)) {
                herbalismManager.herbalismBlockCheck(blockState);
            }
        }

        /* MINING */
        else if (BlockUtils.affectedBySuperBreaker(blockState) && ItemUtils.isPickaxe(heldItem) && SkillType.MINING.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            MiningManager miningManager = mcMMOPlayer.getMiningManager();
            miningManager.miningBlockCheck(blockState);
        }

        /* WOOD CUTTING */
        else if (BlockUtils.isLog(blockState) && ItemUtils.isAxe(heldItem) && SkillType.WOODCUTTING.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            WoodcuttingManager woodcuttingManager = mcMMOPlayer.getWoodcuttingManager();

            if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                woodcuttingManager.processTreeFeller(blockState);
            }
            else {
                woodcuttingManager.woodcuttingBlockCheck(blockState);
            }
        }

        /* EXCAVATION */
        else if (BlockUtils.affectedByGigaDrillBreaker(blockState) && ItemUtils.isShovel(heldItem) && SkillType.EXCAVATION.getPermissions(player) && !mcMMO.getPlaceStore().isTrue(blockState)) {
            ExcavationManager excavationManager = mcMMOPlayer.getExcavationManager();
            excavationManager.excavationBlockCheck(blockState);

            if (mcMMOPlayer.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER)) {
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
        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        BlockState blockState = event.getBlock().getState();
        ItemStack heldItem = player.getItemInHand();

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
        else if (ItemUtils.isFluxPickaxe(heldItem) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
            SmeltingManager smeltingManager = UserManager.getPlayer(player).getSmeltingManager();

            if (smeltingManager.canUseFluxMining(blockState)) {
                if (smeltingManager.processFluxMining(blockState)) {
                    blockState.update(true);
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Monitor BlockDamage events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        BlockState blockState = event.getBlock().getState();

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (BlockUtils.canActivateAbilities(blockState)) {
            ItemStack heldItem = player.getItemInHand();

            if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
                if ((ItemUtils.isPickaxe(heldItem) && !mcMMOPlayer.getAbilityMode(AbilityType.SUPER_BREAKER)) || (ItemUtils.isShovel(heldItem) && !mcMMOPlayer.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER))) {
                    SkillUtils.removeAbilityBuff(heldItem);
                }
            }
            else {
                if ((mcMMOPlayer.getAbilityMode(AbilityType.SUPER_BREAKER) && !BlockUtils.affectedBySuperBreaker(blockState)) || (mcMMOPlayer.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER) && !BlockUtils.affectedByGigaDrillBreaker(blockState))) {
                    SkillUtils.handleAbilitySpeedDecrease(player);
                }
            }

            if (mcMMOPlayer.getToolPreparationMode(ToolType.HOE) && ItemUtils.isHoe(heldItem) && (BlockUtils.affectedByGreenTerra(blockState) || BlockUtils.canMakeMossy(blockState)) && Permissions.greenTerra(player)) {
                mcMMOPlayer.checkAbilityActivation(SkillType.HERBALISM);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && ItemUtils.isAxe(heldItem) && BlockUtils.isLog(blockState) && Permissions.treeFeller(player)) {
                mcMMOPlayer.checkAbilityActivation(SkillType.WOODCUTTING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.PICKAXE) && ItemUtils.isPickaxe(heldItem) && BlockUtils.affectedBySuperBreaker(blockState) && Permissions.superBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(SkillType.MINING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.SHOVEL) && ItemUtils.isShovel(heldItem) && BlockUtils.affectedByGigaDrillBreaker(blockState) && Permissions.gigaDrillBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(SkillType.EXCAVATION);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockUtils.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW || BlockUtils.affectedByBlockCracker(blockState) && Permissions.berserk(player))) {
                mcMMOPlayer.checkAbilityActivation(SkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mcMMOPlayer.getAbilityMode(AbilityType.TREE_FELLER) && BlockUtils.isLog(blockState) && Config.getInstance().getTreeFellerSoundsEnabled()) {
            player.playSound(blockState.getLocation(), Sound.FIZZ, Misc.FIZZ_VOLUME, Misc.getFizzPitch());
        }
    }

    /**
     * Handle BlockDamage events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamageHigher(BlockDamageEvent event) {
        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ItemStack heldItem = player.getItemInHand();
        Block block = event.getBlock();
        BlockState blockState = block.getState();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mcMMOPlayer.getAbilityMode(AbilityType.GREEN_TERRA) && BlockUtils.canMakeMossy(blockState)) {
            if (mcMMOPlayer.getHerbalismManager().processGreenTerra(blockState)) {
                blockState.update(true);
            }
        }
        else if (mcMMOPlayer.getAbilityMode(AbilityType.BERSERK) && heldItem.getType() == Material.AIR) {
            if (AbilityType.BERSERK.blockCheck(block.getState()) && EventUtils.simulateBlockBreak(block, player, true)) {
                event.setInstaBreak(true);
                player.playSound(block.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
            }
            else if (mcMMOPlayer.getUnarmedManager().canUseBlockCracker() && BlockUtils.affectedByBlockCracker(blockState) && EventUtils.simulateBlockBreak(block, player, true)) {
                if (mcMMOPlayer.getUnarmedManager().blockCrackerCheck(blockState)) {
                    blockState.update();
                }
            }
        }
        else if (mcMMOPlayer.getWoodcuttingManager().canUseLeafBlower(heldItem) && BlockUtils.isLeaves(blockState) && EventUtils.simulateBlockBreak(block, player, true)) {
            event.setInstaBreak(true);
            player.playSound(blockState.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
        }
    }
}
