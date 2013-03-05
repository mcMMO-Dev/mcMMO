package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.runnables.StickyPistonTrackerTask;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.skills.woodcutting.Woodcutting;
import com.gmail.nossr50.util.BlockUtils;
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
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {

        List<Block> blocks = event.getBlocks();
        BlockFace direction = event.getDirection();
        Block futureEmptyBlock = event.getBlock().getRelative(direction); // Block that would be air after piston is finished

        for (Block b : blocks) {
            if (mcMMO.placeStore.isTrue(b)) {
                b.getRelative(direction).setMetadata(mcMMO.blockMetadataKey, mcMMO.metadataValue);
                if (b.equals(futureEmptyBlock)) {
                    mcMMO.placeStore.setFalse(b);
                }
            }
        }

        for (Block b : blocks) {
            if (b.getRelative(direction).hasMetadata(mcMMO.blockMetadataKey)) {
                mcMMO.placeStore.setTrue(b.getRelative(direction));
                b.getRelative(direction).removeMetadata(mcMMO.blockMetadataKey, plugin);
            }
        }
    }

    /**
     * Monitor BlockPistonRetract events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isSticky()) {
            // Needed only because under some circumstances Minecraft doesn't move the block
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new StickyPistonTrackerTask(event), 2);
        }
    }

    /**
     * Monitor BlockPlace events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        BlockState blockState = event.getBlock().getState();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockUtils.shouldBeWatched(blockState)) {
            mcMMO.placeStore.setTrue(blockState);
        }

        if (Repair.anvilMessagesEnabled) {
            int blockID = blockState.getTypeId();

            if (blockID == Repair.anvilID) {
                Repair.placedAnvilCheck(player, blockID);
            }
            else if (blockID == Salvage.anvilID) {
                Salvage.placedAnvilCheck(player, blockID);
            }
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

        Player player = event.getPlayer();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        BlockState blockState = event.getBlock().getState();

        ItemStack heldItem = player.getItemInHand();

        /* HERBALISM */
        if (BlockUtils.affectedByGreenTerra(blockState)) {
            HerbalismManager herbalismManager = UserManager.getPlayer(player).getHerbalismManager();

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (Permissions.skillEnabled(player, SkillType.HERBALISM)) {

                // Double drops
                herbalismManager.herbalismBlockCheck(blockState);

                // Triple drops
                if (herbalismManager.canGreenTerraPlant()) {
                    herbalismManager.herbalismBlockCheck(blockState);
                }
            }
        }

        /* MINING */
        else if (BlockUtils.affectedBySuperBreaker(blockState) && ItemUtils.isPickaxe(heldItem) && Permissions.skillEnabled(player, SkillType.MINING) && !mcMMO.placeStore.isTrue(blockState)) {
            MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();
            miningManager.miningBlockCheck(blockState);

            if (mcMMOPlayer.getAbilityMode(AbilityType.SUPER_BREAKER)) {
                miningManager.miningBlockCheck(blockState);
            }
        }

        /* WOOD CUTTING */
        else if (BlockUtils.isLog(blockState) && Permissions.skillEnabled(player, SkillType.WOODCUTTING) && !mcMMO.placeStore.isTrue(blockState)) {
            if (mcMMOPlayer.getAbilityMode(AbilityType.TREE_FELLER) && Permissions.treeFeller(player) && ItemUtils.isAxe(heldItem)) {
                Woodcutting.beginTreeFeller(blockState, player);
            }
            else {
                if (Config.getInstance().getWoodcuttingRequiresTool()) {
                    if (ItemUtils.isAxe(heldItem)) {
                        Woodcutting.beginWoodcutting(player, blockState);
                    }
                }
                else {
                    Woodcutting.beginWoodcutting(player, blockState);
                }
            }
        }

        /* EXCAVATION */
        else if (BlockUtils.affectedByGigaDrillBreaker(blockState) && ItemUtils.isShovel(heldItem) && Permissions.skillEnabled(player, SkillType.EXCAVATION) && !mcMMO.placeStore.isTrue(blockState)) {
            ExcavationManager excavationManager = UserManager.getPlayer(player).getExcavationManager();
            excavationManager.excavationBlockCheck(blockState);

            if (mcMMOPlayer.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(blockState);
            }
        }

        /* Remove metadata from placed watched blocks */
        if (BlockUtils.shouldBeWatched(blockState) && mcMMO.placeStore.isTrue(blockState)) {
            mcMMO.placeStore.setFalse(blockState);
        }
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        BlockState blockState = event.getBlock().getState();
        ItemStack heldItem = player.getItemInHand();

        if (ItemUtils.isSword(heldItem)) {
            HerbalismManager herbalismManager = UserManager.getPlayer(player).getHerbalismManager();

            if (herbalismManager.canUseHylianLuck()) {
                if (herbalismManager.processHylianLuck(blockState)) {
                    blockState.update(true);
                    event.setCancelled(true);
                }
            }
        }
        else if (ItemUtils.isPickaxe(heldItem) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
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

        if (Misc.isNPCEntity(player)) {
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
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.HERBALISM);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && ItemUtils.isAxe(heldItem) && BlockUtils.isLog(blockState) && Permissions.treeFeller(player)) {
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.WOODCUTTING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.PICKAXE) && ItemUtils.isPickaxe(heldItem) && BlockUtils.affectedBySuperBreaker(blockState) && Permissions.superBreaker(player)) {
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.MINING);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.SHOVEL) && ItemUtils.isShovel(heldItem) && BlockUtils.affectedByGigaDrillBreaker(blockState) && Permissions.gigaDrillBreaker(player)) {
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.EXCAVATION);
            }
            else if (mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockUtils.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW || BlockUtils.affectedByBlockCracker(blockState) && Permissions.berserk(player))) {
                SkillUtils.abilityCheck(mcMMOPlayer, SkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (mcMMOPlayer.getAbilityMode(AbilityType.TREE_FELLER) && BlockUtils.isLog(blockState)) {
            player.playSound(blockState.getLocation(), Sound.FIZZ, Misc.FIZZ_VOLUME, Misc.FIZZ_PITCH);
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ItemStack heldItem = player.getItemInHand();
        Block block = event.getBlock();
        BlockState blockState = block.getState();
        HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (herbalismManager.canGreenTerraBlock(blockState)) {
            if (herbalismManager.processGreenTerra(blockState)) {
                blockState.update(true);
            }
        }
        else if (mcMMOPlayer.getAbilityMode(AbilityType.BERSERK)) {
            if (SkillUtils.triggerCheck(player, block, AbilityType.BERSERK)) {
                if (heldItem.getType() == Material.AIR) {
                    plugin.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));

                    event.setInstaBreak(true);
                    player.playSound(block.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.POP_PITCH);
                }
            }
            // Another perm check for the cracked blocks activation
            else if (BlockUtils.affectedByBlockCracker(blockState) && Permissions.blockCracker(player)) {
                if (Unarmed.blockCracker(player, blockState)) {
                    blockState.update();
                }
            }
        }
        else if ((mcMMOPlayer.getProfile().getSkillLevel(SkillType.WOODCUTTING) >= AdvancedConfig.getInstance().getLeafBlowUnlockLevel()) && BlockUtils.isLeaves(blockState)) {
            if (SkillUtils.triggerCheck(player, block, AbilityType.LEAF_BLOWER)) {
                if (Config.getInstance().getWoodcuttingRequiresTool()) {
                    if (ItemUtils.isAxe(heldItem)) {
                        event.setInstaBreak(true);
                        Woodcutting.beginLeafBlower(player, blockState);
                    }
                }
                else if (!(heldItem.getType() == Material.SHEARS)) {
                    event.setInstaBreak(true);
                    Woodcutting.beginLeafBlower(player, blockState);
                }
            }
        }
    }
}
