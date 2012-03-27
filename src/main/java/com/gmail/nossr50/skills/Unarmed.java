package com.gmail.nossr50.skills;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Unarmed {

    private static Random random = new Random();

    /**
     * Apply bonus to Unarmed damage.
     *
     * @param PPa Profile of the attacking player
     * @param event The event to modify
     */
    public static void unarmedBonus(PlayerProfile PPa, EntityDamageByEntityEvent event) {
        final int MAX_BONUS = 8;
        int bonus = 3;

        bonus += PPa.getSkillLevel(SkillType.UNARMED) / 50; //Add 1 DMG for every 50 skill levels

        if (bonus > MAX_BONUS) {
            bonus = MAX_BONUS;
        }

        event.setDamage(event.getDamage() + bonus);
    }

    /**
     * Check for disarm.
     *
     * @param PPa Profile of the attacking player
     * @param defender The defending player
     */
    public static void disarmProcCheck(Player attacker, Player defender) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = Users.getProfile(attacker).getSkillLevel(SkillType.UNARMED);
        int skillCheck = m.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (attacker.getItemInHand().equals(Material.AIR)) {
            if (random.nextInt(3000) <= skillCheck) {
                ItemStack item = defender.getItemInHand();

                defender.sendMessage(mcLocale.getString("Skills.Disarmed"));

                m.mcDropItem(defender.getLocation(), item);
                defender.setItemInHand(new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Check for arrow deflection.
     *
     * @param defender The defending player
     * @param event The event to modify
     */
    public static void deflectCheck(Player defender, EntityDamageByEntityEvent event) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = Users.getProfile(defender).getSkillLevel(SkillType.UNARMED);
        int skillCheck = m.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(2000) <= skillCheck && mcPermissions.getInstance().deflect(defender)) {
            event.setCancelled(true);
            defender.sendMessage(mcLocale.getString("Combat.ArrowDeflect"));
        }
    }
}
