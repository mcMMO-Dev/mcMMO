package com.gmail.nossr50.listeners;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.events.fake.FakeBlockBreakEvent;
import com.gmail.nossr50.events.fake.FakeBlockDamageEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.excavation.ExcavationManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.woodcutting.WoodcuttingManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
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

public class BlockListener implements Listener {
    private final mcMMO pluginRef;

    public BlockListener(final mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItemEvent(BlockDropItemEvent event) {
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
        if (blockCount <= 1){
            for (Item item : event.getItems()) {
                ItemStack is = new ItemStack(item.getItemStack());

                if (is.getAmount() <= 0)
                    continue;

                if (!pluginRef.getDynamicSettingsManager().getBonusDropManager().isBonusDropWhitelisted(is.getType()))
                    continue;

                //If we suspect TEs might be duped only reward block
                if (dontRewardTE) {
                    if (!is.getType().isBlock()) {
                        continue;
                    }
                }

                if (event.getBlock().getMetadata(MetadataConstants.BONUS_DROPS_METAKEY).size() > 0) {
                    BonusDropMeta bonusDropMeta = (BonusDropMeta) event.getBlock().getMetadata(MetadataConstants.BONUS_DROPS_METAKEY).get(0);
                    int bonusCount = bonusDropMeta.asInt();

                    for (int i = 0; i < bonusCount; i++) {
                        event.getBlock().getWorld().dropItemNaturally(event.getBlockState().getLocation(), is);
                    }
                }
            }
        }

        if(event.getBlock().hasMetadata(MetadataConstants.BONUS_DROPS_METAKEY))
            event.getBlock().removeMetadata(MetadataConstants.BONUS_DROPS_METAKEY, pluginRef);
    }

    /**
     * Monitor BlockPistonExtend events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        if(!pluginRef.getConfigManager().getConfigExploitPrevention().doPistonsMarkBlocksUnnatural())
            return;

        BlockFace direction = event.getDirection();
        Block movedBlock = event.getBlock();
//        movedBlock = movedBlock.getRelative(direction, 2);

        for (Block b : event.getBlocks()) {
            if (pluginRef.getBlockTools().shouldBeWatched(b.getState())) {
                movedBlock = b.getRelative(direction);

                pluginRef.getPlaceStore().setTrue(movedBlock);
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
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        if(!pluginRef.getConfigManager().getConfigExploitPrevention().doPistonsMarkBlocksUnnatural()) {
            return;
        }

        // Get opposite direction so we get correct block
        BlockFace direction = event.getDirection();
        Block movedBlock = event.getBlock().getRelative(direction);
        pluginRef.getPlaceStore().setTrue(movedBlock);

        for (Block block : event.getBlocks()) {
            movedBlock = block.getRelative(direction);
            pluginRef.getPlaceStore().setTrue(movedBlock);
        }
    }

    /**
     * Monitor blocks formed by entities (snowmen)
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBlockFormEvent(EntityBlockFormEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        if (pluginRef.getBlockTools().shouldBeWatched(event.getNewState())) {
            pluginRef.getPlaceStore().setTrue(event.getNewState().getBlock());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFormEvent(BlockFormEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;
        
        Block newBlock = event.getNewState().getBlock();
        Material material = newBlock.getType();

        if (pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitSkills().isPreventCobblestoneStoneGeneratorXP()) {
            if (event.getNewState().getType() != Material.OBSIDIAN
                    && pluginRef.getBlockTools().shouldBeWatched(event.getNewState())
                    && pluginRef.getDynamicSettingsManager().getExperienceManager().hasMiningXp(event.getNewState().getBlockData().getMaterial())) { //Hacky fix to prevent trees growing from being marked as unnatural
                pluginRef.getPlaceStore().setTrue(event.getNewState());
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
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        BlockState blockState = event.getBlock().getState();

        /* Check if the blocks placed should be monitored so they do not give out XP in the future */
        if (pluginRef.getBlockTools().shouldBeWatched(blockState)) {
            // Don't count de-barking wood
            if (!Tag.LOGS.isTagged(event.getBlockReplacedState().getType()) || !Tag.LOGS.isTagged(event.getBlockPlaced().getType()))
                pluginRef.getPlaceStore().setTrue(blockState);
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        if (mcMMOPlayer == null)
            return;

        Material repairAnvil = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getRepairBehaviour().getAnvilMaterial();
        Material salvageAnvil = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getSalvageBehaviour().getAnvilMaterial();

        if (blockState.getType() == repairAnvil
                && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.REPAIR, player)) {
            //Make some noise
            mcMMOPlayer.getRepairManager().placedAnvilCheck();

        } else if (blockState.getType() == salvageAnvil
                && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.SALVAGE, player)) {
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
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        for (BlockState replacedBlockState : event.getReplacedBlockStates()) {
            BlockState blockState = replacedBlockState.getBlock().getState();

            /* Check if the blocks placed should be monitored so they do not give out XP in the future */
            if (pluginRef.getBlockTools().shouldBeWatched(blockState)) {
                pluginRef.getPlaceStore().setTrue(blockState);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        BlockState blockState = event.getBlock().getState();

        if (!pluginRef.getBlockTools().shouldBeWatched(blockState)) {
            return;
        }

        pluginRef.getPlaceStore().setFalse(blockState);
    }

    /**
     * Monitor BlockBreak events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        BlockState blockState = event.getBlock().getState();

        if (!pluginRef.getBlockTools().shouldBeWatched(blockState)) {
            return;
        }

        /* ALCHEMY - Cancel any brew in progress for that BrewingStand */
//        if (blockState instanceof BrewingStand && Alchemy.brewingStandMap.containsKey(location)) {
//            Alchemy.brewingStandMap.get(location).cancelBrew();
//        }

        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        //Check if profile is loaded
        if (mcMMOPlayer == null)
            return;

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        /* HERBALISM */
        if (pluginRef.getBlockTools().affectedByGreenTerra(blockState)) {
            HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

            /* Green Terra */
            if (herbalismManager.canActivateAbility()) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            }

            /*
             * We don't check the block store here because herbalism has too many unusual edge cases.
             * Instead, we check it inside the drops handler.
             */
            if (pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.HERBALISM, player)) {
                herbalismManager.processHerbalismBlockBreakEvent(event);
            }
            /*
             * We return here so that we don't unmark any affected blocks
             * due to special checks managing this on their own:
             */
            return;
        }

