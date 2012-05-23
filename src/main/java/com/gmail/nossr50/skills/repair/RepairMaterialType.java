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
            return Permissions.getInstance().stringRepair(player);

        case LEATHER:
            return Permissions.getInstance().leatherRepair(player);

        case WOOD:
            return Permissions.getInstance().woodRepair(player);

        case STONE:
            return Permissions.getInstance().stoneRepair(player);

        case IRON:
            return Permissions.getInstance().ironRepair(player);

        case GOLD:
            return Permissions.getInstance().goldRepair(player);

        case DIAMOND:
            return Permissions.getInstance().diamondRepair(player);

        case OTHER:
            return Permissions.getInstance().otherMaterialRepair(player);

        default:
            return false;
        }
    }
}
