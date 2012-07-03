package com.gmail.nossr50.skills.combat;

import java.util.Random;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Axes {

    private static Random random = new Random();

    /**
     * Apply bonus to damage done by axes.
     *
     * @param attacker The attacking player
     * @param event The event to modify
     */
    public static void axesBonus(Player attacker, EntityDamageByEntityEvent event) {
        final int MAX_BONUS = 4;

        /* Add 1 DMG for every 50 skill levels */
        int bonus = Users.getProfile(attacker).getSkillLevel(SkillType.AXES) / 50;

        if (bonus > MAX_BONUS) {
            bonus = MAX_BONUS;
        }

        event.setDamage(event.getDamage() + bonus);
    }

    /**
     * Check for critical chances on axe damage.
     *
     * @param attacker The attacking player
     * @param event The event to modify
     */
    public static void axeCriticalCheck(Player attacker, EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Tameable) {
            Tameable pet = (Tameable) entity;

            if (pet.isTamed()) {
                AnimalTamer tamer = pet.getOwner();

                if (tamer instanceof Player) {
                    Player owner = (Player) tamer;

                    if (owner == attacker || PartyManager.getInstance().inSameParty(attacker, owner)) {
                        return;
                    }
                }
            }
        }

        final int MAX_BONUS_LEVEL = 750;
        final double PVP_MODIFIER = 1.5;
        final int PVE_MODIFIER = 2;

        PlayerProfile attackerProfile = Users.getProfile(attacker);
        int skillLevel = attackerProfile.getSkillLevel(SkillType.AXES);
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        int randomChance = 2000;

        if (attacker.hasPermission("mcmmo.perks.lucky.axes")) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (random.nextInt(randomChance) <= skillCheck && !entity.isDead()){
            int damage = event.getDamage();

            if (entity instanceof Player){
                event.setDamage((int) (damage * PVP_MODIFIER));
                ((Player) entity).sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));
            }
            else {
                event.setDamage(damage * PVE_MODIFIER);
            }
            attacker.sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));
        }
    }

    /**
     * Check for Impact ability.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param event The event to modify
     */
    public static void impact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event) {

        /*
         * TODO: Finish this skill. The idea is you will greatly damage an opponents armor.
         * When they are unarmored, you have a proc that will stun them and deal additional damage.
         */
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            short durabilityDamage = 5; //Start with 5 durability damage

            /* Every 30 Skill Levels you gain 1 durability damage */
            durabilityDamage += Users.getProfile(attacker).getSkillLevel(SkillType.AXES)/30;

            if (!hasArmor(targetPlayer)) {
                applyGreaterImpact(attacker, target, event);
            }
            else {
                for (ItemStack armor : targetPlayer.getInventory().getArmorContents()) {
                    armor.setDurability((short) (armor.getDurability() + durabilityDamage)); //Damage armor piece
                }
                targetPlayer.updateInventory();
            }
        }
        else {
            applyGreaterImpact(attacker, target, event); //Since mobs are technically unarmored, this will always trigger
        }
    }

    /**
     * Apply Greater Impact ability.
     *
     * @param attacker The attacking player
     * @param target The defending entity
     * @param event The event to modify
     */
    private static void applyGreaterImpact(Player attacker, LivingEntity target, EntityDamageByEntityEvent event) {
        final int GREATER_IMPACT_CHANCE = 25;
        final double GREATER_IMPACT_MULTIPLIER = 1.5;

        if (!Permissions.getInstance().greaterImpact(attacker)) {
            return;
        }

        int randomChance = 100;

        if (attacker.hasPermission("mcmmo.perks.lucky.axes")) {
            randomChance = (int) (randomChance * 0.75);
        }

        if (random.nextInt(randomChance) <= GREATER_IMPACT_CHANCE) {
            event.setDamage(event.getDamage() + 2);
            target.setVelocity(attacker.getLocation().getDirection().normalize().multiply(GREATER_IMPACT_MULTIPLIER));
            attacker.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }
    }

    /**
     * Check if a player has armor.
     *
     * @param player Player whose armor to check
     * @return true if the player has armor, false otherwise
     */
    private static boolean hasArmor(Player player) {
        PlayerInventory inventory = player.getInventory();

        if (inventory.getBoots() != null || inventory.getChestplate() != null || inventory.getHelmet() != null || inventory.getLeggings() != null) {
            return true;
        }
        else {
            return false;
        }
    }
}
