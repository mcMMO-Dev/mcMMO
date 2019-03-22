package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.config.Unload;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class SimpleRepairableManager implements Unload {
    private HashMap<Material, SimpleRepairable> repairables;

    @Override
    public void unload() {
        repairables.clear();
    }

    public SimpleRepairableManager(List<SimpleRepairable> repairablesCollection) {
        this.repairables = new HashMap<Material, SimpleRepairable>(repairablesCollection.size());
        registerRepairables(repairablesCollection);
    }

    public void registerRepairable(SimpleRepairable repairable) {
        Material item = repairable.getItemMaterial();
        repairables.put(item, repairable);
    }

    public void registerRepairables(List<SimpleRepairable> repairables) {
        for (SimpleRepairable repairable : repairables) {
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
