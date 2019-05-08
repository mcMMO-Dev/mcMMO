package com.gmail.nossr50.config;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.config.hocon.*;
import com.gmail.nossr50.config.hocon.admin.ConfigAdmin;
import com.gmail.nossr50.config.hocon.antiexploit.ConfigExploitPrevention;
import com.gmail.nossr50.config.hocon.backup.ConfigAutomatedBackups;
import com.gmail.nossr50.config.hocon.commands.ConfigCommands;
import com.gmail.nossr50.config.hocon.database.ConfigDatabase;
import com.gmail.nossr50.config.hocon.donation.ConfigAuthorAdvertisements;
import com.gmail.nossr50.config.hocon.experience.ConfigExperience;
import com.gmail.nossr50.config.hocon.hardcore.ConfigHardcore;
import com.gmail.nossr50.config.hocon.items.ConfigItems;
import com.gmail.nossr50.config.hocon.language.ConfigLanguage;
import com.gmail.nossr50.config.hocon.metrics.ConfigMetrics;
import com.gmail.nossr50.config.hocon.mobs.ConfigMobs;
import com.gmail.nossr50.config.hocon.motd.ConfigMOTD;
import com.gmail.nossr50.config.hocon.notifications.ConfigNotifications;
import com.gmail.nossr50.config.hocon.particles.ConfigParticles;
import com.gmail.nossr50.config.hocon.party.ConfigParty;
import com.gmail.nossr50.config.hocon.party.data.ConfigPartyData;
import com.gmail.nossr50.config.hocon.playerleveling.ConfigLeveling;
import com.gmail.nossr50.config.hocon.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.config.hocon.skills.acrobatics.ConfigAcrobatics;
import com.gmail.nossr50.config.hocon.skills.alchemy.ConfigAlchemy;
import com.gmail.nossr50.config.hocon.skills.archery.ConfigArchery;
import com.gmail.nossr50.config.hocon.skills.axes.ConfigAxes;
import com.gmail.nossr50.config.hocon.skills.exampleconfigs.ConfigNameRegisterDefaults;
import com.gmail.nossr50.config.hocon.skills.exampleconfigs.MinecraftMaterialWrapper;
import com.gmail.nossr50.config.hocon.skills.excavation.ConfigExcavation;
import com.gmail.nossr50.config.hocon.skills.fishing.ConfigFishing;
import com.gmail.nossr50.config.hocon.skills.herbalism.ConfigHerbalism;
import com.gmail.nossr50.config.hocon.skills.mining.ConfigMining;
import com.gmail.nossr50.config.hocon.skills.repair.ConfigRepair;
import com.gmail.nossr50.config.hocon.skills.salvage.ConfigSalvage;
import com.gmail.nossr50.config.hocon.skills.smelting.ConfigSmelting;
import com.gmail.nossr50.config.hocon.skills.swords.ConfigSwords;
import com.gmail.nossr50.config.hocon.skills.taming.ConfigTaming;
import com.gmail.nossr50.config.hocon.skills.unarmed.ConfigUnarmed;
import com.gmail.nossr50.config.hocon.skills.woodcutting.ConfigWoodcutting;
import com.gmail.nossr50.config.hocon.superabilities.ConfigSuperAbilities;
import com.gmail.nossr50.config.hocon.worldblacklist.ConfigWorldBlacklist;
import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.config.treasure.ExcavationTreasureConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.config.treasure.HerbalismTreasureConfig;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.repair.repairables.RepairableManager;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.gmail.nossr50.skills.salvage.salvageables.SalvageableManager;
import com.gmail.nossr50.util.experience.ExperienceMapManager;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Config Manager handles initializing, loading, and unloading registers for all configs that mcMMO uses
 * This makes sure that mcMMO properly loads and unloads its values on reload
 * <p>
 * Config Manager also holds all of our MultiConfigContainers
 */
public final class ConfigManager {

    /* UNLOAD REGISTER */

    private SkillPropertiesManager skillPropertiesManager;
    private ArrayList<Unload> unloadables;

    /* COLLECTION MANAGERS */
    private ArrayList<File> userFiles;
    private RepairableManager repairableManager;
    private SalvageableManager salvageableManager;

    /* CUSTOM SERIALIZERS */
    private BonusDropManager bonusDropManager;

    /* MOD MANAGERS */

    //TODO: Add these back when modded servers become a thing again

