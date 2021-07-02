package com.gmail.nossr50.listeners;

import com.gmail.nossr50.config.WorldBlacklist;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.InteractType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.events.fake.FakeEntityTameEvent;
import com.gmail.nossr50.events.skills.rupture.McMMOEntityDamageByRuptureEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.mining.BlastMining;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.unarmed.UnarmedManager;
import com.gmail.nossr50.util.BlockUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.compat.layers.persistentdata.AbstractPersistentDataLayer;
import com.gmail.nossr50.util.compat.layers.persistentdata.MobMetaFlagType;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.worldguard.WorldGuardManager;
import com.gmail.nossr50.worldguard.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

public class EntityListener implements Listener {
    private final mcMMO pluginRef;
    private final @NotNull AbstractPersistentDataLayer persistentDataLayer;

    /**
     * We can use this {@link NamespacedKey} for {@link Enchantment} comparisons to
     * check if a {@link Player} has a {@link Trident} enchanted with "Piercing".
     */
    private final NamespacedKey piercingEnchantment = NamespacedKey.minecraft("piercing");

    public EntityListener(final mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        persistentDataLayer = mcMMO.getCompatibilityManager().getPersistentDataLayer();
    }

//    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//    public void onBlockDropItemEvent(EntityDropItemEvent event) {
//        if(event.getEntity() instanceof Block) {
//            Block itemDispensingBlock = (Block) event.getEntity();
//
//            //Is it a berry bush?
//            if(itemDispensingBlock.getType().toString().equalsIgnoreCase("sweet_berry_bush")) {
//                //Berry Bush Time!
//                if (event.getEntity().getMetadata(mcMMO.BONUS_DROPS_METAKEY).size() > 0) {
//                    Bukkit.broadcastMessage("Pop pop!");
//                    BonusDropMeta bonusDropMeta = (BonusDropMeta) event.getEntity().getMetadata(mcMMO.BONUS_DROPS_METAKEY).get(0);
//                    int bonusCount = bonusDropMeta.asInt();
//
//                    for (int i = 0; i < bonusCount; i++) {
//                        Misc.spawnItemNaturally(event.getEntity().getLocation(), event.getItemDrop().getItemStack(), ItemSpawnReason.BONUS_DROPS);
//                    }
//                }
//            }
//
//            if(event.getEntity().hasMetadata(mcMMO.BONUS_DROPS_METAKEY))
//                event.getEntity().removeMetadata(mcMMO.BONUS_DROPS_METAKEY, pluginRef);
//        }
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTransform(EntityTransformEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();

            //Transfer metadata keys from mob-spawned mobs to new mobs
            if(persistentDataLayer.hasMobFlags(livingEntity)) {
                for(Entity entity : event.getTransformedEntities()) {
                    if(entity instanceof LivingEntity) {
                        LivingEntity transformedEntity = (LivingEntity) entity;
                        persistentDataLayer.addMobFlags(livingEntity, transformedEntity);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent event) {
        if(!ExperienceConfig.getInstance().isEndermanEndermiteFarmingPrevented())
            return;

        //It's rare but targets can be null sometimes
        if(event.getTarget() == null)
        {
            return;
        }

        //Prevent entities from giving XP if they target endermite
        if(event.getTarget() instanceof Endermite)
        {
            if(event.getEntity() instanceof Enderman) {
                Enderman enderman = (Enderman) event.getEntity();

                if(!persistentDataLayer.hasMobFlag(MobMetaFlagType.EXPLOITED_ENDERMEN, enderman)) {
                    persistentDataLayer.flagMetadata(MobMetaFlagType.EXPLOITED_ENDERMEN, enderman);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();

            /* WORLD GUARD MAIN FLAG CHECK */
            if(WorldGuardUtils.isWorldGuardLoaded())
            {
                if(!WorldGuardManager.getInstance().hasMainFlag(player))
                    return;
            }

            Entity projectile = event.getProjectile();

            //Should be noted that there are API changes regarding Arrow from 1.13.2 to current versions of the game
            if (!(projectile instanceof Arrow)) {
                return;
            }

            ItemStack bow = event.getBow();

            if (bow != null
                    && bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
                projectile.setMetadata(mcMMO.infiniteArrowKey, mcMMO.metadataValue);
            }

            projectile.setMetadata(mcMMO.bowForceKey, new FixedMetadataValue(pluginRef, Math.min(event.getForce() * mcMMO.p.getAdvancedConfig().getForceMultiplier(), 1.0)));
            projectile.setMetadata(mcMMO.arrowDistanceKey, new FixedMetadataValue(pluginRef, projectile.getLocation()));
            //Cleanup metadata in 1 minute in case normal collection falls through
            CombatUtils.delayArrowMetaCleanup((Projectile) projectile);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if(event.getEntity().getShooter() instanceof Player)
        {
            Player player = (Player) event.getEntity().getShooter();

            /* WORLD GUARD MAIN FLAG CHECK */
            if(WorldGuardUtils.isWorldGuardLoaded()) {
                if(!WorldGuardManager.getInstance().hasMainFlag(player))
                    return;
            }

            Projectile projectile = event.getEntity();
            EntityType entityType = projectile.getType();

            if(entityType == EntityType.ARROW || entityType == EntityType.SPECTRAL_ARROW) {
                CombatUtils.delayArrowMetaCleanup(projectile); //Cleans up metadata 1 minute from now in case other collection methods fall through

                if(!projectile.hasMetadata(mcMMO.bowForceKey))
                    projectile.setMetadata(mcMMO.bowForceKey, new FixedMetadataValue(pluginRef, 1.0));

                if(!projectile.hasMetadata(mcMMO.arrowDistanceKey))
                    projectile.setMetadata(mcMMO.arrowDistanceKey, new FixedMetadataValue(pluginRef, projectile.getLocation()));

                //Check both hands
                if(ItemUtils.doesPlayerHaveEnchantmentInHands(player, "piercing")) {
                    return;
                }

                if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ARCHERY_ARROW_RETRIEVAL, player)) {
                    projectile.setMetadata(mcMMO.trackedArrow, mcMMO.metadataValue);
                }
            }
        }
    }

    /**
     * Monitor EntityChangeBlock events.
     *
     * @param event
     *            The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        Block block = event.getBlock();
        Entity entity = event.getEntity();
        Material notYetReplacedType = block.getState().getType(); //because its from getState() this is the block that hasn't been changed yet, which is likely air/lava/water etc

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
            boolean isTracked = entity.hasMetadata(mcMMO.travelingBlock);

            if (mcMMO.getPlaceStore().isTrue(block) && !isTracked) {
                mcMMO.getPlaceStore().setFalse(block);

                entity.setMetadata(mcMMO.travelingBlock, mcMMO.metadataValue);
            }
            else if (isTracked) {
                mcMMO.getPlaceStore().setTrue(block);
            }
        } else if ((block.getType() == Material.REDSTONE_ORE || block.getType().getKey().getKey().equalsIgnoreCase("deepslate_redstone_ore"))) {
            //Redstone ore fire this event and should be ignored
        }
        else {

            if (mcMMO.getPlaceStore().isTrue(block)) {
                mcMMO.getPlaceStore().setFalse(block);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityCombustByEntityEvent(EntityCombustByEntityEvent event) {
        //Prevent players from setting fire to each other if they are in the same party
        if(event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();

            if(event.getCombuster() instanceof Projectile) {
                Projectile projectile = (Projectile) event.getCombuster();
                if(projectile.getShooter() instanceof Player) {
                    Player attacker = (Player) projectile.getShooter();
                    if(checkParties(event, defender, attacker)) {
                        event.setCancelled(true);
                    }
                }
            } else if(event.getCombuster() instanceof Player) {
                Player attacker = (Player) event.getCombuster();
                if(checkParties(event, defender, attacker)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Handle EntityDamageByEntity events that involve modifying the event.
     *
     * @param event
     *            The event to watch
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event instanceof FakeEntityDamageByEntityEvent || event instanceof McMMOEntityDamageByRuptureEvent) {
            return;
        }

        double damage = event.getFinalDamage();
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();

        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(attacker instanceof Player) {

                if(!WorldGuardManager.getInstance().hasMainFlag((Player) attacker)) {
                    return;
                }

            } else if(attacker instanceof Projectile) {

                Projectile projectile = (Projectile) attacker;

                if(projectile.getShooter() instanceof Player) {
                    if(!WorldGuardManager.getInstance().hasMainFlag((Player) projectile.getShooter())) {
                        return;
                    }
                }

            }
        }

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        // Don't process this event for marked entities, for players this is handled above,
        // However, for entities, we do not wanna cancel this event to allow plugins to observe changes
        // properly

        if (CombatUtils.isProcessingNoInvulnDamage()) {
            return;
        }

        if (event.getEntity() instanceof ArmorStand) {
            return;
        }


        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(defender)) || !defender.isValid() || !(defender instanceof LivingEntity)) {
            return;
        }

        LivingEntity target = (LivingEntity) defender;

        if (CombatUtils.isInvincible(target, damage)) {
            return;
        }

        if (ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(attacker)) {
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
        }
        else if (attacker instanceof TNTPrimed && defender instanceof Player) {
            if (BlastMining.processBlastMiningExplosion(event, (TNTPrimed) attacker, (Player) defender)) {
                return;
            }
        }

        //Friendly fire checks
        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;
            Player attackingPlayer;

            //If the attacker is a Player or a projectile belonging to a player
            if(attacker instanceof Projectile || attacker instanceof Player) {
                if(attacker instanceof Projectile) {
                    Projectile projectile = (Projectile) attacker;
                    if(((Projectile) attacker).getShooter() instanceof Player) {
                        attackingPlayer = (Player) projectile.getShooter();

                        //Check for party friendly fire and cancel the event
                        if (checkParties(event, defendingPlayer, attackingPlayer))
                        {
                            return;
                        }

                    }

                    //Deflect checks
                    final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(defendingPlayer);
                    if (mcMMOPlayer != null) {
                        UnarmedManager unarmedManager = mcMMOPlayer.getUnarmedManager();

                        if (unarmedManager.canDeflect()) {
                            if (projectile instanceof Arrow && unarmedManager.deflectCheck()) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                    }
                } else {
                    attackingPlayer = (Player) attacker;
                    //Check for party friendly fire and cancel the event
                    if (checkParties(event, defendingPlayer, attackingPlayer))
                        return;
                }
            }
        }

        //Required setup for processCombatAttack
        if(attacker instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) attacker).getShooter();
            if(shooter instanceof LivingEntity) {
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
        if(mcMMO.isProjectKorraEnabled()) {
            if(event.getFinalDamage() == 0) {
                return;
            }
        }

        CombatUtils.processCombatAttack(event, attacker, target);
        CombatUtils.handleHealthbars(attacker, target, event.getFinalDamage(), pluginRef);
    }

    @EventHandler(priority =  EventPriority.MONITOR, ignoreCancelled = false)
    public void onEntityDamageMonitor(EntityDamageByEntityEvent entityDamageEvent) {
        if(entityDamageEvent.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityDamageEvent.getEntity();

            if(entityDamageEvent.getFinalDamage() >= livingEntity.getHealth()) {
                //This sets entity names back to whatever they are supposed to be
                CombatUtils.fixNames(livingEntity);
            }
        }

        if(entityDamageEvent.getDamager() instanceof Projectile) {
            CombatUtils.cleanupArrowMetadata((Projectile) entityDamageEvent.getDamager());
        }
    }

    public boolean checkParties(Cancellable event, Player defendingPlayer, Player attackingPlayer) {
        if (!UserManager.hasPlayerDataKey(defendingPlayer) || !UserManager.hasPlayerDataKey(attackingPlayer)) {
            return true;
        }

        // We want to make sure we're not gaining XP or applying abilities
        // when we hit ourselves
        if (defendingPlayer.equals(attackingPlayer)) {
            return true;
        }

        //Party Friendly Fire
        if(!mcMMO.p.getGeneralConfig().getPartyFriendlyFire())
            if ((PartyManager.inSameParty(defendingPlayer, attackingPlayer)
                    || PartyManager.areAllies(defendingPlayer, attackingPlayer))
                    && !(Permissions.friendlyFire(attackingPlayer)
                    && Permissions.friendlyFire(defendingPlayer))) {
                event.setCancelled(true);
                return true;
            }
        return false;
    }

    /**
     * Handle EntityDamage events that involve modifying the event.
     *
     * @param event
     *            The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if(event.getEntity().hasMetadata(mcMMO.EXPLOSION_FROM_RUPTURE)) {
            event.getEntity().removeMetadata(mcMMO.EXPLOSION_FROM_RUPTURE, mcMMO.p);
        }

        if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            /* WORLD GUARD MAIN FLAG CHECK */
            if(WorldGuardUtils.isWorldGuardLoaded())
            {
                if(!WorldGuardManager.getInstance().hasMainFlag(player))
                    return;
            }
        }

        /*
         * Process Registered Interactions
         */

        InteractionManager.processEvent(event, pluginRef, InteractType.ON_ENTITY_DAMAGE);

        /*
         * Old code
         */

        if (event instanceof FakeEntityDamageEvent) {
            return;
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

        if ((ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(entity)) || !entity.isValid() || !(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        if (CombatUtils.isInvincible(livingEntity, damage)) {
            return;
        }

        DamageCause cause = event.getCause();

        if (livingEntity instanceof Player) {
            Player player = (Player) entity;

            if (!UserManager.hasPlayerDataKey(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            //Profile not loaded
            if(mcMMOPlayer == null)
                return;

            /* Check for invincibility */
            if (mcMMOPlayer.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            if (event.getFinalDamage() >= 1) {
                mcMMOPlayer.actualizeRecentlyHurt();
            }

        }

        else if (livingEntity instanceof Tameable) {
            Tameable pet = (Tameable) livingEntity;
            AnimalTamer owner = pet.getOwner();

            if(owner instanceof Player)
            {
                Player player = (Player) owner;
                /* WORLD GUARD MAIN FLAG CHECK */
                if(WorldGuardUtils.isWorldGuardLoaded())
                {
                    if(!WorldGuardManager.getInstance().hasMainFlag(player))
                        return;
                }
            }

            if (Taming.canPreventDamage(pet, owner)) {
                Player player = (Player) owner;
                Wolf wolf = (Wolf) pet;

                //Profile not loaded
                if(UserManager.getPlayer(player) == null)
                {
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
     * @param event
     *            The event to watch
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeathLowest(EntityDeathEvent event) {
        mcMMO.getTransientMetadataTools().cleanAllLivingEntityMetadata(event.getEntity());
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event
     *            The event to watch
     */
    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if(mcMMO.getTransientEntityTracker().isTransientSummon(entity)) {
            mcMMO.getTransientEntityTracker().removeSummon(entity, null, false);
        }

        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld())) {
            return;
        }

        if (ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(entity)) {
            return;
        }

        Archery.arrowRetrievalCheck(entity);
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event
     *            The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

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

    private void trackSpawnedAndPassengers(LivingEntity livingEntity, MobMetaFlagType mobMetaFlagType) {
        persistentDataLayer.flagMetadata(mobMetaFlagType, livingEntity);

        for(Entity passenger : livingEntity.getPassengers()) {
            if(passenger != null) {
                persistentDataLayer.flagMetadata(mobMetaFlagType, livingEntity);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityBreed(EntityBreedEvent event) {
        if(ExperienceConfig.getInstance().isCOTWBreedingPrevented()) {
            if(persistentDataLayer.hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, event.getFather()) || persistentDataLayer.hasMobFlag(MobMetaFlagType.COTW_SUMMONED_MOB, event.getMother())) {
                event.setCancelled(true);
                Animals mom = (Animals) event.getMother();
                Animals father = (Animals) event.getFather();

                //Prevent love mode spam
                mom.setLoveModeTicks(0);
                father.setLoveModeTicks(0);

                //Inform the player
                if(event.getBreeder() instanceof Player) {
                    Player player = (Player) event.getBreeder();
                    NotificationManager.sendPlayerInformationChatOnly(player, "Taming.Summon.COTW.BreedingDisallowed");
                }
            }
        }
    }

    /**
     * Handle ExplosionPrime events that involve modifying the event.
     *
     * @param event
     *            The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        Entity entity = event.getEntity();

        if (!(entity instanceof TNTPrimed) || !entity.hasMetadata(mcMMO.tntMetadataKey)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones
        // using this exact metadata
        Player player = pluginRef.getServer().getPlayerExact(entity.getMetadata(mcMMO.tntMetadataKey).get(0).asString());

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        MiningManager miningManager = UserManager.getPlayer(player).getMiningManager();

        if (miningManager.canUseBiggerBombs()) {
            event.setRadius(miningManager.biggerBombs(event.getRadius()));
        }
    }

    /**
     * Handle EntityExplode events that involve modifying the event.
     *
     * @param event
     *            The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnitityExplode(EntityExplodeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        Entity entity = event.getEntity();

        if (!(entity instanceof TNTPrimed) || !entity.hasMetadata(mcMMO.tntMetadataKey)) {
            return;
        }

        // We can make this assumption because we (should) be the only ones
        // using this exact metadata
        Player player = pluginRef.getServer().getPlayerExact(entity.getMetadata(mcMMO.tntMetadataKey).get(0).asString());

        if (!UserManager.hasPlayerDataKey(player)) {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
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
     * @param event
     *            The event to modify
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
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

        if(mcMMO.getMaterialMapStore().isFood(player.getInventory().getItemInMainHand().getType())) {
            foodInHand = player.getInventory().getItemInMainHand().getType();
        } else if(mcMMO.getMaterialMapStore().isFood(player.getInventory().getItemInOffHand().getType())) {
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
        if(foodInHand.getKey().getKey().equalsIgnoreCase("glow_berries")) {
            if (Permissions.isSubSkillEnabled(player, SubSkillType.HERBALISM_FARMERS_DIET)) {
                event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager().farmersDiet(newFoodLevel));
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
                    event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager().farmersDiet(newFoodLevel));
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
                    event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager().farmersDiet(newFoodLevel));
                }
                return;
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case COOKED_COD:
            case COOKED_SALMON:

                if (Permissions.isSubSkillEnabled(player, SubSkillType.FISHING_FISHERMANS_DIET)) {
                    event.setFoodLevel(UserManager.getPlayer(player).getFishingManager().handleFishermanDiet(newFoodLevel));
                }
                return;

            default:
        }
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event
     *            The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        if (event instanceof FakeEntityTameEvent) {
            return;
        }

        Player player = (Player) event.getOwner();

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        LivingEntity livingEntity = event.getEntity();

        if (!UserManager.hasPlayerDataKey(player)
                || (ExperienceConfig.getInstance().isNPCInteractionPrevented() && Misc.isNPCEntityExcludingVillagers(livingEntity))
                || persistentDataLayer.hasMobFlag(MobMetaFlagType.EGG_MOB, livingEntity)
                || persistentDataLayer.hasMobFlag(MobMetaFlagType.MOB_SPAWNER_MOB, livingEntity)) {
            return;
        }

        persistentDataLayer.flagMetadata(MobMetaFlagType.PLAYER_TAMED_MOB, livingEntity);

        //Profile not loaded
        if(UserManager.getPlayer(player) == null)
        {
            return;
        }

        UserManager.getPlayer(player).getTamingManager().awardTamingXP(livingEntity);
    }

    /**
     * Handle EntityTarget events.
     *
     * @param event
     *            The event to process
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (!(entity instanceof Tameable) || !(target instanceof Player)) {
            return;
        }

        Player player = (Player) target;

        /* WORLD GUARD MAIN FLAG CHECK */
        if(WorldGuardUtils.isWorldGuardLoaded())
        {
            if(!WorldGuardManager.getInstance().hasMainFlag(player))
                return;
        }

        Tameable tameable = (Tameable) entity;

        if (!UserManager.hasPlayerDataKey(player) || !CombatUtils.isFriendlyPet(player, tameable)) {
            return;
        }

        // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player,
        // and the owner is in the same party
        // So we can make some assumptions here, about our casting and our check
        if (!(Permissions.friendlyFire(player) && Permissions.friendlyFire((Player) tameable.getOwner()))) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle PotionSplash events in order to fix broken Splash Potion of
     * Saturation.
     *
     * @param event
     *            The event to process
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        /* WORLD BLACKLIST CHECK */
        if(WorldBlacklist.isWorldBlacklisted(event.getEntity().getWorld()))
            return;

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
                entity.addPotionEffect(new PotionEffect(effect.getType(), duration, effect.getAmplifier(), effect.isAmbient()));
            }
        }
    }
}
