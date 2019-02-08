package com.gmail.nossr50.runnables.skills;

import org.bukkit.entity.LivingEntity;

public class BleedContainer {
    public int bleedTicks;
    public int bleedRank;
    public LivingEntity target;
    public LivingEntity damageSource;

    public BleedContainer(LivingEntity target, int bleedTicks, int bleedRank, LivingEntity damageSource)
    {
        this.target         = target;
        this.bleedTicks     = bleedTicks;
        this.bleedRank      = bleedRank;
        this.damageSource   = damageSource;
    }
}