    /* MISC MANAGERS */
    private TypeSerializerCollection customSerializers;
    private ExperienceMapManager experienceMapManager;

    /* CONFIG INSTANCES */

    private SerializedConfigLoader<ConfigDatabase> configDatabase;
    private SerializedConfigLoader<ConfigScoreboard> configScoreboard;
    private SerializedConfigLoader<ConfigLeveling> configLeveling;
    private SerializedConfigLoader<ConfigWorldBlacklist> configWorldBlacklist;
    private SerializedConfigLoader<ConfigExploitPrevention> configExploitPrevention;
    private SerializedConfigLoader<ConfigHardcore> configHardcore;
    private SerializedConfigLoader<ConfigMetrics> configMetrics;
    private SerializedConfigLoader<ConfigMOTD> configMOTD;
    private SerializedConfigLoader<ConfigAuthorAdvertisements> configAuthorAdvertisements;
    private SerializedConfigLoader<ConfigAutomatedBackups> configAutomatedBackups;
    private SerializedConfigLoader<ConfigCommands> configCommands;
    private SerializedConfigLoader<ConfigItems> configItems;
    private SerializedConfigLoader<ConfigLanguage> configLanguage;
    private SerializedConfigLoader<ConfigParticles> configParticles;
    private SerializedConfigLoader<ConfigParty> configParty;
    private SerializedConfigLoader<ConfigNotifications> configNotifications;
    private SerializedConfigLoader<ConfigSuperAbilities> configSuperAbilities;
    private SerializedConfigLoader<ConfigAdmin> configAdmin;
    private SerializedConfigLoader<ConfigMobs> configMobs;
    private SerializedConfigLoader<ConfigExperience> configExperience;

    private SerializedConfigLoader<ConfigAcrobatics> configAcrobatics;
    private SerializedConfigLoader<ConfigAlchemy> configAlchemy;
    private SerializedConfigLoader<ConfigArchery> configArchery;
    private SerializedConfigLoader<ConfigAxes> configAxes;
    private SerializedConfigLoader<ConfigExcavation> configExcavation;
    private SerializedConfigLoader<ConfigFishing> configFishing;
    private SerializedConfigLoader<ConfigHerbalism> configHerbalism;
    private SerializedConfigLoader<ConfigMining> configMining;
    private SerializedConfigLoader<ConfigRepair> configRepair;
    private SerializedConfigLoader<ConfigSwords> configSwords;
    private SerializedConfigLoader<ConfigTaming> configTaming;
    private SerializedConfigLoader<ConfigUnarmed> configUnarmed;
    private SerializedConfigLoader<ConfigWoodcutting> configWoodcutting;
    private SerializedConfigLoader<ConfigSmelting> configSmelting;
    private SerializedConfigLoader<ConfigSalvage> configSalvage;
    private SerializedConfigLoader<ConfigNameRegisterDefaults> configDefaultExamples;

    private HashMap<PrimarySkillType, SerializedConfigLoader> skillConfigLoaders;

    //Data
    private SerializedConfigLoader<ConfigPartyData> partyData;

    //YAML CONFIGS
    private PotionConfig potionConfig;

    private MainConfig mainConfig;
    private FishingTreasureConfig fishingTreasureConfig;
    private ExcavationTreasureConfig excavationTreasureConfig;
    private HerbalismTreasureConfig herbalismTreasureConfig;
    private ExperienceConfig experienceConfig;
    private AdvancedConfig advancedConfig;
    private CoreSkillsConfig coreSkillsConfig;
    private SoundConfig soundConfig;
    private RankConfig rankConfig;

    private HashMap<Material, Integer> partyItemWeights;
    private HashMap<PartyFeature, Integer> partyFeatureUnlocks;

    /* CONFIG ERRORS */

    private ArrayList<String> configErrors; //Collect errors to whine about to server admins

    public ConfigManager() {
        unloadables = new ArrayList<>();
        userFiles = new ArrayList<>();
    }

    public void loadConfigs() {
        //Register Custom Serializers
        registerCustomTypeSerializers();

        //Serialized Configs
        initSerializedConfigs();

        //Serialized Data
        initSerializedDataFiles();

        //Skill Property Registers
        skillPropertiesManager = new SkillPropertiesManager();
        skillPropertiesManager.fillRegisters();

        //Assign Maps
        partyItemWeights = Maps.newHashMap(configParty.getConfig().getPartyItemShare().getItemShareMap()); //Item Share Weights
        partyFeatureUnlocks = Maps.newHashMap(configParty.getConfig().getPartyXP().getPartyLevel().getPartyFeatureUnlockMap()); //Party Progression

        //Register Bonus Drops
        registerBonusDrops();

        //YAML Configs
        initYAMLConfigs();

        /*
         * Managers
         */

        // Register Managers
        initMiscManagers();
        initCollectionManagers();
    }

