package com.gmail.nossr50.util;

import org.bukkit.entity.Projectile;

import java.util.HashSet;
import java.util.Set;

public class SpawnedProjectileTracker {
    private final Set<Projectile> trackedProjectiles;

    public SpawnedProjectileTracker() {
        trackedProjectiles = new HashSet<>();
    }

    public void trackProjectile(Projectile projectile) {
        trackedProjectiles.add(projectile);
    }

    public void untrackProjectile(Projectile projectile) {
        trackedProjectiles.remove(projectile);
    }

    public boolean isSpawnedProjectile(Projectile projectile) {
        return trackedProjectiles.contains(projectile);
    }
}
