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
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.runnables.StickyPistonTracker;
import com.gmail.nossr50.skills.SkillManagerStore;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.smelting.Smelting;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.skills.utilities.AbilityType;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.skills.utilities.ToolType;
import com.gmail.nossr50.skills.woodcutting.Woodcutting;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

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
            //Needed only because under some circumstances Minecraft doesn't move the block
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new StickyPistonTracker(event), 2);
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
        if (BlockChecks.shouldBeWatched(blockState)) {
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

        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        PlayerProfile profile = mcMMOPlayer.getProfile();
        BlockState blockState = event.getBlock().getState();

        ItemStack heldItem = player.getItemInHand();

        /* HERBALISM */
        if (BlockChecks.affectedByGreenTerra(blockState)) {
            HerbalismManager herbalismManager = SkillManagerStore.getInstance().getHerbalismManager(player.getName());

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                SkillTools.abilityCheck(player, SkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (Permissions.skillEnabled(player, SkillType.HERBALISM)) {

                //Double drops
                herbalismManager.herbalismBlockCheck(blockState);

                //Triple drops
                if (herbalismManager.canGreenTerraPlant()) {
                    herbalismManager.herbalismBlockCheck(blockState);
                }
            }
        }

        /* MINING */
        else if (BlockChecks.affectedBySuperBreaker(blockState) && ItemChecks.isPickaxe(heldItem) && Permissions.skillEnabled(player, SkillType.MINING) && !mcMMO.placeStore.isTrue(blockState)) {
            Mining.miningBlockCheck(blockState, player);

            if (profile.getAbilityMode(AbilityType.SUPER_BREAKER)) {
                Mining.miningBlockCheck(blockState, player);
            }
        }

        /* WOOD CUTTING */
        else if (BlockChecks.isLog(blockState) && Permissions.skillEnabled(player, SkillType.WOODCUTTING) && !mcMMO.placeStore.isTrue(blockState)) {
            if (profile.getAbilityMode(AbilityType.TREE_FELLER) && Permissions.treeFeller(player) && ItemChecks.isAxe(heldItem)) {
                Woodcutting.beginTreeFeller(blockState, player);
            }
            else {
                if (Config.getInstance().getWoodcuttingRequiresTool()) {
                    if (ItemChecks.isAxe(heldItem)) {
                        Woodcutting.beginWoodcutting(player, blockState);
                    }
                }
                else {
                    Woodcutting.beginWoodcutting(player, blockState);
                }
            }
        }

        /* EXCAVATION */
        else if (BlockChecks.affectedByGigaDrillBreaker(blockState) && ItemChecks.isShovel(heldItem) && Permissions.skillEnabled(player, SkillType.EXCAVATION) && !mcMMO.placeStore.isTrue(blockState)) {
            ExcavationManager excavationManager = SkillManagerStore.getInstance().getExcavationManager(player.getName());
            excavationManager.excavationBlockCheck(blockState);

            if (profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(blockState);
            }
        }

        /* Remove metadata from placed watched blocks */
        if (BlockChecks.shouldBeWatched(blockState) && mcMMO.placeStore.isTrue(blockState)) {
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

        String playerName = player.getName();
        BlockState blockState = event.getBlock().getState();
        ItemStack heldItem = player.getItemInHand();

        if (SkillManagerStore.getInstance().getHerbalismManager(playerName).canUseHylianLuck()) {
            if (SkillManagerStore.getInstance().getHerbalismManager(playerName).processHylianLuck(blockState)) {
                blockState.update(true);
                event.setCancelled(true);
            }
        }
        else if (BlockChecks.affectedByFluxMining(blockState) && ItemChecks.isPickaxe(heldItem) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH) && Permissions.fluxMining(player) && !mcMMO.placeStore.isTrue(blockState)) {
            if (Smelting.processFluxMining(blockState, player)) {
                blockState.update(true);
                event.setCancelled(true);
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

        PlayerProfile profile = Users.getPlayer(player).getProfile();
        BlockState blockState = event.getBlock().getState();

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (BlockChecks.canActivateAbilities(blockState)) {
            ItemStack heldItem = player.getItemInHand();

            if (HiddenConfig.getInstance().useEnchantmentBuffs()) {
                if ((ItemChecks.isPickaxe(heldItem) && !profile.getAbilityMode(AbilityType.SUPER_BREAKER)) || (ItemChecks.isShovel(heldItem) && !profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER))) {
                    SkillTools.removeAbilityBuff(heldItem);
                }
            }
            else {
                if ((profile.getAbilityMode(AbilityType.SUPER_BREAKER) && !BlockChecks.affectedBySuperBreaker(blockState)) || (profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER) && !BlockChecks.affectedByGigaDrillBreaker(blockState))) {
                    SkillTools.handleAbilitySpeedDecrease(player);
                }
            }

            if (profile.getToolPreparationMode(ToolType.HOE) && ItemChecks.isHoe(heldItem) && (BlockChecks.affectedByGreenTerra(blockState) || BlockChecks.canMakeMossy(blockState)) && Permissions.greenTerra(player)) {
                SkillTools.abilityCheck(player, SkillType.HERBALISM);
            }
            else if (profile.getToolPreparationMode(ToolType.AXE) && ItemChecks.isAxe(heldItem) && BlockChecks.isLog(blockState) && Permissions.treeFeller(player)) {
                SkillTools.abilityCheck(player, SkillType.WOODCUTTING);
            }
            else if (profile.getToolPreparationMode(ToolType.PICKAXE) && ItemChecks.isPickaxe(heldItem) && BlockChecks.affectedBySuperBreaker(blockState) && Permissions.superBreaker(player)) {
                SkillTools.abilityCheck(player, SkillType.MINING);
            }
            else if (profile.getToolPreparationMode(ToolType.SHOVEL) && ItemChecks.isShovel(heldItem) && BlockChecks.affectedByGigaDrillBreaker(blockState) && Permissions.gigaDrillBreaker(player)) {
                SkillTools.abilityCheck(player, SkillType.EXCAVATION);
            }
            else if (profile.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockChecks.affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW || BlockChecks.affectedByBlockCracker(blockState) && Permissions.berserk(player))) {
                SkillTools.abilityCheck(player, SkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (profile.getAbilityMode(AbilityType.TREE_FELLER) && BlockChecks.isLog(blockState)) {
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

        String playerName = player.getName();
        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        PlayerProfile profile = mcMMOPlayer.getProfile();
        ItemStack heldItem = player.getItemInHand();
        Block block = event.getBlock();
        BlockState blockState = block.getState();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (SkillManagerStore.getInstance().getHerbalismManager(playerName).canGreenTerraBlock(blockState)) {
            if (SkillManagerStore.getInstance().getHerbalismManager(playerName).processGreenTerra(blockState)) {
                blockState.update(true);
            }
        }
        else if (profile.getAbilityMode(AbilityType.BERSERK)) {
            if (SkillTools.triggerCheck(player, block, AbilityType.BERSERK)) {
                if (heldItem.getType() == Material.AIR) {
                    plugin.getServer().getPluginManager().callEvent(new FakePlayerAnimationEvent(player));

                    event.setInstaBreak(true);
                    player.playSound(block.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.POP_PITCH);
                }
            }
            // Another perm check for the cracked blocks activation
            else if (BlockChecks.affectedByBlockCracker(blockState) && Permissions.blockCracker(player)) {
                if (Unarmed.blockCracker(player, blockState)) {
                    blockState.update();
                }
            }
        }
        else if ((profile.getSkillLevel(SkillType.WOODCUTTING) >= AdvancedConfig.getInstance().getLeafBlowUnlockLevel()) && BlockChecks.isLeaves(blockState)) {
            if (SkillTools.triggerCheck(player, block, AbilityType.LEAF_BLOWER)) {
                if (Config.getInstance().getWoodcuttingRequiresTool()) {
                    if (ItemChecks.isAxe(heldItem)) {
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
