package com.gmail.nossr50.skills.repair;

import org.bukkit.Material;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getRepairMasteryMaxLevel();
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static Material anvilMaterial  = Config.getInstance().getRepairAnvilMaterial();
}
