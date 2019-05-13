package com.gmail.nossr50.core;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.experience.ExperienceMapManager;
import org.bukkit.Material;

import java.util.ArrayList;

/**
 * The DSM (Dynamic Settings Manager) is responsible for
 * 1) Inits managers which convert platform generic settings from the configs (specifically pulling from ConfigManager) into usable data for this platform
 * 2) Retrieving or Setting variables for core systems in mcMMO without permanent change (WIP)
 *
 * This class is a WIP, expect API breakages in the future
 * Currently implementation of this class will only be friendly to Bukkit, this will change in the near future
 */
public class DynamicSettingsManager {

    /* UNLOAD REGISTER */
    private SkillPropertiesManager skillPropertiesManager;

    /* COLLECTION MANAGERS */
    private RepairableManager repairableManager;
    private SalvageableManager salvageableManager;

    /* Platform Ready Managers */
    private BonusDropManager bonusDropManager;
    private ExperienceMapManager experienceMapManager;


    public DynamicSettingsManager() {
        /*
         * Managers
         */

        // Register Managers
        initSkillPropertiesManager();
        initMiscManagers();
        initCollectionManagers();
    }

    private void initSkillPropertiesManager() {
        skillPropertiesManager = new SkillPropertiesManager();
        skillPropertiesManager.fillRegisters();
    }

    /**
     * Misc managers
     */
    private void initMiscManagers() {
        experienceMapManager = new ExperienceMapManager();
        //Set the global XP val
        experienceMapManager.setGlobalXpMult(mcMMO.getConfigManager().getConfigExperience().getGlobalXPMultiplier());
        experienceMapManager.buildBlockXPMaps(); //Block XP value maps
        experienceMapManager.fillCombatXPMultiplierMap(mcMMO.getConfigManager().getConfigExperience().getCombatExperienceMap());
//        potionManager = new PotionManager();
    }

    /**
     * Initializes any managers related to config collections
     */
    private void initCollectionManagers() {
        // Handles registration of repairables
        repairableManager = new RepairableManager(getRepairables());

        // Handles registration of salvageables
        salvageableManager = new SalvageableManager(getSalvageables());

        // Handles registration of bonus drops
        bonusDropManager = new BonusDropManager();

        //Register Bonus Drops
        registerBonusDrops();
    }

    /**
     * Get all loaded repairables (loaded from all repairable configs)
     *
     * @return the currently loaded repairables
     */
    public ArrayList<Repairable> getRepairables() {
        return mcMMO.getConfigManager().getConfigRepair().getConfigRepairablesList();
    }

    /**
     * Get all loaded salvageables (loaded from all salvageable configs)
     *
     * @return the currently loaded salvageables
     */
    public ArrayList<Salvageable> getSalvageables() {
        return mcMMO.getConfigManager().getConfigSalvage().getConfigSalvageablesList();
    }

    /**
     * Registers bonus drops from several skill configs
     */
    public void registerBonusDrops() {
        bonusDropManager.addToWhitelistByNameID(mcMMO.getConfigManager().getConfigMining().getBonusDrops());
//        bonusDropManager.addToWhitelistByNameID(configHerbalism.getBonusDrops());
//        bonusDropManager.addToWhitelistByNameID(configWoodcutting.getBonusDrops());
    }

    public RepairableManager getRepairableManager() {
        return repairableManager;
    }

    public SalvageableManager getSalvageableManager() {
        return salvageableManager;
    }

    public ExperienceMapManager getExperienceMapManager() {
        return experienceMapManager;
    }

    public BonusDropManager getBonusDropManager() {
        return bonusDropManager;
    }

    public boolean isBonusDropsEnabled(Material material) {
        return getBonusDropManager().isBonusDropWhitelisted(material);
    }

    public double getSkillMaxBonusLevel(SubSkillType subSkillType) {
        return skillPropertiesManager.getMaxBonusLevel(subSkillType);
    }

    public double getSkillMaxChance(SubSkillType subSkillType) {
        return skillPropertiesManager.getMaxChance(subSkillType);
    }

    public SkillPropertiesManager getSkillPropertiesManager() {
        return skillPropertiesManager;
    }

}
