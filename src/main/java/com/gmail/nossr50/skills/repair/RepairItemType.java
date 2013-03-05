package com.gmail.nossr50.skills.repair;

import org.bukkit.entity.Player;

import com.gmail.nossr50.util.Permissions;

public enum RepairItemType {
    ARMOR,
    TOOL,
    OTHER;

    /**
     * Get the base permissions associated with this RepairItemType.
     *
     * @param player The player to check the permissions for
     * @return true if the player has permissions, false otherwise
     */
    public boolean getPermissions(Player player) {
        switch (this) {
            case ARMOR:
                return Permissions.repairArmor(player);

            case TOOL:
                return Permissions.repairTools(player);

            case OTHER:
                return Permissions.repairOtherItems(player);

            default:
                return false;
        }
    }
}
