package com.gmail.nossr50.skills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;

import com.gmail.nossr50.m;

public class Staves {

    public static void altFire(Material type, Player attacker) {
        switch (type) {
        case BLAZE_ROD:
            attacker.launchProjectile(Fireball.class);
            break;

        case BONE:
            for (Player y : attacker.getWorld().getPlayers()) {
                if (y != attacker && m.isNear(attacker.getLocation(), y.getLocation(), 10) && y.getLevel() > 0) {
                    y.setLevel((int) (y.getLevel() * .75));
                    attacker.sendMessage("You drained your opponent of XP!");
                    y.sendMessage("You feel some of your power leave you...");

                    for (int i = 0; i <= 100; i++) {
                        Location dropLocation = y.getLocation();
                        dropLocation.setX(dropLocation.getX() + (Math.random() * 2));
                        dropLocation.setZ(dropLocation.getZ() + (Math.random() * 2));
                        ExperienceOrb orb = y.getWorld().spawn(dropLocation, ExperienceOrb.class);
                        orb.setExperience((int) (Math.random() * 5));
                    }
                }
            }
            break;

        case STICK:
            for (Player y : attacker.getWorld().getPlayers()) {
                if (y != attacker && m.isNear(attacker.getLocation(), y.getLocation(), 10)) {
                    attacker.sendMessage("You slowed your opponent!");
                    y.sendMessage("You were suddenly slowed...");

                    y.setVelocity(y.getVelocity().multiply(0.5));
                }
            }
            break;

        default:
            break;
        }
    }
}
