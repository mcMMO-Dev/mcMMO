package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.inventory.ItemStack;

public class RepairableBuilder {

    private final ItemStack item;
    private int minimumLevel = 0;
    private short maximumDurability;
    private RepairTransaction repairTransaction;
    private boolean strictMatchingItem = false;
    private boolean strictMatchingRepairTransaction = false;
    private int baseXP = 0;
    private RawNBT rawNBT;
    private int repairCount = 1;
    private PermissionWrapper permissionWrapper;

    public RepairableBuilder(ItemStack item) {
        this.item = item;
        this.maximumDurability = item.getType().getMaxDurability();
    }

    public RepairableBuilder minLevel(Integer minimumLevel) {
        this.minimumLevel = minimumLevel;
        return this;
    }

    public RepairableBuilder maximumDurability(Short maximumDurability) {
        this.maximumDurability = maximumDurability;
        return this;
    }

    public RepairableBuilder repairTransaction(RepairTransaction repairTransaction) {
        this.repairTransaction = repairTransaction;
        return this;
    }

    public RepairableBuilder strictMatchingItem(Boolean strictMatchingItem) {
        this.strictMatchingItem = strictMatchingItem;
        return this;
    }

    public RepairableBuilder strictMatchingRepairTransaction(Boolean strictMatchingRepairTransaction) {
        this.strictMatchingRepairTransaction = strictMatchingRepairTransaction;
        return this;
    }

    public RepairableBuilder baseXP(Integer baseXP) {
        this.baseXP = baseXP;
        return this;
    }

    public RepairableBuilder rawNBT(RawNBT rawNBT) {
        this.rawNBT = rawNBT;
        return this;
    }

    public RepairableBuilder repairCount(Integer repairCount) {
        this.repairCount = repairCount;
        return this;
    }

    public RepairableBuilder permissionWrapper(PermissionWrapper permissionWrapper) {
        this.permissionWrapper = permissionWrapper;
        return this;
    }

    public Repairable build() {
        return makeRepairable();
    }

    private Repairable makeRepairable() {
        Repairable repairable = new Repairable(item, minimumLevel, maximumDurability, repairTransaction,
                strictMatchingItem, strictMatchingRepairTransaction, baseXP, repairCount);

        if(permissionWrapper != null) {
            repairable.setPermissionWrapper(permissionWrapper);
        }

        if(rawNBT != null) {
            repairable.setRawNBT(rawNBT);
        }

        return repairable;
    }

}
