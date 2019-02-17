package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.config.Unload;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SimpleRepairableManager implements RepairableManager {
    private HashMap<Material, Repairable> repairables;

    @Override
    public void unload() {
        repairables.clear();
    }

    public SimpleRepairableManager(List<Repairable> repairablesCollection) {
        this.repairables = new HashMap<Material, Repairable>(repairablesCollection.size());
        registerRepairables(repairablesCollection);
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
