package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.runnables.StickyPistonTracker;
import com.gmail.nossr50.skills.AbilityType;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.SkillTools;
import com.gmail.nossr50.skills.ToolType;
import com.gmail.nossr50.skills.excavation.Excavation;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.Mining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.skills.smelting.SmeltingManager;
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
                b.getRelative(direction).setMetadata("pistonTrack", new FixedMetadataValue(plugin, true));
                if (b.equals(futureEmptyBlock)) {
                    mcMMO.placeStore.setFalse(b);
                }
            }
        }

        for (Block b : blocks) {
            if (b.getRelative(direction).hasMetadata("pistonTrack")) {
                mcMMO.placeStore.setTrue(b.getRelative(direction));
                b.getRelative(direction).removeMetadata("pistonTrack", plugin);
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

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        Block block = event.getBlock();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockChecks.shouldBeWatched(block)) {
            mcMMO.placeStore.setTrue(block);
        }

        if (Repair.anvilMessagesEnabled) {
            int blockID = block.getTypeId();

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
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPCPlayer(player, profile)) {
            return;
        }

        Block block = event.getBlock();
        ItemStack heldItem = player.getItemInHand();

        /* HERBALISM */
        if (BlockChecks.canBeGreenTerra(block)) {
            /* Green Terra */
            if (profile.getToolPreparationMode(ToolType.HOE) && Permissions.greenTerra(player)) {
                SkillTools.abilityCheck(player, SkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (Permissions.herbalism(player)) {
                Herbalism.herbalismProcCheck(block, player, event, plugin); //Double drops

                if (profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                    Herbalism.herbalismProcCheck(block, player, event, plugin); //Triple drops
                }
            }
        }

        /* MINING */
        else if (BlockChecks.canBeSuperBroken(block) && Permissions.mining(player) && !mcMMO.placeStore.isTrue(block)) {
            if (Mining.requiresTool) {
                if (ItemChecks.isPickaxe(heldItem)) {
                    MiningManager miningManager = new MiningManager(player);
                    miningManager.miningBlockCheck(block);
                }
            }
            else {
                MiningManager miningManager = new MiningManager(player);
                miningManager.miningBlockCheck(block);
            }
        }

        /* WOOD CUTTING */
        else if (BlockChecks.isLog(block) && Permissions.woodcutting(player) && !mcMMO.placeStore.isTrue(block)) {
            if (profile.getAbilityMode(AbilityType.TREE_FELLER) && Permissions.treeFeller(player) && ItemChecks.isAxe(heldItem)) {
                Woodcutting.beginTreeFeller(event);
            }
            else {
                if (Woodcutting.REQUIRES_TOOL) {
                    if (ItemChecks.isAxe(heldItem)) {
                        Woodcutting.beginWoodcutting(player, block);
                    }
                }
                else {
                    Woodcutting.beginWoodcutting(player, block);
                }
            }
        }

        /* EXCAVATION */
        else if (BlockChecks.canBeGigaDrillBroken(block) && Permissions.excavation(player) && !mcMMO.placeStore.isTrue(block)) {
            if (Excavation.requiresTool) {
                if (ItemChecks.isShovel(heldItem)) {
                    Excavation.excavationProcCheck(block, player);
                }
            }
            else {
                Excavation.excavationProcCheck(block, player);
            }
        }

        /* Remove metadata from placed watched blocks */
        if (BlockChecks.shouldBeWatched(block) && mcMMO.placeStore.isTrue(block)) {
            mcMMO.placeStore.setFalse(block);
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

        if (Misc.isNPCPlayer(player)) {
            return;
        }

        Block block = event.getBlock();
        ItemStack heldItem = player.getItemInHand();

        if (Permissions.hylianLuck(player) && ItemChecks.isSword(heldItem)) {
            Herbalism.hylianLuck(block, player, event);
        }
        else if (BlockChecks.canBeFluxMined(block) && ItemChecks.isPickaxe(heldItem) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH) && Permissions.fluxMining(player) && !mcMMO.placeStore.isTrue(block)) {
            SmeltingManager smeltingManager = new SmeltingManager(player);
            smeltingManager.fluxMining(event);
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
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPCPlayer(player, profile)) {
            return;
        }

        Block block = event.getBlock();

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (BlockChecks.canActivateAbilities(block)) {
            ItemStack heldItem = player.getItemInHand();

            if (profile.getToolPreparationMode(ToolType.HOE) && ItemChecks.isHoe(heldItem) && (BlockChecks.canBeGreenTerra(block) || BlockChecks.canMakeMossy(block)) && Permissions.greenTerra(player)) {
                SkillTools.abilityCheck(player, SkillType.HERBALISM);
            }
            else if (profile.getToolPreparationMode(ToolType.AXE) && ItemChecks.isAxe(heldItem) && BlockChecks.isLog(block) && Permissions.treeFeller(player)) {
                SkillTools.abilityCheck(player, SkillType.WOODCUTTING);
            }
            else if (profile.getToolPreparationMode(ToolType.PICKAXE) && ItemChecks.isPickaxe(heldItem) && BlockChecks.canBeSuperBroken(block) && Permissions.superBreaker(player)) {
                SkillTools.abilityCheck(player, SkillType.MINING);
            }
            else if (profile.getToolPreparationMode(ToolType.SHOVEL) && ItemChecks.isShovel(heldItem) && BlockChecks.canBeGigaDrillBroken(block) && Permissions.gigaDrillBreaker(player)) {
                SkillTools.abilityCheck(player, SkillType.EXCAVATION);
            }
            else if (profile.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (BlockChecks.canBeGigaDrillBroken(block) || block.getType().equals(Material.SNOW)) && Permissions.berserk(player)) {
                SkillTools.abilityCheck(player, SkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (profile.getAbilityMode(AbilityType.TREE_FELLER) && BlockChecks.isLog(block)) {
            player.playSound(block.getLocation(), Sound.FIZZ, Misc.FIZZ_VOLUME, Misc.FIZZ_PITCH);
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
        PlayerProfile profile = Users.getProfile(player);

        if (Misc.isNPCPlayer(player, profile)) {
            return;
        }

        ItemStack heldItem = player.getItemInHand();
        Block block = event.getBlock();

        /*
         * ABILITY TRIGGER CHECKS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        if (profile.getAbilityMode(AbilityType.GREEN_TERRA) && BlockChecks.canMakeMossy(block)) {
            Herbalism.greenTerra(player, block);
        }
        else if (profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER) && SkillTools.triggerCheck(player, block, AbilityType.GIGA_DRILL_BREAKER)) {
            if (Excavation.requiresTool) {
                if (ItemChecks.isShovel(heldItem)) {
                    event.setInstaBreak(true);
                    Excavation.gigaDrillBreaker(player, block);
                }
            }
            else {
                event.setInstaBreak(true);
                Excavation.gigaDrillBreaker(player, block);
            }
        }
        else if (profile.getAbilityMode(AbilityType.BERSERK) && SkillTools.triggerCheck(player, block, AbilityType.BERSERK)) {
            if (heldItem.getType().equals(Material.AIR)) {
                FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
                plugin.getServer().getPluginManager().callEvent(armswing);

                event.setInstaBreak(true);
            }

            player.playSound(block.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.POP_PITCH);
        }
        else if (profile.getAbilityMode(AbilityType.SUPER_BREAKER) && SkillTools.triggerCheck(player, block, AbilityType.SUPER_BREAKER)) {
            MiningManager miningManager = new MiningManager(player);

            if (Mining.requiresTool) {
                if (ItemChecks.isPickaxe(heldItem)) {
                    event.setInstaBreak(true);
                    miningManager.superBreakerBlockCheck(block);
                }
            }
            else {
                event.setInstaBreak(true);
                miningManager.superBreakerBlockCheck(block);
            }
        }
        else if ((profile.getSkillLevel(SkillType.WOODCUTTING) >= Woodcutting.LEAF_BLOWER_UNLOCK_LEVEL) && BlockChecks.isLeaves(block)) {
            if (SkillTools.triggerCheck(player, block, AbilityType.LEAF_BLOWER)) {
                if (Woodcutting.REQUIRES_TOOL) {
                    if (ItemChecks.isAxe(heldItem)) {
                        event.setInstaBreak(true);
                        Woodcutting.beginLeafBlower(player, block);
                    }
                }
                else if (!heldItem.getType().equals(Material.SHEARS)) {
                    event.setInstaBreak(true);
                    Woodcutting.beginLeafBlower(player, block);
                }
            }
        }
    }
}
