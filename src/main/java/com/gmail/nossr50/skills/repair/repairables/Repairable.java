package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Repairable {
    private final ItemStack item;
    private int minimumLevel = 0;
    private short maximumDurability;
    private RepairTransaction repairTransaction;
    private boolean strictMatchingItem = false;
//    private boolean strictMatchingRepairTransaction = false;
    private int baseXP = 0;
    private RawNBT rawNBT;
    private int repairCount = 1;
    private PermissionWrapper permissionWrapper;
    private boolean hasPermission = false;
    private boolean hasNBT = false;

    public Repairable(ItemStack item, int minimumLevel, short maximumDurability, RepairTransaction repairTransaction, boolean strictMatchingItem, int baseXP, int repairCount) {
        this.item = item;
        this.minimumLevel = minimumLevel;
        this.maximumDurability = maximumDurability;
        this.repairTransaction = repairTransaction;
        this.strictMatchingItem = strictMatchingItem;
//        this.strictMatchingRepairTransaction = strictMatchingRepairTransaction;
        this.baseXP = baseXP;
        this.repairCount = repairCount;
    }

    public ItemStack getItem() {
        return item;
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

    public boolean isStrictMatchingItem() {
        return strictMatchingItem;
    }

    public void setStrictMatchingItem(boolean strictMatchingItem) {
        this.strictMatchingItem = strictMatchingItem;
    }

//    public boolean isStrictMatchingRepairTransaction() {
//        return strictMatchingRepairTransaction;
//    }
//
//    public void setStrictMatchingRepairTransaction(boolean strictMatchingRepairTransaction) {
//        this.strictMatchingRepairTransaction = strictMatchingRepairTransaction;
//    }

    public int getBaseXP() {
        return baseXP;
    }

    public void setBaseXP(int baseXP) {
        this.baseXP = baseXP;
    }

    public RawNBT getRawNBT() {
        return rawNBT;
    }

    public void setRawNBT(RawNBT rawNBT) {
        this.rawNBT = rawNBT;
        hasNBT = true;
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
        hasPermission = true;
    }

    public boolean hasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }

    public boolean hasNBT() {
        return hasNBT;
    }

    public void setHasNBT(boolean hasNBT) {
        this.hasNBT = hasNBT;
    }
}
