package com.gmail.nossr50.events.skills.secondaryabilities;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.skills.SecondaryAbility;

public class SecondaryAbilityWeightedActivationCheckEvent extends SecondaryAbilityEvent {
    private double chance;

    public SecondaryAbilityWeightedActivationCheckEvent(Player player, SecondaryAbility ability, double chance) {
        super(player, ability);
        this.chance = chance;
    }

    /**
     * Gets the activation chance of the ability 0D being no chance,  1.0D being 100% chance
     *
     * @return The activation chance of the ability
     */
    public double getChance() {
        return chance;
    }

    /**
     * Sets the activation chance of the ability [0D-1.0D]
     *
     * @param chance The activation chance of the ability
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    /**
     * Sets the activation chance of the ability to 100% or 0%
     *
     * @param success whether it should be successful or not
     */
    public void setSuccessful(boolean success) {
        this.chance = success ? 1.0D : 0D;
    }
}
