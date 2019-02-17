package com.gmail.nossr50.core.runnables.skills;


import com.gmail.nossr50.core.mcmmo.entity.Living;

public class BleedContainer {
    public int bleedTicks;
    public int bleedRank;
    public Living target;
    public Living damageSource;

    public BleedContainer(Living target, int bleedTicks, int bleedRank, Living damageSource) {
        this.target = target;
        this.bleedTicks = bleedTicks;
        this.bleedRank = bleedRank;
        this.damageSource = damageSource;
    }
}
