package com.gmail.nossr50.skills.repair;

public class RepairableManagerFactory {
    public static RepairableManager getRepairManager() {
        // TODO: Add in loading from config what type of manager we want.
        return new SimpleRepairableManager();
    }

    public static RepairableManager getRepairManager(int repairablesSize) {
        // TODO: Add in loading from config what type of manager we want.
        return new SimpleRepairableManager(repairablesSize);
    }
}
