package com.gmail.nossr50.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.Taming;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;

public class EntityListener implements Listener {
    private final mcMMO plugin;

    public EntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor EntityChangeBlock events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof FallingBlock)) {
            return;
        }

        Block block = event.getBlock();
        boolean isTracked = entity.hasMetadata(mcMMO.entityMetadataKey);

        if (mcMMO.placeStore.isTrue(block) && !isTracked) {
            mcMMO.placeStore.setFalse(block);
            entity.setMetadata(mcMMO.entityMetadataKey, mcMMO.metadataValue);
        }
        else if (isTracked) {
            mcMMO.placeStore.setTrue(block);
        }
    }

    /**
     * Handle EntityDamageByEntity events that involve modifying the event.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event instanceof FakeEntityDamageByEntityEvent || event.getDamage() <= 0) {
            return;
        }

        Entity defender = event.getEntity();

        if (Misc.isNPCEntity(defender) || !defender.isValid() || !(defender instanceof LivingEntity)) {
            return;
        }

        Entity attacker = event.getDamager();
        LivingEntity target = (LivingEntity) defender;

        if (CombatUtils.isInvincible(target, event.getDamage())) {
            return;
        }

        if (attacker instanceof Projectile) {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable) {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer != null) {
                attacker = (Entity) animalTamer;
            }
        }

        if (defender instanceof Player && attacker instanceof Player) {
            Player defendingPlayer = (Player) defender;
            Player attackingPlayer = (Player) attacker;

            // We want to make sure we're not gaining XP or applying abilities when we hit ourselves
            if (defendingPlayer.equals(attackingPlayer)) {
                return;
            }

            if (PartyManager.inSameParty(defendingPlayer, attackingPlayer) && !(Permissions.friendlyFire(attackingPlayer) && Permissions.friendlyFire(defendingPlayer))) {
                event.setCancelled(true);
                return;
            }
        }

        CombatUtils.combatChecks(event, attacker, target);
    }

    /**
     * Handle EntityDamage events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof FakeEntityDamageEvent || event.getDamage() <= 0) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        DamageCause cause = event.getCause();
        LivingEntity livingEntity = (LivingEntity) entity;

        if (CombatUtils.isInvincible(livingEntity, event.getDamage())) {
            return;
        }

        if (livingEntity instanceof Player) {
            Player player = (Player) entity;

            if (Misc.isNPCEntity(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            /* Check for invincibility */
            if (mcMMOPlayer.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            switch (cause) {
                case FALL:
                    AcrobaticsManager acrobaticsManager = mcMMOPlayer.getAcrobaticsManager();

                    if (acrobaticsManager.canRoll()) {
                        event.setDamage(acrobaticsManager.rollCheck(event.getDamage()));

                        if (event.getDamage() == 0) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;

                case BLOCK_EXPLOSION:
                    MiningManager miningManager = mcMMOPlayer.getMiningManager();

                    if (miningManager.canUseDemolitionsExpertise()) {
                        event.setDamage(miningManager.processDemolitionsExpertise(event.getDamage()));

                        if (event.getDamage() == 0) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                    break;

                default:
                    break;
            }

            if (event.getDamage() >= 1) {
                mcMMOPlayer.actualizeRecentlyHurt();
            }
        }
        else if (livingEntity instanceof Tameable) {
            Tameable pet = (Tameable) livingEntity;
            AnimalTamer owner = pet.getOwner();

            if (Taming.canPreventDamage(pet, owner)) {
                Player player = (Player) owner;
                Wolf wolf = (Wolf) pet;

                TamingManager tamingManager = UserManager.getPlayer(player).getTamingManager();

                switch (cause) {
                    case CONTACT:
                    case FIRE:
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

                            if (event.getDamage() == 0) {
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

                            if (event.getDamage() == 0) {
                                event.setCancelled(true);
                            }
                        }
                        return;

                    default:
                        return;
                }
            }
        }
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (Misc.isNPCEntity(entity)) {
            return;
        }

        entity.setFireTicks(0);
        BleedTimerTask.remove(entity);
        Archery.arrowRetrievalCheck(entity);
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Misc.isSpawnerXPEnabled) {
            return;
        }

        SpawnReason reason = event.getSpawnReason();

        if (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG) {
            event.getEntity().setMetadata(mcMMO.entityMetadataKey, mcMMO.metadataValue);
        }
    }

    /**
     * Handle ExplosionPrime events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (!plugin.tntIsTracked(id)) {
                return;
            }

            MiningManager miningManager = UserManager.getPlayer(plugin.getTNTPlayer(id)).getMiningManager();

            if (miningManager.canUseBiggerBombs()) {
                event.setRadius(miningManager.biggerBombs(event.getRadius()));
            }
        }
    }

    /**
     * Handle EntityExplode events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnitityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (!plugin.tntIsTracked(id)) {
                return;
            }

            MiningManager miningManager = UserManager.getPlayer(plugin.getTNTPlayer(id)).getMiningManager();

            if (miningManager.canUseBlastMining()) {
                miningManager.blastMiningDropProcessing(event.getYield(), event.blockList());
                event.setYield(0);
            }

            plugin.removeFromTNTTracker(id);
        }
    }

    /**
     * Handle FoodLevelChange events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (Misc.isNPCEntity(player)) {
                return;
            }

            int currentFoodLevel = player.getFoodLevel();
            int newFoodLevel = event.getFoodLevel();
            int foodChange = newFoodLevel - currentFoodLevel;

            if (foodChange <= 0) {
                return;
            }

            /*
             * Some foods have 3 ranks
             * Some foods have 5 ranks
             * The number of ranks is based on how 'common' the item is
             * We can adjust this quite easily if we find something is giving too much of a bonus
             */
            switch (player.getItemInHand().getType()) {
                case BAKED_POTATO:  /* RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                case BREAD:         /* RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                case CARROT_ITEM:   /* RESTORES 2 HUNGER - RESTORES 4 1/2 HUNGER @ 1000 */
                case GOLDEN_CARROT: /* RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                case MUSHROOM_SOUP: /* RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                case PUMPKIN_PIE:   /* RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                    if (Permissions.farmersDiet(player)) {
                        event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager().farmersDiet(Herbalism.farmersDietRankLevel1, newFoodLevel));
                    }
                    return;

                case COOKIE:           /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                case MELON:            /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                case POISONOUS_POTATO: /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                case POTATO_ITEM:      /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                    if (Permissions.farmersDiet(player)) {
                        event.setFoodLevel(UserManager.getPlayer(player).getHerbalismManager().farmersDiet(Herbalism.farmersDietRankLevel2, newFoodLevel));
                    }
                    return;

                case COOKED_FISH: /* RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                    if (Permissions.fishermansDiet(player)) {
                        event.setFoodLevel(UserManager.getPlayer(player).getFishingManager().handleFishermanDiet(Fishing.fishermansDietRankLevel1, newFoodLevel));
                    }
                    return;

                case RAW_FISH:    /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    if (Permissions.fishermansDiet(player)) {
                        event.setFoodLevel(UserManager.getPlayer(player).getFishingManager().handleFishermanDiet(Fishing.fishermansDietRankLevel2, newFoodLevel));
                    }
                    return;

                default:
                    return;
            }
        }
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event The event to watch
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();
        LivingEntity entity = event.getEntity();

        if (Misc.isNPCEntity(player) || Misc.isNPCEntity(entity) || entity.hasMetadata(mcMMO.entityMetadataKey)) {
            return;
        }

        UserManager.getPlayer(player).getTamingManager().awardTamingXP(entity);
    }

    /**
     * Handle EntityTarget events.
     *
     * @param event The event to process
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (entity instanceof Tameable && target instanceof Player) {
            Player player = (Player) target;
            Tameable tameable = (Tameable) entity;

            if (!CombatUtils.isFriendlyPet(player, tameable)) {
                return;
            }

            // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
            // So we can make some assumptions here, about our casting and our check
            if (!(Permissions.friendlyFire(player) && Permissions.friendlyFire((Player) tameable.getOwner()))) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
