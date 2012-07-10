package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.gathering.BlastMining;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Skills;
import com.gmail.nossr50.util.Users;

public class EntityListener implements Listener {
    private final mcMMO plugin;

    public EntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    /**
     * Monitor EntityDamageByEntity events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event instanceof FakeEntityDamageByEntityEvent) {
            return;
        }

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (attacker instanceof Projectile) {
            attacker = ((Projectile) attacker).getShooter();
        }
        else if (attacker instanceof Tameable) {
            AnimalTamer animalTamer = ((Tameable) attacker).getOwner();

            if (animalTamer instanceof Entity) {
                attacker = (Entity) animalTamer;
            }
        }

        if (defender instanceof Player) {
            Player defendingPlayer = (Player) defender;

            if (!defendingPlayer.isOnline()) {
                return;
            }

            if (attacker instanceof Player) {
                if (PartyManager.getInstance().inSameParty(defendingPlayer, (Player) attacker)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        /* Check for invincibility */
        if (defender instanceof LivingEntity) {
            LivingEntity livingDefender = (LivingEntity) defender;

            if (!Misc.isInvincible(livingDefender, event)) {
                Combat.combatChecks(event);
            }
        }
    }

    /**
     * Monitor EntityDamage events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof FakeEntityDamageEvent) {
            return;
        }

        Entity entity = event.getEntity();
        DamageCause cause = event.getCause();

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity lEntity = (LivingEntity) entity;

        if (lEntity instanceof Player) {
            /* Check for invincibility */
            Player player = (Player) entity;

            if (!player.isOnline()) {
                return;
            }

            PlayerProfile profile = Users.getProfile(player);

            if (profile == null) {
                return;
            }

            if (profile.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            if (!Misc.isInvincible(player, event)) {
                if (cause == DamageCause.FALL && !player.isInsideVehicle() && !(player.getItemInHand().getType() == Material.ENDER_PEARL)) {
                    AcrobaticsManager acroManager = new AcrobaticsManager(player);
                    acroManager.rollCheck(event);
                }
                else if (cause == DamageCause.BLOCK_EXPLOSION && Permissions.getInstance().demolitionsExpertise(player)) {
                    BlastMining.demolitionsExpertise(player, event);
                }

                if (event.getDamage() >= 1) {
                    profile.actualizeRecentlyHurt();
                }
            }
        } else if (lEntity instanceof Tameable) {
            Tameable pet = (Tameable) lEntity;
            AnimalTamer owner = pet.getOwner();

            if ((!Misc.isInvincible(lEntity, event)) && pet.isTamed() && (owner instanceof Player) && pet instanceof Wolf) {
                TamingManager tamingManager = new TamingManager((Player) owner);
                tamingManager.preventDamage(event);
            }
        }
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        entity.setFireTicks(0);
        BleedTimer.remove(entity);
        Archery.arrowRetrievalCheck(entity);
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        SpawnReason reason = event.getSpawnReason();

        if ((reason.equals(SpawnReason.SPAWNER) || reason.equals(SpawnReason.SPAWNER_EGG)) && !Config.getInstance().getExperienceGainsMobspawnersEnabled()) {
            event.getEntity().setMetadata("mcmmoFromMobSpawner", new FixedMetadataValue(plugin, true));
        }
    }

    /**
     * Monitor ExplosionPrime events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);

                if (Permissions.getInstance().biggerBombs(player)) {
                    BlastMining.biggerBombs(player, event);
                }
            }
        }
    }

    /**
     * Monitor EntityExplode events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEnitityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (event.getEntity() instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);
                BlastMining.dropProcessing(player, event);
                plugin.removeFromTNTTracker(id);
            }
        }
    }

    /**
     * Monitor FoodLevelChange events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerProfile profile = Users.getProfile(player);
            int currentFoodLevel = player.getFoodLevel();
            int newFoodLevel = event.getFoodLevel();

            if (!Permissions.getInstance().farmersDiet(player)) {
                return;
            }

            /*
             * Some foods have 3 ranks
             * Some foods have 5 ranks
             * The number of ranks is based on how 'common' the item is
             * We can adjust this quite easily if we find something is giving too much of a bonus
             */

            if (newFoodLevel > currentFoodLevel) {
                Material food = player.getItemInHand().getType();
                int herbLevel = profile.getSkillLevel(SkillType.HERBALISM);
                int foodChange = newFoodLevel - currentFoodLevel;
                int rankChange = 0;

                switch (food) {
                case BREAD:
                    /* BREAD RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                    rankChange = 200;
                    break;

                case COOKIE:
                    /* COOKIE RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                    rankChange = 400;
                    break;

                case MELON:
                    /* MELON RESTORES  1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    rankChange = 400;
                    break;

                case MUSHROOM_SOUP:
                    /* MUSHROOM SOUP RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                    rankChange = 200;
                    break;

                default:
                    return;
                }

                for (int i = 200; i <= 1000; i += rankChange) {
                    if (herbLevel >= i) {
                        foodChange++;
                    }
                }

                /* Make sure we don't go over the max value */
                newFoodLevel = currentFoodLevel + foodChange;
                if (newFoodLevel > 20) {
                    event.setFoodLevel(20);
                }
                else {
                    event.setFoodLevel(newFoodLevel);
                }
            }
        }
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event The event to watch
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();

        if (Permissions.getInstance().taming(player) && !event.getEntity().hasMetadata("mcmmoSummoned")) {
            PlayerProfile profile = Users.getProfile(player);
            EntityType type = event.getEntityType();
            int xp = 0;

            switch (type) {
            case WOLF:
                xp = Config.getInstance().getTamingXPWolf();
                break;

            case OCELOT:
                xp = Config.getInstance().getTamingXPOcelot();
                break;

            default:
                break;
            }

            Skills.xpProcessing(player, profile, SkillType.TAMING, xp);
        }
    }
}
