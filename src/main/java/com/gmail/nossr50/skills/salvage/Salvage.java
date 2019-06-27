package com.gmail.nossr50.skills.salvage;

import org.bukkit.Material;

public class Salvage {

    public static Material anvilMaterial;
    public static boolean arcaneSalvageDowngrades;
    public static boolean arcaneSalvageEnchantLoss;

    public Salvage() {
        anvilMaterial = pluginRef.getConfigManager().getConfigSalvage().getGeneral().getSalvageAnvilMaterial();
        arcaneSalvageDowngrades = pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().isDowngradesEnabled();
        arcaneSalvageEnchantLoss = pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().isMayLoseEnchants();
    }

    protected static int calculateSalvageableAmount(short currentDurability, short maxDurability, int baseAmount) {
        double percentDamaged = (maxDurability <= 0) ? 1D : (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }
}