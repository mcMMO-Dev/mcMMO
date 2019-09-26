package com.gmail.nossr50.listeners;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final mcMMO pluginRef;

    public PlayerListener(final mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Monitor PlayerTeleportEvents.
     * <p>
     * These events are monitored for the purpose of setting the
     * player's last teleportation timestamp, in order to prevent
     * possible Acrobatics exploitation.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName())) {
            //Remove scoreboards
            pluginRef.getScoreboardManager().teardownPlayer(event.getPlayer());
            return;
        } else if(pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getFrom().getWorld().getName())) {
            //This only fires if they are traveling to a non-blacklisted world from a blacklisted world

            //Setup scoreboards
            pluginRef.getScoreboardManager().setupPlayer(event.getPlayer());
        }

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || event.getFrom().equals(event.getTo())) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        if (pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse())
            pluginRef.getUserManager().getPlayer(player).actualizeTeleportATS();


        pluginRef.getUserManager().getPlayer(player).actualizeTeleportATS();
    }
    /**
     * Handle PlayerDeathEvents at the lowest priority.
     * <p>
     * These events are used to modify the death message of a player when
     * needed to correct issues potentially caused by the custom naming used
     * for mob healthbars.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerDeathLowest(PlayerDeathEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getEntity().getWorld().getName()))
            return;

        String deathMessage = event.getDeathMessage();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getEntity()))
                return;
        }

        if (deathMessage == null) {
            return;
        }

        Player player = event.getEntity();
        event.setDeathMessage(pluginRef.getMobHealthBarManager().fixDeathMessage(deathMessage, player));
    }

    /**
     * Monitor PlayerDeathEvents.
     * <p>
     * These events are monitored for the purpose of dealing the penalties
     * associated with hardcore and vampirism modes. If neither of these
     * modes are enabled, or if the player who died has hardcore bypass
     * permissions, this handler does nothing.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeathMonitor(PlayerDeathEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getEntity().getWorld().getName()))
            return;

        boolean statLossEnabled = pluginRef.getHardcoreManager().isStatLossEnabled();
        boolean vampirismEnabled = pluginRef.getHardcoreManager().isVampirismEnabled();

        if (!statLossEnabled && !vampirismEnabled) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (!killedPlayer.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY) || pluginRef.getPermissionTools().hardcoreBypass(killedPlayer)) {
            return;
        }

        Player killer = killedPlayer.getKiller();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(killedPlayer))
                return;
        }

        if (statLossEnabled || (killer != null && vampirismEnabled)) {
            if (pluginRef.getEventManager().callPreDeathPenaltyEvent(killedPlayer).isCancelled()) {
                return;
            }

            if (killer != null && vampirismEnabled) {
                pluginRef.getHardcoreManager().invokeVampirism(killer, killedPlayer);
            }

            if (statLossEnabled) {
                pluginRef.getHardcoreManager().invokeStatPenalty(killedPlayer);
            }
        }
    }

    /**
     * Monitor PlayerChangedWorldEvents.
     * <p>
     * These events are monitored for the purpose of removing god mode or
     * player parties if they are not allowed on the world the player has
     * changed to.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        mcMMOPlayer.checkGodMode();
        mcMMOPlayer.checkParty();
    }

    /**
     * Monitor PlayerDropItemEvents.
     * <p>
     * These events are monitored for the purpose of flagging sharable
     * dropped items, as well as removing ability buffs from pickaxes
     * and shovels.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(event.getPlayer()))
                return;
        }

        Item drop = event.getItemDrop();
        ItemStack dropStack = drop.getItemStack();

        if (pluginRef.getItemTools().isSharable(dropStack)) {
            drop.setMetadata(MetadataConstants.DROPPED_ITEM_TRACKING_METAKEY, MetadataConstants.metadataValue);
        }

        pluginRef.getSkillTools().removeAbilityBuff(dropStack);
    }

    /**
     * Handle PlayerFishEvents at the highest priority.
     * <p>
     * These events are used for the purpose of handling our anti-exploit
     * code, as well as dealing with ice fishing.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFishHighest(PlayerFishEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || !pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.FISHING, player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        FishingManager fishingManager = pluginRef.getUserManager().getPlayer(player).getFishingManager();

        switch (event.getState()) {
            case CAUGHT_FISH:
                //TODO Update to new API once available! Waiting for case CAUGHT_TREASURE:
                Item fishingCatch = (Item) event.getCaught();

                if (pluginRef.getConfigManager().getConfigFishing().isOverrideVanillaTreasures()) {
                    if (fishingCatch.getItemStack().getType() != Material.SALMON &&
                            fishingCatch.getItemStack().getType() != Material.COD &&
                            fishingCatch.getItemStack().getType() != Material.TROPICAL_FISH &&
                            fishingCatch.getItemStack().getType() != Material.PUFFERFISH) {
                        fishingCatch.setItemStack(new ItemStack(Material.SALMON, 1));
                    }
                }

                if (pluginRef.getPermissionTools().isSubSkillEnabled(player, SubSkillType.FISHING_INNER_PEACE)) {
                    //Don't modify XP below vanilla values
                    if (fishingManager.addInnerPeaceVanillaXPBoost(event.getExpToDrop()) > 1)
                        event.setExpToDrop(fishingManager.addInnerPeaceVanillaXPBoost(event.getExpToDrop()));
                }
                return;

            case IN_GROUND:
                Block block = player.getTargetBlock(null, 100);

                if (fishingManager.canIceFish(block)) {

                    cancelFishingEventAndDropXp(event, player);

                    fishingManager.iceFishing(event.getHook(), block);
                }
                return;

            default:
        }
    }

    private void cancelFishingEventAndDropXp(PlayerFishEvent event, Player player) {
        event.setCancelled(true);
        ExperienceOrb experienceOrb = (ExperienceOrb) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(event.getExpToDrop());
    }

    /**
     * Monitor PlayerFishEvents.
     * <p>
     * These events are monitored for the purpose of handling the various
     * Fishing skills and abilities.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFishMonitor(PlayerFishEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (!pluginRef.getUserManager().hasPlayerDataKey(player) || !pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.FISHING, player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        Entity caught = event.getCaught();
        FishingManager fishingManager = pluginRef.getUserManager().getPlayer(player).getFishingManager();

        //Track the hook
        if (pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitFishing().isPreventFishingExploits()) {
            if (event.getHook().getMetadata(MetadataConstants.FISH_HOOK_REF_METAKEY).size() == 0) {
                fishingManager.setFishHookReference(event.getHook());
            }

            //Spam Fishing
            if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && fishingManager.isFishingTooOften()) {
                event.setExpToDrop(0);
                fishingManager.setFishHookReference(event.getHook());
            }

            //Spam Fishing
            if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && fishingManager.isFishingTooOften()) {
                event.setExpToDrop(0);

                if (caught instanceof Item) {
                    Item caughtItem = (Item) caught;
                    caughtItem.remove();
                }

                return;
            }
        }


        switch (event.getState()) {
            case FISHING:
                if (fishingManager.canMasterAngler()) {
                    fishingManager.masterAngler(event.getHook());
                    fishingManager.setFishingTarget();
                }
                return;
            case CAUGHT_FISH:
                if (pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitFishing().isPreventFishingExploits()) {
                    if (fishingManager.isExploitingFishing(event.getHook().getLocation().toVector())) {
                        player.sendMessage(pluginRef.getLocaleManager().getString("Fishing.ScarcityTip", pluginRef.getConfigManager().getConfigExploitPrevention().getOverFishingAreaSize() * 2));
                        event.setExpToDrop(0);
                        Item caughtItem = (Item) caught;
                        caughtItem.remove();
                        return;
                    }
                }

                fishingManager.handleFishing((Item) caught);
                fishingManager.setFishingTarget();
                //fishingManager.setFishHookReference(null);
                return;
            case CAUGHT_ENTITY:
                if (fishingManager.canShake(caught)) {
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
                    //TODO: SHAKE REWRITE
//                    fishingManager.shakeCheck((LivingEntity) caught);
                    fishingManager.setFishingTarget();
                }
                return;
            default:
        }
    }

    /**
     * Handle PlayerPickupItemEvents at the highest priority.
     * <p>
     * These events are used to handle item sharing between party members and
     * are also used to handle item pickup for the Unarmed skill.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getEntity().getWorld().getName()))
            return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            /* WORLD GUARD MAIN FLAG CHECK */
            if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
                if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                    return;
            }

            if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
                return;
            }

            //Profile not loaded
            if (pluginRef.getUserManager().getPlayer(player) == null) {
                return;
            }

            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

            Item drop = event.getItem();
            //Remove tracking
            ItemStack dropStack = drop.getItemStack();
            if(drop.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
                drop.removeMetadata(MetadataConstants.ARROW_TRACKER_METAKEY, pluginRef);
            }

            if (drop.hasMetadata(MetadataConstants.DISARMED_ITEM_METAKEY)) {
                if (!player.getName().equals(drop.getMetadata(MetadataConstants.DISARMED_ITEM_METAKEY).get(0).asString())) {
                    event.setCancelled(true);
                }

                return;
            }


            if (!drop.hasMetadata(MetadataConstants.DROPPED_ITEM_TRACKING_METAKEY) && mcMMOPlayer.inParty() && pluginRef.getItemTools().isSharable(dropStack)) {
                event.setCancelled(mcMMOPlayer.getParty().getShareHandler().handleItemShare(drop, mcMMOPlayer));

                pluginRef.getSoundManager().sendSound(player, player.getLocation(), SoundType.POP);
            }

            /*if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                Unarmed.handleItemPickup(player, event);
                *//*boolean cancel = MainConfig.getInstance().getUnarmedItemPickupDisabled() || pickupSuccess;
                event.setCancelled(cancel);

                if (pickupSuccess) {

                    return;
                }*//*
            }*/
        }
    }

    /**
     * Monitor PlayerQuitEvents.
     * <p>
     * These events are monitored for the purpose of resetting player
     * variables and other garbage collection tasks that must take place when
     * a player exits the server.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        //There's an issue with using Async saves on player quit
        //Basically there are conditions in which an async task does not execute fast enough to save the data if the server shutdown shortly after this task was scheduled
        mcMMOPlayer.logout(true);
    }

    /**
     * Monitor PlayerJoinEvents.
     * <p>
     * These events are monitored for the purpose of initializing player
     * variables, as well as handling the MOTD display and other important
     * join messages.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Delay loading for 3 seconds in case the player has a save task running, its hacky but it should do the trick
        new PlayerProfileLoadingTask(pluginRef, player).runTaskLaterAsynchronously(pluginRef, 60);

        if (pluginRef.getConfigManager().getConfigMOTD().isEnableMOTD()) {
            pluginRef.getMessageOfTheDayUtils().displayAll(player);
        }

        if (pluginRef.isXPEventEnabled()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("XPRate.Event", pluginRef.getDynamicSettingsManager().getExperienceManager().getGlobalXpMult()));
        }

        //TODO: Remove this warning after 2.2 is done
        if (pluginRef.getDescription().getVersion().contains("SNAPSHOT")) {
            event.getPlayer().sendMessage(ChatColor.RED + "WARNING: " + ChatColor.WHITE + "This dev build version of mcMMO is in the MIDDLE of completely rewriting the configs, there may be game breaking bugs. It is not recommended to play on this version of mcMMO, please grab the latest stable release from https://www.mcmmo.org and use that instead!");
        }

        if (pluginRef.isXPEventEnabled() && pluginRef.getConfigManager().getConfigEvent().isShowXPRateInfoOnPlayerJoin()) {
            player.sendMessage(pluginRef.getLocaleManager().getString("XPRate.Event", pluginRef.getDynamicSettingsManager().getExperienceManager().getGlobalXpMult()));
        }
    }

    /**
     * Monitor PlayerRespawnEvents.
     * <p>
     * These events are monitored for the purpose of setting the
     * player's last respawn timestamp, in order to prevent
     * possible exploitation.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        pluginRef.getUserManager().getPlayer(player).actualizeRespawnATS();
    }

    /**
     * Handle PlayerInteractEvents at the lowest priority.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (event.getHand() != EquipmentSlot.HAND || !pluginRef.getUserManager().hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        MiningManager miningManager = mcMMOPlayer.getMiningManager();
        Block block = event.getClickedBlock();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Material type = block.getType();

                if (!pluginRef.getConfigManager().getConfigSuperAbilities().isMustSneakToActivate() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getRepairBehaviour().getAnvilMaterial()
                            && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.REPAIR, player)
                            && pluginRef.getRepairableManager().isRepairable(heldItem)
                            && heldItem.getAmount() <= 1) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to repair an enchanted item
                        if (repairManager.checkConfirmation(true)) {
                            repairManager.handleRepair(heldItem);
                            player.updateInventory();
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getSalvageBehaviour().getAnvilMaterial()
                            && pluginRef.getSkillTools().doesPlayerHaveSkillPermission(PrimarySkillType.SALVAGE, player)
                            && pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR)
                            && pluginRef.getSalvageableManager().isSalvageable(heldItem)
                            && heldItem.getAmount() <= 1) {
                                SalvageManager salvageManager = pluginRef.getUserManager().getPlayer(player).getSalvageManager();
                                event.setCancelled(true);

                                // Make sure the player knows what he's doing when trying to salvage an enchanted item
                                if (salvageManager.checkConfirmation(true)) {
                                    pluginRef.getSkillTools().handleAbilitySpeedDecrease(player);
                                    salvageManager.handleSalvage(block.getLocation(), heldItem);
                                    player.updateInventory();
                                }
                    }
                }
                /* BLAST MINING CHECK */
                else if (miningManager.canDetonate()) {
                    if (type == Material.TNT) {
                        event.setCancelled(true); // Don't detonate the TNT if they're too close
                    } else {
                        miningManager.remoteDetonation();
                    }
                }

                break;

            case LEFT_CLICK_BLOCK:
                //TODO: Not sure why this code is here, I disabled it for now.
                //TODO: Not sure why this code is here, I disabled it for now.
                //TODO: Not sure why this code is here, I disabled it for now.
