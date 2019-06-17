package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.skills.repair.RepairTransaction;
import com.gmail.nossr50.util.nbt.RawNBT;
import org.bukkit.Material;

public class Repairable {
    private final Material itemMaterial;
    private final int minimumLevel;
    private final short maximumDurability;
    private RepairTransaction repairTransaction;
    private boolean strictMatching;
    private int baseXP;
    private RawNBT rawNBT;
    private int repairCount;

    public Repairable(Material itemMaterial, RepairTransaction repairTransaction, int minimumLevel, int repairCount, int baseXP, RawNBT rawNBT) {
        this(itemMaterial.getKey().getKey(), repairTransaction, minimumLevel, repairCount, baseXP, false, rawNBT);
    }

    public Repairable(Material itemMaterial, RepairTransaction repairTransaction, int minimumLevel, int repairCount, int baseXP) {
        this(itemMaterial.getKey().getKey(), repairTransaction, minimumLevel, repairCount, baseXP, false, null);
    }

    public Repairable(String itemMaterial, RepairTransaction repairTransaction, int minimumLevel, int repairCount, int baseXP, boolean strictMatching, RawNBT rawNBT) {
        this.itemMaterial = Material.matchMaterial(itemMaterial);
        this.minimumLevel = Math.max(0, minimumLevel);

        this.maximumDurability = this.itemMaterial.getMaxDurability();
        this.repairCount = repairCount;
        this.repairTransaction = repairTransaction;
        this.strictMatching = strictMatching;
        this.baseXP = baseXP;
        this.rawNBT = rawNBT;
    }

    public RawNBT getRawNBT() {
        return rawNBT;
    }

    public int getRepairCount() {
        return repairCount;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }

    public RepairTransaction getRepairTransaction() {
        return repairTransaction;
    }

    public boolean useStrictMatching() {
        return strictMatching;
    }

    public int getBaseXP() {
        return baseXP;
    }

    public short getMaximumDurability() {
        return maximumDurability;
    }

    public short getBaseRepairDurability() {
        return (short)  (maximumDurability / repairCount);
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }
}