        /* MINING */
        else if (pluginRef.getBlockTools().affectedBySuperBreaker(blockState) && pluginRef.getItemTools().isPickaxe(heldItem) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.MINING, player) && !pluginRef.getPlaceStore().isTrue(blockState)) {
            MiningManager miningManager = mcMMOPlayer.getMiningManager();
            miningManager.miningBlockCheck(blockState);
        }

        /* WOOD CUTTING */
        else if (pluginRef.getBlockTools().isLog(blockState) && pluginRef.getItemTools().isAxe(heldItem) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.WOODCUTTING, player) && !pluginRef.getPlaceStore().isTrue(blockState)) {
            WoodcuttingManager woodcuttingManager = mcMMOPlayer.getWoodcuttingManager();
            if (woodcuttingManager.canUseTreeFeller(heldItem)) {
                woodcuttingManager.processTreeFeller(blockState);
            } else {
                woodcuttingManager.woodcuttingBlockCheck(blockState);
            }
        }

        /* EXCAVATION */
        else if (pluginRef.getBlockTools().affectedByGigaDrillBreaker(blockState) && pluginRef.getItemTools().isShovel(heldItem) && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.EXCAVATION, player) && !pluginRef.getPlaceStore().isTrue(blockState)) {
            ExcavationManager excavationManager = mcMMOPlayer.getExcavationManager();
            excavationManager.excavationBlockCheck(blockState);

            if (mcMMOPlayer.getSuperAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER)) {
                excavationManager.gigaDrillBreaker(blockState);
            }
        }

        /* Remove metadata from placed watched blocks */
        pluginRef.getPlaceStore().setFalse(blockState);
    }

    /**
     * Handle BlockBreak events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreakHigher(BlockBreakEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockBreakEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        BlockState blockState = event.getBlock().getState();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getHerbalismBehaviour().isRecentlyRegrown(blockState)) {
            event.setCancelled(true);
            return;
        }

        if (pluginRef.getItemTools().isSword(heldItem)) {
            HerbalismManager herbalismManager = pluginRef.getUserManager().getPlayer(player).getHerbalismManager();

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
            SmeltingManager smeltingManager = pluginRef.getUserManager().getPlayer(player).getSmeltingManager();

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
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }
        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        //Profile not loaded
        if (mcMMOPlayer == null) {
            return;
        }

        /*
         * ABILITY PREPARATION CHECKS
         *
         * We check permissions here before processing activation.
         */
        if (pluginRef.getBlockTools().canActivateAbilities(blockState)) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            if (mcMMOPlayer.getToolPreparationMode(ToolType.HOE) && pluginRef.getItemTools().isHoe(heldItem) && (pluginRef.getBlockTools().affectedByGreenTerra(blockState) || pluginRef.getBlockTools().canMakeMossy(blockState)) && pluginRef.getPermissionTools().greenTerra(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.HERBALISM);
            } else if (mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && pluginRef.getItemTools().isAxe(heldItem) && pluginRef.getBlockTools().isLog(blockState) && pluginRef.getPermissionTools().treeFeller(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.WOODCUTTING);
            } else if (mcMMOPlayer.getToolPreparationMode(ToolType.PICKAXE) && pluginRef.getItemTools().isPickaxe(heldItem) && pluginRef.getBlockTools().affectedBySuperBreaker(blockState) && pluginRef.getPermissionTools().superBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.MINING);
            } else if (mcMMOPlayer.getToolPreparationMode(ToolType.SHOVEL) && pluginRef.getItemTools().isShovel(heldItem) && pluginRef.getBlockTools().affectedByGigaDrillBreaker(blockState) && pluginRef.getPermissionTools().gigaDrillBreaker(player)) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.EXCAVATION);
            } else if (mcMMOPlayer.getToolPreparationMode(ToolType.FISTS) && heldItem.getType() == Material.AIR && (pluginRef.getBlockTools().affectedByGigaDrillBreaker(blockState) || blockState.getType() == Material.SNOW || pluginRef.getBlockTools().affectedByBlockCracker(blockState) && pluginRef.getPermissionTools().berserk(player))) {
                mcMMOPlayer.checkAbilityActivation(PrimarySkillType.UNARMED);
            }
        }

        /*
         * TREE FELLER SOUNDS
         *
         * We don't need to check permissions here because they've already been checked for the ability to even activate.
         */
        pluginRef.getSoundManager().sendSound(player, blockState.getLocation(), SoundType.FIZZ);
    }

    private Player getPlayerFromFurnace(Block furnaceBlock) {
        List<MetadataValue> metadata = furnaceBlock.getMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY);

        if (metadata.isEmpty()) {
            return null;
        }

        return pluginRef.getServer().getPlayerExact(metadata.get(0).asString());
    }

    /**
     * Handle BlockDamage events where the event is modified.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamageHigher(BlockDamageEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getBlock().getWorld().getName()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getPlayer()))
                return;
        }

        if (event instanceof FakeBlockDamageEvent) {
            return;
        }

        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
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
        if (mcMMOPlayer.getSuperAbilityMode(SuperAbilityType.GREEN_TERRA) && pluginRef.getBlockTools().canMakeMossy(blockState)) {
            if (mcMMOPlayer.getHerbalismManager().processGreenTerraBlockConversion(blockState)) {
                blockState.update(true);
            }
        } else if (mcMMOPlayer.getSuperAbilityMode(SuperAbilityType.BERSERK) && (heldItem.getType() == Material.AIR || pluginRef.getConfigManager().getConfigUnarmed().doItemsCountAsUnarmed())) {
            if (pluginRef.getSkillTools().superAbilityBlockCheck(SuperAbilityType.BERSERK, block.getState())
                    && pluginRef.getEventManager().simulateBlockBreak(block, player, true)) {
                event.setInstaBreak(true);
                pluginRef.getSoundManager().sendSound(player, block.getLocation(), SoundType.POP);
            } else if (mcMMOPlayer.getUnarmedManager().canUseBlockCracker() && pluginRef.getBlockTools().affectedByBlockCracker(blockState) && pluginRef.getEventManager().simulateBlockBreak(block, player, true)) {
                if (mcMMOPlayer.getUnarmedManager().blockCrackerCheck(blockState)) {
                    blockState.update();
                }
            }
        } else if (mcMMOPlayer.getWoodcuttingManager().canUseLeafBlower(heldItem) && pluginRef.getBlockTools().isLeaves(blockState) && pluginRef.getEventManager().simulateBlockBreak(block, player, true)) {
            event.setInstaBreak(true);
            pluginRef.getSoundManager().sendSound(player, block.getLocation(), SoundType.POP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamageCleanup(BlockDamageEvent event) {
        Player player = event.getPlayer();

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.DEBUG_STICK) {
            debugStickDump(player, event.getBlock().getState());
        }
    }

    //TODO: Rewrite this
    //TODO: Convert into locale strings
    private void debugStickDump(Player player, BlockState blockState) {
        //Profile not loaded
        if(pluginRef.getUserManager().getPlayer(player) == null)
        {
            return;
        }

        if(pluginRef.getUserManager().getPlayer(player).isDebugMode())
        {
            if(pluginRef.getPlaceStore().isTrue(blockState))
                player.sendMessage("[mcMMO DEBUG] This block is not natural and does not reward treasures/XP");
            else
                player.sendMessage("[mcMMO DEBUG] World Guard main flag is DENIED for this player in this region");

            if (pluginRef.getWorldGuardManager().hasXPFlag(player))
                player.sendMessage("[mcMMO DEBUG] World Guard xp flag is permitted for this player in this region");
            else
                player.sendMessage("[mcMMO DEBUG] World Guard xp flag is not permitted for this player in this region");
        }

        if (blockState instanceof Furnace) {
            Furnace furnace = (Furnace) blockState;
            if (furnace.hasMetadata(MetadataConstants.FURNACE_TRACKING_METAKEY)) {
                player.sendMessage("[mcMMO DEBUG] This furnace has a registered owner");
                Player furnacePlayer = getPlayerFromFurnace(furnace.getBlock());
                if (furnacePlayer != null) {
                    player.sendMessage("[mcMMO DEBUG] This furnace is owned by player " + furnacePlayer.getName());
                }
                else
                    player.sendMessage("[mcMMO DEBUG] This furnace does not have a registered owner");
            }

            if(pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceBars().isEnableXPBars())
                player.sendMessage("[mcMMO DEBUG] XP bars are enabled, however you should check per-skill settings to make sure those are enabled.");

            player.sendMessage(ChatColor.RED+"You can turn this debug info off by typing "+ ChatColor.GOLD+"/mmodebug");
        }

        if (pluginRef.getConfigManager().getConfigLeveling().isEnableXPBars())
            player.sendMessage("[mcMMO DEBUG] XP bars are enabled, however you should check per-skill settings to make sure those are enabled.");
    }

}
