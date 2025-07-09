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

    public static Material anvilMaterial = mcMMO.p.getGeneralConfig().getSalvageAnvilMaterial();

    /*public static int    salvageMaxPercentageLevel = mcMMO.p.getAdvancedConfig().getSalvageMaxPercentageLevel();
    public static double salvageMaxPercentage      = mcMMO.p.getAdvancedConfig().getSalvageMaxPercentage();

    public static int advancedSalvageUnlockLevel = RankUtils.getRankUnlockLevel(SubSkillType.SALVAGE_SCRAP_COLLECTOR, 1);*/

    public static boolean arcaneSalvageDowngrades = mcMMO.p.getAdvancedConfig()
            .getArcaneSalvageEnchantDowngradeEnabled();
    public static boolean arcaneSalvageEnchantLoss = mcMMO.p.getAdvancedConfig()
            .getArcaneSalvageEnchantLossEnabled();

    static int calculateSalvageableAmount(int currentDurability, short maxDurability,
            int baseAmount) {
        double percentDamaged = (maxDurability <= 0) ? 1D
                : (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }
}