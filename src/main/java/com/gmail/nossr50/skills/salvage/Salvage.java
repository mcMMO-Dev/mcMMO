package com.gmail.nossr50.skills.salvage;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public final class Salvage {

    /**
     * This is a static utility class, therefore we don't want any instances of this class. Making
     * the constructor private prevents accidents like that.
     */
    private Salvage() {
    }

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.GeneralConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static Material anvilMaterial = mcMMO.p.getGeneralConfig().getSalvageAnvilMaterial();

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static boolean arcaneSalvageDowngrades = mcMMO.p.getAdvancedConfig()
            .getArcaneSalvageEnchantDowngradeEnabled();

    /**
     * No longer used by mcMMO internally, use the config calls instead.
     * @see com.gmail.nossr50.config.AdvancedConfig
     */
    @Deprecated(since = "2.2.052", forRemoval = true)
    public static boolean arcaneSalvageEnchantLoss = mcMMO.p.getAdvancedConfig()
            .getArcaneSalvageEnchantLossEnabled();

    static int calculateSalvageableAmount(int currentDurability, short maxDurability,
            int baseAmount) {
        double percentDamaged = (maxDurability <= 0) ? 1D
                : (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }
}