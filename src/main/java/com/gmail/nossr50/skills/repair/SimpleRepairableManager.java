package com.gmail.nossr50.skills.repair;

import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class SimpleRepairableManager implements RepairableManager {
    private HashMap<Integer, Repairable> repairables;

    protected SimpleRepairableManager() {
        this(55);
    }

    protected SimpleRepairableManager(int repairablesSize) {
        this.repairables = new HashMap<Integer, Repairable>(repairablesSize);
    }

    @Override
    public void registerRepairable(Repairable repairable) {
        Integer itemId = repairable.getItemId();
        repairables.put(itemId, repairable);
    }

    @Override
    public void registerRepairables(List<Repairable> repairables) {
        for (Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    @Override
    public boolean isRepairable(int itemId) {
        return repairables.containsKey(itemId);
    }

    @Override
    public boolean isRepairable(ItemStack itemStack) {
        return isRepairable(itemStack.getTypeId());
    }

    @Override
    public Repairable getRepairable(int id) {
        return repairables.get(id);
    }
}
