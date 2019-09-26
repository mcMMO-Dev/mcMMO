package com.gmail.nossr50.skills.repair.repairables;

import com.gmail.nossr50.datatypes.items.ItemMatch;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class RepairableManager {
    private HashMap<ItemMatch<?>, Repairable> repairables;

    public RepairableManager(List<Repairable> repairablesCollection) {
        this.repairables = new HashMap<>(repairablesCollection.size());
        registerRepairables(repairablesCollection);
    }

    public void registerRepairable(Repairable repairable) {
        repairables.put(repairable.getItemMatch(), repairable);
    }

    public void registerRepairables(List<Repairable> repairables) {
        for (Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
    //TODO: Make these use item matching
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
