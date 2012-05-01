package com.gmail.nossr50.skills.combat;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

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

        PlayerProfile PPa = Users.getProfile(attacker);
        int skillLevel = PPa.getSkillLevel(SkillType.UNARMED);
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        ItemStack inHand = defender.getItemInHand();

        if (!inHand.getType().equals(Material.AIR)) {
            if (random.nextInt(3000) <= skillCheck && !ironGrip(defender, attacker)) {
                defender.sendMessage(LocaleLoader.getString("Skills.Disarmed"));

                Misc.mcDropItem(defender.getLocation(), inHand);
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
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(2000) <= skillCheck && Permissions.getInstance().deflect(defender)) {
            event.setCancelled(true);
            defender.sendMessage(LocaleLoader.getString("Combat.ArrowDeflect"));
        }
    }

    public static boolean ironGrip(Player defender, Player attacker) {
        final int MAX_BONUS_LEVEL = 1000;

        PlayerProfile PPd = Users.getProfile(defender);
        int skillLevel = PPd.getSkillLevel(SkillType.UNARMED);
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(1000) <= skillCheck) {
            defender.sendMessage(ChatColor.GREEN + "Your iron grip kept you from being disarmed!"); //TODO: Use locale
            attacker.sendMessage(ChatColor.RED + "Your opponent has an iron grip!"); //TODO: Use locale
            return true;
        }
        else {
            return false;
        }
    }
}
