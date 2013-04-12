package com.gmail.nossr50.datatypes.treasure;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ShakeTreasure extends Treasure {
    private EntityType mob;

    public ShakeTreasure(ItemStack drop, int xp, double dropChance, int dropLevel, EntityType mob) {
        super(drop, xp, dropChance, dropLevel);
        this.mob = mob;
    }

    public EntityType getMob() {
        return mob;
    }

    public void setMob(EntityType mob) {
        this.mob = mob;
    }
}
