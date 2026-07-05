package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public class Repair {
    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static int repairMasteryMaxBonusLevel = mcMMO.p.getAdvancedConfig()
            .getMaxBonusLevel(SubSkillType.REPAIR_REPAIR_MASTERY);

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static double repairMasteryMaxBonus = mcMMO.p.getAdvancedConfig()
            .getRepairMasteryMaxBonus();

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static Material anvilMaterial = mcMMO.p.getGeneralConfig().getRepairAnvilMaterial();
}
