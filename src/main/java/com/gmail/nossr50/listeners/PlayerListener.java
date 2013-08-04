package com.gmail.nossr50.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatManagerFactory;
import com.gmail.nossr50.chat.PartyChatManager;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.Unarmed;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.HardcoreManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.Motd;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillUtils;

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

        if (Misc.isNPCEntity(player) || !Config.getInstance().getPreventXPAfterTeleport()) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (mcMMOPlayer == null) {
            return;
        }

        mcMMOPlayer.actualizeTeleportATS();
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

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
        if (!Config.getInstance().getHardcoreEnabled()) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (Misc.isNPCEntity(killedPlayer) || Permissions.hardcoreBypass(killedPlayer)) {
            return;
        }

        Player killer = killedPlayer.getKiller();

        if (killer != null && Config.getInstance().getHardcoreVampirismEnabled()) {
            HardcoreManager.invokeVampirism(killer, killedPlayer);
        }

        HardcoreManager.invokeStatPenalty(killedPlayer);
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (mcMMOPlayer == null) {
            return;
        }

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

        if (Misc.isNPCEntity(player) || !Permissions.skillEnabled(player, SkillType.FISHING)) {
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
                if (Permissions.vanillaXpBoost(player, SkillType.FISHING)) {
                    event.setExpToDrop(fishingManager.handleVanillaXpBoost(event.getExpToDrop()));
                }
                return;

            case IN_GROUND:
                Block block = player.getTargetBlock(null, 100);

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

        if (Misc.isNPCEntity(player) || !Permissions.skillEnabled(player, SkillType.FISHING)) {
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        Item drop = event.getItem();
        ItemStack dropStack = drop.getItemStack();

        if (!drop.hasMetadata(mcMMO.droppedItemKey) && mcMMOPlayer.inParty() && ItemUtils.isSharable(dropStack)) {
            event.setCancelled(ShareHandler.handleItemShare(drop, mcMMOPlayer));

            if (event.isCancelled()) {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
                return;
            }
        }

        if ((mcMMOPlayer.isUsingUnarmed() && ItemUtils.isSharable(dropStack)) || mcMMOPlayer.getAbilityMode(AbilityType.BERSERK)) {
            event.setCancelled(Unarmed.handleItemPickup(player.getInventory(), drop));

            if (event.isCancelled()) {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, Misc.POP_VOLUME, Misc.getPopPitch());
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        mcMMOPlayer.resetAbilityMode();
        BleedTimerTask.bleedOut(player);
        mcMMOPlayer.getProfile().save();
        UserManager.remove(player.getName());
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

        UserManager.addUser(player).actualizeRespawnATS();
        ScoreboardManager.enablePowerLevelDisplay(player);

        if (Config.getInstance().getMOTDEnabled() && Permissions.motd(player)) {
            Motd.displayAll(player);
        }

        if (plugin.isXPEventEnabled()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event", Config.getInstance().getExperienceGainsGlobalMultiplier()));
        }

        if (Permissions.updateNotifications(player) && plugin.isUpdateAvailable()) {
            player.sendMessage(LocaleLoader.getString("UpdateChecker.outdated"));
            player.sendMessage(LocaleLoader.getString("UpdateChecker.newavailable"));
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

        if (Misc.isNPCEntity(player)) {
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

        if (Misc.isNPCEntity(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        // TODO: This shouldn't be possible - this is probably a band-aid for a larger issue somewhere else.
        if (mcMMOPlayer == null) {
            return;
        }

        MiningManager miningManager = mcMMOPlayer.getMiningManager();
        Block block = event.getClickedBlock();
        ItemStack heldItem = player.getItemInHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                int blockID = block.getTypeId();

                if (!Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (blockID == Repair.repairAnvilId && Permissions.skillEnabled(player, SkillType.REPAIR) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to repair an enchanted item
                        if (!(heldItem.getEnchantments().size() > 0) || repairManager.checkConfirmation(blockID, true)) {
                            repairManager.handleRepair(heldItem);
                            player.updateInventory();
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (blockID == Repair.salvageAnvilId && Permissions.salvage(player) && Repair.isSalvageable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();
                        event.setCancelled(true);

                        // Make sure the player knows what he's doing when trying to salvage an enchanted item
                        if (!(heldItem.getEnchantments().size() > 0) || repairManager.checkConfirmation(blockID, true)) {
                            repairManager.handleSalvage(block.getLocation(), heldItem);
                            player.updateInventory();
                        }
                    }
                }
                /* BLAST MINING CHECK */
                else if (miningManager.canDetonate()) {
                    if (blockID == Material.TNT.getId()) {
                        event.setCancelled(true); // Don't detonate the TNT if they're too close
                    }
                    else {
                        miningManager.remoteDetonation();
                    }
                }

                break;

            case LEFT_CLICK_BLOCK:
                blockID = block.getTypeId();

                if ((Config.getInstance().getAbilitiesOnlyActivateWhenSneaking() && player.isSneaking()) || !Config.getInstance().getAbilitiesOnlyActivateWhenSneaking()) {
                    /* REPAIR CHECKS */
                    if (blockID == Repair.repairAnvilId && Permissions.skillEnabled(player, SkillType.REPAIR) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();

                        // Cancel repairing an enchanted item
                        if (repairManager.checkConfirmation(blockID, false) && Config.getInstance().getRepairConfirmRequired()) {
                            mcMMOPlayer.setLastAnvilUse(Repair.repairAnvilId, 0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Repair.Pretty.Name")));
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (blockID == Repair.salvageAnvilId && Permissions.salvage(player) && Repair.isSalvageable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();

                        // Cancel salvaging an enchanted item
                        if (repairManager.checkConfirmation(blockID, false) && Config.getInstance().getRepairConfirmRequired()) {
                            mcMMOPlayer.setLastAnvilUse(Repair.salvageAnvilId, 0);
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

        if (Misc.isNPCEntity(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        // TODO: This shouldn't be possible - this is probably a band-aid for a larger issue somewhere else.
        if (mcMMOPlayer == null) {
            return;
        }

        ItemStack heldItem = player.getItemInHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Block block = event.getClickedBlock();
                BlockState blockState = block.getState();

                /* ACTIVATION & ITEM CHECKS */
                if (BlockUtils.canActivateAbilities(blockState)) {
                    if (Config.getInstance().getAbilitiesEnabled()) {
                        if (BlockUtils.canActivateHerbalism(blockState)) {
                            SkillUtils.activationCheck(player, SkillType.HERBALISM);
                        }

                        SkillUtils.activationCheck(player, SkillType.AXES);
                        SkillUtils.activationCheck(player, SkillType.EXCAVATION);
                        SkillUtils.activationCheck(player, SkillType.MINING);
                        SkillUtils.activationCheck(player, SkillType.SWORDS);
                        SkillUtils.activationCheck(player, SkillType.UNARMED);
                        SkillUtils.activationCheck(player, SkillType.WOODCUTTING);
                    }

                    ChimaeraWing.activationCheck(player);
                }

                /* GREEN THUMB CHECK */
                HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

                if (herbalismManager.canGreenThumbBlock(blockState)) {
                    player.setItemInHand(new ItemStack(Material.SEEDS, heldItem.getAmount() - 1));

                    if (herbalismManager.processGreenThumbBlocks(blockState) && SkillUtils.blockBreakSimulate(block, player, false)) {
                        blockState.update(true);
                    }
                }

                /* SHROOM THUMB CHECK */
                else if (herbalismManager.canUseShroomThumb(blockState)) {
                    if (herbalismManager.processShroomThumb(blockState) && SkillUtils.blockBreakSimulate(block, player, false)) {
                        blockState.update(true);
                    }
                }
                break;

            case RIGHT_CLICK_AIR:

                /* ACTIVATION CHECKS */
                if (Config.getInstance().getAbilitiesEnabled()) {
                    SkillUtils.activationCheck(player, SkillType.AXES);
                    SkillUtils.activationCheck(player, SkillType.EXCAVATION);
                    SkillUtils.activationCheck(player, SkillType.HERBALISM);
                    SkillUtils.activationCheck(player, SkillType.MINING);
                    SkillUtils.activationCheck(player, SkillType.SWORDS);
                    SkillUtils.activationCheck(player, SkillType.UNARMED);
                    SkillUtils.activationCheck(player, SkillType.WOODCUTTING);
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

                /* CALL OF THE WILD CHECKS */
                if (player.isSneaking() && Permissions.callOfTheWild(player)) {
                    Material type = heldItem.getType();
                    TamingManager tamingManager = mcMMOPlayer.getTamingManager();

                    if (type == Material.RAW_FISH) {
                        tamingManager.summonOcelot();
                    }
                    else if (type == Material.BONE) {
                        tamingManager.summonWolf();
                    }
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

        if (Misc.isNPCEntity(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (mcMMOPlayer == null) {
            mcMMO.p.debug(player.getName() + " is currently chatting, but has never logged on to the server.");
            return;
        }

        ChatManager chatManager = null;

        if (mcMMOPlayer.getPartyChatMode()) {
            Party party = mcMMOPlayer.getParty();

            if (party == null) {
                mcMMOPlayer.togglePartyChat();
                player.sendMessage(LocaleLoader.getString("Commands.Party.None"));
                return;
            }

            chatManager = ChatManagerFactory.getChatManager(plugin, ChatMode.PARTY);
            ((PartyChatManager) chatManager).setParty(party);
        }
        else if (mcMMOPlayer.getAdminChatMode()) {
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
                String localizedName = SkillUtils.getSkillName(skill).toLowerCase();

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
}
