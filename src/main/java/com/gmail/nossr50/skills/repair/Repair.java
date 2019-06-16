package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public class Repair {
    private static Repair instance;
    private Material anvilMaterial;

    public Repair() {
        anvilMaterial = mcMMO.getConfigManager().getConfigRepair().getRepairGeneral().getRepairAnvilMaterial();
    }

    public static Repair getInstance() {
        if (instance == null)
            instance = new Repair();

        return instance;
    }

    public Material getAnvilMaterial() {
        return anvilMaterial;
    }
}
