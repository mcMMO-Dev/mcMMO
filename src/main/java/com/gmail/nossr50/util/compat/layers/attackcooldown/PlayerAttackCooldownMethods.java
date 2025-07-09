package com.gmail.nossr50.util.compat.layers.attackcooldown;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerAttackCooldownMethods {
    /**
     * Grabs the attack strength for a player Should be noted that as of today there is no way to
     * capture a players current attack strength in spigot when they attack an entity outside of
     * network packet listening
     *
     * @param player target player
     * @return the float value of the player's attack strength
     */
    float getAttackStrength(@NotNull Player player)
            throws InvocationTargetException, IllegalAccessException;

    float getCooldownValue(@NotNull Player player)
            throws InvocationTargetException, IllegalAccessException;

    void resetAttackStrength(@NotNull Player player)
            throws InvocationTargetException, IllegalAccessException;

    int getCooldownFieldValue(@NotNull Player player)
            throws InvocationTargetException, IllegalAccessException;

    void setCooldownFieldValue(@NotNull Player player, int fieldValue)
            throws InvocationTargetException, IllegalAccessException;
}
