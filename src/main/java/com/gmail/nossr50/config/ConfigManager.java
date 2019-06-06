package com.gmail.nossr50.config;

import com.gmail.nossr50.config.hocon.SerializedConfigLoader;
import com.gmail.nossr50.config.hocon.admin.ConfigAdmin;
import com.gmail.nossr50.config.hocon.antiexploit.ConfigExploitPrevention;
import com.gmail.nossr50.config.hocon.backup.ConfigAutomatedBackups;
import com.gmail.nossr50.config.hocon.commands.ConfigCommands;
import com.gmail.nossr50.config.hocon.database.ConfigDatabase;
import com.gmail.nossr50.config.hocon.donation.ConfigAuthorAdvertisements;
import com.gmail.nossr50.config.hocon.event.ConfigEvent;
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
import com.gmail.nossr50.config.hocon.serializers.*;
import com.gmail.nossr50.config.hocon.skills.acrobatics.ConfigAcrobatics;
import com.gmail.nossr50.config.hocon.skills.alchemy.ConfigAlchemy;
import com.gmail.nossr50.config.hocon.skills.archery.ConfigArchery;
import com.gmail.nossr50.config.hocon.skills.axes.ConfigAxes;
import com.gmail.nossr50.config.hocon.skills.coreskills.ConfigCoreSkills;
import com.gmail.nossr50.config.hocon.skills.exampleconfigs.ConfigNameRegisterDefaults;
import com.gmail.nossr50.config.hocon.skills.exampleconfigs.MinecraftMaterialWrapper;
import com.gmail.nossr50.config.hocon.skills.excavation.ConfigExcavation;
import com.gmail.nossr50.config.hocon.skills.fishing.ConfigFishing;
import com.gmail.nossr50.config.hocon.skills.herbalism.ConfigHerbalism;
import com.gmail.nossr50.config.hocon.skills.mining.ConfigMining;
import com.gmail.nossr50.config.hocon.skills.ranks.ConfigRanks;
import com.gmail.nossr50.config.hocon.skills.ranks.SkillRankProperty;
import com.gmail.nossr50.config.hocon.skills.repair.ConfigRepair;
import com.gmail.nossr50.config.hocon.skills.salvage.ConfigSalvage;
import com.gmail.nossr50.config.hocon.skills.smelting.ConfigSmelting;
import com.gmail.nossr50.config.hocon.skills.swords.ConfigSwords;
import com.gmail.nossr50.config.hocon.skills.taming.ConfigTaming;
import com.gmail.nossr50.config.hocon.skills.unarmed.ConfigUnarmed;
import com.gmail.nossr50.config.hocon.skills.woodcutting.ConfigWoodcutting;
import com.gmail.nossr50.config.hocon.superabilities.ConfigSuperAbilities;
import com.gmail.nossr50.config.hocon.worldblacklist.ConfigWorldBlacklist;
import com.gmail.nossr50.config.treasure.ExcavationTreasureConfig;
import com.gmail.nossr50.config.treasure.FishingTreasureConfig;
import com.gmail.nossr50.config.treasure.HerbalismTreasureConfig;
import com.gmail.nossr50.datatypes.experience.CustomXPPerk;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.properties.DamageProperty;
import com.gmail.nossr50.datatypes.skills.properties.SkillCeiling;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.repair.repairables.Repairable;
import com.gmail.nossr50.skills.salvage.salvageables.Salvageable;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Config Manager handles initializing, loading, and unloading registers for all configs that mcMMO uses
 * This makes sure that mcMMO properly loads and unloads its values on reload
 * Settings in configs are sometimes not platform-ready, you can find platform ready implementations in the {@link com.gmail.nossr50.core.DynamicSettingsManager DynamicSettingsManager}
 */
public final class ConfigManager {

    /* File array - Used for backups */
    private ArrayList<File> userFiles;

    /* Custom Serialization */
    private TypeSerializerCollection customSerializers;

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
    private SerializedConfigLoader<ConfigCoreSkills> configCoreSkills;
    private SerializedConfigLoader<ConfigEvent> configEvent;
    private SerializedConfigLoader<ConfigRanks> configRanks;
    private SerializedConfigLoader<ConfigNameRegisterDefaults> configDefaultExamples;

