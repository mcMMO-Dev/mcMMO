package com.gmail.nossr50.events.skills.secondaryabilities;

import org.bukkit.entity.Player;
import com.gmail.nossr50.datatypes.skills.SecondaryAbilityType;

public class SecondaryAbilityWeightedActivationCheckEvent extends SecondaryAbilityEvent {

    private double chance;

    public SecondaryAbilityWeightedActivationCheckEvent(Player player, SecondaryAbilityType ability, double chance) {
        super(player, ability);
        this.chance = chance;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = Math.min(1D, chance);
    }

    public void setSuccessful(boolean success) {
        this.chance = success ? 1.0D : 0D;
    }
}
