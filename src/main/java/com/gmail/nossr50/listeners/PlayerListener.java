package com.gmail.nossr50.listeners;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatManagerFactory;
import com.gmail.nossr50.chat.PartyChatManager;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.HardcoreManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.Motd;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.adapter.SoundAdapter;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.inventory.EquipmentSlot;

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
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || Config.getInstance().getXPAfterTeleportCooldown() <= 0 || event.getFrom().equals(event.getTo())) {
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
        String deathMessage = event.getDeathMessage();

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
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || !SkillType.FISHING.getPermissions(player)) {
            return;
        }

        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        switch (event.getState()) {
            case FISHING:
                if (!Permissions.krakenBypass(player)) {
                    event.setCancelled(fishingManager.exploitPrevention());
                }
                return;

            case CAUGHT_FISH:
                //TODO Update to new API once available! Waiting for case CAUGHT_TREASURE:
                Item fishingCatch = (Item) event.getCaught();

                if (Config.getInstance().getFishingOverrideTreasures() && fishingCatch.getItemStack().getType() != Material.RAW_FISH) {
                    fishingCatch.setItemStack(new ItemStack(Material.RAW_FISH, 1));
                }

                if (Permissions.vanillaXpBoost(player, SkillType.FISHING)) {
                    event.setExpToDrop(fishingManager.handleVanillaXpBoost(event.getExpToDrop()));
                }
                return;

            case IN_GROUND:
                Block block = player.getTargetBlock((HashSet<Material>) null, 100);

                if (fishingManager.canIceFish(block)) {
                    event.setCancelled(true);
                    fishingManager.iceFishing(event.getHook(), block);
                }
                return;

            default:
                return;
        }
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
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player) || !SkillType.FISHING.getPermissions(player)) {
            return;
        }

        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();
        Entity caught = event.getCaught();

        switch (event.getState()) {
            case FISHING:
                if (fishingManager.canMasterAngler()) {
                    fishingManager.masterAngler(event.getHook());
                }
                return;

            case CAUGHT_FISH:
                fishingManager.handleFishing((Item) caught);
                return;

            case CAUGHT_ENTITY:
                if (fishingManager.canShake(caught)) {
                    fishingManager.shakeCheck((LivingEntity) caught);
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
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

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
                player.playSound(player.getLocation(), SoundAdapter.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
                return;
            }
        }

        if ((mcMMOPlayer.isUsingUnarmed() && ItemUtils.isSharable(dropStack) && !Config.getInstance().getUnarmedItemsAsUnarmed()) || mcMMOPlayer.getAbilityMode(AbilityType.BERSERK)) {
            boolean pickupSuccess = Unarmed.handleItemPickup(player.getInventory(), drop);
            boolean cancel = Config.getInstance().getUnarmedItemPickupDisabled() || pickupSuccess;
            event.setCancelled(cancel);

            if (pickupSuccess) {
                player.playSound(player.getLocation(), SoundAdapter.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
                player.updateInventory();
                return;
            }
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

        if (Config.getInstance().getMOTDEnabled() && Permissions.motd(player)) {
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
     * Handle PlayerInteractEvents at the lowest priority.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        MiningManager miningManager = mcMMOPlayer.getMiningManager();
        Block block = event.getClickedBlock();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Material type = block.getType();

                if (!Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial && SkillType.REPAIR.getPermissions(player) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to repair an enchanted item
                        if (!(heldItem.getEnchantments().size() > 0) || repairManager.checkConfirmation(true)) {
                            repairManager.handleRepair(heldItem);
                            player.updateInventory();
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == Salvage.anvilMaterial && SkillType.SALVAGE.getPermissions(player) && mcMMO.getSalvageableManager().isSalvageable(heldItem)) {
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
                    }
                    else {
                        miningManager.remoteDetonation();
                    }
                }

                break;

            case LEFT_CLICK_BLOCK:
                type = block.getType();

                if (!Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial && SkillType.REPAIR.getPermissions(player) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();

                        // Cancel repairing an enchanted item
                        if (repairManager.checkConfirmation(false)) {
                            repairManager.setLastAnvilUse(0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Repair.Pretty.Name")));
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == Salvage.anvilMaterial && SkillType.SALVAGE.getPermissions(player) && mcMMO.getSalvageableManager().isSalvageable(heldItem)) {
                        SalvageManager salvageManager = mcMMOPlayer.getSalvageManager();

                        // Cancel salvaging an enchanted item
                        if (salvageManager.checkConfirmation(false)) {
                            salvageManager.setLastAnvilUse(0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Salvage.Pretty.Name")));
                        }
                    }
                }

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
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }
                
                Block block = event.getClickedBlock();
                BlockState blockState = block.getState();

                /* ACTIVATION & ITEM CHECKS */
                if (BlockUtils.canActivateAbilities(blockState)) {
                    if (Config.getInstance().getAbilitiesEnabled()) {
                        if (BlockUtils.canActivateHerbalism(blockState)) {
                            mcMMOPlayer.processAbilityActivation(SkillType.HERBALISM);
                        }

                        mcMMOPlayer.processAbilityActivation(SkillType.AXES);
                        mcMMOPlayer.processAbilityActivation(SkillType.EXCAVATION);
                        mcMMOPlayer.processAbilityActivation(SkillType.MINING);
                        mcMMOPlayer.processAbilityActivation(SkillType.SWORDS);
                        mcMMOPlayer.processAbilityActivation(SkillType.UNARMED);
                        mcMMOPlayer.processAbilityActivation(SkillType.WOODCUTTING);
                    }

                    ChimaeraWing.activationCheck(player);
                }

                /* GREEN THUMB CHECK */
                HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

                if (herbalismManager.canGreenThumbBlock(blockState)) {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.SEEDS, heldItem.getAmount() - 1));

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

            case RIGHT_CLICK_AIR:
                if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }
                
                /* ACTIVATION CHECKS */
                if (Config.getInstance().getAbilitiesEnabled()) {
                    mcMMOPlayer.processAbilityActivation(SkillType.AXES);
                    mcMMOPlayer.processAbilityActivation(SkillType.EXCAVATION);
                    mcMMOPlayer.processAbilityActivation(SkillType.HERBALISM);
                    mcMMOPlayer.processAbilityActivation(SkillType.MINING);
                    mcMMOPlayer.processAbilityActivation(SkillType.SWORDS);
                    mcMMOPlayer.processAbilityActivation(SkillType.UNARMED);
                    mcMMOPlayer.processAbilityActivation(SkillType.WOODCUTTING);
                }

                /* ITEM CHECKS */
                ChimaeraWing.activationCheck(player);

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
                Material type = heldItem.getType();
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
        }
        else if (mcMMOPlayer.isChatEnabled(ChatMode.ADMIN)) {
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
        if (!Config.getInstance().getLocale().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase();

            // Do these ACTUALLY have to be lower case to work properly?
            for (SkillType skill : SkillType.values()) {
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
        if (!mcMMO.getHolidayManager().isAprilFirst()) {
            return;
        }

        mcMMO.getHolidayManager().handleStatisticEvent(event);
    }
}
