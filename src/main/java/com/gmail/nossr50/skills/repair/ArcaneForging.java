package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.mcMMO;

public class ArcaneForging {

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static boolean arcaneForgingDowngrades = mcMMO.p.getAdvancedConfig()
            .getArcaneForgingDowngradeEnabled();

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static boolean arcaneForgingEnchantLoss = mcMMO.p.getAdvancedConfig()
            .getArcaneForgingEnchantLossEnabled();
}
