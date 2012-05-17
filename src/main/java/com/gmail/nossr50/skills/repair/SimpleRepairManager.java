package com.gmail.nossr50.skills.repair;

import java.util.HashMap;

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
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isRepairable(int itemId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void handleRepair(Player player, ItemStack item) {
        // TODO Auto-generated method stub
    }
}
