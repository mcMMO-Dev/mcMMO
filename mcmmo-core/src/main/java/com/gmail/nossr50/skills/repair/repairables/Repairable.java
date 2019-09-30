package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;

public class Repairable {
    private int minimumLevel;
    private short maximumDurability;
    private RepairTransaction repairTransaction;
    private int baseXP;
    private ItemMatch itemMatch;
    private int repairCount;
    private PermissionWrapper permissionWrapper;
    private boolean hasPermission = false;

    public Repairable(ItemMatch itemMatch, int minimumLevel, short maximumDurability, RepairTransaction repairTransaction, int baseXP, int repairCount, PermissionWrapper permissionWrapper) {
        this.minimumLevel = minimumLevel;
        this.maximumDurability = maximumDurability;
        this.repairTransaction = repairTransaction;
        this.baseXP = baseXP;
        this.itemMatch = itemMatch;
        this.repairCount = repairCount;
        this.permissionWrapper = permissionWrapper;

        if(permissionWrapper != null)
            hasPermission = true;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(int minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public short getMaximumDurability() {
        return maximumDurability;
    }

    public void setMaximumDurability(short maximumDurability) {
        this.maximumDurability = maximumDurability;
    }

    public RepairTransaction getRepairTransaction() {
        return repairTransaction;
    }

    public void setRepairTransaction(RepairTransaction repairTransaction) {
        this.repairTransaction = repairTransaction;
    }

    public int getBaseXP() {
        return baseXP;
    }

    public void setBaseXP(int baseXP) {
        this.baseXP = baseXP;
    }

    public ItemMatch getItemMatch() {
        return itemMatch;
    }

    public void setItemMatch(ItemMatch itemMatch) {
        this.itemMatch = itemMatch;
    }

    public int getRepairCount() {
        return repairCount;
    }

    public void setRepairCount(int repairCount) {
        this.repairCount = repairCount;
    }

    public PermissionWrapper getPermissionWrapper() {
        return permissionWrapper;
    }

    public void setPermissionWrapper(PermissionWrapper permissionWrapper) {
        this.permissionWrapper = permissionWrapper;
    }

    public boolean hasPermissionNode() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

}