//                type = block.getType();
//                if (!pluginRef.getConfigManager().getConfigSuperAbilities().isMustSneakToActivate() || player.isSneaking()) {
//                    /* REPAIR CHECKS */
//                    if (type == Repair.getInstance().getAnvilMaterial() && PrimarySkillType.REPAIR.doesPlayerHaveSkillPermission(player) && pluginRef.getRepairableManager().isRepairable(heldItem)) {
//                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
//
//                        // Cancel repairing an enchanted item
//                        if (repairManager.checkConfirmation(false)) {
//                            repairManager.setLastAnvilUse(0);
//                            player.sendMessage(pluginRef.getLocaleManager().getString("Skills.Cancelled", pluginRef.getLocaleManager().getString("Repair.Pretty.Name")));
//                        }
//                    }
//                    /* SALVAGE CHECKS */
//                    else if (type == Salvage.anvilMaterial && PrimarySkillType.SALVAGE.doesPlayerHaveSkillPermission(player) && pluginRef.getSalvageableManager().isSalvageable(heldItem)) {
//                        SalvageManager salvageManager = mcMMOPlayer.getSalvageManager();
//
//                        // Cancel salvaging an enchanted item
//                        if (salvageManager.checkConfirmation(false)) {
//                            salvageManager.setLastAnvilUse(0);
//                            player.sendMessage(pluginRef.getLocaleManager().getString("Skills.Cancelled", pluginRef.getLocaleManager().getString("Salvage.Pretty.Name")));
//                        }
//                    }
//                }

                break;

            default:
                break;
        }
    }

    /**
     * Monitor PlayerInteractEvents.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractMonitor(PlayerInteractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (pluginRef.getDynamicSettingsManager().isWorldBlacklisted(event.getPlayer().getWorld().getName()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (pluginRef.getWorldGuardUtils().isWorldGuardLoaded()) {
            if (!pluginRef.getWorldGuardManager().hasMainFlag(player))
                return;
        }

        if (event.getHand() != EquipmentSlot.HAND || !pluginRef.getUserManager().hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if (pluginRef.getUserManager().getPlayer(player) == null) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        //Spam Fishing Detection
        if (pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitFishing().isPreventFishingExploits()
            && (heldItem.getType() == Material.FISHING_ROD || player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isInsideVehicle() && (player.getVehicle() instanceof Minecart || player.getVehicle() instanceof PoweredMinecart)) {
                    player.getVehicle().eject();
                    player.setVelocity(player.getEyeLocation().getDirection().multiply(10));
                }

                mcMMOPlayer.getFishingManager().setFishingRodCastTimestamp();
            }
        }


        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                Block block = event.getClickedBlock();
                BlockState blockState = block.getState();

                /* ACTIVATION & ITEM CHECKS */
                if (pluginRef.getBlockTools().canActivateTools(blockState)) {
                    if (pluginRef.getConfigManager().getConfigSuperAbilities().isSuperAbilitiesEnabled()) {
                        if (pluginRef.getBlockTools().canActivateHerbalism(blockState)) {
                            mcMMOPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                        }

                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.AXES);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.MINING);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                    }

                    //TODO: Something needs to be done about this
                    ChimaeraWing chimaeraWing = new ChimaeraWing(pluginRef, mcMMOPlayer);
                    chimaeraWing.activationCheck();
                }

                /* GREEN THUMB CHECK */
                HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

                if (heldItem.getType() == Material.BONE_MEAL) {
                    switch (blockState.getType()) {
                        case BEETROOTS:
                        case CARROT:
                        case COCOA:
                        case WHEAT:
                        case NETHER_WART_BLOCK:
                        case POTATO:
                            pluginRef.getPlaceStore().setFalse(blockState);
                    }
                }

                FakePlayerAnimationEvent fakeSwing = new FakePlayerAnimationEvent(event.getPlayer()); //PlayerAnimationEvent compat        
                if (herbalismManager.canGreenThumbBlock(blockState)) {
                    Bukkit.getPluginManager().callEvent(fakeSwing);
                    player.getInventory().setItemInMainHand(new ItemStack(Material.WHEAT_SEEDS, heldItem.getAmount() - 1));
                    if (herbalismManager.processGreenThumbBlocks(blockState) && pluginRef.getEventManager().simulateBlockBreak(block, player, false)) {
                        blockState.update(true);
                    }
                }
                /* SHROOM THUMB CHECK */
                else if (herbalismManager.canUseShroomThumb(blockState)) {
                    Bukkit.getPluginManager().callEvent(fakeSwing);
                    event.setCancelled(true);
                    if (herbalismManager.processShroomThumb(blockState) && pluginRef.getEventManager().simulateBlockBreak(block, player, false)) {
                        blockState.update(true);
                    }
                }
                break;

            case RIGHT_CLICK_AIR:
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                /* ACTIVATION CHECKS */
                if (pluginRef.getConfigManager().getConfigSuperAbilities().isSuperAbilitiesEnabled()) {
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.AXES);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.MINING);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                }

                /* ITEM CHECKS */
                //TODO: Something needs to be done about this
                ChimaeraWing chimaeraWing = new ChimaeraWing(pluginRef, mcMMOPlayer);
                chimaeraWing.activationCheck();

                /* BLAST MINING CHECK */
                MiningManager miningManager = mcMMOPlayer.getMiningManager();
                if (miningManager.canDetonate()) {
                    miningManager.remoteDetonation();
                }

                break;

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:

                if (!player.isSneaking()) {
                    break;
                }

                /* CALL OF THE WILD CHECKS */
                TamingManager tamingManager = mcMMOPlayer.getTamingManager();
                tamingManager.processCallOfTheWild();

                break;

            default:
                break;
        }
    }

    /**
     * Handle PlayerChatEvents at high priority.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (pluginRef.getMiscTools().isNPCEntityExcludingVillagers(player) || !pluginRef.getUserManager().hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getOfflinePlayer(player);

        if (mcMMOPlayer == null) {
            pluginRef.debug(player.getName() + "is chatting, but is currently not logged in to the server.");
            pluginRef.debug("Party & Admin chat will not work properly for this player.");
            return;
        }

        ChatManager chatManager = null;

        if (mcMMOPlayer.isChatEnabled(ChatMode.PARTY)) {
            Party party = mcMMOPlayer.getParty();

            if (party == null) {
                mcMMOPlayer.disableChat(ChatMode.PARTY);
                player.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.None"));
                return;
            }

            pluginRef.getChatManager().processPartyChat(party, player, event.getMessage());
            event.setCancelled(true);
        } else if (mcMMOPlayer.isChatEnabled(ChatMode.ADMIN)) {
            pluginRef.getChatManager().processAdminChat(player, event.getMessage());
            event.setCancelled(true);
        }
    }

    /**
     * Handle "ugly" aliasing /skillname commands, since setAliases doesn't work.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!pluginRef.getConfigManager().getConfigLanguage().getTargetLanguage().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase();

            // Do these ACTUALLY have to be lower case to work properly?
            for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
                String skillName = primarySkillType.toString().toLowerCase();
                String localizedName = pluginRef.getSkillTools().getLocalizedSkillName(primarySkillType).toLowerCase();

                if (command.equalsIgnoreCase(localizedName)) {
                    event.setMessage(message.replace(command, skillName));
                    break;
                }

                if (lowerCaseCommand.equals(skillName)) {
                    break;
                }
            }
        }
    }
}
