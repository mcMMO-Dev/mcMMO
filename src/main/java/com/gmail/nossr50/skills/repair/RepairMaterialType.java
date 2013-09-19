package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;
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
                return Permissions.repairString(player);

            case LEATHER:
                return Permissions.repairLeather(player);

            case WOOD:
                return Permissions.repairWood(player);

            case STONE:
                return Permissions.repairStone(player);

            case IRON:
                return Permissions.repairIron(player);

            case GOLD:
                return Permissions.repairGold(player);

            case DIAMOND:
                return Permissions.repairDiamond(player);

            case OTHER:
                return Permissions.repairOtherMaterials(player);

            default:
                return false;
        }
    }

    public Material getDefaultRepairMaterial() {
        switch (this) {
            case STRING:
                return Material.STRING;

            case LEATHER:
                return Material.LEATHER;

            case WOOD:
                return Material.WOOD;

            case STONE:
                return Material.COBBLESTONE;

            case IRON:
                return Material.IRON_INGOT;

            case GOLD:
                return Material.GOLD_INGOT;

            case DIAMOND:
                return Material.DIAMOND;

            case OTHER:
            default:
                return null;
        }
    }
}
