package com.gmail.nossr50.skills.repair;

import com.gmail.nossr50.mcMMO;
import org.bukkit.Material;

public class Repair {
    private static Repair instance;
    private Material anvilMaterial;
    private double repairMasteryMaxBonus;
    private double repairMasteryMaxBonusLevel;

    public Repair() {
        anvilMaterial = mcMMO.getConfigManager().getConfigRepair().getRepairGeneral().getRepairAnvilMaterial();

        //TODO: Replace this horrid shit
        if (mcMMO.isRetroModeEnabled()) {
            repairMasteryMaxBonus = mcMMO.getConfigManager().getConfigRepair().getRepairSubSkills().getRepairMastery().getSettings().getRetro().getMaxBonusPercentage();
            repairMasteryMaxBonusLevel = mcMMO.getConfigManager().getConfigRepair().getRepairSubSkills().getRepairMastery().getSettings().getRetro().getMaxBonusLevel();
        } else {
            repairMasteryMaxBonus = mcMMO.getConfigManager().getConfigRepair().getRepairSubSkills().getRepairMastery().getSettings().getStandard().maxBonusPercentage;
            repairMasteryMaxBonusLevel = mcMMO.getConfigManager().getConfigRepair().getRepairSubSkills().getRepairMastery().getSettings().getStandard().maxBonusLevel;
        }
    }

    public static Repair getInstance() {
        if (instance == null)
            instance = new Repair();

        return instance;
    }

    public Material getAnvilMaterial() {
        return anvilMaterial;
    }

    public double getRepairMasteryMaxBonus() {
        return repairMasteryMaxBonus;
    }

    public double getRepairMasteryMaxBonusLevel() {
        return repairMasteryMaxBonusLevel;
    }
}
