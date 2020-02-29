package com.gmail.nossr50.mcmmo.bukkit.platform.entity;

import com.gmail.nossr50.mcmmo.api.data.MMOEntity;

import org.bukkit.entity.Entity;

public class BukkitMMOEntity implements MMOEntity<Entity> {

    Entity entity;

    public BukkitMMOEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getNative() {
        return null;
    }
}
