package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public class Repair {
    public static int repairMasteryMaxBonusLevel = mcMMO.p.getAdvancedConfig()
            .getMaxBonusLevel(SubSkillType.REPAIR_REPAIR_MASTERY);
    public static double repairMasteryMaxBonus = mcMMO.p.getAdvancedConfig()
            .getRepairMasteryMaxBonus();

    public static Material anvilMaterial = mcMMO.p.getGeneralConfig().getRepairAnvilMaterial();
}
