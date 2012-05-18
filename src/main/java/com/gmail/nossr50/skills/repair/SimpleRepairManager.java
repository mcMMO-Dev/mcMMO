package com.gmail.nossr50.skills.repair;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SimpleRepairManager implements RepairManager {
    private HashMap<Integer, Repairable> repairables;

    protected SimpleRepairManager() {
        this(55);
    }

    protected SimpleRepairManager(int repairablesSize) {
        this.repairables = new HashMap<Integer, Repairable>(repairablesSize);
    }

    @Override
    public void registerRepairable(Repairable repairable) {
        Integer itemId = repairable.getItemId();
        repairables.put(itemId, repairable);
    }

    @Override
    public void registerRepairables(List<Repairable> repairables) {
        for(Repairable repairable : repairables) {
            registerRepairable(repairable);
        }
    }

    @Override
    public boolean isRepairable(int itemId) {
        return repairables.containsKey(itemId);
    }

    @Override
    public void handleRepair(Player player, ItemStack item) {
        // TODO Auto-generated method stub
    }
}
