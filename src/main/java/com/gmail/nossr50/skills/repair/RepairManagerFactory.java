package com.gmail.nossr50.skills.repair;

public class RepairManagerFactory {
    public static RepairManager getRepairManager() {
        // TODO: Add in loading from config what type of manager we want.
        return new SimpleRepairManager();
    }

    public static RepairManager getRepairManager(int repairablesSize) {
        // TODO: Add in loading from config what type of manager we want.
        return new SimpleRepairManager(repairablesSize);
    }
}
