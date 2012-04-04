package com.gmail.nossr50.skills;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.Combat;
import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcPermissions;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.SkillType;
import com.gmail.nossr50.locale.mcLocale;

public class Archery {

    private static Random random = new Random();

    /**
     * Track arrows fired for later retrieval.
     *
     * @param plugin mcMMO plugin instance
     * @param entity Entity damaged by the arrow
     * @param PPa PlayerProfile of the player firing the arrow
     */
    public static void trackArrows(mcMMO plugin, Entity entity, PlayerProfile PPa) {
        final int MAX_BONUS_LEVEL = 1000;
        int skillLevel = PPa.getSkillLevel(SkillType.ARCHERY);

        if (!plugin.arrowTracker.containsKey(entity)) {
            plugin.arrowTracker.put(entity, 0);
        }

        if (skillLevel > MAX_BONUS_LEVEL || (random.nextInt(1000) <= skillLevel)) {
            plugin.arrowTracker.put(entity, 1);
        }
    }

    /**
     * Check for Daze.
     *
     * @param defender Defending player
     * @param attacker Attacking player
     */
    public static void dazeCheck(Player defender, Player attacker) {
        final int MAX_BONUS_LEVEL = 1000;

        int skillLevel = Users.getProfile(attacker).getSkillLevel(SkillType.ARCHERY);
        Location loc = defender.getLocation();
        int skillCheck = m.skillCheck(skillLevel, MAX_BONUS_LEVEL);

        if (random.nextInt(10) > 5) {
            loc.setPitch(90);
        }
        else {
            loc.setPitch(-90);
        }

        if (random.nextInt(2000) <= skillCheck && mcPermissions.getInstance().daze(attacker)) {
            defender.teleport(loc);
            Combat.dealDamage(defender, 4);
            defender.sendMessage(mcLocale.getString("Combat.TouchedFuzzy"));
            attacker.sendMessage(mcLocale.getString("Combat.TargetDazed"));
        }
    }

    /**
     * Check for arrow retrieval.
     *
     * @param entity The entity hit by the arrows
     * @param plugin mcMMO plugin instance
     */
    public static void arrowRetrievalCheck(Entity entity, mcMMO plugin) {
        if (plugin.arrowTracker.containsKey(entity)) {
            m.mcDropItems(entity.getLocation(), new ItemStack(Material.ARROW), plugin.arrowTracker.get(entity));
        }

        plugin.arrowTracker.remove(entity);
    }
}