    private void initYAMLConfigs() {
        potionConfig = new PotionConfig();
    }

    private void initSerializedDataFiles() {
        partyData = new SerializedConfigLoader<>(ConfigPartyData.class, "partydata.conf", "PartyData", null);
    }

    private void initSerializedConfigs() {
        configDatabase = new SerializedConfigLoader<>(ConfigDatabase.class, "database_settings.conf", "Database", null);
        configScoreboard = new SerializedConfigLoader<>(ConfigScoreboard.class, "scoreboard.conf", "Scoreboard", null);
        configLeveling = new SerializedConfigLoader<>(ConfigLeveling.class, "player_leveling.conf", "Player-Leveling", null);
        configWorldBlacklist = new SerializedConfigLoader<>(ConfigWorldBlacklist.class, "world_blacklist.conf", "World-Blacklist", null);
        configExploitPrevention = new SerializedConfigLoader<>(ConfigExploitPrevention.class, "anti_exploit.conf", "Anti-Exploit", null);
        configMOTD = new SerializedConfigLoader<>(ConfigMOTD.class, "message_of_the_day.conf", "MOTD", null);
        configHardcore = new SerializedConfigLoader<>(ConfigHardcore.class, "hardcore_mode.conf", "Hardcore-Mode", null);
        configMetrics = new SerializedConfigLoader<>(ConfigMetrics.class, "analytics_reporting.conf", "Analytic-Reporting", null);
        configAuthorAdvertisements = new SerializedConfigLoader<>(ConfigAuthorAdvertisements.class, "author_support_advertisements.conf", "mcMMO", null);
        configAutomatedBackups = new SerializedConfigLoader<>(ConfigAutomatedBackups.class, "automated_backups.conf", "Automated-Backups", null);
        configCommands = new SerializedConfigLoader<>(ConfigCommands.class, "commands.conf", "Commands", null);
        configItems = new SerializedConfigLoader<>(ConfigItems.class, "custom_items.conf", "Items", null);
        configLanguage = new SerializedConfigLoader<>(ConfigLanguage.class, "language.conf", "Language", null);
        configParticles = new SerializedConfigLoader<>(ConfigParticles.class, "particle_spawning.conf", "Particles", null);
        configParty = new SerializedConfigLoader<>(ConfigParty.class, "party.conf", "Party", null);
        configNotifications = new SerializedConfigLoader<>(ConfigNotifications.class, "alerts_and_notifications.conf", "Notifications", null);
        configSuperAbilities = new SerializedConfigLoader<>(ConfigSuperAbilities.class, "skill_super_abilities.conf", "Super-Abilities", null);
        configAdmin = new SerializedConfigLoader<>(ConfigAdmin.class, "admin.conf", "Admin", null);
        configMobs = new SerializedConfigLoader<>(ConfigMobs.class, "creatures.conf", "Creatures", null);
        configExperience = new SerializedConfigLoader<>(ConfigExperience.class, "experience.conf", "Experience", null);

        configDefaultExamples = new SerializedConfigLoader<>(ConfigNameRegisterDefaults.class, "minecraft_item_block_name_examples.conf", "Minecraft", null);
        initSerializedSkillConfigs();
    }

