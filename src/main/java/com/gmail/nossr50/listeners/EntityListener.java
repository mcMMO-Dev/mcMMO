package com.gmail.nossr50.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.events.fake.FakeEntityDamageByEntityEvent;
import com.gmail.nossr50.events.fake.FakeEntityDamageEvent;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.skills.Combat;
import com.gmail.nossr50.skills.SkillType;
import com.gmail.nossr50.skills.Skills;
import com.gmail.nossr50.skills.acrobatics.AcrobaticsManager;
import com.gmail.nossr50.skills.archery.Archery;
import com.gmail.nossr50.skills.mining.MiningManager;
import com.gmail.nossr50.skills.taming.TamingManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class EntityListener implements Listener {
    private final mcMMO plugin;

    public EntityListener(final mcMMO plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof FallingBlock) {
            Block block = event.getBlock();

            if (mcMMO.placeStore.isTrue(block) && !mcMMO.placeStore.isSpawnedMob(entity)) {
                mcMMO.placeStore.setFalse(block);
                mcMMO.placeStore.addSpawnedMob(entity);
            }
            else if (mcMMO.placeStore.isSpawnedMob(entity)) {
                mcMMO.placeStore.setTrue(block);
                mcMMO.placeStore.removeSpawnedMob(entity);
            }
        }
    }

    /**
     * Monitor EntityDamageByEntity events.
     *
     * @param event The event to monitor
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event instanceof FakeEntityDamageByEntityEvent)
            return;

        if (event.getDamage() <= 0)
            return;

        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();

        if (attacker.hasMetadata("NPC") || defender.hasMetadata("NPC")) return; // Check if either players is are Citizens NPCs

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

        if (entity.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

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
                else if (cause == DamageCause.BLOCK_EXPLOSION && Permissions.demolitionsExpertise(player)) {
                    MiningManager miningManager = new MiningManager(player);
                    miningManager.demolitionsExpertise(event);
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

        if (entity.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        entity.setFireTicks(0);
        BleedTimer.remove(entity);
        Archery.arrowRetrievalCheck(entity);
        mcMMO.placeStore.removeSpawnedMob(entity);
        mcMMO.placeStore.removeSpawnedPet(entity);
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
            mcMMO.placeStore.addSpawnedMob(event.getEntity());
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

        if (entity.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);

                if (Permissions.biggerBombs(player)) {
                    MiningManager miningManager = new MiningManager(player);
                    miningManager.biggerBombs(event);
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

        if (entity == null) return;

        if (entity.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        if (entity instanceof TNTPrimed) {
            int id = entity.getEntityId();

            if (plugin.tntIsTracked(id)) {
                Player player = plugin.getTNTPlayer(id);
                MiningManager miningManager = new MiningManager(player);
                miningManager.blastMiningDropProcessing(event);
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
        AdvancedConfig advancedConfig = AdvancedConfig.getInstance();
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

            PlayerProfile profile = Users.getProfile(player);
            int currentFoodLevel = player.getFoodLevel();
            int newFoodLevel = event.getFoodLevel();

            /*
             * Some foods have 3 ranks
             * Some foods have 5 ranks
             * The number of ranks is based on how 'common' the item is
             * We can adjust this quite easily if we find something is giving too much of a bonus
             */

            if (newFoodLevel > currentFoodLevel) {
                Material food = player.getItemInHand().getType();
                int herbLevel = profile.getSkillLevel(SkillType.HERBALISM);
                int fishLevel = profile.getSkillLevel(SkillType.FISHING);
                int foodChange = newFoodLevel - currentFoodLevel;
                int rankChange = 0;
                boolean fish = false;
                boolean herb = false;
                int herbFoodRank1 = advancedConfig.getFarmerDietRankChange();
                int herbFoodRank2 = advancedConfig.getFarmerDietRankChange() * 2;
                int herbFoodRankMax = advancedConfig.getFarmerDietRankChange() * 5;
                int fishFoodRank1 = advancedConfig.getFishermanDietRankChange();
                int fishFoodRank2 = advancedConfig.getFishermanDietRankChange() * 2;
                int fishFoodRankMax = advancedConfig.getFishermanDietRankChange() * 5;

                switch (food) {
                case BREAD:
                    /* BREAD RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case COOKIE:
                    /* COOKIE RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank2;
                    break;

                case MELON:
                    /* MELON RESTORES  1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank2;
                    break;

                case MUSHROOM_SOUP:
                    /* MUSHROOM SOUP RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case CARROT_ITEM:
                    /* CARROT RESTORES 2 HUNGER - RESTORES 4 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case POTATO_ITEM:
                    /* POTATO RESTORES 1/2 HUNGER - RESTORES 2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank2;
                    break;

                case BAKED_POTATO:
                    /* BAKED POTATO RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case POISONOUS_POTATO:
                    /* POISONOUS POTATO RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank2;
                    break;

                case GOLDEN_CARROT:
                    /* GOLDEN CARROT RESTORES 3 HUNGER - RESTORES 5 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case PUMPKIN_PIE:
                    /* PUMPKIN PIE RESTORES 4 HUNGER - RESTORES 6 1/2 HUNGER @ 1000 */
                    herb = true;
                    rankChange = herbFoodRank1;
                    break;

                case RAW_FISH:
                    /* RAW FISH RESTORES 1 HUNGER - RESTORES 2 1/2 HUNGER @ 1000 */
                    rankChange = fishFoodRank2;
                    fish = true;
                    break;

                case COOKED_FISH:
                    /* COOKED FISH RESTORES 2 1/2 HUNGER - RESTORES 5 HUNGER @ 1000 */
                    rankChange = fishFoodRank1;
                    fish = true;
                    break;

                default:
                    return;
                }

                if (herb) {
                    if (!Permissions.farmersDiet(player)) {
                        return;
                    }

                    for (int i = herbFoodRank1; i <= herbFoodRankMax; i += rankChange) {
                        if (herbLevel >= i) {
                            foodChange++;
                        }
                    }
                }
                else if (fish) {
                    if (!Permissions.fishermansDiet(player)) {
                        return;
                    }

                    for (int i = fishFoodRank1; i <= fishFoodRankMax; i += rankChange) {
                        if (fish && fishLevel >= i) {
                            foodChange++;
                        }
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

        if (player.hasMetadata("NPC")) return; // Check if this player is a Citizens NPC

        if (Permissions.taming(player) && !mcMMO.placeStore.isSpawnedPet(event.getEntity())) {
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
