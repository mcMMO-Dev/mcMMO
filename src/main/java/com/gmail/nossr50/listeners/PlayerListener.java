package com.gmail.nossr50.listeners;

import com.gmail.nossr50.api.FakeBlockBreakEventType;
import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.events.McMMOReplaceVanillaTreasureEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ChimaeraWing;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.HardcoreManager;
import com.gmail.nossr50.util.LogUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.MobHealthbarUtils;
import com.gmail.nossr50.util.Motd;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    // Marks commands that match an English skill name and must be left untouched
    private static final String KEEP_COMMAND = "";

    private final mcMMO plugin;
    private final Map<UUID, EquipmentSlot> fishingHandsByPlayer = new ConcurrentHashMap<>();
    // volatile so command handling on other region threads (Folia) sees a fully built cache
    private volatile SkillCommandAliasCache skillCommandAliases;

    public PlayerListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Localized-skill-command lookup derived from the loaded locale; rebuilt whenever the
     * locale generation changes (e.g. after /mcmmoreloadlocale).
     */
    private record SkillCommandAliasCache(int localeGeneration,
            Map<String, String> replacementByCommand) {
    }

    /**
     * Monitor PlayerTeleportEvents.
     * <p>
     * These events are monitored for the purpose of setting the player's last teleportation
     * timestamp, in order to prevent possible Acrobatics exploitation.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
            //Remove scoreboards
            if (mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.teardownPlayer(event.getPlayer());
            }
            return;
        } else if (WorldBlacklist.isWorldBlacklisted(event.getFrom().getWorld())
                && mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            //This only fires if they are travelling to a non-blacklisted world from a blacklisted world

            //Setup scoreboards
            ScoreboardManager.setupPlayer(event.getPlayer());
        }

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        if (!UserManager.hasPlayerDataKey(player)
                || mcMMO.p.getGeneralConfig().getXPAfterTeleportCooldown() <= 0 || event.getFrom()
                .equals(event.getTo())) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        mmoPlayer.actualizeTeleportATS();
    }

    /**
     * Handle {@link EntityDamageByEntityEvent} at the highest priority.
     * <p>
     * This handler is used to clear the names of mobs with health bars to fix death messages
     * showing mob health bars on death.
     *
     * @param event the event to listen to
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event) {
        // we only care about players as this is for fixing player death messages
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // get the attacker
        LivingEntity attacker;
        if (event.getDamager() instanceof LivingEntity) {
            attacker = (LivingEntity) event.getDamager();
        }
        // attempt to find creator of a projectile
        else if (event.getDamager() instanceof Projectile
                && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
            attacker = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }

        if (attacker instanceof HumanEntity) {
            return;
        }

        // world blacklist check
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        // world guard main flag check
        if (WorldGuardUtils.isWorldGuardLoaded() && !WorldGuardManager.getInstance()
                .hasMainFlag((Player) event.getEntity())) {
            return;
        }

        // we only want to handle player deaths
        if ((player.getHealth() - event.getFinalDamage()) > 0) {
            return;
        }

        // temporarily clear the mob's name
        MobHealthbarUtils.restoreNameFromSnapshot(attacker);

        // set the name back
        mcMMO.p.getFoliaLib().getScheduler().runAtEntityLater(attacker,
                () -> MobHealthbarUtils.handleMobHealthbars(attacker, 0, mcMMO.p), 1);
    }

    /**
     * Monitor PlayerDeathEvents.
     * <p>
     * These events are monitored for the purpose of dealing the penalties associated with hardcore
     * and vampirism modes. If neither of these modes are enabled, or if the player who died has
     * hardcore bypass permissions, this handler does nothing.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeathMonitor(PlayerDeathEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        boolean statLossEnabled = HardcoreManager.isStatLossEnabled();
        boolean vampirismEnabled = HardcoreManager.isVampirismEnabled();

        if (!statLossEnabled && !vampirismEnabled) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (!killedPlayer.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA)
                || Permissions.hardcoreBypass(killedPlayer)) {
            return;
        }

        Player killer = killedPlayer.getKiller();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(killedPlayer)) {
                return;
            }
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDeathNormal(PlayerDeathEvent playerDeathEvent) {
        SkillUtils.removeAbilityBoostsFromInventory(playerDeathEvent.getEntity());
    }

    /**
     * Monitor PlayerChangedWorldEvents.
     * <p>
     * These events are monitored for the purpose of removing god mode or player parties if they are
     * not allowed on the world the player has changed to.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        mmoPlayer.checkGodMode();
        mmoPlayer.checkParty();
    }

    /**
     * Monitor PlayerDropItemEvents.
     * <p>
     * These events are monitored for the purpose of flagging sharable dropped items, as well as
     * removing ability buffs from pickaxes and shovels.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer())) {
                return;
            }
        }

        // TODO: This sharing item system seems very unoptimized, temporarily disabling
        /*if (ItemUtils.isSharable(event.getItemDrop().getItemStack())) {
            event.getItemDrop().getItemStack().setMetadata(
                    MetadataConstants.METADATA_KEY_TRACKED_ITEM,
                    MetadataConstants.MCMMO_METADATA_VALUE);
        }*/

        SkillUtils.removeAbilityBuff(event.getItemDrop().getItemStack());
    }

    /**
     * Handle PlayerFishEvents at the lowest priority.
     * <p>
     * These events are used for tracking fish exploits.
     *
     * @param event The event to modify
     */
    @EventHandler
    public void onPlayerFishLowest(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        if (!(event.getCaught() instanceof Item caughtItem)) {
            return;
        }

        Player player = event.getPlayer();
        final McMMOPlayer mmoPlayer = ListenerGuards.resolveEligiblePlayer(player);

        if (mmoPlayer == null || !mcMMO.p.getSkillTools()
            .doesPlayerHaveSkillPermission(player, PrimarySkillType.FISHING)) {
            return;
        }

        FishingManager fishingManager = mmoPlayer.getFishingManager();

        if (ExperienceConfig.getInstance().isFishingExploitingPrevented()) {

            fishingManager.processExploiting(event.getHook().getLocation().toVector());

            if (fishingManager.isExploitingFishing()) {
                player.sendMessage(LocaleLoader.getString("Fishing.ScarcityTip",
                    ExperienceConfig.getInstance()
                        .getFishingExploitingOptionMoveRange()));
                event.setExpToDrop(0);
                caughtItem.remove();
            }
        }
    }

    /**
     * Handle PlayerFishEvents at the highest priority.
     * <p>
     * These events are used for the purpose of handling our anti-exploit code, as well as dealing
     * with ice fishing.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishHighest(PlayerFishEvent event) {
        Player player = event.getPlayer();
        final McMMOPlayer mmoPlayer = ListenerGuards.resolveEligiblePlayer(player);

        if (mmoPlayer == null || !mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.FISHING)) {
            return;
        }

        FishingManager fishingManager = mmoPlayer.getFishingManager();

        switch (event.getState()) {
            // CAUGHT_FISH happens for any item caught (including junk and treasure)
            case CAUGHT_FISH:
                if (event.getCaught() != null) {
                    Item fishingCatch = (Item) event.getCaught();
                    final Material caughtType = fishingCatch.getItemStack().getType();

                    if (mcMMO.p.getGeneralConfig().getFishingOverrideTreasures() &&
                            caughtType != Material.SALMON &&
                            caughtType != Material.COD &&
                            caughtType != Material.TROPICAL_FISH &&
                            caughtType != Material.PUFFERFISH) {

                        ItemStack replacementCatch = new ItemStack(Material.SALMON, 1);

                        McMMOReplaceVanillaTreasureEvent replaceVanillaTreasureEvent = new McMMOReplaceVanillaTreasureEvent(
                                fishingCatch, replacementCatch, player);
                        Bukkit.getPluginManager().callEvent(replaceVanillaTreasureEvent);

                        //Replace
                        replacementCatch = replaceVanillaTreasureEvent.getReplacementItemStack();
                        fishingCatch.setItemStack(replacementCatch);
                    }

                    if (Permissions.vanillaXpBoost(player, PrimarySkillType.FISHING)) {
                        //Don't modify XP below vanilla values
                        final int boostedXp = fishingManager.handleVanillaXpBoost(
                                event.getExpToDrop());
                        if (boostedXp > 1) {
                            event.setExpToDrop(boostedXp);
                        }
                    }
                }
                return;

            case IN_GROUND:
                Block block = player.getTargetBlock(null, 100);
                EquipmentSlot fishingHand = getFishingHandForEvent(player, event.getHand());

                if (fishingManager.canIceFish(block)) {

                    cancelFishingEventAndDropXp(event, player);

                    fishingManager.iceFishing(event.getHook(), block, fishingHand);
                }
                return;

            default:
        }
    }

    private void cancelFishingEventAndDropXp(PlayerFishEvent event, Player player) {
        event.setCancelled(true);
        ExperienceOrb experienceOrb = (ExperienceOrb) player.getWorld()
                .spawnEntity(player.getEyeLocation(), EntityType.EXPERIENCE_ORB);
        experienceOrb.setExperience(event.getExpToDrop());
    }

    /**
     * Monitor PlayerFishEvents.
     * <p>
     * These events are monitored for the purpose of handling the various Fishing skills and
     * abilities.
     *
     * @param event The event to monitor
     */
    // HIGHEST instead of MONITOR: this handler mutates the event (vanilla XP removal, caught
    // item replacement), which the MONITOR contract forbids and which hid those changes from
    // plugins observing the final result. The name is historical.
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFishMonitor(PlayerFishEvent event) {
        Player player = event.getPlayer();
        final McMMOPlayer mmoPlayer = ListenerGuards.resolveEligiblePlayer(player);

        if (mmoPlayer == null || !mcMMO.p.getSkillTools()
                .doesPlayerHaveSkillPermission(player, PrimarySkillType.FISHING)) {
            return;
        }

        Entity caught = event.getCaught();
        FishingManager fishingManager = mmoPlayer.getFishingManager();

        if (ExperienceConfig.getInstance().isFishingExploitingPrevented()) {
            //Spam Fishing
            if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH
                    && fishingManager.isFishingTooOften()) {
                event.setExpToDrop(0);

                if (caught instanceof Item caughtItem) {
                    caughtItem.remove();
                }

                return;
            }
        }

        switch (event.getState()) {
            case FISHING:
                EquipmentSlot fishingHand = getFishingHandForEvent(player, event.getHand());

                if (fishingManager.canMasterAngler()) {
                    ItemStack inHand = getItemInEventHand(player, fishingHand);

                    if (isFishingRod(inHand)) {
                        int lureLevel = inHand.getEnchantmentLevel(Enchantment.LURE);
                        fishingManager.masterAngler(event.getHook(), lureLevel);
                        fishingManager.setFishingTarget();
                    }
                }
                return;
            case CAUGHT_FISH:
                EquipmentSlot caughtFishingHand = getFishingHandForEvent(player, event.getHand());

                if (caught instanceof Item caughtItem) {
                    fishingManager.processFishing(caughtItem, caughtFishingHand);
                    fishingManager.setFishingTarget();
                }
                return;
            case CAUGHT_ENTITY:
                if (fishingManager.canShake(caught)) {
                    fishingManager.shakeCheck((LivingEntity) caught);
                    fishingManager.setFishingTarget();
                }
                return;
            default:
        }
    }

    static ItemStack getItemInEventHand(Player player, EquipmentSlot hand) {
        if (hand == EquipmentSlot.HAND) {
            return player.getInventory().getItemInMainHand();
        }

        if (hand == EquipmentSlot.OFF_HAND) {
            return player.getInventory().getItemInOffHand();
        }

        return null;
    }

    static boolean isFishingRod(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.FISHING_ROD;
    }

    /**
     * Decides whether a click on the given block is a repair or salvage anvil use. The perform
     * path (right click) additionally requires a single held item and the Scrap Collector rank
     * for salvage; the cancel-confirmation path (left click) does not.
     */
    private AnvilInteraction.Use resolveAnvilUse(Player player, Material clickedType,
            ItemStack heldItem, boolean performingUse) {
        return AnvilInteraction.resolve(clickedType,
                mcMMO.p.getGeneralConfig().getRepairAnvilMaterial(),
                mcMMO.p.getGeneralConfig().getSalvageAnvilMaterial(),
                performingUse, heldItem.getAmount(),
                () -> mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.REPAIR),
                () -> mcMMO.getRepairableManager().isRepairable(heldItem),
                () -> mcMMO.p.getSkillTools()
                        .doesPlayerHaveSkillPermission(player, PrimarySkillType.SALVAGE)
                        && (!performingUse || RankUtils.hasUnlockedSubskill(player,
                                SubSkillType.SALVAGE_SCRAP_COLLECTOR)),
                () -> mcMMO.getSalvageableManager().isSalvageable(heldItem));
    }

    private EquipmentSlot getFishingHandForEvent(Player player, EquipmentSlot eventHand) {
        if (eventHand == EquipmentSlot.HAND || eventHand == EquipmentSlot.OFF_HAND) {
            fishingHandsByPlayer.put(player.getUniqueId(), eventHand);
            return eventHand;
        }

        return fishingHandsByPlayer.get(player.getUniqueId());
    }

    /**
     * Handle PlayerPickupItemEvents at the highest priority.
     * <p>
     * These events are used to clear tracking metadata from picked-up drops and to stop players
     * from picking up items knocked loose by Disarm before their owner can.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (Misc.isNPCEntityExcludingVillagers(event.getEntity())) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            final McMMOPlayer mmoPlayer = ListenerGuards.resolveEligiblePlayer(player);

            if (mmoPlayer == null) {
                return;
            }

            final Item drop = event.getItem();

            //Remove tracking
            if (drop.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW)) {
                drop.removeMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW, mcMMO.p);
            }

            if (drop.hasMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM)) {
                if (!player.getName()
                        .equals(drop.getMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM)
                                .get(0).asString())) {
                    event.setCancelled(true);
                }

            }

        }
    }

    /**
     * Monitor PlayerQuitEvents.
     * <p>
     * These events are monitored for the purpose of resetting player variables and other garbage
     * collection tasks that must take place when a player exits the server.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        fishingHandsByPlayer.remove(player.getUniqueId());

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        //Use a sync save if the server is shutting down to avoid race conditions
        mmoPlayer.logout(mcMMO.isServerShutdownExecuted());
        mcMMO.getTransientMetadataTools().cleanLivingEntityMetadata(event.getPlayer());
    }

    /**
     * Monitor PlayerJoinEvents.
     * <p>
     * These events are monitored for the purpose of initializing player variables, as well as
     * handling the MOTD display and other important join messages.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Delay loading for 3 seconds in case the player has a save task running, its hacky but it should do the trick
        mcMMO.p.getFoliaLib().getScheduler()
                .runLaterAsync(new PlayerProfileLoadingTask(player), 60);

        if (mcMMO.p.getGeneralConfig().getMOTDEnabled() && Permissions.motd(player)) {
            Motd.displayAll(player);
        }

        if (plugin.isXPEventEnabled() && mcMMO.p.getGeneralConfig().playerJoinEventInfo()) {
            player.sendMessage(LocaleLoader.getString("XPRate.Event",
                    ExperienceConfig.getInstance().getExperienceGainsGlobalMultiplier()));
        }
    }

    /**
     * Monitor PlayerRespawnEvents.
     * <p>
     * These events are monitored for the purpose of setting the player's last respawn timestamp, in
     * order to prevent possible exploitation.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        mmoPlayer.actualizeRespawnATS();
    }

    /**
     * Handle PlayerInteractEvents at the lowest priority.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteractLowest(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

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

        if (event.getClickedBlock() == null) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();
        //The blacklist contains interactable blocks so its a convenient filter
        if (clickedBlockType == mcMMO.p.getGeneralConfig().getRepairAnvilMaterial()
                || clickedBlockType == mcMMO.p.getGeneralConfig().getSalvageAnvilMaterial()) {
            event.setUseItemInHand(Event.Result.ALLOW);

            if (!event.getPlayer().isSneaking() && mcMMO.getMaterialMapStore()
                    .isToolActivationBlackListed(clickedBlockType)) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }

        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player)
                || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        MiningManager miningManager = mmoPlayer.getMiningManager();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Material type = clickedBlock.getType();

                if (!mcMMO.p.getGeneralConfig().getAbilitiesOnlyActivateWhenSneaking()
                        || player.isSneaking()) {
                    switch (resolveAnvilUse(player, type, heldItem, true)) {
                        case REPAIR -> {
                            RepairManager repairManager = mmoPlayer.getRepairManager();
                            event.setCancelled(true);

                            // Make sure the player knows what he's doing when trying to repair an enchanted item
                            if (repairManager.checkConfirmation(heldItem, true)) {
                                repairManager.handleRepair(heldItem);
                            }
                        }
                        case SALVAGE -> {
                            SalvageManager salvageManager = mmoPlayer.getSalvageManager();
                            event.setCancelled(true);

                            // Make sure the player knows what he's doing when trying to salvage an enchanted item
                            if (salvageManager.checkConfirmation(heldItem, true)) {
                                SkillUtils.removeAbilityBoostsFromInventory(player);
                                salvageManager.handleSalvage(clickedBlock.getLocation(), heldItem);
                            }
                        }
                        case NONE -> {
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
                type = clickedBlock.getType();

                if (!mcMMO.p.getGeneralConfig().getAbilitiesOnlyActivateWhenSneaking()
                        || player.isSneaking()) {
                    switch (resolveAnvilUse(player, type, heldItem, false)) {
                        case REPAIR -> {
                            RepairManager repairManager = mmoPlayer.getRepairManager();

                            // Cancel repairing an enchanted item
                            if (repairManager.checkConfirmation(false)) {
                                repairManager.setLastAnvilUse(0);
                                player.sendMessage(LocaleLoader.getString("Skills.Cancelled",
                                        LocaleLoader.getString("Repair.Pretty.Name")));
                            }
                        }
                        case SALVAGE -> {
                            SalvageManager salvageManager = mmoPlayer.getSalvageManager();

                            // Cancel salvaging an enchanted item
                            if (salvageManager.checkConfirmation(false)) {
                                salvageManager.setLastAnvilUse(0);
                                player.sendMessage(LocaleLoader.getString("Skills.Cancelled",
                                        LocaleLoader.getString("Salvage.Pretty.Name")));
                            }
                        }
                        case NONE -> {
                        }
                    }
                }

                break;

            default:
                break;
        }
    }

    /**
     * Handle PlayerInteractEvents that need to deny item use during a pending anvil
     * confirmation.
     * <p>
     * The client follows up an anvil click with a use-item action for the same hand, and the
     * server resolves that action as its own interact event (right-click air when its raytrace
     * no longer hits the anvil). Without denying item use here, vanilla behavior such as armor
     * quick-equipping can consume the item mid-confirmation, swapping worn armor into the
     * player's hand.
     * <p>
     * This must be its own handler that does not ignore cancelled events: an interaction with
     * air reports itself as cancelled from the moment it is constructed, so the follow-up
     * use-item event never reaches a handler registered with ignoreCancelled. It also runs
     * above LOWEST so the anvil use-item allowance set there cannot override the denial.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractAnvilConfirmation(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
            return;
        }

        final Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()
                && !WorldGuardManager.getInstance().hasMainFlag(player)) {
            return;
        }

        denyItemUseWhileAnvilConfirmationPending(event, player);
    }

    /**
     * Deny using the held item while a salvage or repair confirmation is pending for it.
     *
     * @param event The event to modify
     * @param player The interacting player
     */
    private void denyItemUseWhileAnvilConfirmationPending(PlayerInteractEvent event,
            Player player) {
        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player)
                || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
        if (mmoPlayer == null) {
            return;
        }

        final ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (mmoPlayer.getSalvageManager().isAwaitingConfirmation(heldItem)
                || mmoPlayer.getRepairManager().isAwaitingConfirmation(heldItem)) {
            event.setUseItemInHand(Event.Result.DENY);
        }
    }

    /**
     * Monitor PlayerInteractEvents.
     *
     * @param event The event to monitor
     */
    // HIGHEST instead of MONITOR: this handler mutates the event and world state (ability
    // activation, item consumption, event cancellation), which the MONITOR contract forbids.
    // ignoreCancelled stays false because interact events arrive "cancelled" for air clicks by
    // design. The name is historical.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractMonitor(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

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

        if (event.getHand() != EquipmentSlot.HAND
                || !UserManager.hasPlayerDataKey(player)
                || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

        //Profile not loaded
        if (mmoPlayer == null) {
            return;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();

        //Spam Fishing Detection
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (ExperienceConfig.getInstance().isFishingExploitingPrevented()
                    && (heldItem.getType() == Material.FISHING_ROD
                    || player.getInventory().getItemInOffHand().getType()
                    == Material.FISHING_ROD)) {
                if (player.isInsideVehicle() && (player.getVehicle() instanceof Minecart
                        || player.getVehicle() instanceof PoweredMinecart)) {
                    player.getVehicle().eject();
                }

                //mmoPlayer.getFishingManager().setFishingRodCastTimestamp();
            }
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR
                        && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                //Hmm
                if (event.getClickedBlock() == null) {
                    return;
                }

                Block block = event.getClickedBlock();
                BlockState blockState = block.getState();

                /* ACTIVATION & ITEM CHECKS */
                if (BlockUtils.canActivateTools(blockState)) {
                    if (mcMMO.p.getGeneralConfig().getAbilitiesEnabled()) {
                        if (BlockUtils.canActivateHerbalism(blockState)) {
                            mmoPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                        }

                        mmoPlayer.processAbilityActivation(PrimarySkillType.AXES);
                        mmoPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                        mmoPlayer.processAbilityActivation(PrimarySkillType.MINING);
                        mmoPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                        mmoPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                        mmoPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                    }

                    ChimaeraWing.activationCheck(player);
                }

                HerbalismManager herbalismManager = mmoPlayer.getHerbalismManager();

                if (!event.isCancelled() || event.useInteractedBlock() != Event.Result.DENY) {
                    if (herbalismManager.canGreenThumbBlock(blockState)) {
                        //call event for Green Thumb Block
                        if (!EventUtils.callSubSkillBlockEvent(player,
                                SubSkillType.HERBALISM_GREEN_THUMB, block).isCancelled()) {
                            // Bukkit.getPluginManager().callEvent(fakeSwing);
                            player.getInventory().getItemInMainHand()
                                    .setAmount(heldItem.getAmount() - 1);
                            if (herbalismManager.processGreenThumbBlocks(blockState)
                                    && EventUtils.simulateBlockBreak(block, player,
                                    FakeBlockBreakEventType.FAKE)) {
                                blockState.update(true);
                            }
                        }
                    }
                    /* SHROOM THUMB CHECK */
                    else if (herbalismManager.canUseShroomThumb(blockState)) {
                        if (!EventUtils.callSubSkillBlockEvent(player,
                                SubSkillType.HERBALISM_SHROOM_THUMB, block).isCancelled()) {
                            // Bukkit.getPluginManager().callEvent(fakeSwing);
                            event.setCancelled(true);
                            if (herbalismManager.processShroomThumb(blockState)
                                    && EventUtils.simulateBlockBreak(block, player,
                                    FakeBlockBreakEventType.FAKE)) {
                                blockState.update(true);
                            }
                        }
                    } else {
                        herbalismManager.processBerryBushHarvesting(blockState);
                    }
                }
                break;

            case RIGHT_CLICK_AIR:
                if (player.getInventory().getItemInOffHand().getType() != Material.AIR
                        && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                /* ACTIVATION CHECKS */
                if (mcMMO.p.getGeneralConfig().getAbilitiesEnabled()) {
                    mmoPlayer.processAbilityActivation(PrimarySkillType.AXES);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.EXCAVATION);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.HERBALISM);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.MINING);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.SWORDS);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.UNARMED);
                    mmoPlayer.processAbilityActivation(PrimarySkillType.WOODCUTTING);
                }

                /* ITEM CHECKS */
                ChimaeraWing.activationCheck(player);

                /* BLAST MINING CHECK */
                MiningManager miningManager = mmoPlayer.getMiningManager();
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
                TamingManager tamingManager = mmoPlayer.getTamingManager();

                if (type == mcMMO.p.getGeneralConfig()
                        .getTamingCOTWMaterial(CallOfTheWildType.WOLF.getConfigEntityTypeEntry())) {
                    tamingManager.summonWolf();
                } else if (type == mcMMO.p.getGeneralConfig()
                        .getTamingCOTWMaterial(CallOfTheWildType.CAT.getConfigEntityTypeEntry())) {
                    tamingManager.summonOcelot();
                } else if (type == mcMMO.p.getGeneralConfig().getTamingCOTWMaterial(
                        CallOfTheWildType.HORSE.getConfigEntityTypeEntry())) {
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

        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(player)) || !UserManager.hasPlayerDataKey(
                player)) {
            return;
        }

        final McMMOPlayer mmoPlayer = UserManager.getOfflinePlayer(player);

        if (mmoPlayer == null) {
            LogUtils.debug(mcMMO.p.getLogger(), player.getName()
                    + "is chatting, but is currently not logged in to the server.");
            LogUtils.debug(mcMMO.p.getLogger(),
                    "Party & Admin chat will not work properly for this player.");
            return;
        }

        if (plugin.getChatManager().isChatChannelEnabled(mmoPlayer.getChatChannel())) {
            if (mmoPlayer.getChatChannel() != ChatChannel.NONE) {
                if (plugin.getChatManager().isMessageAllowed(mmoPlayer)) {
                    //If the message is allowed we cancel this event to avoid double sending messages
                    plugin.getChatManager().processPlayerMessage(mmoPlayer, event.getMessage(),
                            event.isAsynchronous());
                    event.setCancelled(true);
                } else {
                    //Message wasn't allowed, remove the player from their channel
                    plugin.getChatManager()
                            .setOrToggleChatChannel(mmoPlayer, mmoPlayer.getChatChannel());
                }
            }
        }
    }

    /**
     * Handle "ugly" aliasing /skillname commands, since setAliases doesn't work.
     * <p>
     * Runs at LOW priority so command filtering plugins listening at LOWEST (command whitelists,
     * blockers, etc.) inspect the command exactly as the player typed it, while later handlers
     * and the server itself see the rewritten English skill command. Cancelled events are left
     * untouched so a command blocked by such a plugin is not rewritten back into an executable
     * form.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!mcMMO.p.getGeneralConfig().getLocale().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase(Locale.ENGLISH);

            final String replacement = getSkillCommandReplacements().get(lowerCaseCommand);

            if (replacement != null && !replacement.equals(KEEP_COMMAND)) {
                event.setMessage(message.replace(command, replacement));
            }
        }
    }

    private Map<String, String> getSkillCommandReplacements() {
        SkillCommandAliasCache aliases = skillCommandAliases;

        if (aliases == null
                || aliases.localeGeneration() != LocaleLoader.getLocaleGeneration()) {
            aliases = new SkillCommandAliasCache(LocaleLoader.getLocaleGeneration(),
                    buildSkillCommandReplacements());
            skillCommandAliases = aliases;
        }

        return aliases.replacementByCommand();
    }

    private Map<String, String> buildSkillCommandReplacements() {
        final Map<String, String> replacementByCommand = new HashMap<>();

        // Do these ACTUALLY have to be lower case to work properly?
        for (PrimarySkillType skill : PrimarySkillType.values()) {
            String skillName = skill.toString().toLowerCase(Locale.ENGLISH);
            String localizedName = mcMMO.p.getSkillTools().getLocalizedSkillName(skill)
                    .toLowerCase(Locale.ENGLISH);

            // First mapping wins, matching the old first-match-breaks loop order
            replacementByCommand.putIfAbsent(localizedName, skillName);
            replacementByCommand.putIfAbsent(skillName, KEEP_COMMAND);
        }

        return replacementByCommand;
    }

    /**
     * When a {@link Player} attempts to place an {@link ItemStack} into an {@link ItemFrame}, we
     * want to make sure to remove any Ability buffs from that item.
     *
     * @param event The {@link PlayerInteractEntityEvent} to handle
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        /*
         *  We can check for an instance instead of EntityType here, so we are
         *  ready for the infamous "Glow Item Frame" in 1.17 too!
         */
        if (event.getRightClicked() instanceof ItemFrame frame) {

            // Check for existing items (ignore rotations)
            if (frame.getItem().getType() != Material.AIR) {
                return;
            }

            // Get the item the Player is about to place
            ItemStack itemInHand;

            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                itemInHand = event.getPlayer().getInventory().getItemInOffHand();
            } else {
                itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            }

            // and remove any skill ability buffs!
            SkillUtils.removeAbilityBuff(itemInHand);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        SkillUtils.removeAbilityBuff(event.getMainHandItem());
        SkillUtils.removeAbilityBuff(event.getOffHandItem());
    }
}
