package com.gmail.nossr50.util.compat.layers;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public interface PlayerAttackCooldownMethods {
    /**
     * Grabs the attack strength for a player
     * Should be noted that as of today there is no way to capture a players current attack strength in spigot when they attack an entity outside of network packet listening
     * @param player target player
     * @return the float value of the player's attack strength
     */
    float getAttackStrength(Player player) throws InvocationTargetException, IllegalAccessException;

    float getCooldownValue(Player player) throws InvocationTargetException, IllegalAccessException;

    void resetAttackStrength(Player player) throws InvocationTargetException, IllegalAccessException;
}
