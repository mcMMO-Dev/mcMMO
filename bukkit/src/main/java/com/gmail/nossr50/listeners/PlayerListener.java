package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.core.chat.ChatManager;
import com.gmail.nossr50.core.chat.ChatManagerFactory;
import com.gmail.nossr50.core.chat.PartyChatManager;
import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.config.WorldBlacklist;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.chat.ChatMode;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.ShareHandler;
import com.gmail.nossr50.core.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.child.salvage.Salvage;
import com.gmail.nossr50.core.skills.child.salvage.SalvageManager;
import com.gmail.nossr50.core.skills.primary.fishing.FishingManager;
import com.gmail.nossr50.core.skills.primary.herbalism.HerbalismManager;
import com.gmail.nossr50.core.skills.primary.mining.MiningManager;
import com.gmail.nossr50.core.skills.primary.repair.Repair;
import com.gmail.nossr50.core.skills.primary.repair.RepairManager;
import com.gmail.nossr50.core.skills.primary.taming.TamingManager;
import com.gmail.nossr50.core.util.*;
import com.gmail.nossr50.core.util.skills.SkillUtils;
import com.gmail.nossr50.core.util.sounds.SoundManager;
import com.gmail.nossr50.core.util.sounds.SoundType;
import com.gmail.nossr50.core.worldguard.WorldGuardManager;
import com.gmail.nossr50.core.worldguard.WorldGuardUtils;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    private final mcMMO plugin;

    public PlayerListener(final mcMMO plugin) {
        this.plugin = plugin;
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
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || MainConfig.getInstance().getXPAfterTeleportCooldown() <= 0 || event.getFrom().equals(event.getTo())) {
            return;
        }

        UserManager.getPlayer(player).actualizeTeleportATS();
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
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        String deathMessage = event.getDeathMessage();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getEntity()))
                return;
        }

        if (deathMessage == null) {
            return;
        }

        Player player = event.getEntity();
        event.setDeathMessage(MobHealthbarUtils.fixDeathMessage(deathMessage, player));
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
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        boolean statLossEnabled = HardcoreManager.isStatLossEnabled();
        boolean vampirismEnabled = HardcoreManager.isVampirismEnabled();

        if (!statLossEnabled && !vampirismEnabled) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (!killedPlayer.hasMetadata(mcMMO.playerDataKey) || Permissions.hardcoreBypass(killedPlayer)) {
            return;
        }

        Player killer = killedPlayer.getKiller();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(killedPlayer))
                return;
        }

        if (statLossEnabled || (killer != null && vampirismEnabled)) {
            if (EventUtils.callPreDeathPenaltyEvent(killedPlayer).isCancelled()) {
                return;
            }

            if (killer != null && vampirismEnabled) {
                HardcoreManager.invokeVampirism(killer, killedPlayer);
            }

            if (statLossEnabled) {
                HardcoreManager.invokeStatPenalty(killedPlayer);
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

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

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
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        Item drop = event.getItemDrop();
        ItemStack dropStack = drop.getItemStack();

        if (ItemUtils.isSharable(dropStack)) {
            drop.setMetadata(mcMMO.droppedItemKey, mcMMO.metadataValue);
        }

        SkillUtils.removeAbilityBuff(dropStack);
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
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || !PrimarySkillType.FISHING.getPermissions(player)) {
            return;
        }

        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        switch (event.getState()) {
            case CAUGHT_FISH:
                //TODO Update to new API once available! Waiting for case CAUGHT_TREASURE:
                Item fishingCatch = (Item) event.getCaught();

                if (MainConfig.getInstance().getFishingOverrideTreasures() &&
                        fishingCatch.getItemStack().getType() != Material.SALMON &&
                        fishingCatch.getItemStack().getType() != Material.COD &&
                        fishingCatch.getItemStack().getType() != Material.TROPICAL_FISH &&
                        fishingCatch.getItemStack().getType() != Material.PUFFERFISH) {
                    fishingCatch.setItemStack(new ItemStack(Material.SALMON, 1));
                }

                if (Permissions.vanillaXpBoost(player, PrimarySkillType.FISHING)) {
                    //Don't modify XP below vanilla values
                    if (fishingManager.handleVanillaXpBoost(event.getExpToDrop()) > 1)
                        event.setExpToDrop(fishingManager.handleVanillaXpBoost(event.getExpToDrop()));
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
                return;
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
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || !PrimarySkillType.FISHING.getPermissions(player)) {
            return;
        }

        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        Entity caught = event.getCaught();
        //event.setExpToDrop(event.getExpToDrop()); //Redundant?

        switch (event.getState()) {
            case FISHING:
                if (fishingManager.canMasterAngler()) {
                    fishingManager.masterAngler(event.getHook());
                    fishingManager.setFishingTarget();
                }
                return;

            case CAUGHT_FISH:
                if (fishingManager.exploitPrevention(event.getHook().getBoundingBox()))
                    return;
                fishingManager.handleFishing((Item) caught);
                fishingManager.setFishingTarget();
                return;

            case CAUGHT_ENTITY:
                if (fishingManager.canShake(caught)) {
                    fishingManager.shakeCheck((LivingEntity) caught);
                    fishingManager.setFishingTarget();
                }
                return;

            default:
                return;
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
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            /* WORLD GUARD MAIN FLAG CHECK */
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasMainFlag(player))
                    return;
            }

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            Item drop = event.getItem();
            ItemStack dropStack = drop.getItemStack();

            if (drop.hasMetadata(mcMMO.disarmedItemKey)) {
                if (!player.getName().equals(drop.getMetadata(mcMMO.disarmedItemKey).get(0).asString())) {
                    event.setCancelled(true);
                }

                return;
            }

            if (!drop.hasMetadata(mcMMO.droppedItemKey) && mcMMOPlayer.inParty() && ItemUtils.isSharable(dropStack)) {
                event.setCancelled(ShareHandler.handleItemShare(drop, mcMMOPlayer));

                if (event.isCancelled()) {
                    SoundManager.sendSound(player, player.getLocation(), SoundType.POP);
                    return;
                }
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

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        mcMMOPlayer.logout(false);
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading

        if (MainConfig.getInstance().getMOTDEnabled() && Permissions.motd(player)) {
            Motd.displayAll(player);
        }

        if (plugin.isXPEventEnabled()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event", ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()));
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

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        UserManager.getPlayer(player).actualizeRespawnATS();
    }

    /**
     * Handle PlayerInteractEvents at high priority.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
        	return;
        }

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
            	return;
            }
        }
        
        /* PLAYER ELIGABILITY CHECK */
        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        MiningManager miningManager = mcMMOPlayer.getMiningManager();
        Block block = event.getClickedBlock();
        BlockState blockState = block.getState();
        Material type = block.getType();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (!Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial && PrimarySkillType.REPAIR.getPermissions(player) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to repair an enchanted item
                        if (!(heldItem.getEnchantments().size() > 0) || repairManager.checkConfirmation(true)) {
                            repairManager.handleRepair(heldItem);
                            player.updateInventory();
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == Salvage.anvilMaterial && PrimarySkillType.SALVAGE.getPermissions(player) && mcMMO.getSalvageableManager().isSalvageable(heldItem)) {
                        SalvageManager salvageManager = UserManager.getPlayer(player).getSalvageManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to salvage an enchanted item
                        if (!(heldItem.getEnchantments().size() > 0) || salvageManager.checkConfirmation(true)) {
                            SkillUtils.handleAbilitySpeedDecrease(player);
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
                
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }
                
                /* ACTIVATION & ITEM CHECKS */
                if (BlockUtils.canActivateTools(blockState)) {
                    if (Config.getInstance().getAbilitiesEnabled()) {
                        if (BlockUtils.canActivateHerbalism(blockState)) {
                            mcMMOPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                        }
                        
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.AXES);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.MINING);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                        mcMMOPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                    }

                    ChimaeraWing.activationCheck(player);
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
                            mcMMO.getPlaceStore().setFalse(blockState);
                        default:
                        	break;
                    }
                }

                if (herbalismManager.canGreenThumbBlock(blockState)) {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.WHEAT_SEEDS, heldItem.getAmount() - 1));

                    if (herbalismManager.processGreenThumbBlocks(blockState) && EventUtils.simulateBlockBreak(block, player, false)) {
                        blockState.update(true);
                    }
                }

                /* SHROOM THUMB CHECK */
                else if (herbalismManager.canUseShroomThumb(blockState)) {
                    event.setCancelled(true);
                    if (herbalismManager.processShroomThumb(blockState) && EventUtils.simulateBlockBreak(block, player, false)) {
                        blockState.update(true);
                    }
                }
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (!Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial && PrimarySkillType.REPAIR.getPermissions(player) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();

                        // Cancel repairing an enchanted item
                        if (repairManager.checkConfirmation(false)) {
                            repairManager.setLastAnvilUse(0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Repair.Pretty.Name")));
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == Salvage.anvilMaterial && PrimarySkillType.SALVAGE.getPermissions(player) && mcMMO.getSalvageableManager().isSalvageable(heldItem)) {
                        SalvageManager salvageManager = mcMMOPlayer.getSalvageManager();

                        // Cancel salvaging an enchanted item
                        if (salvageManager.checkConfirmation(false)) {
                            salvageManager.setLastAnvilUse(0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Salvage.Pretty.Name")));
                        }
                    }
                }
                
                if (!player.isSneaking()) {
                    break;
                }

                /* CALL OF THE WILD CHECKS */
                TamingManager tamingManager = mcMMOPlayer.getTamingManager();

                if (type == Config.getInstance().getTamingCOTWMaterial(EntityType.WOLF)) {
                    tamingManager.summonWolf();
                }
                else if (type == Config.getInstance().getTamingCOTWMaterial(EntityType.OCELOT)) {
                    tamingManager.summonOcelot();
                }
                else if (type == Config.getInstance().getTamingCOTWMaterial(EntityType.HORSE)) {
                    tamingManager.summonHorse();
                }

                break;
            case RIGHT_CLICK_AIR: // NOTE: DOES NOT RUN IF PLAYER CLICKS WITH EMPTY HAND; CLIENT PACKET LIMITATION
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }
                
                /* ACTIVATION CHECKS */
                if (Config.getInstance().getAbilitiesEnabled()) {
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.AXES);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.MINING);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                    mcMMOPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                }

                /* ITEM CHECKS */
                ChimaeraWing.activationCheck(player);

                /* BLAST MINING CHECK */
                if (miningManager.canDetonate()) {
                    miningManager.remoteDetonation();
                }

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

        if (Misc.isNPCEntity(player) || !UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(player);

        if (mcMMOPlayer == null) {
            mcMMO.p.debug(player.getName() + "is chatting, but is currently not logged in to the server.");
            mcMMO.p.debug("Party & Admin chat will not work properly for this player.");
            return;
        }

        ChatManager chatManager = null;

        if (mcMMOPlayer.isChatEnabled(ChatMode.PARTY)) {
            Party party = mcMMOPlayer.getParty();

            if (party == null) {
                mcMMOPlayer.disableChat(ChatMode.PARTY);
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            chatManager = ChatManagerFactory.getChatManager(plugin, ChatMode.PARTY);
            ((PartyChatManager) chatManager).setParty(party);
        } else if (mcMMOPlayer.isChatEnabled(ChatMode.ADMIN)) {
            chatManager = ChatManagerFactory.getChatManager(plugin, ChatMode.ADMIN);
        }

        if (chatManager != null) {
            chatManager.handleChat(player, event.getMessage(), event.isAsynchronous());
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
        if (!MainConfig.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase();

            // Do these ACTUALLY have to be lower case to work properly?
            for (PrimarySkillType skill : PrimarySkillType.values()) {
                String skillName = skill.toString().toLowerCase();
                String localizedName = skill.getName().toLowerCase();

                if (lowerCaseCommand.equals(localizedName)) {
                    event.setMessage(message.replace(command, skillName));
                    break;
                }

                if (lowerCaseCommand.equals(skillName)) {
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        if (!mcMMO.getHolidayManager().isAprilFirst()) {
            return;
        }

        mcMMO.getHolidayManager().handleStatisticEvent(event);
    }
}
