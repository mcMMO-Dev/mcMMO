package com.gmail.nossr50.skills.combat;

import java.util.Random;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.party.Party;
import com.gmail.nossr50.runnables.BleedTimer;
import com.gmail.nossr50.util.Combat;
import com.gmail.nossr50.util.ItemChecks;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Swords {

    private static Random random = new Random();

    /**
     * Check for Bleed effect.
     *
     * @param attacker The attacking player
     * @param entity The defending entity
     */
    public static void bleedCheck(Player attacker, LivingEntity entity) {

        if (entity instanceof Tameable) {
            Tameable pet = (Tameable) entity;

            if (pet.isTamed()) {
                AnimalTamer tamer = pet.getOwner();

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
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(1000) <= skillCheck && !entity.isDead()) {
            int bleedTicks = 0;

            if (skillLevel >= 750) {
                bleedTicks = 3;
            }
            else {
                bleedTicks = 2;
            }
            
            BleedTimer.add(entity, bleedTicks);
            attacker.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding"));
        }
    }

    /**
     * Counter-attack entities.
     *
     * @param event The event to modify
     */
    public static void counterAttackChecks(Entity attacker, Player defender, int damage) {
        if (!(attacker instanceof LivingEntity)) {
            return;
        }

        PlayerProfile PPd = Users.getProfile(defender);

        if (ItemChecks.isSword(defender.getItemInHand()) && Permissions.getInstance().counterAttack(defender)) {
            final int MAX_BONUS_LEVEL = 600;
            final int COUNTER_ATTACK_MODIFIER = 2;

            int skillLevel = PPd.getSkillLevel(SkillType.SWORDS);
            int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

            if (random.nextInt(2000) <= skillCheck) {
                Combat.dealDamage((LivingEntity) attacker, damage / COUNTER_ATTACK_MODIFIER);
                defender.sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

                if (attacker instanceof Player) {
                    ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
                }
            }
        }
    }
}