    @SuppressWarnings(value = "unchecked")
    private void initSerializedSkillConfigs() {
        //Init HashMap
        skillConfigLoaders = new HashMap<>();

        //Init and register serialized skill configs
        registerSkillConfig(PrimarySkillType.ACROBATICS, ConfigAcrobatics.class);
        registerSkillConfig(PrimarySkillType.ALCHEMY, ConfigAlchemy.class);
        registerSkillConfig(PrimarySkillType.SALVAGE, ConfigSalvage.class);
        registerSkillConfig(PrimarySkillType.ARCHERY, ConfigArchery.class);
        registerSkillConfig(PrimarySkillType.AXES, ConfigAxes.class);
        registerSkillConfig(PrimarySkillType.EXCAVATION, ConfigExcavation.class);
        registerSkillConfig(PrimarySkillType.FISHING, ConfigFishing.class);
        registerSkillConfig(PrimarySkillType.HERBALISM, ConfigHerbalism.class);
        registerSkillConfig(PrimarySkillType.MINING, ConfigMining.class);
        registerSkillConfig(PrimarySkillType.REPAIR, ConfigRepair.class);
        registerSkillConfig(PrimarySkillType.SWORDS, ConfigSwords.class);
        registerSkillConfig(PrimarySkillType.TAMING, ConfigTaming.class);
        registerSkillConfig(PrimarySkillType.UNARMED, ConfigUnarmed.class);
        registerSkillConfig(PrimarySkillType.WOODCUTTING, ConfigWoodcutting.class);
        registerSkillConfig(PrimarySkillType.SMELTING, ConfigSmelting.class);

        //Setup Typed refs
        configAcrobatics = (SerializedConfigLoader<ConfigAcrobatics>) skillConfigLoaders.get(PrimarySkillType.ACROBATICS).getConfig();
        configAlchemy = (SerializedConfigLoader<ConfigAlchemy>) skillConfigLoaders.get(PrimarySkillType.ALCHEMY).getConfig();
        configSalvage = (SerializedConfigLoader<ConfigSalvage>) skillConfigLoaders.get(PrimarySkillType.SALVAGE).getConfig();
        configArchery = (SerializedConfigLoader<ConfigArchery>) skillConfigLoaders.get(PrimarySkillType.ARCHERY).getConfig();
        configAxes = (SerializedConfigLoader<ConfigAxes>) skillConfigLoaders.get(PrimarySkillType.AXES).getConfig();
        configExcavation = (SerializedConfigLoader<ConfigExcavation>) skillConfigLoaders.get(PrimarySkillType.EXCAVATION).getConfig();
        configFishing = (SerializedConfigLoader<ConfigFishing>) skillConfigLoaders.get(PrimarySkillType.FISHING).getConfig();
        configHerbalism = (SerializedConfigLoader<ConfigHerbalism>) skillConfigLoaders.get(PrimarySkillType.HERBALISM).getConfig();
        configMining = (SerializedConfigLoader<ConfigMining>) skillConfigLoaders.get(PrimarySkillType.MINING).getConfig();
        configRepair = (SerializedConfigLoader<ConfigRepair>) skillConfigLoaders.get(PrimarySkillType.REPAIR).getConfig();
        configSwords = (SerializedConfigLoader<ConfigSwords>) skillConfigLoaders.get(PrimarySkillType.SWORDS).getConfig();
        configTaming = (SerializedConfigLoader<ConfigTaming>) skillConfigLoaders.get(PrimarySkillType.TAMING).getConfig();
        configUnarmed = (SerializedConfigLoader<ConfigUnarmed>) skillConfigLoaders.get(PrimarySkillType.UNARMED).getConfig();
        configWoodcutting = (SerializedConfigLoader<ConfigWoodcutting>) skillConfigLoaders.get(PrimarySkillType.WOODCUTTING).getConfig();
        configSmelting = (SerializedConfigLoader<ConfigSmelting>) skillConfigLoaders.get(PrimarySkillType.SMELTING).getConfig();
    }

    private void registerCustomTypeSerializers() {
    /*
     TypeTokens are obtained in two ways

        For Raw basic classes:

            TypeToken<String> stringTok = TypeToken.of(String.class);
            TypeToken<Integer> intTok = TypeToken.of(Integer.class);

        For Generics:

            TypeToken<List<String>> stringListTok = new TypeToken<List<String>>() {};

        Wildcard example:

            TypeToken<Map<?, ?>> wildMapTok = new TypeToken<Map<?, ?>>() {};

     */

        /*
            List of default serializers for reference
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(URI.class), new URISerializer());
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(URL.class), new URLSerializer());
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(UUID.class), new UUIDSerializer());
            DEFAULT_SERIALIZERS.registerPredicate(input -> input.getRawType().isAnnotationPresent(ConfigSerializable.class), new AnnotatedObjectSerializer());
            DEFAULT_SERIALIZERS.registerPredicate(NumberSerializer.getPredicate(), new NumberSerializer());
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(String.class), new StringSerializer());
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(Boolean.class), new BooleanSerializer());
            DEFAULT_SERIALIZERS.registerType(new TypeToken<Map<?, ?>>() {}, new MapSerializer());
            DEFAULT_SERIALIZERS.registerType(new TypeToken<List<?>>() {}, new ListSerializer());
            DEFAULT_SERIALIZERS.registerType(new TypeToken<Enum<?>>() {}, new EnumValueSerializer());
            DEFAULT_SERIALIZERS.registerType(TypeToken.of(Pattern.class), new PatternSerializer());
         */

        customSerializers = TypeSerializers.getDefaultSerializers().newChild();

        mcMMO.p.getLogger().info("Registering custom type serializers for Configurate...");
        customSerializers.registerType(new TypeToken<PrimarySkillType>() {
        }, new CustomEnumValueSerializer());
        customSerializers.registerType(new TypeToken<Material>() {
        }, new CustomEnumValueSerializer());
        customSerializers.registerType(new TypeToken<PartyFeature>() {
        }, new CustomEnumValueSerializer());
        customSerializers.registerType(new TypeToken<FormulaType>() {
        }, new CustomEnumValueSerializer());
        customSerializers.registerType(TypeToken.of(Repairable.class), new RepairableSerializer());
        customSerializers.registerType(TypeToken.of(Salvageable.class), new SalvageableSerializer());
        customSerializers.registerType(TypeToken.of(MinecraftMaterialWrapper.class), new MinecraftMaterialWrapperSerializer());
    }

