package com.gmail.nossr50.mcmmo.bukkit.platform.entity.living;

import com.gmail.nossr50.mcmmo.api.data.MMOEntity;

import org.bukkit.entity.LivingEntity;

public class BukkitMMOLivingEntity implements MMOEntity<LivingEntity> {

    LivingEntity entity;

    public BukkitMMOLivingEntity(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity getNative() {
        return entity;
    }
}
