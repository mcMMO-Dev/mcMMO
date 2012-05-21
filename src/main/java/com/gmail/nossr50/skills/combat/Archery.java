package com.gmail.nossr50.skills.combat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class Archery {
    private static Random random = new Random();
    public static Map<Entity, Integer> arrowTracker = new HashMap<Entity, Integer>();

    /**
     * Track arrows fired for later retrieval.
     *
     * @param entity Entity damaged by the arrow
     * @param PPa PlayerProfile of the player firing the arrow
     */
    public static void trackArrows(Entity entity, PlayerProfile PPa) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = PPa.getSkillLevel(SkillType.ARCHERY);
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(1000) <= skillCheck) {
            for (Entry<Entity, Integer> entry : arrowTracker.entrySet()) {
                if (entry.getKey() == entity) { //Shouldn't we be using .equals() here?
                    entry.setValue(entry.getValue() + 1);
                    return;
                }
            }

            arrowTracker.put(entity, 1);
        }
    }

    /**
     * Check for Daze.
     *
     * @param defender Defending player
     * @param attacker Attacking player
     * @param event The event to modify
     */
    public static void dazeCheck(Player defender, Player attacker, EntityDamageByEntityEvent event) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = Users.getProfile(attacker).getSkillLevel(SkillType.ARCHERY);
        Location location = defender.getLocation();
        int skillCheck = Misc.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(10) > 5) {
            location.setPitch(90);
        }
        else {
            location.setPitch(-90);
        }

        if (random.nextInt(2000) <= skillCheck) {
            defender.teleport(location);
            event.setDamage(event.getDamage() + 4);
            defender.sendMessage(LocaleLoader.getString("Combat.TouchedFuzzy"));
            attacker.sendMessage(LocaleLoader.getString("Combat.TargetDazed"));
        }
    }

    /**
     * Check for arrow retrieval.
     *
     * @param entity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(Entity entity) {
        for (Iterator<Map.Entry<Entity, Integer>> it = arrowTracker.entrySet().iterator() ; it.hasNext() ; ) { //This is a wee bit confusing...
            Entry<Entity, Integer> entry = it.next();

            if (entry.getKey() == entity) { //Shouldn't we be using .equals() here?
                Misc.dropItems(entity.getLocation(), new ItemStack(Material.ARROW), entry.getValue());
                it.remove();
                return;
            }
        }
    }
}
