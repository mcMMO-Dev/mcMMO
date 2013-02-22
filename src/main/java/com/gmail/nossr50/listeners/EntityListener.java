package com.gmail.nossr50.listeners;

import org.bukkit.Material;
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
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.skills.acrobatics.Acrobatics;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.fishing.Fishing;
import com.gmail.nossr50.skills.herbalism.Herbalism;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

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

        if (entity instanceof FallingBlock) {
            Block block = event.getBlock();

            if (mcMMO.placeStore.isTrue(block) && !entity.hasMetadata(mcMMO.entityMetadataKey)) {
                mcMMO.placeStore.setFalse(block);
                entity.setMetadata(mcMMO.entityMetadataKey, mcMMO.entityMetadata);
            }
            else if (entity.hasMetadata(mcMMO.entityMetadataKey)) {
                mcMMO.placeStore.setTrue(block);
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
        if (event instanceof FakeEntityDamageByEntityEvent || event.getDamage() <= 0)
            return;

        Entity defender = event.getEntity();

        if (Misc.isNPCEntity(defender) || defender.isDead()) {
            return;
        }

        Entity attacker = event.getDamager();

        if (attacker instanceof Projectile) {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable) {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer != null) {
                attacker = (Entity) animalTamer;
            }
        }

        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;

            // TODO: Is this even possible?
            if (!defendingPlayer.isOnline()) {
                return;
            }

            if (attacker instanceof Player) {
                Player attackingPlayer = (Player) attacker;

                if (defendingPlayer == attackingPlayer) {
                    return;
                }
                else if (PartyManager.inSameParty(defendingPlayer, attackingPlayer)) {
                    if (!(Permissions.friendlyFire(attackingPlayer) && Permissions.friendlyFire(defendingPlayer))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        /* Check for invincibility */
        if (defender instanceof LivingEntity) {
            LivingEntity livingDefender = (LivingEntity) defender;

            if (!CombatTools.isInvincible(livingDefender, event.getDamage())) {
                CombatTools.combatChecks(event, attacker, livingDefender);
            }
        }
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

        if (livingEntity instanceof Player) {
            Player player = (Player) entity;

            // TODO: Is it even possible for the player to be off-line here?
            if (!player.isOnline() || Misc.isNPCEntity(player)) {
                return;
            }

            McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
            PlayerProfile profile = mcMMOPlayer.getProfile();

            /* Check for invincibility */
            if (profile.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            if (!CombatTools.isInvincible(player, event.getDamage())) {
                if (cause == DamageCause.FALL && player.getItemInHand().getType() != Material.ENDER_PEARL && !(Acrobatics.afkLevelingDisabled && player.isInsideVehicle()) && Permissions.roll(player)) {
                    event.setDamage(Acrobatics.processRoll(player, event.getDamage()));

                    if (event.getDamage() == 0) {
                        event.setCancelled(true);
                    }
                }
                else if (cause == DamageCause.BLOCK_EXPLOSION && Permissions.demolitionsExpertise(player)) {
                    MiningManager miningManager = new MiningManager(mcMMOPlayer);
                    miningManager.demolitionsExpertise(event);
                }

                if (event.getDamage() >= 1) {
                    profile.actualizeRecentlyHurt();
                }
            }
        }
        else if (livingEntity instanceof Tameable) {
            Tameable pet = (Tameable) livingEntity;
            AnimalTamer owner = pet.getOwner();

            if ((!CombatTools.isInvincible(livingEntity, event.getDamage())) && pet.isTamed() && owner instanceof Player && pet instanceof Wolf) {
                TamingManager tamingManager = new TamingManager(Users.getPlayer((Player) owner));
                tamingManager.preventDamage(event);
            }
        }
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to watch
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (Misc.isNPCEntity(entity)) {
            return;
        }

        entity.setFireTicks(0);
        BleedTimer.remove(entity);
        Archery.arrowRetrievalCheck(entity);
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event The event to watch
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (Misc.isSpawnerXPEnabled) {
            return;
        }
        else if (event.getEntity() == null) {
            return;
        }

        SpawnReason reason = event.getSpawnReason();

        if (reason == SpawnReason.SPAWNER || reason == SpawnReason.SPAWNER_EGG) {
            event.getEntity().setMetadata(mcMMO.entityMetadataKey, mcMMO.entityMetadata);
        }
    }

    /**
     * Handle ExplosionPrime events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);

                if (Permissions.biggerBombs(player)) {
                    MiningManager miningManager = new MiningManager(Users.getPlayer(player));
                    miningManager.biggerBombs(event);
                }
            }
        }
    }

    /**
     * Handle EntityExplode events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnitityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);

                MiningManager miningManager = new MiningManager(Users.getPlayer(player));
                miningManager.blastMiningDropProcessing(event);

                plugin.removeFromTNTTracker(id);
            }
        }
    }

    /**
     * Handle FoodLevelChange events that involve modifying the event.
     *
     * @param event The event to modify
     */
    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
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

            /*
             * Some foods have 3 ranks
             * Some foods have 5 ranks
             * The number of ranks is based on how 'common' the item is
             * We can adjust this quite easily if we find something is giving too much of a bonus
             */
            if (foodChange > 0) {
                switch (player.getItemInHand().getType()) {
                case BAKED_POTATO:  /* RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                case BREAD:         /* RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                case CARROT_ITEM:   /* RESTORES 2 HUNGER - RESTORES 4 1/2 HUNGER @ 1000 */
                case GOLDEN_CARROT: /* RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                case MUSHROOM_SOUP: /* RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                case PUMPKIN_PIE:   /* RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                    Herbalism.farmersDiet(player, Herbalism.farmersDietRankLevel1, event);
                    break;

                case COOKIE:            /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                case MELON:             /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                case POISONOUS_POTATO:  /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                case POTATO_ITEM:       /* RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                    Herbalism.farmersDiet(player, Herbalism.farmersDietRankLevel2, event);
                    break;

                case COOKED_FISH:   /* RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                    Fishing.beginFishermansDiet(player, Fishing.fishermansDietRankLevel1, event);
                    break;

                case RAW_FISH:      /* RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    Fishing.beginFishermansDiet(player, Fishing.fishermansDietRankLevel2, event);
                    break;

                default:
                    return;
                }
            }
        }
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event The event to watch
     */
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();

        if (Misc.isNPCEntity(player)) {
            return;
        }

        TamingManager tamingManager = new TamingManager(Users.getPlayer(player));
        tamingManager.awardTamingXP(event);
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Tameable && event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            Tameable tameable = (Tameable) event.getEntity();

            if (CombatTools.isFriendlyPet(player, tameable)) {
                // isFriendlyPet ensures that the Tameable is: Tamed, owned by a player, and the owner is in the same party
                // So we can make some assumptions here, about our casting and our check
                Player owner = (Player) tameable.getOwner();
                if (!(Permissions.friendlyFire(player) && Permissions.friendlyFire(owner))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
