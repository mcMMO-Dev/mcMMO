package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.chat.ChatChannel;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.taming.CallOfTheWildType;
import com.gmail.nossr50.events.McMMOReplaceVanillaTreasureEvent;
import com.gmail.nossr50.events.fake.FakePlayerAnimationEvent;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.ShareHandler;
import com.gmail.nossr50.runnables.MobHealthDisplayUpdaterTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.skills.fishing.FishingManager;
import com.gmail.nossr50.skills.herbalism.HerbalismManager;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.repair.Repair;
import com.gmail.nossr50.skills.repair.RepairManager;
import com.gmail.nossr50.skills.salvage.Salvage;
import com.gmail.nossr50.skills.salvage.SalvageManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.*;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

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
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld())) {
            //Remove scoreboards
            if(mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
                ScoreboardManager.teardownPlayer(event.getPlayer());
            }
            return;
        } else if(WorldBlacklist.isWorldBlacklisted(event.getFrom().getWorld()) && mcMMO.p.getGeneralConfig().getScoreboardsEnabled()) {
            //This only fires if they are travelling to a non-blacklisted world from a blacklisted world

            //Setup scoreboards
            ScoreboardManager.setupPlayer(event.getPlayer());
        }

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || mcMMO.p.getGeneralConfig().getXPAfterTeleportCooldown() <= 0 || event.getFrom().equals(event.getTo())) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        UserManager.getPlayer(player).actualizeTeleportATS();
    }

    /**
     * Handle {@link EntityDamageByEntityEvent} at the highest priority.
     * <p>
     * This handler is used to clear the names of mobs with health bars to
     * fix death messages showing mob health bars on death.
     *
     * @param event the event to listen to
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntityHighest(EntityDamageByEntityEvent event) {
        // we only care about players as this is for fixing player death messages
        if (!(event.getEntity() instanceof Player player))
            return;

        // get the attacker
        LivingEntity attacker;
        if (event.getDamager() instanceof LivingEntity)
            attacker = (LivingEntity) event.getDamager();
        // attempt to find creator of a projectile
        else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity)
            attacker = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
        else
            return;

        if (attacker instanceof HumanEntity)
            return;

        // world blacklist check
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;
        // world guard main flag check
        if (WorldGuardUtils.isWorldGuardLoaded() && !WorldGuardManager.getInstance().hasMainFlag((Player) event.getEntity()))
            return;

        // we only want to handle player deaths
        if ((player.getHealth() - event.getFinalDamage()) > 0)
            return;

        // temporarily clear the mob's name
        new MobHealthDisplayUpdaterTask(attacker).run();

        // set the name back
        new BukkitRunnable() {
            @Override
            public void run() {
                MobHealthbarUtils.handleMobHealthbars(attacker, 0, mcMMO.p);
            }
        }.runTaskLater(mcMMO.p, 1);
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
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        boolean statLossEnabled = HardcoreManager.isStatLossEnabled();
        boolean vampirismEnabled = HardcoreManager.isVampirismEnabled();

        if (!statLossEnabled && !vampirismEnabled) {
            return;
        }

        Player killedPlayer = event.getEntity();

        if (!killedPlayer.hasMetadata(MetadataConstants.METADATA_KEY_PLAYER_DATA) || Permissions.hardcoreBypass(killedPlayer)) {
            return;
        }

        Player killer = killedPlayer.getKiller();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(killedPlayer))
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onPlayerDeathNormal(PlayerDeathEvent playerDeathEvent) {
        SkillUtils.removeAbilityBoostsFromInventory(playerDeathEvent.getEntity());
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

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
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
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(event.getPlayer()))
                return;
        }

        Item drop = event.getItemDrop();
        ItemStack dropStack = drop.getItemStack();

        if (ItemUtils.isSharable(dropStack)) {
            drop.setMetadata(MetadataConstants.METADATA_KEY_TRACKED_ITEM, MetadataConstants.MCMMO_METADATA_VALUE);
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
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishHighest(PlayerFishEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || !mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.FISHING)) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        switch (event.getState()) {
            case CAUGHT_FISH:
                //TODO Update to new API once available! Waiting for case CAUGHT_TREASURE
                if(event.getCaught() != null) {
                    Item fishingCatch = (Item) event.getCaught();

                    if (mcMMO.p.getGeneralConfig().getFishingOverrideTreasures() &&
                            fishingCatch.getItemStack().getType() != Material.SALMON &&
                            fishingCatch.getItemStack().getType() != Material.COD &&
                            fishingCatch.getItemStack().getType() != Material.TROPICAL_FISH &&
                            fishingCatch.getItemStack().getType() != Material.PUFFERFISH) {

                        ItemStack replacementCatch = new ItemStack(Material.SALMON, 1);

                        McMMOReplaceVanillaTreasureEvent replaceVanillaTreasureEvent = new McMMOReplaceVanillaTreasureEvent(fishingCatch, replacementCatch);
                        Bukkit.getPluginManager().callEvent(replaceVanillaTreasureEvent);

                        //Replace
                        replacementCatch = replaceVanillaTreasureEvent.getReplacementItemStack();
                        fishingCatch.setItemStack(replacementCatch);
                    }

                    if (Permissions.vanillaXpBoost(player, PrimarySkillType.FISHING)) {
                        //Don't modify XP below vanilla values
                        if(fishingManager.handleVanillaXpBoost(event.getExpToDrop()) > 1)
                            event.setExpToDrop(fishingManager.handleVanillaXpBoost(event.getExpToDrop()));
                    }
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
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (!UserManager.hasPlayerDataKey(player) || !mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.FISHING)) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        Entity caught = event.getCaught();
        FishingManager fishingManager = UserManager.getPlayer(player).getFishingManager();

        //Track the hook
        if(ExperienceConfig.getInstance().isFishingExploitingPrevented())
        {
            if(event.getHook().getMetadata(MetadataConstants.METADATA_KEY_FISH_HOOK_REF).size() == 0)
            {
                fishingManager.setFishHookReference(event.getHook());
            }

            //Spam Fishing
            if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH && fishingManager.isFishingTooOften())
            {
                event.setExpToDrop(0);

                if(caught instanceof Item caughtItem)
                {
                    caughtItem.remove();
                }

                return;
            }
        }

        switch (event.getState()) {
            case FISHING:
                if (fishingManager.canMasterAngler()) {
                    int lureLevel = 0;
                    ItemStack inHand = player.getInventory().getItemInMainHand();

                    //Grab lure level
                    if(inHand != null && inHand.getType().getKey().getKey().equalsIgnoreCase("fishing_rod")) {
                        if(inHand.getItemMeta().hasEnchants()) {
                            for(Enchantment enchantment : inHand.getItemMeta().getEnchants().keySet()) {
                                if(enchantment.toString().toLowerCase().contains("lure")) {
                                    lureLevel = inHand.getEnchantmentLevel(enchantment);
                                }
                            }
                        }
                    }

                    fishingManager.masterAngler(event.getHook(), lureLevel);
                    fishingManager.setFishingTarget();
                }
                return;
            case CAUGHT_FISH:
                if(caught instanceof Item) {
                    if(ExperienceConfig.getInstance().isFishingExploitingPrevented()) {
                        if (fishingManager.isExploitingFishing(event.getHook().getLocation().toVector())) {
                            player.sendMessage(LocaleLoader.getString("Fishing.ScarcityTip", ExperienceConfig.getInstance().getFishingExploitingOptionMoveRange()));
                            event.setExpToDrop(0);
                            Item caughtItem = (Item) caught;
                            caughtItem.remove();

                            return;
                        }
                    }

                    fishingManager.processFishing((Item) caught);
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
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if(Misc.isNPCEntityExcludingVillagers(event.getEntity())) {
            return;
        }

        if(event.getEntity() instanceof Player player)
        {

            /* WORLD GUARD MAIN FLAG CHECK */
            if(WorldGuardUtils.isWorldGuardLoaded())
            {
                if(!WorldGuardManager.getInstance().hasMainFlag(player))
                    return;
            }

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            //Profile not loaded
            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
            if(mcMMOPlayer == null) {
                return;
            }

            Item drop = event.getItem();
            ItemStack dropStack = drop.getItemStack();

            //Remove tracking
            if(drop.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW)) {
                drop.removeMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW, mcMMO.p);
            }

            if (drop.hasMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM)) {
                if (!player.getName().equals(drop.getMetadata(MetadataConstants.METADATA_KEY_DISARMED_ITEM).get(0).asString())) {
                    event.setCancelled(true);
                }

                return;
            }

            if (!drop.hasMetadata(MetadataConstants.METADATA_KEY_TRACKED_ITEM) && mcMMOPlayer.inParty() && ItemUtils.isSharable(dropStack)) {
                event.setCancelled(ShareHandler.handleItemShare(drop, mcMMOPlayer));

                if (event.isCancelled()) {
                    SoundManager.sendSound(player, player.getLocation(), SoundType.POP);
                }
            }

            /*if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                Unarmed.handleItemPickup(player, event);
                *//*boolean cancel = Config.getInstance().getUnarmedItemPickupDisabled() || pickupSuccess;
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

        //Profile not loaded
        if(mcMMOPlayer == null) {
            return;
        }

        //Use a sync save if the server is shutting down to avoid race conditions
        mcMMOPlayer.logout(mcMMO.isServerShutdownExecuted());
        mcMMO.getTransientMetadataTools().cleanLivingEntityMetadata(event.getPlayer());
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
        new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(mcMMO.p, 60);

        if (mcMMO.p.getGeneralConfig().getMOTDEnabled() && Permissions.motd(player)) {
            Motd.displayAll(player);
        }

        if (plugin.isXPEventEnabled() && mcMMO.p.getGeneralConfig().playerJoinEventInfo()) {
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

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
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
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if(event.getClickedBlock() == null)
            return;

        Block clickedBlock = event.getClickedBlock();
        Material clickedBlockType = clickedBlock.getType();
        //The blacklist contains interactable blocks so its a convenient filter
        if(clickedBlockType == Repair.anvilMaterial || clickedBlockType == Salvage.anvilMaterial) {
            event.setUseItemInHand(Event.Result.ALLOW);

            if(!event.getPlayer().isSneaking() && mcMMO.getMaterialMapStore().isToolActivationBlackListed(clickedBlockType)) {
                event.setUseInteractedBlock(Event.Result.DENY);
            }
        }

        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        MiningManager miningManager = mcMMOPlayer.getMiningManager();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                Material type = clickedBlock.getType();

                if (!mcMMO.p.getGeneralConfig().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial
                            && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.REPAIR)
                            && mcMMO.getRepairableManager().isRepairable(heldItem)
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
                    else if (type == Salvage.anvilMaterial
                            && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.SALVAGE)
                            && RankUtils.hasUnlockedSubskill(player, SubSkillType.SALVAGE_SCRAP_COLLECTOR)
                            && mcMMO.getSalvageableManager().isSalvageable(heldItem)
                            && heldItem.getAmount() <= 1) {
                                SalvageManager salvageManager = UserManager.getPlayer(player).getSalvageManager();
                                event.setCancelled(true);

                                // Make sure the player knows what he's doing when trying to salvage an enchanted item
                                if (salvageManager.checkConfirmation(true)) {
                                    SkillUtils.removeAbilityBoostsFromInventory(player);
                                    salvageManager.handleSalvage(clickedBlock.getLocation(), heldItem);
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
                type = clickedBlock.getType();

                if (!mcMMO.p.getGeneralConfig().getAbilitiesOnlyActivateWhenSneaking() || player.isSneaking()) {
                    /* REPAIR CHECKS */
                    if (type == Repair.anvilMaterial && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.REPAIR) && mcMMO.getRepairableManager().isRepairable(heldItem)) {
                        RepairManager repairManager = mcMMOPlayer.getRepairManager();

                        // Cancel repairing an enchanted item
                        if (repairManager.checkConfirmation(false)) {
                            repairManager.setLastAnvilUse(0);
                            player.sendMessage(LocaleLoader.getString("Skills.Cancelled", LocaleLoader.getString("Repair.Pretty.Name")));
                        }
                    }
                    /* SALVAGE CHECKS */
                    else if (type == Salvage.anvilMaterial && mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.SALVAGE) && mcMMO.getSalvageableManager().isSalvageable(heldItem)) {
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
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getPlayer().getWorld()))
            return;

        Player player = event.getPlayer();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        if (event.getHand() != EquipmentSlot.HAND || !UserManager.hasPlayerDataKey(player) || player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        //Spam Fishing Detection
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
        {
            if(ExperienceConfig.getInstance().isFishingExploitingPrevented()
                       && (heldItem.getType() == Material.FISHING_ROD || player.getInventory().getItemInOffHand().getType() == Material.FISHING_ROD))
            {
                if(player.isInsideVehicle() && (player.getVehicle() instanceof Minecart || player.getVehicle() instanceof PoweredMinecart))
                {
                    player.getVehicle().eject();
                }

                //mcMMOPlayer.getFishingManager().setFishingRodCastTimestamp();
            }
        }

        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK:
                if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                //Hmm
                if(event.getClickedBlock() == null)
                    return;

                Block block = event.getClickedBlock();
                BlockState blockState = block.getState();

                /* ACTIVATION & ITEM CHECKS */
                if (BlockUtils.canActivateTools(blockState)) {
                    if (mcMMO.p.getGeneralConfig().getAbilitiesEnabled()) {
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

                HerbalismManager herbalismManager = mcMMOPlayer.getHerbalismManager();

                FakePlayerAnimationEvent fakeSwing = new FakePlayerAnimationEvent(event.getPlayer()); //PlayerAnimationEvent compat
                if(!event.isCancelled() || event.useInteractedBlock() != Event.Result.DENY) {
                    //TODO: Is this code to set false from bone meal even needed? I'll have to double check later.
                    if (heldItem.getType() == Material.BONE_MEAL) {
                        switch (blockState.getType()) {
                            case BEETROOTS:
                            case CARROT:
                            case COCOA:
                            case WHEAT:
                            case NETHER_WART_BLOCK:
                            case POTATO:
                                mcMMO.getPlaceStore().setFalse(blockState);
                                break;
                        }
                    }

                    if (herbalismManager.canGreenThumbBlock(blockState)) {
                        //call event for Green Thumb Block
                        if(!EventUtils.callSubSkillBlockEvent(player, SubSkillType.HERBALISM_GREEN_THUMB, block).isCancelled()) {
                            Bukkit.getPluginManager().callEvent(fakeSwing);
                            player.getInventory().getItemInMainHand().setAmount(heldItem.getAmount() - 1);
                            player.updateInventory();
                            if (herbalismManager.processGreenThumbBlocks(blockState) && EventUtils.simulateBlockBreak(block, player, false)) {
                                blockState.update(true);
                            }
                        }
                    }
                    /* SHROOM THUMB CHECK */
                    else if (herbalismManager.canUseShroomThumb(blockState)) {
                        if(!EventUtils.callSubSkillBlockEvent(player, SubSkillType.HERBALISM_SHROOM_THUMB, block).isCancelled()) {
                            Bukkit.getPluginManager().callEvent(fakeSwing);
                            event.setCancelled(true);
                            if (herbalismManager.processShroomThumb(blockState)
                                    && EventUtils.simulateBlockBreak(block, player, false)) {
                                blockState.update(true);
                            }
                        }
                    } else {
                        herbalismManager.processBerryBushHarvesting(blockState);
                    }
                }
                break;

            case RIGHT_CLICK_AIR:
                if(player.getInventory().getItemInOffHand().getType() != Material.AIR && !player.isInsideVehicle() && !player.isSneaking()) {
                    break;
                }

                /* ACTIVATION CHECKS */
                if (mcMMO.p.getGeneralConfig().getAbilitiesEnabled()) {
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

                if (type == mcMMO.p.getGeneralConfig().getTamingCOTWMaterial(CallOfTheWildType.WOLF.getConfigEntityTypeEntry())) {
                    tamingManager.summonWolf();
                }
                else if (type == mcMMO.p.getGeneralConfig().getTamingCOTWMaterial(CallOfTheWildType.CAT.getConfigEntityTypeEntry())) {
                    tamingManager.summonOcelot();
                }
                else if (type == mcMMO.p.getGeneralConfig().getTamingCOTWMaterial(CallOfTheWildType.HORSE.getConfigEntityTypeEntry())) {
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

        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(player)) || !UserManager.hasPlayerDataKey(player)) {
            return;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getOfflinePlayer(player);

        if (mcMMOPlayer == null) {
            mcMMO.p.debug(player.getName() + "is chatting, but is currently not logged in to the server.");
            mcMMO.p.debug("Party & Admin chat will not work properly for this player.");
            return;
        }

        if(plugin.getChatManager().isChatChannelEnabled(mcMMOPlayer.getChatChannel())) {
            if(mcMMOPlayer.getChatChannel() != ChatChannel.NONE) {
                if(plugin.getChatManager().isMessageAllowed(mcMMOPlayer)) {
                    //If the message is allowed we cancel this event to avoid double sending messages
                    plugin.getChatManager().processPlayerMessage(mcMMOPlayer, event.getMessage(), event.isAsynchronous());
                    event.setCancelled(true);
                } else {
                    //Message wasn't allowed, remove the player from their channel
                    plugin.getChatManager().setOrToggleChatChannel(mcMMOPlayer, mcMMOPlayer.getChatChannel());
                }
            }
        }
    }

    /**
     * Handle "ugly" aliasing /skillname commands, since setAliases doesn't work.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!mcMMO.p.getGeneralConfig().getLocale().equalsIgnoreCase("en_US")) {
            String message = event.getMessage();
            String command = message.substring(1).split(" ")[0];
            String lowerCaseCommand = command.toLowerCase(Locale.ENGLISH);

            // Do these ACTUALLY have to be lower case to work properly?
            for (PrimarySkillType skill : PrimarySkillType.values()) {
                String skillName = skill.toString().toLowerCase(Locale.ENGLISH);
                String localizedName = mcMMO.p.getSkillTools().getLocalizedSkillName(skill).toLowerCase(Locale.ENGLISH);

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

    /**
     * When a {@link Player} attempts to place an {@link ItemStack}
     * into an {@link ItemFrame}, we want to make sure to remove any
     * Ability buffs from that item.
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
            }
            else {
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