    /**
     * Gets the serializers registered and used by mcMMO
     * This includes all default serializers
     *
     * @return our custom serializers
     */
    public TypeSerializerCollection getCustomSerializers() {
        return customSerializers;
    }

    private void registerSkillConfig(PrimarySkillType primarySkillType, Class clazz) {
        skillConfigLoaders.put(primarySkillType, SkillConfigFactory.initSkillConfig(primarySkillType, clazz));
    }

    /**
     * Misc managers
     */
    private void initMiscManagers() {
        experienceMapManager = new ExperienceMapManager();
        //Set the global XP val
        experienceMapManager.setGlobalXpMult(getConfigExperience().getGlobalXPMultiplier());
        experienceMapManager.buildBlockXPMaps(); //Block XP value maps
    }

    /**
     * Initializes any managers related to config collections
     */
    private void initCollectionManagers() {
        // Handles registration of repairables
        repairableManager = new RepairableManager(getRepairables());
        unloadables.add(repairableManager);

        // Handles registration of salvageables
        salvageableManager = new SalvageableManager(getSalvageables());
        unloadables.add(salvageableManager);

        // Handles registration of bonus drops
        bonusDropManager = new BonusDropManager();
        unloadables.add(bonusDropManager);
    }

    /**
     * Get all loaded repairables (loaded from all repairable configs)
     *
     * @return the currently loaded repairables
     */
    public ArrayList<Repairable> getRepairables() {
        return getConfigRepair().getConfigRepairablesList();
    }

    /**
     * Get all loaded salvageables (loaded from all salvageable configs)
     *
     * @return the currently loaded salvageables
     */
    public ArrayList<Salvageable> getSalvageables() {
        return getConfigSalvage().getConfigSalvageablesList();
    }

