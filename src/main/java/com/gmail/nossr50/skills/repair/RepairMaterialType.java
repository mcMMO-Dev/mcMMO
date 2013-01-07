package com.gmail.nossr50.skills.repair;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Permissions;

public enum RepairMaterialType {
    STRING,
    LEATHER,
    WOOD,
    STONE,
    IRON,
    GOLD,
    DIAMOND,
    OTHER;

    /**
     * Get the base permissions associated with this RepairMaterialType.
     *
     * @param player The player to check the permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
        switch (this) {
        case STRING:
            return Permissions.stringRepair(player);

        case LEATHER:
            return Permissions.leatherRepair(player);

        case WOOD:
            return Permissions.woodRepair(player);

        case STONE:
            return Permissions.stoneRepair(player);

        case IRON:
            return Permissions.ironRepair(player);

        case GOLD:
            return Permissions.goldRepair(player);

        case DIAMOND:
            return Permissions.diamondRepair(player);

        case OTHER:
            return Permissions.otherMaterialRepair(player);

        default:
            return false;
        }
    }
}
