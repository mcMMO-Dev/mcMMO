package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.config.MainConfig;
import org.bukkit.Material;

public class Repair {
    public static int    repairMasteryMaxBonusLevel = AdvancedConfig.getInstance().getMaxBonusLevel(SubSkillType.REPAIR_REPAIR_MASTERY);
    public static double repairMasteryMaxBonus      = AdvancedConfig.getInstance().getRepairMasteryMaxBonus();

    public static Material anvilMaterial  = MainConfig.getInstance().getRepairAnvilMaterial();
}
