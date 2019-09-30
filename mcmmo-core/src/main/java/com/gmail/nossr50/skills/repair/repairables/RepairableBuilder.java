//package com.gmail.nossr50.skills.repair.repairables;
//
//import com.gmail.nossr50.datatypes.items.ItemMatch;
//import com.gmail.nossr50.datatypes.permissions.PermissionWrapper;
//import com.gmail.nossr50.skills.repair.RepairTransaction;
//
//public class RepairableBuilder {
//
//    private int minimumLevel;
//    private short maximumDurability;
//    private RepairTransaction repairTransaction;
//    private int baseXP;
//    private ItemMatch itemMatch;
//    private int repairCount;
//    private PermissionWrapper permissionWrapper;
//
//    public RepairableBuilder(ItemMatch itemMatch, Short maximumDurability, RepairTransaction repairTransaction) {
//        this.itemMatch = itemMatch;
//        this.maximumDurability = maximumDurability;
//        this.repairTransaction = repairTransaction;
//    }
//
//    public RepairableBuilder addMinLevel(Integer minimumLevel) {
//        this.minimumLevel = minimumLevel;
//        return this;
//    }
//
//    public RepairableBuilder setMaximumDurability(Short maximumDurability) {
//        this.maximumDurability = maximumDurability;
//        return this;
//    }
//
//    public RepairableBuilder setRepairTransaction(RepairTransaction repairTransaction) {
//        this.repairTransaction = repairTransaction;
//        return this;
//    }
//
//    public RepairableBuilder setBaseXP(Integer baseXP) {
//        this.baseXP = baseXP;
//        return this;
//    }
//
//    public RepairableBuilder setRepairCount(Integer repairCount) {
//        this.repairCount = repairCount;
//        return this;
//    }
//
//    public RepairableBuilder addPermissionWrapper(PermissionWrapper permissionWrapper) {
//        this.permissionWrapper = permissionWrapper;
//        return this;
//    }
//
//    public Repairable build() {
//        return makeRepairable();
//    }
//
//    private Repairable makeRepairable() {
//        Repairable repairable = new Repairable(itemMatch, minimumLevel, maximumDurability, repairTransaction,
//                baseXP, repairCount, permissionWrapper);
//
//        return repairable;
//    }
//
//}
