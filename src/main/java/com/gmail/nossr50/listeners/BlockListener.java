package com.gmail.nossr50.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.getspout.spoutapi.sound.SoundEffect;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.AbilityType;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.datatypes.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.runnables.StickyPistonTracker;
import com.gmail.nossr50.skills.gathering.Excavation;
import com.gmail.nossr50.skills.gathering.Herbalism;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.gathering.WoodCutting;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.Salvage;
import com.gmail.nossr50.spout.SpoutSounds;
import com.gmail.nossr50.util.BlockChecks;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.ModChecks;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
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
        // Block that would be air after piston is finished
        Block futureEmptyBlock = event.getBlock().getRelative(direction);

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
     * @param event The event to monitor
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
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Config configInstance = Config.getInstance();

        Block block = event.getBlock();
        Player player = event.getPlayer();
        int id = block.getTypeId();

        if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockChecks.shouldBeWatched(block)) {
            mcMMO.placeStore.setTrue(block);
        }

        if (id == configInstance.getRepairAnvilId() && configInstance.getRepairAnvilMessagesEnabled()) {
            Repair.placedAnvilCheck(player, id);
        }
        if (id == configInstance.getSalvageAnvilId() && configInstance.getRepairAnvilMessagesEnabled()) {
            Salvage.placedAnvilCheck(player, id);
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

        if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        PlayerProfile profile = Users.getProfile(player);

        if (profile == null) {
            return;
        }

        Block block = event.getBlock();
        ItemStack inHand = player.getItemInHand();

        Config configInstance = Config.getInstance();

        /* HERBALISM */
        if (BlockChecks.canBeGreenTerra(block)) {
            /* Green Terra */
            if (profile.getToolPreparationMode(ToolType.HOE) && Permissions.greenTerra(player)) {
                Skills.abilityCheck(player, SkillType.HERBALISM);
            }

            /* Triple drops */
            if (profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                Herbalism.herbalismProcCheck(block, player, event, plugin);
            }

            if (Permissions.herbalism(player)) {
                Herbalism.herbalismProcCheck(block, player, event, plugin);
            }
        }

        /* MINING */
        else if (BlockChecks.canBeSuperBroken(block) && Permissions.mining(player)) {
            MiningManager miningManager = new MiningManager(player);
            if (configInstance.getMiningRequiresTool()) {
                if (ItemChecks.isPickaxe(inHand)) {
                    miningManager.miningBlockCheck(block);
                }
            }
            else {
                miningManager.miningBlockCheck(block);
            }
        }

        /* WOOD CUTTING */
        else if (BlockChecks.isLog(block) && Permissions.woodcutting(player)) {
            if (configInstance.getWoodcuttingRequiresTool()) {
                if (ItemChecks.isAxe(inHand)) {
                    WoodCutting.woodcuttingBlockCheck(player, block);
                }
            }
            else {
                WoodCutting.woodcuttingBlockCheck(player, block);
            }

            if (profile.getAbilityMode(AbilityType.TREE_FELLER) && Permissions.treeFeller(player) && ItemChecks.isAxe(inHand)) {
                WoodCutting.treeFeller(event);
            }
        }

        /* EXCAVATION */
        else if (BlockChecks.canBeGigaDrillBroken(block) && Permissions.excavation(player) && !mcMMO.placeStore.isTrue(block)) {
            if (configInstance.getExcavationRequiresTool()) {
                if (ItemChecks.isShovel(inHand)) {
                    Excavation.excavationProcCheck(block, player);
                }
            }
            else {
                Excavation.excavationProcCheck(block, player);
            }
        }

        if (Permissions.hylianLuck(player) && ItemChecks.isSword(player.getItemInHand())) {
            Herbalism.hylianLuck(block, player, event);
        }

        //Remove metadata when broken
        if (BlockChecks.shouldBeWatched(block)) {
            mcMMO.placeStore.setFalse(block);
        }
    }

    /**
     * Monitor BlockDamage events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        final int LEAF_BLOWER_LEVEL = 100;

        Player player = event.getPlayer();

        if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        PlayerProfile profile = Users.getProfile(player);

        if (profile == null) {
            return;
        }

        ItemStack inHand = player.getItemInHand();
        Block block = event.getBlock();
        Material material = block.getType();

        Config configInstance = Config.getInstance();

        /*
         * ABILITY PREPARATION CHECKS
         */
        if (BlockChecks.abilityBlockCheck(block)) {
            if (profile.getToolPreparationMode(ToolType.HOE) && (BlockChecks.canBeGreenTerra(block) || BlockChecks.makeMossy(block))) {
                Skills.abilityCheck(player, SkillType.HERBALISM);
            }
            else if (profile.getToolPreparationMode(ToolType.AXE) && BlockChecks.isLog(block) && Permissions.treeFeller(player)) {  //TODO: Why are we checking the permissions here?
                Skills.abilityCheck(player, SkillType.WOODCUTTING);
            }
            else if (profile.getToolPreparationMode(ToolType.PICKAXE) && BlockChecks.canBeSuperBroken(block)) {
                Skills.abilityCheck(player, SkillType.MINING);
            }
            else if (profile.getToolPreparationMode(ToolType.SHOVEL) && BlockChecks.canBeGigaDrillBroken(block)) {
                Skills.abilityCheck(player, SkillType.EXCAVATION);
            }
            else if (profile.getToolPreparationMode(ToolType.FISTS) && (BlockChecks.canBeGigaDrillBroken(block) || material.equals(Material.SNOW))) {
                Skills.abilityCheck(player, SkillType.UNARMED);
            }
        }

        /* TREE FELLER SOUNDS */
        if (mcMMO.spoutEnabled && BlockChecks.isLog(block) && profile.getAbilityMode(AbilityType.TREE_FELLER)) {
            SpoutSounds.playSoundForPlayer(SoundEffect.FIZZ, player, block.getLocation());
        }

        /*
         * ABILITY TRIGGER CHECKS
         */
        if (profile.getAbilityMode(AbilityType.GREEN_TERRA) && Permissions.greenTerra(player) && BlockChecks.makeMossy(block)) {
            Herbalism.greenTerra(player, block);
        }
        else if (profile.getAbilityMode(AbilityType.GIGA_DRILL_BREAKER) && Skills.triggerCheck(player, block, AbilityType.GIGA_DRILL_BREAKER)) {
            if (configInstance.getExcavationRequiresTool()) {
                if (ItemChecks.isShovel(inHand)) {
                    event.setInstaBreak(true);
                    Excavation.gigaDrillBreaker(player, block);
                }
            }
            else {
                event.setInstaBreak(true);
                Excavation.gigaDrillBreaker(player, block);
            }
        }
        else if (profile.getAbilityMode(AbilityType.BERSERK) && Skills.triggerCheck(player, block, AbilityType.BERSERK)) {
            if (inHand.getType().equals(Material.AIR)) {
                FakePlayerAnimationEvent armswing = new FakePlayerAnimationEvent(player);
                plugin.getServer().getPluginManager().callEvent(armswing);

                event.setInstaBreak(true);
            }

            if (mcMMO.spoutEnabled) {
                SpoutSounds.playSoundForPlayer(SoundEffect.POP, player, block.getLocation());
            }
        }
        else if (profile.getAbilityMode(AbilityType.SUPER_BREAKER) && Skills.triggerCheck(player, block, AbilityType.SUPER_BREAKER)) {
            MiningManager miningManager = new MiningManager(player);

            if (configInstance.getMiningRequiresTool()) {
                if (ItemChecks.isPickaxe(inHand)) {
                    event.setInstaBreak(true);
                    miningManager.superBreakerBlockCheck(block);
                }
            }
            else {
                event.setInstaBreak(true);
                miningManager.superBreakerBlockCheck(block);
            }
        }
        else if (profile.getSkillLevel(SkillType.WOODCUTTING) >= LEAF_BLOWER_LEVEL && (material.equals(Material.LEAVES) || (configInstance.getBlockModsEnabled() && ModChecks.isCustomLeafBlock(block)))) {
            if (Skills.triggerCheck(player, block, AbilityType.LEAF_BLOWER)) {
                if (configInstance.getWoodcuttingRequiresTool()) {
                    if (ItemChecks.isAxe(inHand)) {
                        event.setInstaBreak(true);
                        WoodCutting.leafBlower(player, block);
                    }
                }
                else if (!inHand.getType().equals(Material.SHEARS)) {
                    event.setInstaBreak(true);
                    WoodCutting.leafBlower(player, block);
                }
            }
        }
    }
}
