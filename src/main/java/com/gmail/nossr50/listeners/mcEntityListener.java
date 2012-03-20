package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
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

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.events.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.FakeEntityDamageEvent;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.mcBleedTimer;
import com.gmail.nossr50.skills.Acrobatics;
import com.gmail.nossr50.skills.Archery;
import com.gmail.nossr50.skills.BlastMining;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.Taming;

public class mcEntityListener implements Listener {
    private final mcMMO plugin;

    public mcEntityListener(final mcMMO plugin) {
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

        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker instanceof Player && defender instanceof Player) {
            if (!defender.getWorld().getPVP()) {
                return;
            }

            if (Party.getInstance().inSameParty((Player)defender, (Player)attacker)) {
                event.setCancelled(true);
                return;
            }
        }

        /* Check for invincibility */
        if (defender instanceof LivingEntity) {
            LivingEntity livingDefender = (LivingEntity)defender;

            if (!m.isInvincible(livingDefender, event)) {
                Combat.combatChecks(event, plugin);
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
        EntityType type = entity.getType();
        DamageCause cause = event.getCause();
        
        switch(type) {
        case PLAYER:

            /* Check for invincibility */
            Player player = (Player) entity;
            PlayerProfile PP = Users.getProfile(player);

            if (PP.getGodMode()) {
                event.setCancelled(true);
                return;
            }

            if (!m.isInvincible(player, event)) {
                if (cause == DamageCause.FALL && mcPermissions.getInstance().acrobatics(player)) {
                    Acrobatics.acrobaticsCheck(player, event);
                }
                else if (cause == DamageCause.BLOCK_EXPLOSION && mcPermissions.getInstance().demolitionsExpertise(player)) {
                    BlastMining.demolitionsExpertise(player, event);
                }

                if (event.getDamage() >= 1) {
                    PP.setRecentlyHurt(System.currentTimeMillis());
                }
            }
            break;

        case WOLF:
            Wolf wolf = (Wolf) entity;

            if ((!m.isInvincible(wolf, event)) && wolf.isTamed() && (wolf.getOwner() instanceof Player)) {
                Taming.preventDamage(event);
            }
            break;

        default:
            break;
        }
    }

    /**
     * Monitor EntityDeath events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity x = event.getEntity();
        x.setFireTicks(0);

        /* Remove bleed track */
        mcBleedTimer.remove(x);

        Archery.arrowRetrievalCheck(x, plugin);

        if (x instanceof Player) {
            Users.getProfile((Player)x).setBleedTicks(0);
        }
    }

    /**
     * Monitor CreatureSpawn events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(SpawnReason.SPAWNER) && !LoadProperties.xpGainsMobSpawners) {
            event.getEntity().setMetadata("mcmmoFromMobSpawner", new FixedMetadataValue(plugin, true));
        }
    }

    /**
     * Monitor ExplosionPrime events.
     *
     * @param event The event to monitor
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntTracker.containsKey(id)) {
                Player player = plugin.tntTracker.get(id);

                if (mcPermissions.getInstance().biggerBombs(player)) {
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
    @EventHandler (priority = EventPriority.LOW)
    public void onEnitityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if (event.getEntity() instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntTracker.containsKey(id)) {
                Player player = plugin.tntTracker.get(id);
                BlastMining.dropProcessing(player, event);
                plugin.tntTracker.remove(id);
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
        if (LoadProperties.herbalismHungerBonus) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                PlayerProfile PP = Users.getProfile(player);
                int currentFoodLevel = player.getFoodLevel();
                int newFoodLevel = event.getFoodLevel();

                /* Some foods have 3 ranks
                 * Some foods have 5 ranks
                 * The number of ranks is based on how 'common' the item is
                 * We can adjust this quite easily if we find something is giving too much of a bonus
                 */

                if (newFoodLevel > currentFoodLevel) {
                    Material food = player.getItemInHand().getType();
                    int herbLevel = PP.getSkillLevel(SkillType.HERBALISM);
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
    }

    /**
     * Monitor EntityTame events.
     *
     * @param event The event to watch
     */
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEntityTame(EntityTameEvent event) {
        Player player = (Player) event.getOwner();

        if (mcPermissions.getInstance().taming(player) && !event.getEntity().hasMetadata("mcmmoSummoned")) {
            PlayerProfile PP = Users.getProfile(player);
            EntityType type = event.getEntityType();
            int xp = 0;

            switch (type) {
            case WOLF:
                xp = LoadProperties.mtameWolf;
                break;

            case OCELOT:
                xp = LoadProperties.mtameOcelot;
                break;

            default:
                break;
            }

            PP.addXP(SkillType.TAMING, xp, player);
            Skills.XpCheckSkill(SkillType.TAMING, player);
        }
    }
}