    /**
     * Unloads all config options (prepares for reload)
     */
    public void unloadAllConfigsAndRegisters() {
        //Unload
        for (Unload unloadable : unloadables) {
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
    public void registerUnloadable(Unload unload) {
        if (!unloadables.contains(unload))
            unloadables.add(unload);
    }

    /**
     * Registers an unloadable
     * Unloadables call unload() on plugin disable to cleanup registries
     */
    public void registerUserFile(File userFile) {
        if (!userFiles.contains(userFile))
            userFiles.add(userFile);
    }

    /**
     * Registers bonus drops from several skill configs
     */
    public void registerBonusDrops() {
        bonusDropManager.addToWhitelistByNameID(getConfigMining().getBonusDrops());
//        bonusDropManager.addToWhitelistByNameID(configHerbalism.getBonusDrops());
//        bonusDropManager.addToWhitelistByNameID(configWoodcutting.getBonusDrops());
    }

    public void validateConfigs() {

    }

    public void reloadConfigs() {
        mcMMO.p.getLogger().info("Reloading config values...");
        unloadAllConfigsAndRegisters(); //Unload Everything
        loadConfigs(); //Load everything again
    }

    /*
     * GETTER BOILER PLATE
     */

    /**
     * Used to back up our zip files real easily
     *
     * @return
     */
    public ArrayList<File> getConfigFiles() {
        return userFiles;
    }

    public RepairableManager getRepairableManager() {
        return repairableManager;
    }

    public SalvageableManager getSalvageableManager() {
        return salvageableManager;
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

    public ExperienceMapManager getExperienceMapManager() {
        return experienceMapManager;
    }

    public ConfigDatabase getConfigDatabase() {
        return configDatabase.getConfig();
    }

    public ConfigScoreboard getConfigScoreboard() {
        return configScoreboard.getConfig();
    }

    public ConfigLeveling getConfigLeveling() {
        return configLeveling.getConfig();
    }

    public ConfigWorldBlacklist getConfigWorldBlacklist() {
        return configWorldBlacklist.getConfig();
    }

    public ConfigExploitPrevention getConfigExploitPrevention() {
        return configExploitPrevention.getConfig();
    }

    public ConfigMOTD getConfigMOTD() {
        return configMOTD.getConfig();
    }

    public ConfigHardcore getConfigHardcore() {
        return configHardcore.getConfig();
    }

    public ConfigMetrics getConfigMetrics() {
        return configMetrics.getConfig();
    }

    public ConfigAuthorAdvertisements getConfigAds() {
        return configAuthorAdvertisements.getConfig();
    }

    public ConfigAutomatedBackups getConfigAutomatedBackups() {
        return configAutomatedBackups.getConfig();
    }

    public ConfigCommands getConfigCommands() {
        return configCommands.getConfig();
    }

    public ConfigItems getConfigItems() {
        return configItems.getConfig();
    }

    public ConfigLanguage getConfigLanguage() {
        return configLanguage.getConfig();
    }

    public ConfigParticles getConfigParticles() {
        return configParticles.getConfig();
    }

    public ConfigParty getConfigParty() {
        return configParty.getConfig();
    }

    public ConfigNotifications getConfigNotifications() {
        return configNotifications.getConfig();
    }

    public ConfigSuperAbilities getConfigSuperAbilities() {
        return configSuperAbilities.getConfig();
    }

    public HashMap<Material, Integer> getPartyItemWeights() {
        return partyItemWeights;
    }

    public HashMap<PartyFeature, Integer> getPartyFeatureUnlocks() {
        return partyFeatureUnlocks;
    }

    public ConfigAdmin getConfigAdmin() {
        return configAdmin.getConfig();
    }

    public ConfigMobs getConfigMobs() {
        return configMobs.getConfig();
    }

    public ConfigAcrobatics getConfigAcrobatics() {
        return configAcrobatics.getConfig();
    }

    public ConfigAlchemy getConfigAlchemy() {
        return configAlchemy.getConfig();
    }

    public ConfigArchery getConfigArchery() {
        return configArchery.getConfig();
    }

    public ConfigAxes getConfigAxes() {
        return configAxes.getConfig();
    }

    public ConfigExcavation getConfigExcavation() {
        return configExcavation.getConfig();
    }

    public ConfigFishing getConfigFishing() {
        return configFishing.getConfig();
    }

    public ConfigHerbalism getConfigHerbalism() {
        return configHerbalism.getConfig();
    }

    public ConfigMining getConfigMining() {
        return configMining.getConfig();
    }

    public ConfigRepair getConfigRepair() {
        return configRepair.getConfig();
    }

    public ConfigSwords getConfigSwords() {
        return configSwords.getConfig();
    }

    public ConfigTaming getConfigTaming() {
        return configTaming.getConfig();
    }

    public ConfigUnarmed getConfigUnarmed() {
        return configUnarmed.getConfig();
    }

    public ConfigWoodcutting getConfigWoodcutting() {
        return configWoodcutting.getConfig();
    }

    public ConfigSmelting getConfigSmelting() {
        return configSmelting.getConfig();
    }

    public ConfigSalvage getConfigSalvage() {
        return configSalvage.getConfig();
    }

    public BonusDropManager getBonusDropManager() {
        return bonusDropManager;
    }

    /**
     * Checks if this plugin is using retro mode
     * Retro mode is a 0-1000 skill system
     * Standard mode is scaled for 1-100
     *
     * @return true if retro mode is enabled
     */
    public boolean isRetroMode() {
        return getConfigLeveling().getConfigSectionLevelingGeneral().getConfigSectionLevelScaling().isRetroModeEnabled();
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

    public ConfigExperience getConfigExperience() {
        return configExperience.getConfig();
    }

    public SkillPropertiesManager getSkillPropertiesManager() {
        return skillPropertiesManager;
    }
}
