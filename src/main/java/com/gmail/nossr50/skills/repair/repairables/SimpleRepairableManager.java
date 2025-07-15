package com.gmail.nossr50.skills.repair.repairables;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SimpleRepairableManager implements RepairableManager {
    private final HashMap<Material, Repairable> repairables;

    public SimpleRepairableManager() {
        this(55);
    }

    public SimpleRepairableManager(int repairablesSize) {
        this.repairables = new HashMap<>(repairablesSize);
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
