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
import org.bukkit.event.block.BlockPhysicsEvent;
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
import com.gmail.nossr50.skills.gathering.Mining;
import com.gmail.nossr50.skills.gathering.WoodCutting;
import com.gmail.nossr50.skills.repair.Repair;
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

        for (Block b : blocks) {
            if (mcMMO.placeStore.isTrue(b)) {
                b.getRelative(direction).setMetadata("pistonTrack", new FixedMetadataValue(plugin, true));
                mcMMO.placeStore.setFalse(b);
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
     * Monitor BlockPhysics events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        //TODO: Figure out how to REMOVE metadata from the location the sand/gravel fell from.
        Material type = event.getChangedType();

        if (type == Material.GRAVEL || type == Material.SAND) {
            Block fallenBlock = event.getBlock().getRelative(BlockFace.UP);

            if (fallenBlock.getType() == type) {
                mcMMO.placeStore.setTrue(fallenBlock);
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
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new StickyPistonTracker(event), 0);
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
        Material type = block.getType();

        /* Code to prevent issues with placed falling Sand/Gravel not being tracked */
        if (type.equals(Material.SAND) || type.equals(Material.GRAVEL)) {
            for (int y = -1;  y + block.getY() >= 0; y--) {
                if (block.getRelative(0, y, 0).getType().equals(Material.AIR)) {
                    continue;
                }
                else {
                    Block newLocation = block.getRelative(0, y + 1, 0);
                    mcMMO.placeStore.setTrue(newLocation);
                    break;
                }
            }
        }

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (BlockChecks.shouldBeWatched(block)) {
            if (!((type == Material.SAND || type == Material.GRAVEL) && block.getRelative(BlockFace.DOWN).getType() == Material.AIR)) { //Don't wanna track sand that's gonna fall.
                mcMMO.placeStore.setTrue(block);
            }
        }

        if (id == configInstance.getRepairAnvilId() && configInstance.getRepairAnvilMessagesEnabled()) {
            Repair.placedAnvilCheck(player, id);
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

        if (profile == null) {
            return;
        }

        Block block = event.getBlock();
        ItemStack inHand = player.getItemInHand();

        Config configInstance = Config.getInstance();
        Permissions permInstance = Permissions.getInstance();

        /* HERBALISM */
        if (BlockChecks.canBeGreenTerra(block)) {
            /* Green Terra */
            if (profile.getToolPreparationMode(ToolType.HOE) && permInstance.greenTerra(player)) {
                Skills.abilityCheck(player, SkillType.HERBALISM);
            }

            /* Triple drops */
            if (profile.getAbilityMode(AbilityType.GREEN_TERRA)) {
                Herbalism.herbalismProcCheck(block, player, event, plugin);
            }

            if (permInstance.herbalism(player)) {
                Herbalism.herbalismProcCheck(block, player, event, plugin);
            }
        }

        /* MINING */
        else if (BlockChecks.canBeSuperBroken(block) && permInstance.mining(player)) {
            if (configInstance.getMiningRequiresTool()) {
                if (ItemChecks.isPickaxe(inHand)) {
                    Mining.miningBlockCheck(player, block);
                }
            }
            else {
                Mining.miningBlockCheck(player, block);
            }
        }

        /* WOOD CUTTING */
        else if (BlockChecks.isLog(block) && permInstance.woodcutting(player)) {
            if (configInstance.getWoodcuttingRequiresTool()) {
                if (ItemChecks.isAxe(inHand)) {
                    WoodCutting.woodcuttingBlockCheck(player, block);
                }
            }
            else {
                WoodCutting.woodcuttingBlockCheck(player, block);
            }

            if (profile.getAbilityMode(AbilityType.TREE_FELLER) && permInstance.treeFeller(player) && ItemChecks.isAxe(inHand)) {
                WoodCutting.treeFeller(event);
            }
        }

        /* EXCAVATION */
        else if (BlockChecks.canBeGigaDrillBroken(block) && permInstance.excavation(player) && !mcMMO.placeStore.isTrue(block)) {
            if (configInstance.getExcavationRequiresTool()) {
                if (ItemChecks.isShovel(inHand)) {
                    Excavation.excavationProcCheck(block, player);
                }
            }
            else {
                Excavation.excavationProcCheck(block, player);
            }
        }

        //Remove metadata when broken
        if (BlockChecks.shouldBeWatched(block)) {
            mcMMO.placeStore.setFalse(block);
        }

        //Remove metadata from fallen sand/gravel
        Material aboveType = block.getRelative(BlockFace.UP).getType();

        if (aboveType == Material.SAND || aboveType == Material.GRAVEL) {
            for (int y = 1; block.getY() + y <= block.getWorld().getMaxHeight(); y++) {
                Block relative = block.getRelative(0, y, 0);
                Material relativeType = relative.getType();

                if ((relativeType == Material.SAND || relativeType == Material.GRAVEL) && mcMMO.placeStore.isTrue(relative)) {
                    mcMMO.placeStore.setFalse(relative);
                }
                else if (!BlockChecks.shouldBeWatched(relative) && mcMMO.placeStore.isTrue(relative)){
                    mcMMO.placeStore.setFalse(relative);
                }
                else {
                    break;
                }
            }
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
        PlayerProfile profile = Users.getProfile(player);

        if (profile == null) {
            return;
        }

        ItemStack inHand = player.getItemInHand();
        Block block = event.getBlock();
        Material material = block.getType();

        Config configInstance = Config.getInstance();
        Permissions permInstance = Permissions.getInstance();

        /*
         * ABILITY PREPARATION CHECKS
         */
        if (BlockChecks.abilityBlockCheck(block)) {
            if (profile.getToolPreparationMode(ToolType.HOE) && (BlockChecks.canBeGreenTerra(block) || BlockChecks.makeMossy(block))) {
                Skills.abilityCheck(player, SkillType.HERBALISM);
            }
            else if (profile.getToolPreparationMode(ToolType.AXE) && BlockChecks.isLog(block) && permInstance.treeFeller(player)) {  //TODO: Why are we checking the permissions here?
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
        if (profile.getAbilityMode(AbilityType.GREEN_TERRA) && permInstance.greenTerra(player) && BlockChecks.makeMossy(block)) {
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
            if (configInstance.getMiningRequiresTool()) {
                if (ItemChecks.isPickaxe(inHand)) {
                    event.setInstaBreak(true);
                    Mining.superBreakerBlockCheck(player, block);
                }
            }
            else {
                event.setInstaBreak(true);
                Mining.superBreakerBlockCheck(player, block);
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
