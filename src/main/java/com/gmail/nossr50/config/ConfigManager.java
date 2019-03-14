package com.gmail.nossr50.config;

import com.gmail.nossr50.config.collectionconfigs.RepairConfig;
import com.gmail.nossr50.config.collectionconfigs.SalvageConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.hocon.SerializedConfigLoader;
import com.gmail.nossr50.config.hocon.database.ConfigDatabase;
import com.gmail.nossr50.config.hocon.playerleveling.ConfigLeveling;
import com.gmail.nossr50.config.hocon.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.config.hocon.worldblacklist.ConfigWorldBlacklist;
import com.gmail.nossr50.config.party.ItemWeightConfig;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.config.treasure.ExcavationTreasureConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.config.treasure.HerbalismTreasureConfig;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.SimpleRepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SimpleSalvageableManager;
import com.gmail.nossr50.util.experience.ExperienceMapManager;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * The Config Manager handles initializing, loading, and unloading registers for all configs that mcMMO uses
 * This makes sure that mcMMO properly loads and unloads its values on reload
 *
 * Config Manager also holds all of our MultiConfigContainers
 */
public final class ConfigManager {

    /* UNLOAD REGISTER */

    private ArrayList<Unload> unloadables;
    private ArrayList<File> userFiles;

    /* MULTI CONFIG INSTANCES */

    //private MultiConfigContainer<Repairable> repairableMultiConfigContainer;
    //private MultiConfigContainer<Salvageable> salvageableMultiConfigContainer;

    /* COLLECTION MANAGERS */

    private SimpleRepairableManager simpleRepairableManager;
    private SimpleSalvageableManager simpleSalvageableManager;

    /* MOD MANAGERS */

    //TODO: Add these back when modded servers become a thing again

    /* MISC MANAGERS */

    private ExperienceMapManager experienceMapManager;

    //private ModManager modManager;

    /*private ToolConfigManager toolConfigManager;
    private ArmorConfigManager armorConfigManager;
    private BlockConfigManager blockConfigManager;
    private EntityConfigManager entityConfigManager;*/

    /* CONFIG INSTANCES */

    private SerializedConfigLoader<ConfigDatabase> configDatabase;
    private SerializedConfigLoader<ConfigScoreboard> configScoreboard;
    private SerializedConfigLoader<ConfigLeveling> configLeveling;
    private SerializedConfigLoader<ConfigWorldBlacklist> configWorldBlacklist;
    private MainConfig mainConfig;
    private FishingTreasureConfig fishingTreasureConfig;
    private ExcavationTreasureConfig excavationTreasureConfig;
    private HerbalismTreasureConfig herbalismTreasureConfig;
    private ExperienceConfig experienceConfig;
    private AdvancedConfig advancedConfig;
    private PotionConfig potionConfig;
    private CoreSkillsConfig coreSkillsConfig;
    private SoundConfig soundConfig;
    private RankConfig rankConfig;
    private ItemWeightConfig itemWeightConfig;
    private RepairConfig repairConfig;
    private SalvageConfig salvageConfig;

    /* CONFIG ERRORS */

    private ArrayList<String> configErrors; //Collect errors to whine about to server admins

    public ConfigManager()
    {
        unloadables = new ArrayList<>();
        userFiles = new ArrayList<>();
    }

    public void loadConfigs()
    {
        // Load Config Files
        // I'm pretty these are supposed to be done in a specific order, so don't rearrange them willy nilly

        //TODO: Not sure about the order of MainConfig
        //Serialized Configs
        configDatabase = new SerializedConfigLoader<>(ConfigDatabase.class, "database_settings.conf", null);
        configScoreboard = new SerializedConfigLoader<>(ConfigScoreboard.class, "scoreboard.conf", null);
        configLeveling = new SerializedConfigLoader<>(ConfigLeveling.class, "player_leveling.conf", null);
        configWorldBlacklist = new SerializedConfigLoader<>(ConfigWorldBlacklist.class, "world_blacklist.conf", null);

        mainConfig = new MainConfig();

        fishingTreasureConfig = new FishingTreasureConfig();
        excavationTreasureConfig = new ExcavationTreasureConfig();
        herbalismTreasureConfig = new HerbalismTreasureConfig();

        advancedConfig = new AdvancedConfig();

        //TODO: Not sure about the order of experience config
        experienceConfig = new ExperienceConfig();

        potionConfig = new PotionConfig();

        coreSkillsConfig = new CoreSkillsConfig();

        soundConfig = new SoundConfig();

        rankConfig = new RankConfig();

        itemWeightConfig = new ItemWeightConfig();

        repairConfig = new RepairConfig();

        salvageConfig = new SalvageConfig();


        /*if (MainConfig.getInstance().getToolModsEnabled()) {
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
        }*/

        // Multi Config Containers
        //initMultiConfigContainers();

        /*
         * Managers
         */

        // Register Managers
        initMiscManagers();
        initCollectionManagers();
    }

