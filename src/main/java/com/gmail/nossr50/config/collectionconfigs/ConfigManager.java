package com.gmail.nossr50.config.collectionconfigs;

import com.gmail.nossr50.config.*;
import com.gmail.nossr50.config.mods.ArmorConfigManager;
import com.gmail.nossr50.config.mods.BlockConfigManager;
import com.gmail.nossr50.config.mods.EntityConfigManager;
import com.gmail.nossr50.config.mods.ToolConfigManager;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.config.treasure.TreasureConfig;
import com.gmail.nossr50.skills.child.ChildConfig;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SimpleSalvageableManager;

import java.util.ArrayList;

/**
 *
 * The Config Manager handles initializing, loading, and unloading registers for all configs that mcMMO uses
 * This makes sure that mcMMO properly loads and unloads its values on reload
 *
 * Config Manager also holds all of our MultiConfigContainers
 *
 * MultiConfigContainers
 *      Represents a collection of config files that serve a similar purpose
 *      As an example, with Repair you can have an unlimited number of files named repair.*.yml and each one will be treated the same and have its collections registered
 *      The master file is always named x.vanilla.yml, for example "repair.vanilla.yml"
 *          To be honest I'm not sure how many people make use of this system, but I'm keeping it since its been in mcMMO for like 6+ years
 */
public final class ConfigManager {

    /* UNLOAD REGISTER */

    private ArrayList<Unload> unloadables;

    /* MULTI CONFIG INSTANCES */

    private MultiConfigContainer<Repairable> repairableMultiConfigContainer;
    private MultiConfigContainer<Salvageable> salvageableMultiConfigContainer;

    /* COLLECTION MANAGERS */

    private SimpleRepairableManager simpleRepairableManager;
    private SimpleSalvageableManager simpleSalvageableManager;

    /* CONFIG INSTANCES */

    private TreasureConfig treasureConfig;
    private AdvancedConfig advancedConfig;
    private PotionConfig potionConfig;
    private CoreSkillsConfig coreSkillsConfig;
    private SoundConfig soundConfig;
    private RankConfig rankConfig;

    public ConfigManager()
    {
        unloadables = new ArrayList<>();

        // Load Config Files
        // I'm pretty these are supposed to be done in a specific order, so don't rearrange them willy nilly
        treasureConfig = new TreasureConfig();
        unloadables.add(treasureConfig);

        advancedConfig = new AdvancedConfig();
        unloadables.add(advancedConfig);

        potionConfig = new PotionConfig();
        unloadables.add(potionConfig);

        coreSkillsConfig = new CoreSkillsConfig();
        unloadables.add(coreSkillsConfig);

        soundConfig = new SoundConfig();
        unloadables.add(soundConfig);

        rankConfig = new RankConfig();
        unloadables.add(rankConfig);

        //TODO: This config serves no purpose so its getting removed
        new ChildConfig();

        if (MainConfig.getInstance().getToolModsEnabled()) {
            new ToolConfigManager();
        }

        if (MainConfig.getInstance().getArmorModsEnabled()) {
            new ArmorConfigManager();
        }

        if (MainConfig.getInstance().getBlockModsEnabled()) {
            new BlockConfigManager();
        }

        if (MainConfig.getInstance().getEntityModsEnabled()) {
            new EntityConfigManager();
        }

        // Multi Config Containers
        initMultiConfigContainers();

        // Register Managers
        initCollectionManagers();
    }

    /**
     * Initializes all of our Multi Config Containers
     */
    private void initMultiConfigContainers()
    {
        //Repair
        repairableMultiConfigContainer = new MultiConfigContainer<>("repair", CollectionClassType.REPAIR);
        unloadables.add(repairableMultiConfigContainer);

        //Salvage
        salvageableMultiConfigContainer = new MultiConfigContainer<>("salvage", CollectionClassType.SALVAGE);
        unloadables.add(salvageableMultiConfigContainer);
    }

    /**
     * Initializes any managers related to config collections
     */
    private void initCollectionManagers()
    {
        // Handles registration of repairables
        simpleRepairableManager = new SimpleRepairableManager(getRepairables());
        unloadables.add(simpleRepairableManager);

        // Handles registration of salvageables
        simpleSalvageableManager = new SimpleSalvageableManager(getSalvageables());
        unloadables.add(simpleSalvageableManager);
    }

    /**
     * Get all loaded repairables (loaded from all repairable configs)
     * @return the currently loaded repairables
     */
    public ArrayList<Repairable> getRepairables()
    {
        return (ArrayList<Repairable>) repairableMultiConfigContainer.getCollection();
    }

    /**
     * Get all loaded salvageables (loaded from all salvageable configs)
     * @return the currently loaded salvageables
     */
    public ArrayList<Salvageable> getSalvageables()
    {
        return (ArrayList<Salvageable>) salvageableMultiConfigContainer.getCollection();
    }

    /**
     * Unloads all config options (prepares for reload)
     */
    public void unloadAllConfigsAndRegisters()
    {
        //Unload
        for(Unload unloadable : unloadables)
        {
            unloadable.unload();
        }

        //Clear
        unloadables.clear();
    }
}