    private ConfigAcrobatics configAcrobatics;
    private ConfigAlchemy configAlchemy;
    private ConfigArchery configArchery;
    private ConfigAxes configAxes;
    private ConfigExcavation configExcavation;
    private ConfigFishing configFishing;
    private ConfigHerbalism configHerbalism;
    private ConfigMining configMining;
    private ConfigRepair configRepair;
    private ConfigSwords configSwords;
    private ConfigTaming configTaming;
    private ConfigUnarmed configUnarmed;
    private ConfigWoodcutting configWoodcutting;
    private ConfigSmelting configSmelting;
    private ConfigSalvage configSalvage;

    private HashMap<PrimarySkillType, SerializedConfigLoader<?>> skillConfigLoaders;

    //Data
    private SerializedConfigLoader<ConfigPartyData> partyData;

    //YAML CONFIGS

    private MainConfig mainConfig;
    private FishingTreasureConfig fishingTreasureConfig;
    private ExcavationTreasureConfig excavationTreasureConfig;
    private HerbalismTreasureConfig herbalismTreasureConfig;
    private AdvancedConfig advancedConfig;
    private SoundConfig soundConfig;

    /* CONFIG ERRORS */

    private ArrayList<String> configErrors; //Collect errors to whine about to server admins

    public ConfigManager() {
        userFiles = new ArrayList<>();
    }

    public void loadConfigs() {
        //Register Custom Serializers
        registerCustomTypeSerializers();

        //Serialized Configs
        initSerializedConfigs();

        //Serialized Data
        initSerializedDataFiles();
    }

    private void initSerializedDataFiles() {
        partyData = new SerializedConfigLoader<>(ConfigPartyData.class, "partydata.conf", "PartyData", null);
    }

