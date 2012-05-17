package com.gmail.nossr50.skills.repair;

public class SimpleRepairable implements Repairable {
    private final int itemId, repairMaterialId, minimumQuantity, minimumLevel;
    private final short maximumDurability, baseRepairDurability;

    protected SimpleRepairable(int itemId, int repairMaterialId, int minimumLevel, int minimumQuantity, short maximumDurability) {
        this.itemId = itemId;
        this.repairMaterialId = repairMaterialId;
        this.minimumLevel = minimumLevel;
        this.minimumQuantity = minimumQuantity;
        this.maximumDurability = maximumDurability;
        this.baseRepairDurability = (short) (maximumDurability / minimumQuantity);
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public int getRepairMaterialId() {
        return repairMaterialId;
    }

    @Override
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    @Override
    public short getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public short getBaseRepairDurability() {
        return baseRepairDurability;
    }

    @Override
    public int getMinimumLevel() {
        return minimumLevel;
    }
}
