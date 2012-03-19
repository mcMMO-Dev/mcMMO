package com.gmail.nossr50.skills;

import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.nossr50.mcMMO;

public class Staves {

    /**
     * Fire a projectile on alt-fire from a staff.
     *
     * @param type The type of staff
     * @param attacker The attacking player
     * @param plugin mcMMO plugin instance
     */
    public static void altFire(Material type, Player attacker, mcMMO plugin) {
        Projectile projectile = null;

        switch (type) {
        case BLAZE_ROD:
            projectile = attacker.launchProjectile(Fireball.class);
            break;

        case BONE:
            projectile = attacker.launchProjectile(Snowball.class);
            break;

        case STICK:
            projectile = attacker.launchProjectile(Egg.class);
            break;

        default:
            break;
        }

        projectile.setMetadata("mcmmoFiredFromStaff", new FixedMetadataValue(plugin, true));
    }
}
