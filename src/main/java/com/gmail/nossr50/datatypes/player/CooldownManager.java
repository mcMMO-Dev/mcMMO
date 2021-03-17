package com.gmail.nossr50.datatypes.player;

import com.gmail.nossr50.util.Misc;
import com.neetgames.mcmmo.UniqueDataType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CooldownManager {
    private final @NotNull PlayerData playerDataRef;

    public CooldownManager(@NotNull PlayerData playerDataRef) {
        this.playerDataRef = playerDataRef;
    }

    /*
     * Teleportation cooldown & warmup
     */

    public int getChimeraWingLastUse() {
        return (int) playerDataRef.getUniqueData(UniqueDataType.CHIMAERA_WING_DATS);
    }

    public void actualizeChimeraWingLastUse() {
        playerDataRef.setChimaeraWingDATS((int) (System.currentTimeMillis() / Misc.TIME_CONVERSION_FACTOR));
    }

    public @Nullable Location getTeleportCommenceLocation() {
        return teleportCommence;
    }

    public void setTeleportCommenceLocation(Location location) {
        teleportCommence = location;
    }

    public void actualizeTeleportCommenceLocation(Player player) {
        teleportCommence = player.getLocation();
    }
}