    private void initSerializedConfigs() {
        //There's some race conditions here because mcMMO is goddamn spaghetti mess, language has to load first
        configLanguage = new SerializedConfigLoader<>(ConfigLanguage.class, "language.conf", "Language", null);

        /*
         * No more race conditions
         */

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
        configParticles = new SerializedConfigLoader<>(ConfigParticles.class, "particle_spawning.conf", "Particles", null);
        configParty = new SerializedConfigLoader<>(ConfigParty.class, "party.conf", "Party", null);
        configNotifications = new SerializedConfigLoader<>(ConfigNotifications.class, "alerts_and_notifications.conf", "Notifications", null);
        configSuperAbilities = new SerializedConfigLoader<>(ConfigSuperAbilities.class, "skill_super_abilities.conf", "Super-Abilities", null);
        configAdmin = new SerializedConfigLoader<>(ConfigAdmin.class, "admin.conf", "Admin", null);
        configMobs = new SerializedConfigLoader<>(ConfigMobs.class, "creatures.conf", "Creatures", null);
        configExperience = new SerializedConfigLoader<>(ConfigExperience.class, "experience.conf", "Experience", null);
        configCoreSkills = new SerializedConfigLoader<>(ConfigCoreSkills.class, "core_skills.conf", "Core-Skills", null);
        configEvent = new SerializedConfigLoader<>(ConfigEvent.class, "events.conf", "Events", null);
        configRanks = new SerializedConfigLoader<>(ConfigRanks.class, "ranks.conf", "Skill-Ranks", null);

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
        configAcrobatics = (ConfigAcrobatics) skillConfigLoaders.get(PrimarySkillType.ACROBATICS).getConfig();
        configAlchemy = (ConfigAlchemy) skillConfigLoaders.get(PrimarySkillType.ALCHEMY).getConfig();
        configSalvage = (ConfigSalvage) skillConfigLoaders.get(PrimarySkillType.SALVAGE).getConfig();
        configArchery = (ConfigArchery) skillConfigLoaders.get(PrimarySkillType.ARCHERY).getConfig();
        configAxes = (ConfigAxes) skillConfigLoaders.get(PrimarySkillType.AXES).getConfig();
        configExcavation = (ConfigExcavation) skillConfigLoaders.get(PrimarySkillType.EXCAVATION).getConfig();
        configFishing = (ConfigFishing) skillConfigLoaders.get(PrimarySkillType.FISHING).getConfig();
        configHerbalism = (ConfigHerbalism) skillConfigLoaders.get(PrimarySkillType.HERBALISM).getConfig();
        configMining = (ConfigMining) skillConfigLoaders.get(PrimarySkillType.MINING).getConfig();
        configRepair = (ConfigRepair) skillConfigLoaders.get(PrimarySkillType.REPAIR).getConfig();
        configSwords = (ConfigSwords) skillConfigLoaders.get(PrimarySkillType.SWORDS).getConfig();
        configTaming = (ConfigTaming) skillConfigLoaders.get(PrimarySkillType.TAMING).getConfig();
        configUnarmed = (ConfigUnarmed) skillConfigLoaders.get(PrimarySkillType.UNARMED).getConfig();
        configWoodcutting = (ConfigWoodcutting) skillConfigLoaders.get(PrimarySkillType.WOODCUTTING).getConfig();
        configSmelting = (ConfigSmelting) skillConfigLoaders.get(PrimarySkillType.SMELTING).getConfig();
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
        customSerializers.registerType(TypeToken.of(CustomXPPerk.class), new CustomXPPerkSerializer());
        customSerializers.registerType(TypeToken.of(DamageProperty.class), new DamagePropertySerializer());
        customSerializers.registerType(TypeToken.of(SkillCeiling.class), new SkillCeilingSerializer());
        customSerializers.registerType(TypeToken.of(SkillRankProperty.class), new SkillRankPropertySerializer());
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
     * Registers an unloadable
     * Unloadables call unload() on plugin disable to cleanup registries
     */
    public void registerUserFile(File userFile) {
        if (!userFiles.contains(userFile))
            userFiles.add(userFile);
    }

    public void validateConfigs() {

    }

    /**
     * Reload the configs
     * Technically this reloads a lot of stuff, not just configs
     */
    public void reloadConfigs() {
        mcMMO.p.getLogger().info("Reloading config values...");
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

    public ConfigCoreSkills getConfigCoreSkills() {
        return configCoreSkills.getConfig();
    }

    public SoundConfig getSoundConfig() {
        return soundConfig;
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

    public ConfigAdmin getConfigAdmin() {
        return configAdmin.getConfig();
    }

    public ConfigMobs getConfigMobs() {
        return configMobs.getConfig();
    }

    public ConfigAcrobatics getConfigAcrobatics() {
        return configAcrobatics;
    }

    public ConfigAlchemy getConfigAlchemy() {
        return configAlchemy;
    }

    public ConfigArchery getConfigArchery() {
        return configArchery;
    }

    public ConfigAxes getConfigAxes() {
        return configAxes;
    }

    public ConfigExcavation getConfigExcavation() {
        return configExcavation;
    }

    public ConfigFishing getConfigFishing() {
        return configFishing;
    }

    public ConfigHerbalism getConfigHerbalism() {
        return configHerbalism;
    }

    public ConfigMining getConfigMining() {
        return configMining;
    }

    public ConfigRepair getConfigRepair() {
        return configRepair;
    }

    public ConfigSwords getConfigSwords() {
        return configSwords;
    }

    public ConfigTaming getConfigTaming() {
        return configTaming;
    }

    public ConfigUnarmed getConfigUnarmed() {
        return configUnarmed;
    }

    public ConfigWoodcutting getConfigWoodcutting() {
        return configWoodcutting;
    }

    public ConfigSmelting getConfigSmelting() {
        return configSmelting;
    }

    public ConfigSalvage getConfigSalvage() {
        return configSalvage;
    }

    public ConfigEvent getConfigEvent() {
        return configEvent.getConfig();
    }

    public ConfigRanks getConfigRanks() {
        return configRanks.getConfig();
    }

    /**
     * Used to programmatically grab rank data for skills
     * @return root node for the ranks config file
     */
    public CommentedConfigurationNode getConfigRanksRootNode() {
        return configRanks.getRootNode();
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

    public ConfigExperience getConfigExperience() {
        return configExperience.getConfig();
    }
}
