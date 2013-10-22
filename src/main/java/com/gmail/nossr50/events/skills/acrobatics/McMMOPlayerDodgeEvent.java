package com.gmail.nossr50.events.skills.acrobatics;

import org.bukkit.entity.Player;

public class McMMOPlayerDodgeEvent extends McMMOPlayerAcrobaticsEvent {
    private double damageTaken;
    private float xpGained;
    private boolean useParticles;

    public McMMOPlayerDodgeEvent(Player player, double damageTaken, float xpGained) {
        super(player);
        this.damageTaken = damageTaken;
        this.xpGained = xpGained;
        useParticles = true;
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

    public boolean shouldUseParticles() {
        return useParticles;
    }

    public void setUseParticles(boolean useParticles) {
        this.useParticles = useParticles;
    }
}
