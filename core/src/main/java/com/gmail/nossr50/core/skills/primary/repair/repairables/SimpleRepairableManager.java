package com.gmail.nossr50.core.skills.primary.repair.repairables;


import com.gmail.nossr50.core.mcmmo.item.ItemStack;

import java.util.List;

public class SimpleRepairableManager implements RepairableManager {
    private HashMap<Material, Repairable> repairables;

    public SimpleRepairableManager() {
        this(55);
    }

    public SimpleRepairableManager(int repairablesSize) {
        this.repairables = new HashMap<Material, Repairable>(repairablesSize);
    }

    @Override
    public void registerRepairable(Repairable repairable) {
        Material item = repairable.getItemMaterial();
        repairables.put(item, repairable);
    }

    @Override
    public void registerRepairables(List<Repairable> repairables) {
        for (Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    @Override
    public boolean isRepairable(Material type) {
        return repairables.containsKey(type);
    }

    @Override
    public boolean isRepairable(ItemStack itemStack) {
        return isRepairable(itemStack.getType());
    }

    @Override
    public Repairable getRepairable(Material type) {
        return repairables.get(type);
    }
}
