package com.gmail.nossr50.skills;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.ItemChecks;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.mcBleedTimer;

public class Swords {

    /**
     * Check for Bleed effect.
     *
     * @param attacker The attacking player
     * @param entity The defending entity
     * @param plugin mcMMO plugin instance
     */
    public static void bleedCheck(Player attacker, LivingEntity entity, mcMMO plugin) {

        if (entity instanceof Wolf) {
            Wolf wolf = (Wolf) entity;

            if (wolf.isTamed()) {
                AnimalTamer tamer = wolf.getOwner();

                if (tamer instanceof Player) {
                    Player owner = (Player) tamer;

                    if (owner == attacker || Party.getInstance().inSameParty(attacker, owner)) {
                        return;
                    }
                }
            }
        }

        final int MAX_BONUS_LEVEL = 750;

        PlayerProfile PPa = Users.getProfile(attacker);
        int skillLevel = PPa.getSkillLevel(SkillType.SWORDS);
        int skillCheck = m.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (Math.random() * 1000 <= skillCheck && !entity.isDead()) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                int bleedTicks;

                if (skillLevel >= 750) {
                    bleedTicks = 3;
                }
                else {
                    bleedTicks = 2;
                }

                Users.getProfile(target).addBleedTicks(bleedTicks);
            }
            else {
                mcBleedTimer.add(entity);
            }
            attacker.sendMessage(mcLocale.getString("Swords.EnemyBleeding"));
        }
    }

    /**
     * Counter-attack entities.
     *
     * @param event The event to modify
     */
    public static void counterAttackChecks(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();

        if (!(attacker instanceof LivingEntity)) {
            return;
        }

        Entity target = event.getEntity();

        if (target instanceof Player) {
            Player defender = (Player) target;
            PlayerProfile PPd = Users.getProfile(defender);

            if (ItemChecks.isSword(defender.getItemInHand()) && mcPermissions.getInstance().swords(defender)) {
                final int MAX_BONUS_LEVEL = 600;
                final int COUNTER_ATTACK_MODIFIER = 2;

                int skillLevel = PPd.getSkillLevel(SkillType.SWORDS);
                int skillCheck = m.skillCheck(skillLevel, MAX_BONUS_LEVEL);

                if (Math.random() * 2000 <= skillCheck) {
                    Combat.dealDamage((LivingEntity) attacker, event.getDamage() / COUNTER_ATTACK_MODIFIER);
                    defender.sendMessage(mcLocale.getString("Swords.CounterAttacked"));

                    if (attacker instanceof Player) {
                        ((Player) attacker).sendMessage(mcLocale.getString("Swords.HitByCounterAttack"));
                    }
                }
            }
        }
    }
}
