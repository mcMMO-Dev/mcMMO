package com.gmail.nossr50.runnables.skills;

import org.bukkit.entity.LivingEntity;

public class BleedContainer {
    public int bleedTicks;
    public int bleedRank;
    public int toolTier;
    public LivingEntity target;
    public LivingEntity damageSource;

    public BleedContainer(LivingEntity target, int bleedTicks, int bleedRank, int toolTier,
            LivingEntity damageSource) {
        this.target = target;
        this.bleedTicks = bleedTicks;
        this.bleedRank = bleedRank;
        this.toolTier = toolTier;
        this.damageSource = damageSource;
    }
}
