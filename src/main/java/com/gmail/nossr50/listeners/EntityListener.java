package com.gmail.nossr50.listeners;

import static com.gmail.nossr50.util.AttributeMapper.MAPPED_MAX_HEALTH;
import static com.gmail.nossr50.util.MobMetadataUtils.addMobFlags;
import static com.gmail.nossr50.util.MobMetadataUtils.flagMetadata;
import static com.gmail.nossr50.util.MobMetadataUtils.hasMobFlag;
import static com.gmail.nossr50.util.MobMetadataUtils.hasMobFlags;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.events.fake.FakeEntityTameEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.metadata.MobMetaFlagType;
import com.gmail.nossr50.runnables.TravelingBlockMetaCleanup;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.crossbows.Crossbows;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.MetadataConstants;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ProjectileUtils;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class EntityListener implements Listener {
    private final mcMMO pluginRef;

    /**
     * We can use this {@link NamespacedKey} for {@link Enchantment} comparisons to check if a
     * {@link Player} has a {@link Trident} enchanted with "Piercing".
     */
    private final NamespacedKey piercingEnchantment = NamespacedKey.minecraft("piercing");
    private final static Set<EntityType> TRANSFORMABLE_ENTITIES
            = Set.of(EntityType.SLIME, EntityType.MAGMA_CUBE);

    public EntityListener(final mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTransform(EntityTransformEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {

            //Transfer metadata keys from mob-spawned mobs to new mobs
            if (hasMobFlags(livingEntity)) {
                for (Entity entity : event.getTransformedEntities()) {
                    if (entity instanceof LivingEntity transformedEntity) {
                        addMobFlags(livingEntity, transformedEntity);
                    }
                }
            }

            // Clear the original slime/magma cubes metadata - it's dead.
            if (TRANSFORMABLE_ENTITIES.contains(livingEntity.getType())) {
                mcMMO.getTransientMetadataTools().cleanLivingEntityMetadata(livingEntity);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
        if (!ExperienceConfig.getInstance().isEndermanEndermiteFarmingPrevented()) {
            return;
        }

        //It's rare but targets can be null sometimes
        if (event.getTarget() == null) {
            return;
        }

        //Prevent entities from giving XP if they target endermite
        if (event.getTarget() instanceof Endermite) {
            if (event.getEntity() instanceof Enderman enderman) {

                if (!hasMobFlag(MobMetaFlagType.EXPLOITED_ENDERMEN, enderman)) {
                    flagMetadata(MobMetaFlagType.EXPLOITED_ENDERMEN, enderman);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onEntityShootBow(EntityShootBowEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            Entity projectile = event.getProjectile();

            //Should be noted that there are API changes regarding Arrow from 1.13.2 to current versions of the game
            if (!(projectile instanceof Arrow arrow)) {
                return;
            }

            ItemStack bow = event.getBow();

            if (bow == null) {
                return;
            }

            if (bow.containsEnchantment(mcMMO.p.getEnchantmentMapper().getInfinity())) {
                projectile.setMetadata(MetadataConstants.METADATA_KEY_INF_ARROW,
                        MetadataConstants.MCMMO_METADATA_VALUE);
            }

            // Set BowType, Force, and Distance metadata
            projectile.setMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE,
                    new FixedMetadataValue(pluginRef, Math.min(
                            event.getForce() * mcMMO.p.getAdvancedConfig().getForceMultiplier(),
                            1.0)));
            projectile.setMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE,
                    new FixedMetadataValue(pluginRef, arrow.getLocation()));

            //Cleanup metadata in 1 minute in case normal collection falls through
            CombatUtils.delayArrowMetaCleanup(arrow);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (event.getEntity().getShooter() instanceof Player player) {

            /* WORLD GUARD MAIN FLAG CHECK */
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                    return;
                }
            }

            if (event.getEntity() instanceof Arrow arrow) {
                // Delayed metadata cleanup in case other cleanup hooks fail
                CombatUtils.delayArrowMetaCleanup(arrow);

                // If fired from an item with multi-shot, we need to track
                if (ItemUtils.doesPlayerHaveEnchantmentInHands(player, "multishot")) {
                    arrow.setMetadata(MetadataConstants.METADATA_KEY_MULTI_SHOT_ARROW,
                            MetadataConstants.MCMMO_METADATA_VALUE);
                }

                if (!arrow.hasMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE)) {
                    arrow.setMetadata(MetadataConstants.METADATA_KEY_BOW_FORCE,
                            new FixedMetadataValue(pluginRef, 1.0));
                }

                if (!arrow.hasMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE)) {
                    arrow.setMetadata(MetadataConstants.METADATA_KEY_ARROW_DISTANCE,
                            new FixedMetadataValue(pluginRef, arrow.getLocation()));
                }

                //Check both hands
                if (ItemUtils.doesPlayerHaveEnchantmentInHands(player, "piercing")) {
                    return;
                }

                if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.ARCHERY_ARROW_RETRIEVAL,
                        UserManager.getPlayer(player))) {
                    arrow.setMetadata(MetadataConstants.METADATA_KEY_TRACKED_ARROW,
                            MetadataConstants.MCMMO_METADATA_VALUE);
                }
            }
        }
    }

    /**
     * Monitor EntityChangeBlock events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        Block block = event.getBlock();
        Entity entity = event.getEntity();
        Material notYetReplacedType = block.getState()
                .getType(); //because its from getState() this is the block that hasn't been changed yet, which is likely air/lava/water etc

        // When the event is fired for the falling block that changes back to a
        // normal block
        // event.getBlock().getType() returns AIR
        if (!BlockUtils.shouldBeWatched(block.getState())
                && notYetReplacedType != Material.WATER && notYetReplacedType != Material.LAVA
                && block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR) {
            return;
        }
        //I could just have it mark all blocks after this but it would potentially cause some really edge case consistency issues that no one would notice

        /*
         * This mess of code tries to avoid marking the moved block as true in our place store
         * It's a headache to read but it works, I'm tempted to just remove it
         */
        if (entity instanceof FallingBlock || entity instanceof Enderman) {
            boolean isTracked = entity.hasMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK);

            if (mcMMO.getUserBlockTracker().isIneligible(block) && !isTracked) {
                mcMMO.getUserBlockTracker().setEligible(block);

                entity.setMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK,
                        MetadataConstants.MCMMO_METADATA_VALUE);
                TravelingBlockMetaCleanup metaCleanupTask = new TravelingBlockMetaCleanup(entity,
                        pluginRef);
                mcMMO.p.getFoliaLib().getScheduler().runAtEntityTimer(entity, metaCleanupTask, 20,
                        20 * 60); //6000 ticks is 5 minutes
            } else if (isTracked) {
                BlockUtils.setUnnaturalBlock(block);
                entity.removeMetadata(MetadataConstants.METADATA_KEY_TRAVELING_BLOCK, pluginRef);
            }
        } else if ((block.getType() == Material.REDSTONE_ORE || block.getType().getKey().getKey()
                .equalsIgnoreCase("deepslate_redstone_ore"))) {
            //Redstone ore fire this event and should be ignored
        } else {
            if (mcMMO.getUserBlockTracker().isIneligible(block)) {
                mcMMO.getUserBlockTracker().setEligible(block);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event) {
        //Prevent players from setting fire to each other if they are in the same party
        if (event.getEntity() instanceof Player defender) {

            if (event.getCombuster() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player attacker) {
                    if (checkIfInPartyOrSamePlayer(event, defender, attacker)) {
                        event.setCancelled(true);
                    }
                }
            } else if (event.getCombuster() instanceof Player attacker) {
                if (checkIfInPartyOrSamePlayer(event, defender, attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Handle EntityDamageByEntity events that involve modifying the event.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (CombatUtils.hasIgnoreDamageMetadata(livingEntity)) {
                return;
            }
        }

        double damage = event.getFinalDamage();
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();

        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (attacker instanceof Player) {

                if (!WorldGuardManager.getInstance().hasMainFlag((Player) attacker)) {
                    return;
                }

            } else if (attacker instanceof Projectile projectile) {

                if (projectile.getShooter() instanceof Player) {
                    if (!WorldGuardManager.getInstance()
                            .hasMainFlag((Player) projectile.getShooter())) {
                        return;
                    }
                }

            }
        }

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        // Don't process this event for marked entities, for players this is handled above,
        // However, for entities, we do not wanna cancel this event to allow plugins to observe changes
        // properly

        if (event.getEntity() instanceof ArmorStand) {
            return;
        }

        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(defender)) || !defender.isValid()
                || !(defender instanceof LivingEntity target)) {
            return;
        }

        if (CombatUtils.isInvincible(target, damage)) {
            return;
        }

        if (ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(attacker)) {
            return;
        }

        if (CombatUtils.hasIgnoreDamageMetadata(target)) {
            return;
        }

        if (attacker instanceof Tameable) {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer != null && ((OfflinePlayer) animalTamer).isOnline()) {
                attacker = (Entity) animalTamer;
            }
        } else if (attacker instanceof TNTPrimed tntAttacker && defender instanceof Player) {
            if (BlastMining.processBlastMiningExplosion(event, tntAttacker,
                    (Player) defender)) {
                return;
            }
        }

        //Friendly fire checks
        if (defender instanceof Player defendingPlayer) {
            //If the attacker is a Player or a projectile belonging to a player
            if (attacker instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof Player attackingPlayer
                        && !attackingPlayer.equals(defendingPlayer)) {
                    //Check for friendly fire and cancel the event
                    if (checkIfInPartyOrSamePlayer(event, defendingPlayer, attackingPlayer)) {
                        return;
                    }
                }

                //Deflect checks
                final McMMOPlayer mmoPlayer = UserManager.getPlayer(defendingPlayer);
                if (mmoPlayer != null) {
                    UnarmedManager unarmedManager = mmoPlayer.getUnarmedManager();

                    if (unarmedManager.canDeflect()) {
                        if (projectile instanceof Arrow && unarmedManager.deflectCheck()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            } else if (attacker instanceof Player attackingPlayer) {
                if (checkIfInPartyOrSamePlayer(event, defendingPlayer, attackingPlayer)) {
                    return;
                }
            }
        }

        //Required setup for processCombatAttack
        if (attacker instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) attacker).getShooter();
            if (shooter instanceof LivingEntity) {
                attacker = (LivingEntity) shooter;
            }
        }

        /*
         * This was put here to solve a plugin conflict with a mod called Project Korra
         * Project Korra sends out a damage event with exactly 0 damage
         * mcMMO does some calculations for the damage in an event and it ends up dividing by zero,
         *  as a result of the modifiers for the event being 0 and the damage set for this event being 0.
         *
         * Surprising this kind of thing
         *
         */
        if (mcMMO.isProjectKorraEnabled()) {
            if (event.getFinalDamage() == 0) {
                return;
            }
        }

        CombatUtils.processCombatAttack(event, attacker, target);
        CombatUtils.handleHealthbars(attacker, target, event.getFinalDamage(), pluginRef);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onEntityDamageMonitor(EntityDamageByEntityEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof LivingEntity livingEntity) {

            if (entityDamageEvent.getFinalDamage() >= livingEntity.getHealth()) {
                //This sets entity names back to whatever they are supposed to be
                CombatUtils.fixNames(livingEntity);
            }
        }

        if (entityDamageEvent.getDamager() instanceof Arrow arrow) {
            CombatUtils.delayArrowMetaCleanup(arrow);
        }

        if (entityDamageEvent.getEntity() instanceof Player player
                && entityDamageEvent.getDamager() instanceof Player) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
            if (mmoPlayer != null) {
                if (mmoPlayer.isDebugMode()) {
                    player.sendMessage(ChatColor.GOLD
                            + "(mmodebug start of combat report) EntityDamageByEntityEvent DEBUG Info:");
                    player.sendMessage("You are being damaged by another player in this event");
                    player.sendMessage("Raw Damage: " + entityDamageEvent.getDamage());
                    player.sendMessage("Your max health: " + player.getAttribute(MAPPED_MAX_HEALTH)
                            .getValue());
                    player.sendMessage("Your current health: " + player.getHealth());

                    player.sendMessage(ChatColor.GREEN + "Damage Modifiers (final damage)");
                    for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                        player.sendMessage(
                                "Modifier " + modifier.name() + ": " + entityDamageEvent.getDamage(
                                        modifier));
                    }

                    player.sendMessage("Final damage: " + entityDamageEvent.getFinalDamage());

                    if (entityDamageEvent.isCancelled()) {
                        player.sendMessage(
                                "Event was cancelled, which means no damage should be done.");
                    }

                    player.sendMessage(ChatColor.RED + "(mmodebug end of combat report)");
                }
            }
        }

        if (entityDamageEvent.getDamager() instanceof Player player
                && entityDamageEvent.getEntity() instanceof Player otherPlayer) {
            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
            if (mmoPlayer != null) {
                if (mmoPlayer.isDebugMode()) {
                    player.sendMessage(ChatColor.GOLD
                            + "(mmodebug start of combat report) EntityDamageByEntityEvent DEBUG Info:");
                    player.sendMessage("You are dealing damage to another player in this event");
                    player.sendMessage("Raw Damage: " + entityDamageEvent.getDamage());

                    player.sendMessage(ChatColor.GREEN + "Damage Modifiers (final damage)");
                    for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                        player.sendMessage(
                                "Modifier " + modifier.name() + ": " + entityDamageEvent.getDamage(
                                        modifier));
                    }

                    player.sendMessage("Final damage: " + entityDamageEvent.getFinalDamage());
                    player.sendMessage("Target players max health: " + otherPlayer.getAttribute(
                            MAPPED_MAX_HEALTH).getValue());
                    player.sendMessage("Target players current health: " + otherPlayer.getHealth());

                    if (entityDamageEvent.isCancelled()) {
                        player.sendMessage(
                                "Event was cancelled, which means no damage should be done.");
                    }

                    player.sendMessage(ChatColor.RED + "(mmodebug end of combat report)");
                }
            }
        }
    }

    public boolean checkIfInPartyOrSamePlayer(Cancellable event, Player defendingPlayer,
            Player attackingPlayer) {
        // This check is probably necessary outside of the party system
        if (defendingPlayer.equals(attackingPlayer)) {
            return true;
        }

        if (!pluginRef.isPartySystemEnabled()) {
            return false;
        }

        if (!UserManager.hasPlayerDataKey(defendingPlayer) || !UserManager.hasPlayerDataKey(
                attackingPlayer)) {
            return true;
        }

        //Party Friendly Fire
        if (!mcMMO.p.getGeneralConfig().getPartyFriendlyFire()) {
            if ((mcMMO.p.getPartyManager().inSameParty(defendingPlayer, attackingPlayer)
                    || mcMMO.p.getPartyManager().areAllies(defendingPlayer, attackingPlayer))
                    && !(Permissions.friendlyFire(attackingPlayer)
                    && Permissions.friendlyFire(defendingPlayer))) {
                event.setCancelled(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Handle EntityDamage events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (event.getEntity().hasMetadata(MetadataConstants.METADATA_KEY_EXPLOSION_FROM_RUPTURE)) {
            event.getEntity()
                    .removeMetadata(MetadataConstants.METADATA_KEY_EXPLOSION_FROM_RUPTURE, mcMMO.p);
        }

        if (event.getEntity() instanceof Player player) {
            /* WORLD GUARD MAIN FLAG CHECK */
            if (WorldGuardUtils.isWorldGuardLoaded()) {
                if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                    return;
                }
            }
        }

        /*
         * Process Registered Interactions
         */

        InteractionManager.processEvent(event, pluginRef, InteractType.ON_ENTITY_DAMAGE);

        /*
         * Old code
         */

        if (event.getEntity() instanceof LivingEntity livingEntity) {
            if (CombatUtils.hasIgnoreDamageMetadata(livingEntity)) {
                return;
            }
        }

        double damage = event.getFinalDamage();

        if (damage <= 0) {
            return;
        }

        Entity entity = event.getEntity();

        /*
        As far as I can tell at one point we registered meta-data about custom damage and we no longer do that.
        if (entity.hasMetadata(mcMMO.customDamageKey)) {
            entity.removeMetadata(mcMMO.customDamageKey, plugin);
            return;
        }
        */

        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(entity)) || !entity.isValid()
                || !(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (CombatUtils.isInvincible(livingEntity, damage)) {
            return;
        }

        DamageCause cause = event.getCause();

        if (livingEntity instanceof Player) {
            final Player player = (Player) entity;

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            final McMMOPlayer mmoPlayer = UserManager.getPlayer(player);

            //Profile not loaded
            if (mmoPlayer == null) {
                return;
            }

            /* Check for invincibility */
            if (mmoPlayer.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            if (event.getFinalDamage() >= 1) {
                mmoPlayer.actualizeRecentlyHurt();
            }

        } else if (livingEntity instanceof Tameable pet) {
            AnimalTamer owner = pet.getOwner();

            if (owner instanceof Player player) {
                /* WORLD GUARD MAIN FLAG CHECK */
                if (WorldGuardUtils.isWorldGuardLoaded()) {
                    if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                        return;
                    }
                }
            }

            if (Taming.canPreventDamage(pet, owner)) {
                final Player player = (Player) owner;
                Wolf wolf = (Wolf) pet;

                //Profile not loaded
                if (UserManager.getPlayer(player) == null) {
                    return;
                }

                TamingManager tamingManager = UserManager.getPlayer(player).getTamingManager();

                switch (cause) {
                    case CONTACT:
                    case FIRE:
                    case HOT_FLOOR:
                    case LAVA:
                        if (tamingManager.canUseEnvironmentallyAware()) {
                            tamingManager.processEnvironmentallyAware(wolf, event.getDamage());
                        }
                        return;

                    case FALL:
                        if (tamingManager.canUseEnvironmentallyAware()) {
                            event.setCancelled(true);
                        }
                        return;

                    case ENTITY_ATTACK:
                    case PROJECTILE:
                        if (tamingManager.canUseThickFur()) {
                            event.setDamage(Taming.processThickFur(wolf, event.getDamage()));

                            if (event.getFinalDamage() == 0) {
                                event.setCancelled(true);
                            }
                        }
                        return;

                    case FIRE_TICK:
                        if (tamingManager.canUseThickFur()) {
                            Taming.processThickFurFire(wolf);
                        }
                        return;

                    case MAGIC:
                    case POISON:
                    case WITHER:
                        if (tamingManager.canUseHolyHound()) {
                            Taming.processHolyHound(wolf, event.getDamage());
                        }
                        return;

                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                    case LIGHTNING:
                        if (tamingManager.canUseShockProof()) {
                            event.setDamage(Taming.processShockProof(wolf, event.getDamage()));

                            if (event.getFinalDamage() == 0) {
                                event.setCancelled(true);
                            }
                        }
                        return;

                    default:
                }
            }
        }
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathLowest(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        // Clear metadata for Slimes/Magma Cubes after transformation events take place, otherwise small spawned slimes will not have any tags
        if (TRANSFORMABLE_ENTITIES.contains(entity.getType())) {
            return;
        }

        mcMMO.getTransientMetadataTools().cleanLivingEntityMetadata(entity);
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to watch
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (mcMMO.getTransientEntityTracker().isTransient(entity)) {
            mcMMO.getTransientEntityTracker().killSummonAndCleanMobFlags(entity, null, false);
        }

        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(entity)) {
            return;
        }

        Archery.arrowRetrievalCheck(entity);
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        LivingEntity livingEntity = event.getEntity();

        switch (event.getSpawnReason()) {
            case NETHER_PORTAL:
                trackSpawnedAndPassengers(livingEntity, MobMetaFlagType.NETHER_PORTAL_MOB);
                break;
            case SPAWNER:
            case SPAWNER_EGG:
                trackSpawnedAndPassengers(livingEntity, MobMetaFlagType.MOB_SPAWNER_MOB);
                break;
            case DISPENSE_EGG:
            case EGG:
                trackSpawnedAndPassengers(livingEntity, MobMetaFlagType.EGG_MOB);
                break;
            case BREEDING:
                trackSpawnedAndPassengers(livingEntity, MobMetaFlagType.PLAYER_BRED_MOB);
                break;
            default:
        }
    }

    private void trackSpawnedAndPassengers(LivingEntity livingEntity,
            MobMetaFlagType mobMetaFlagType) {
        flagMetadata(mobMetaFlagType, livingEntity);

        for (Entity passenger : livingEntity.getPassengers()) {
            if (passenger != null) {
                flagMetadata(mobMetaFlagType, livingEntity);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityBreed(EntityBreedEvent event) {
        if (ExperienceConfig.getInstance().isCOTWBreedingPrevented()) {
            if (hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, event.getFather()) || hasMobFlag(
                    MobMetaFlagType.COTW_SUMMONED_MOB, event.getMother())) {
                event.setCancelled(true);
                Animals mom = (Animals) event.getMother();
                Animals father = (Animals) event.getFather();

                //Prevent love mode spam
                mom.setLoveModeTicks(0);
                father.setLoveModeTicks(0);

                //Inform the player
                if (event.getBreeder() instanceof Player player) {
                    NotificationManager.sendPlayerInformationChatOnly(player,
                            "Taming.Summon.COTW.BreedingDisallowed");
                }
            }
        }
    }

    /**
     * Handle ExplosionPrime events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        final Entity entity = event.getEntity();

        if (!(entity instanceof TNTPrimed) || !entity.hasMetadata(
                MetadataConstants.METADATA_KEY_TRACKED_TNT)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones
        // using this exact metadata
        final Player player = pluginRef.getServer().getPlayerExact(
                entity.getMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT).get(0).asString());

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        // Profile is not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        final MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();

        if (miningManager.canUseBiggerBombs()) {
            event.setRadius(miningManager.biggerBombs(event.getRadius()));
        }
    }

    /**
     * Handle EntityExplode events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnitityExplode(EntityExplodeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof TNTPrimed) || !entity.hasMetadata(
                MetadataConstants.METADATA_KEY_TRACKED_TNT)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones
        // using this exact metadata
        Player player = pluginRef.getServer().getPlayerExact(
                entity.getMetadata(MetadataConstants.METADATA_KEY_TRACKED_TNT).get(0).asString());

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();

        if (miningManager.canUseBlastMining()) {
            miningManager.blastMiningDropProcessing(event.getYield(), event);
//            event.setYield(0);
        }
    }

    /**
     * Handle FoodLevelChange events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) {
            return;
        }

        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        int currentFoodLevel = player.getFoodLevel();
        int newFoodLevel = event.getFoodLevel();
        int foodChange = newFoodLevel - currentFoodLevel;

        if (foodChange <= 0) {
            return;
        }

        //Determine which hand is eating food
        //The main hand is used over the off hand if they both have food, so check the main hand first
        Material foodInHand;

        if (mcMMO.getMaterialMapStore()
                .isFood(player.getInventory().getItemInMainHand().getType())) {
            foodInHand = player.getInventory().getItemInMainHand().getType();
        } else if (mcMMO.getMaterialMapStore()
                .isFood(player.getInventory().getItemInOffHand().getType())) {
            foodInHand = player.getInventory().getItemInOffHand().getType();
        } else {
            return; //Not Food
        }

        /*
         * Some foods have 3 ranks Some foods have 5 ranks The number of ranks
         * is based on how 'common' the item is We can adjust this quite easily
         * if we find something is giving too much of a bonus
         */

        //Hacky 1.17 support
        if (foodInHand.getKey().getKey().equalsIgnoreCase("glow_berries")) {
            if (Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_FARMERS_DIET)) {
                event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager()
                        .farmersDiet(newFoodLevel));
            }

            return;
        }

        switch (foodInHand) {
            case BAKED_POTATO: /*
             * RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @
             * 1000
             */
            case BEETROOT:
            case BREAD: /* RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
            case CARROT: /*
             * RESTORES 2 HUNGER - RESTORES 4 1/2 HUNGER @
             * 1000
             */
            case GOLDEN_CARROT: /*
             * RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @
             * 1000
             */
            case MUSHROOM_STEW: /*
             * RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @
             * 1000
             */
            case PUMPKIN_PIE: /*
             * RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @
             * 1000
             */
                if (Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_FARMERS_DIET)) {
                    event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager()
                            .farmersDiet(newFoodLevel));
                }
                return;

            case COOKIE: /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
            case MELON_SLICE: /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
            case POISONOUS_POTATO: /*
             * RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER
             * @ 1000
             */
            case POTATO: /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                if (Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_FARMERS_DIET)) {
                    event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager()
                            .farmersDiet(newFoodLevel));
                }
                return;
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case COOKED_COD:
            case COOKED_SALMON:

                if (Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_FISHERMANS_DIET)) {
                    event.setFoodLevel(UserManager.getPlayer(player).getFishingManager()
                            .handleFishermanDiet(newFoodLevel));
                }
                return;

            default:
        }
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (event instanceof FakeEntityTameEvent) {
            return;
        }

        final Player player = (Player) event.getOwner();

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        LivingEntity livingEntity = event.getEntity();

        if (!UserManager.hasPlayerDataKey(player)
                || (ExperienceConfig.getInstance().isNPCInteractionPrevented()
                && Misc.isNPCEntityExcludingVillagers(livingEntity))
                || hasMobFlag(MobMetaFlagType.EGG_MOB, livingEntity)
                || hasMobFlag(MobMetaFlagType.MOB_SPAWNER_MOB, livingEntity)) {
            return;
        }

        flagMetadata(MobMetaFlagType.PLAYER_TAMED_MOB, livingEntity);

        //Profile not loaded
        if (UserManager.getPlayer(player) == null) {
            return;
        }

        UserManager.getPlayer(player).getTamingManager().awardTamingXP(livingEntity);
    }

    /**
     * Handle EntityTarget events.
     *
     * @param event The event to process
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (!(entity instanceof Tameable tameable) || !(target instanceof Player player)) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if (WorldGuardUtils.isWorldGuardLoaded()) {
            if (!WorldGuardManager.getInstance().hasMainFlag(player)) {
                return;
            }
        }

        if (!UserManager.hasPlayerDataKey(player) || !CombatUtils.isFriendlyPet(player, tameable)) {
            return;
        }

        // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player,
        // and the owner is in the same party
        // So we can make some assumptions here, about our casting and our check
        if (!(Permissions.friendlyFire(player) && Permissions.friendlyFire(
                (Player) tameable.getOwner()))) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle PotionSplash events in order to fix broken Splash Potion of Saturation.
     *
     * @param event The event to process
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        ItemMeta meta = event.getPotion().getItem().getItemMeta();

        if (meta == null) {
            return;
        }

        for (PotionEffect effect : ((PotionMeta) meta).getCustomEffects()) {
            if (!effect.getType().equals(PotionEffectType.SATURATION)) {
                return;
            }

            for (LivingEntity entity : event.getAffectedEntities()) {
                int duration = (int) (effect.getDuration() * event.getIntensity(entity));
                entity.addPotionEffect(
                        new PotionEffect(effect.getType(), duration, effect.getAmplifier(),
                                effect.isAmbient()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        /* WORLD BLACKLIST CHECK */
        if (WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (event.getEntity() instanceof Arrow arrow) {
            if (ProjectileUtils.isCrossbowProjectile(arrow)) {
                Crossbows.processCrossbows(event, pluginRef, arrow);
            }
        }
    }
}
