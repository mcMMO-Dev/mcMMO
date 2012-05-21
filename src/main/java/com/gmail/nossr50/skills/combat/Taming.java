package com.gmail.nossr50.skills.combat;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Taming {
    private static Random random = new Random();

    /**
     * Apply the Fast Food Service ability.
     *
     * @param PPo The PlayerProfile of the wolf's owner
     * @param theWolf The wolf using the ability
     * @param damage The damage being absorbed by the wolf  
     */
    public static void fastFoodService (PlayerProfile PPo, Wolf theWolf, int damage) {
        final int SKILL_ACTIVATION_LEVEL = 50;
        final int ACTIVATION_CHANCE = 50;

        int health = theWolf.getHealth();
        int maxHealth = theWolf.getMaxHealth();

        if (PPo.getSkillLevel(SkillType.TAMING) >= SKILL_ACTIVATION_LEVEL) {
            if (health < maxHealth) {
                if (random.nextInt(100) < ACTIVATION_CHANCE) {
                    if (health + damage <= maxHealth) {
                        theWolf.setHealth(health + damage);
                    }
                    else {
                        theWolf.setHealth(maxHealth);
                    }
                }
            }
        }
    }

    /**
     * Apply the Sharpened Claws ability.
     *
     * @param PPo The PlayerProfile of the wolf's owner
     * @param event The event to modify
     */
    public static void sharpenedClaws(PlayerProfile PPo, EntityDamageEvent event) {
        final int SKILL_ACTIVATION_LEVEL = 750;
        final int SHARPENED_CLAWS_BONUS = 2;

        if (PPo.getSkillLevel(SkillType.TAMING) >= SKILL_ACTIVATION_LEVEL) {
            event.setDamage(event.getDamage() + SHARPENED_CLAWS_BONUS);
        }
    }

    /**
     * Apply the Gore ability.
     *
     * @param PPo The PlayerProfile of the wolf's owner
     * @param event The event to modify
     * @param master The wolf's master
     */
    public static void gore(PlayerProfile PPo, EntityDamageEvent event, Player master) {
        final int GORE_MULTIPLIER = 2;

        if (random.nextInt(1000) <= PPo.getSkillLevel(SkillType.TAMING)) {
            Entity entity = event.getEntity();
            event.setDamage(event.getDamage() * GORE_MULTIPLIER);

            if (entity instanceof Player) {
                ((Player) entity).sendMessage(LocaleLoader.getString("Combat.StruckByGore"));
            }

            BleedTimer.add((LivingEntity) entity, 2);

            master.sendMessage(LocaleLoader.getString("Combat.Gore"));
        }
    }

    /**
     * Get the name of a tameable animal's owner.
     *
     * @param beast The animal whose owner's name to get
     * @return the name of the animal's owner, or "Offline Master" if the owner is offline
     */
    private static String getOwnerName(Tameable beast) {
        AnimalTamer tamer = beast.getOwner();

        if (tamer instanceof Player) {
            return ((Player) tamer).getName();
        }
        else {
            return "Offline Master";
        }
    }

    /**
     * Prevent damage to wolves based on various skills.
     *
     * @param event The event to modify
     */
    public static void preventDamage(EntityDamageEvent event) {
        final int ENVIRONMENTALLY_AWARE_LEVEL = 100;
        final int THICK_FUR_LEVEL = 250;
        final int SHOCK_PROOF_LEVEL = 500;

        final int THICK_FUR_MODIFIER = 2;
        final int SHOCK_PROOF_MODIFIER = 6;

        if (!(event.getEntity() instanceof Wolf)) {
            return;
        }

        DamageCause cause = event.getCause();
        Wolf wolf = (Wolf) event.getEntity();
        Player master = (Player) wolf.getOwner();
        int skillLevel = Users.getProfile(master).getSkillLevel(SkillType.TAMING);

        switch (cause) {

        /* Environmentally Aware */
        case CONTACT:
        case LAVA:
        case FIRE:
            if (Permissions.getInstance().environmentallyAware(master)) {
                if (skillLevel >= ENVIRONMENTALLY_AWARE_LEVEL) {
                    if (event.getDamage() >= wolf.getHealth()) {
                        return;
                    }

                    wolf.teleport(master.getLocation());
                    master.sendMessage(LocaleLoader.getString("Taming.Listener.Wolf"));
                }
            }
            break;

        case FALL:
            if (Permissions.getInstance().environmentallyAware(master)) {
                if (skillLevel >= ENVIRONMENTALLY_AWARE_LEVEL) {
                    event.setCancelled(true);
                }
            }
            break;

        /* Thick Fur */
        case FIRE_TICK:
            if (Permissions.getInstance().thickFur(master)) {
                if(skillLevel >= THICK_FUR_LEVEL) {
                    wolf.setFireTicks(0);
                }
            }
            break;

        case ENTITY_ATTACK:
        case PROJECTILE:
            if (Permissions.getInstance().thickFur(master)) {
                if (skillLevel >= THICK_FUR_LEVEL) {
                    event.setDamage(event.getDamage() / THICK_FUR_MODIFIER);
                }
            }
            break;

        /* Shock Proof */
        case ENTITY_EXPLOSION:
        case BLOCK_EXPLOSION:
            if (Permissions.getInstance().shockProof(master)) {
                if (skillLevel >= SHOCK_PROOF_LEVEL) {
                    event.setDamage(event.getDamage() / SHOCK_PROOF_MODIFIER);
                }
            }
            break;

        default:
            break;
        }
    }

    /**
     * Summon an animal.
     *
     * @param type Type of animal to summon
     * @param player Player summoning the animal
     */
    public static void animalSummon(EntityType type, Player player, mcMMO plugin) {
        ItemStack item = player.getItemInHand();
        Material summonItem = null;
        int summonAmount = 0;

        switch (type) {
        case WOLF:
            summonItem = Material.BONE;
            summonAmount = Config.getInstance().getTamingCOTWWolfCost();
            break;

        case OCELOT:
            summonItem = Material.RAW_FISH;
            summonAmount = Config.getInstance().getTamingCOTWOcelotCost();
            break;

        default:
            break;
        }

        if (item.getType().equals(summonItem)) {
            if (item.getAmount() >= summonAmount) {
                for (Entity x : player.getNearbyEntities(40, 40, 40)) {
                    if (x.getType().equals(type)) {
                        switch (type) {
                        case WOLF:
                            player.sendMessage(LocaleLoader.getString("Taming.Summon.Fail.Wolf"));
                            return;

                        case OCELOT:
                            player.sendMessage(LocaleLoader.getString("Taming.Summon.Fail.Ocelot"));
                            return;

                        default:
                            return;
                        }
                    }
                }

                LivingEntity entity = player.getWorld().spawnCreature(player.getLocation(), type);
                entity.setMetadata("mcmmoSummoned", new FixedMetadataValue(plugin, true));
                ((Tameable) entity).setOwner(player);

                if (entity.getType().equals(EntityType.OCELOT)) {
                    ((Ocelot) entity).setCatType(Ocelot.Type.getType(1 + random.nextInt(3)));
                }

                if (entity.getType().equals(EntityType.WOLF)) {
                    entity.setHealth(entity.getMaxHealth());
                }

                player.setItemInHand(new ItemStack(summonItem, item.getAmount() - summonAmount));
                player.sendMessage(LocaleLoader.getString("Taming.Summon.Complete"));
            }
            else {
                player.sendMessage(LocaleLoader.getString("Skills.NeedMore") + " " + ChatColor.GRAY + Misc.prettyItemString(summonItem.getId()));
            }
        }
    }

    /**
     * Inspect a tameable animal for details.
     *
     * @param event Event to modify
     * @param target Animal to inspect
     * @param inspector Player inspecting the animal
     */
    public static void beastLore(EntityDamageByEntityEvent event, LivingEntity target, Player inspector) {
        if (target instanceof Tameable) {
            Tameable beast = (Tameable) target;
            String message = LocaleLoader.getString("Combat.BeastLore") + " ";
            int health = target.getHealth();
            event.setCancelled(true);

            if (beast.isTamed()) {
                message = message.concat(LocaleLoader.getString("Combat.BeastLoreOwner", new Object[] {getOwnerName(beast)}) + " ");
            }

            message = message.concat(LocaleLoader.getString("Combat.BeastLoreHealth", new Object[] {health, target.getMaxHealth()}));
            inspector.sendMessage(message);
        }
    }
}
