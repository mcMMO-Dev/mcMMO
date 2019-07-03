package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class SalvageBehaviour {

    private final mcMMO pluginRef;

    private Material anvilMaterial;
    private boolean arcaneSalvageDowngrades;
    private boolean arcaneSalvageEnchantLoss;

    public SalvageBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;

        anvilMaterial = pluginRef.getConfigManager().getConfigSalvage().getGeneral().getSalvageAnvilMaterial();
        arcaneSalvageDowngrades = pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().isDowngradesEnabled();
        arcaneSalvageEnchantLoss = pluginRef.getConfigManager().getConfigSalvage().getConfigArcaneSalvage().isMayLoseEnchants();
    }

    public int calculateSalvageableAmount(short currentDurability, short maxDurability, int baseAmount) {
        double percentDamaged = (maxDurability <= 0) ? 1D : (double) (maxDurability - currentDurability) / maxDurability;

        return (int) Math.floor(baseAmount * percentDamaged);
    }

    public Material getAnvilMaterial() {
        return anvilMaterial;
    }

    public boolean isArcaneSalvageDowngrades() {
        return arcaneSalvageDowngrades;
    }

    public boolean isArcaneSalvageEnchantLoss() {
        return arcaneSalvageEnchantLoss;
    }
}
