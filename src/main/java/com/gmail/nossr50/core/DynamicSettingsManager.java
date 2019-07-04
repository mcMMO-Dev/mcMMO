package com.gmail.nossr50.core;

import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.behaviours.SkillBehaviourManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.experience.ExperienceManager;
import com.google.common.collect.Maps;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The DSM (Dynamic Settings Manager) is responsible for
 * 1) Inits managers which convert platform generic settings from the configs (specifically pulling from ConfigManager) into usable data for this platform
 * 2) Retrieving or Setting variables for core systems in mcMMO without permanent change (WIP)
 * <p>
 * This class is a WIP, expect API breakages in the future
 * Currently implementation of this class will only be friendly to Bukkit, this will change in the near future
 */
public class DynamicSettingsManager {

    private final mcMMO pluginRef;

    /* UNLOAD REGISTER */
    private SkillPropertiesManager skillPropertiesManager;

    /* COLLECTION MANAGERS */
    private RepairableManager repairableManager;
    private SalvageableManager salvageableManager;

    /* Platform Ready Managers */
    private BonusDropManager bonusDropManager;
    private ExperienceManager experienceManager;
    private WorldBlackListManager worldBlackListManager;

    /* Party Settings */
    private HashMap<Material, Integer> partyItemWeights;
    private HashMap<PartyFeature, Integer> partyFeatureUnlocks;

    /* Skill Behaviours */
    private SkillBehaviourManager skillBehaviourManager;
    //TODO: This class is a band-aid fix for a large problem with mcMMO code, they will be removed once the new skill system is in place


    public DynamicSettingsManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        /*
         * Managers
         */

        //Assign Maps
        initPartySettings();

        // Register Managers
        initSkillPropertiesManager();
        initMiscManagers();
        initCollectionManagers();
    }

    private void initPartySettings() {
        partyItemWeights = Maps.newHashMap(pluginRef.getConfigManager().getConfigParty().getPartyItemShare().getItemShareMap()); //Item Share Weights
        partyFeatureUnlocks = Maps.newHashMap(pluginRef.getConfigManager().getConfigParty().getPartyXP().getPartyLevel().getPartyFeatureUnlockMap()); //Party Progression
    }

    private void initSkillPropertiesManager() {
        skillPropertiesManager = new SkillPropertiesManager(pluginRef);
        skillPropertiesManager.fillRegisters();
    }

    /**
     * Misc managers
     */
    private void initMiscManagers() {
        //Init Skill Behaviour Manager
        skillBehaviourManager = new SkillBehaviourManager(pluginRef);

        initExperienceManager();

        initWorldBlackList();
    }

    private void initWorldBlackList() {
        worldBlackListManager = new WorldBlackListManager();
        worldBlackListManager.addBlackListedWorlds(pluginRef.getConfigManager().getConfigWorldBlacklist().getBlackListedWorlds());
    }

    private void initExperienceManager() {
        experienceManager = new ExperienceManager(pluginRef);
        //Set the global XP val
        experienceManager.setGlobalXpMult(pluginRef.getConfigManager().getConfigExperience().getGlobalXPMultiplier());
        experienceManager.buildBlockXPMaps(); //Block XP value maps
        experienceManager.fillCombatXPMultiplierMap(pluginRef.getConfigManager().getConfigExperience().getCombatExperienceMap());
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
        bonusDropManager = new BonusDropManager(pluginRef);

        //Register Bonus Drops
        registerBonusDrops();
    }

    /**
     * Get all loaded repairables (loaded from all repairable configs)
     *
     * @return the currently loaded repairables
     */
    public ArrayList<Repairable> getRepairables() {
        return pluginRef.getConfigManager().getConfigRepair().getConfigRepairablesList();
    }

    /**
     * Get all loaded salvageables (loaded from all salvageable configs)
     *
     * @return the currently loaded salvageables
     */
    public ArrayList<Salvageable> getSalvageables() {
        return pluginRef.getConfigManager().getConfigSalvage().getConfigSalvageablesList();
    }

    /**
     * Registers bonus drops from several skill configs
     */
    public void registerBonusDrops() {
        bonusDropManager.addToWhitelistByNameID(pluginRef.getConfigManager().getConfigMining().getBonusDrops());
        bonusDropManager.addToWhitelistByNameID(pluginRef.getConfigManager().getConfigHerbalism().getBonusDrops());
//        bonusDropManager.addToWhitelistByNameID(mcMMO.getConfigManager().getConfigWoodcutting().getBonusDrops());
    }

    public RepairableManager getRepairableManager() {
        return repairableManager;
    }

    public SalvageableManager getSalvageableManager() {
        return salvageableManager;
    }

    public ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    public BonusDropManager getBonusDropManager() {
        return bonusDropManager;
    }

    public HashMap<Material, Integer> getPartyItemWeights() {
        return partyItemWeights;
    }

    public HashMap<PartyFeature, Integer> getPartyFeatureUnlocks() {
        return partyFeatureUnlocks;
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

    public SkillBehaviourManager getSkillBehaviourManager() {
        return skillBehaviourManager;
    }

    public WorldBlackListManager getWorldBlackListManager() {
        return worldBlackListManager;
    }

    public boolean isWorldBlacklisted(String worldName) {
        return getWorldBlackListManager().isWorldBlacklisted(worldName);
    }
}
