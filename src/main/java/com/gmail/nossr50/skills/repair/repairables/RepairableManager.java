package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.config.Unload;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class RepairableManager implements Unload {
    private HashMap<Material, Repairable> repairables;

    public RepairableManager(List<Repairable> repairablesCollection) {
        this.repairables = new HashMap<>(repairablesCollection.size());
        registerRepairables(repairablesCollection);
    }

    @Override
    public void unload() {
        repairables.clear();
    }

    public void registerRepairable(Repairable repairable) {
        Material item = repairable.getItemMaterial();
        repairables.put(item, repairable);
    }

    public void registerRepairables(List<Repairable> repairables) {
        for (Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    public boolean isRepairable(Material type) {
        return repairables.containsKey(type);
    }

    public boolean isRepairable(ItemStack itemStack) {
        return isRepairable(itemStack.getType());
    }

    public Repairable getRepairable(Material type) {
        return repairables.get(type);
    }
}
