package com.gmail.nossr50.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;

public class Acrobatics {

    /**
     * Check for fall damage reduction.
     *
     * @param player The player whose fall damage to modify
     * @param event The event to check
     */
    public static void acrobaticsCheck(Player player, EntityDamageEvent event) {
        final int ROLL_XP_MODIFIER = 80;
        final int FALL_XP_MODIFIER = 120;

        PlayerProfile PP = Users.getProfile(player);
        int acrovar = PP.getSkillLevel(SkillType.ACROBATICS);
        boolean gracefulRoll = player.isSneaking();
        int damage = event.getDamage();

        if (gracefulRoll) {
            acrovar = acrovar * 2;
        }

        if (acrovar > 1000 || Math.random() * 1000 <= acrovar) {
            int threshold = 7;

            if (gracefulRoll) {
                threshold = threshold * 2;
            }

            int newDamage = damage - threshold;

            if (newDamage < 0) {
                newDamage = 0;
            }

            /* Check for death */
            if (player.getHealth() - damage >= 1) {
                PP.addXP(SkillType.ACROBATICS, damage * ROLL_XP_MODIFIER, player);
                Skills.XpCheckSkill(SkillType.ACROBATICS, player);

                event.setDamage(newDamage);

                if (event.getDamage() <= 0) {
                    event.setCancelled(true);
                }

                if (gracefulRoll) {
                    player.sendMessage(mcLocale.getString("Acrobatics.GracefulRoll"));
                }
                else {
                    player.sendMessage(mcLocale.getString("Acrobatics.Roll"));
                }
            }
        }
        else if (player.getHealth() - damage >= 1) {
            PP.addXP(SkillType.ACROBATICS, event.getDamage() * FALL_XP_MODIFIER, player);
            Skills.XpCheckSkill(SkillType.ACROBATICS, player);
        }
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public static void dodgeChecks(EntityDamageByEntityEvent event) {
        final int DODGE_MODIFIER = 120;

        Player defender = (Player) event.getEntity();
        PlayerProfile PPd = Users.getProfile(defender);

        /* PARTY CHECK */
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            if (Party.getInstance().inSameParty(defender, attacker)) {
                return;
            }
        }

        if (mcPermissions.getInstance().acrobatics(defender)) {
            int skillCheck = 0;

            if (PPd.getSkillLevel(SkillType.ACROBATICS) <= 800) {
                skillCheck = PPd.getSkillLevel(SkillType.ACROBATICS);
            }
            else {
                skillCheck = 800;
            }

            if (Math.random() * 4000 <= skillCheck) {
                defender.sendMessage(mcLocale.getString("Acrobatics.Dodge"));

                if (System.currentTimeMillis() >= 5000 + PPd.getRespawnATS() && defender.getHealth() >= 1) {
                    PPd.addXP(SkillType.ACROBATICS, event.getDamage() * DODGE_MODIFIER, defender);
                    Skills.XpCheckSkill(SkillType.ACROBATICS, defender);
                }

                event.setDamage(event.getDamage() / 2);

                //Needs to do minimal damage
                if (event.getDamage() <= 0) {
                    event.setDamage(1);
                }
            }
        }
    }
}
