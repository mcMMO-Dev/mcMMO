package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public class Repair {
    public static Material anvilMaterial;

    public Repair() {
        anvilMaterial = mcMMO.getConfigManager().getConfigRepair().getRepairGeneral().getRepairAnvilMaterial();
    }

    public static Material getRepairAnvilMaterial()
    {
        return anvilMaterial;
    }
}
