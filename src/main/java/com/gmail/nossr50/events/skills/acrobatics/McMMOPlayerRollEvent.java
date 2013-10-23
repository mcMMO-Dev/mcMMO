package com.gmail.nossr50.events.skills.acrobatics;

import org.bukkit.entity.Player;

public class McMMOPlayerRollEvent extends McMMOPlayerAcrobaticsEvent {
    private double damageTaken;
    private float xpGained;
    private boolean graceful;

    public McMMOPlayerRollEvent(Player player, double damageTaken, float xpGained, boolean graceful) {
        super(player);
        this.damageTaken = damageTaken;
        this.xpGained = xpGained;
        this.graceful = graceful;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public void setDamageTaken(double damageTaken) {
        this.damageTaken = damageTaken;
    }

    public float getXpGained() {
        return xpGained;
    }

    public void setXpGained(float xpGained) {
        this.xpGained = xpGained;
    }

    public boolean isGraceful() {
        return graceful;
    }
}
