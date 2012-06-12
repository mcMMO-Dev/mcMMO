package com.gmail.nossr50.skills.taming;

import org.bukkit.entity.Wolf;

public class FastFoodServiceEventHandler {
    private Wolf wolf;

    public FastFoodServiceEventHandler (Wolf wolf) {
        this.wolf = wolf;
    }

    protected void modifyHealth(int damage) {
        int health = wolf.getHealth();
        int maxHealth = wolf.getMaxHealth();

        if (health < maxHealth) {
            int newHealth = health + damage;

            if (newHealth <= maxHealth) {
                wolf.setHealth(newHealth);
            }
            else {
                wolf.setHealth(maxHealth);
            }
        }
    }
}
