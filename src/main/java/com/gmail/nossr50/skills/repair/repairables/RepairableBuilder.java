package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.items.CustomItemTarget;
import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.inventory.ItemStack;

public class RepairableBuilder {

    private int minimumLevel;
    private short maximumDurability;
    private RepairTransaction repairTransaction;
    private int baseXP;
    private CustomItemTarget customItemTarget;
    private int repairCount;
    private PermissionWrapper permissionWrapper;

    public RepairableBuilder(CustomItemTarget customItemTarget, Short maximumDurability, RepairTransaction repairTransaction) {
        this.customItemTarget = customItemTarget;
        this.maximumDurability = maximumDurability;
        this.repairTransaction = repairTransaction;
    }

    public RepairableBuilder addMinLevel(Integer minimumLevel) {
        this.minimumLevel = minimumLevel;
        return this;
    }

    public RepairableBuilder setMaximumDurability(Short maximumDurability) {
        this.maximumDurability = maximumDurability;
        return this;
    }

    public RepairableBuilder setRepairTransaction(RepairTransaction repairTransaction) {
        this.repairTransaction = repairTransaction;
        return this;
    }

    public RepairableBuilder setBaseXP(Integer baseXP) {
        this.baseXP = baseXP;
        return this;
    }

    public RepairableBuilder setRepairCount(Integer repairCount) {
        this.repairCount = repairCount;
        return this;
    }

    public RepairableBuilder addPermissionWrapper(PermissionWrapper permissionWrapper) {
        this.permissionWrapper = permissionWrapper;
        return this;
    }

    public Repairable build() {
        return makeRepairable();
    }

    private Repairable makeRepairable() {
        Repairable repairable = new Repairable(customItemTarget, minimumLevel, maximumDurability, repairTransaction,
                baseXP, repairCount, permissionWrapper);

        return repairable;
    }

}