    /**
     * Misc managers
     */
    private void initMiscManagers()
    {
        experienceMapManager = new ExperienceMapManager();
    }

    /**
     * Initializes all of our Multi Config Containers
     */
    /*private void initMultiConfigContainers()
    {
        //Repair
        repairableMultiConfigContainer = new MultiConfigContainer<>("repair", CollectionClassType.REPAIR);
        unloadables.add(repairableMultiConfigContainer);

        //Salvage
        salvageableMultiConfigContainer = new MultiConfigContainer<>("salvage", CollectionClassType.SALVAGE);
        unloadables.add(salvageableMultiConfigContainer);
    }*/

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
        return (ArrayList<Repairable>) repairConfig.genericCollection;
    }

    /**
     * Get all loaded salvageables (loaded from all salvageable configs)
     * @return the currently loaded salvageables
     */
    public ArrayList<Salvageable> getSalvageables()
    {
        return (ArrayList<Salvageable>) salvageConfig.genericCollection;
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
        userFiles.clear();
    }

    /**
     * Registers an unloadable
     * Unloadables call unload() on plugin disable to cleanup registries
     */
    public void registerUnloadable(Unload unload)
    {
        if(!unloadables.contains(unload))
            unloadables.add(unload);
    }

    /**
     * Registers an unloadable
     * Unloadables call unload() on plugin disable to cleanup registries
     */
    public void registerUserFile(File userFile)
    {
        if(!userFiles.contains(userFile))
            userFiles.add(userFile);
    }

    /*
     * GETTER BOILER PLATE
     */

    /**
     * Used to back up our zip files real easily
     * @return
     */
    public ArrayList<File> getConfigFiles()
    {
        return userFiles;
    }

    public SimpleRepairableManager getSimpleRepairableManager() {
        return simpleRepairableManager;
    }

    public SimpleSalvageableManager getSimpleSalvageableManager() {
        return simpleSalvageableManager;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public FishingTreasureConfig getFishingTreasureConfig() {
        return fishingTreasureConfig;
    }

    public ExcavationTreasureConfig getExcavationTreasureConfig() {
        return excavationTreasureConfig;
    }

    public HerbalismTreasureConfig getHerbalismTreasureConfig() {
        return herbalismTreasureConfig;
    }

    public AdvancedConfig getAdvancedConfig() {
        return advancedConfig;
    }

    public PotionConfig getPotionConfig() {
        return potionConfig;
    }

    public CoreSkillsConfig getCoreSkillsConfig() {
        return coreSkillsConfig;
    }

    public SoundConfig getSoundConfig() {
        return soundConfig;
    }

    public RankConfig getRankConfig() {
        return rankConfig;
    }

    public ExperienceConfig getExperienceConfig() {
        return experienceConfig;
    }

    public ItemWeightConfig getItemWeightConfig() {
        return itemWeightConfig;
    }

    public ExperienceMapManager getExperienceMapManager() {
        return experienceMapManager;
    }

    public ConfigDatabase getConfigDatabase() { return configDatabase.getConfig(); }

    public ConfigScoreboard getConfigScoreboard() { return configScoreboard.getConfig(); }

    public ConfigLeveling getConfigLeveling() {
        return configLeveling.getConfig();
    }

    public ConfigWorldBlacklist getConfigWorldBlacklist() {
        return configWorldBlacklist.getConfig();
    }
}
