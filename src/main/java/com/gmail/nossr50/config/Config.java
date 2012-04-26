package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDType;

public class Config extends ConfigLoader{

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public static String locale;
    public static Boolean enableMotd, statsTracking, eventCallback;
    public static int saveInterval;

    /* mySQL */
    public static boolean getUseMySQL() { return config.getBoolean("MySQL.Enabled", false); }
    public static String getMySQLTablePrefix() { return config.getString("MySQL.Database.TablePrefix", "mcmmo_"); }
    public static String getMySQLDatabaseName() { return config.getString("MySQL.Database.Name", "DatabaseName"); }
    public static String getMySQLUserName() { return config.getString("MySQL.Database.User_Name", "UserName"); } //Really should be labeled under MySQL.User_Name instead...
    public static int getMySQLServerPort() { return config.getInt("MySQL.Server.Port", 3306); }
    public static String getMySQLServerName() { return config.getString("MySQL.Server.Address", "localhost"); }
    public static String getMySQLUserPassword() { 
        if (config.getString("MySQL.Database.User_Password", null) != null) {
            return config.getString("MySQL.Database.User_Password", null);
        }
        else {
            return "";
        }
    }

    /* Commands */
    public static boolean getCommandXPLockEnabled() { return config.getBoolean("Commands.xplock.Enabled", true); }
    public static boolean getCommandXPRateEnabled() { return config.getBoolean("Commands.xprate.Enabled", true); }
    public static boolean getCommandMCTopEnabled() { return config.getBoolean("Commands.mctop.Enabled", true); }
    public static boolean getCommandAddXPEnabled() { return config.getBoolean("Commands.addxp.Enabled", true); }
    public static boolean getCommandAddLevelsEnabled() { return config.getBoolean("Commands.addlevels.Enabled", true); }
    public static boolean getCommandMCAbilityEnabled() { return config.getBoolean("Commands.mcability.Enabled", true); }
    public static boolean getCommandMCRefreshEnabled() { return config.getBoolean("Commands.mcrefresh.Enabled", true); }
    public static boolean getCommandmcMMOEnabled() { return config.getBoolean("Commands.mcmmo.Enabled", true); }
    public static boolean getCommandMCCEnabled() { return config.getBoolean("Commands.mcc.Enabled", true); }
    public static boolean getCommandMCGodEnabled() { return config.getBoolean("Commands.mcgod.Enabled", true); }
    public static boolean getCommandMCStatsEnabled() { return config.getBoolean("Commands.mcstats.Enabled", true); }
    public static boolean getCommandMmoeditEnabled() { return config.getBoolean("Commands.mmoedit.Enabled", true); }
    public static boolean getCommandMCRemoveEnabled() { return config.getBoolean("Commands.mcremove.Enable", true); }
    public static boolean getCommandPTPEnabled() { return config.getBoolean("Commands.ptp.Enabled", true); }
    public static boolean getCommandPartyEnabled() { return config.getBoolean("Commands.party.Enabled", true); }
    public static boolean getCommandInspectEnabled() { return config.getBoolean("Commands.inspect.Enabled", true); }
    public static boolean getCommandInviteEnabled() { return config.getBoolean("Commands.invite.Enabled", true); }
    public static boolean getCommandAcceptEnabled() { return config.getBoolean("Commands.accept.Enabled", true); }
    public static boolean getCommandAdminChatAEnabled() { return config.getBoolean("Commands.a.Enabled", true); }
    public static boolean getCommandPartyChatPEnabled() { return config.getBoolean("Commands.p.Enabled", true); }

    public static int ptpCommandCooldown;
    public static Boolean donateMessage;

    /* Tool Level Requirements */
    public static Boolean perLevelTools;
    public static int sAxe, sHoe, sShovel, sSword, sPickaxe;
    public static int iAxe, iHoe, iShovel, iSword, iPickaxe;
    public static int gAxe, gHoe, gShovel, gSword, gPickaxe;
    public static int dAxe, dHoe, dShovel, dSword, dPickaxe;

    /* Items */
    public static Boolean chimaeraWingEnable;
    public static int chimaeraId, feathersConsumedByChimaeraWing;

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public static Boolean enableOnlyActivateWhenSneaking, enableAbilityMessages, enableAbilities;

    /* Durability Settings */
    public static Boolean toolsLoseDurabilityFromAbilities;
    public static int abilityDurabilityLoss;

    /* Cooldowns */
    public static int superBreakerCooldown, blastMiningCooldown, greenTerraCooldown,
                      gigaDrillBreakerCooldown, treeFellerCooldown, berserkCooldown,
                      serratedStrikeCooldown, skullSplitterCooldown;

    /* Thresholds */
    public static int treeFellerThreshold;

    /*
     * SKILL SETTINGS
     */

    /* Tool Requirements */
    public static Boolean miningrequirespickaxe, excavationRequiresShovel, woodcuttingrequiresaxe;

    /* Excavation */
    public static int mbase;

    /* Fishing */
    public static int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }
    public static boolean getFishingDropsEnabled() { return config.getBoolean("Fishing.Drops_Enabled", true); }
    public static int getFishingTierLevelsTier1() { return config.getInt("Fishing.Tier_Levels.Tier1", 0); }
    public static int getFishingTierLevelsTier2() { return config.getInt("Fishing.Tier_Levels.Tier2", 200); }
    public static int getFishingTierLevelsTier3() { return config.getInt("Fishing.Tier_Levels.Tier3", 400); }
    public static int getFishingTierLevelsTier4() { return config.getInt("Fishing.Tier_Levels.Tier4", 600); }
    public static int getFishingTierLevelsTier5() { return config.getInt("Fishing.Tier_Levels.Tier5", 800); }

    /* Herbalism */
    public static Boolean herbalismHungerBonus, wheatRegrowth;
    public static int mmelon, mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mvines, mlilypad, mnetherwart;
    public static Boolean enableCobbleToMossy, enableSmoothToMossy, enableDirtToGrass;

    /* Mining */
    public static int getMiningXPGoldOre() { return config.getInt("Experience.Mining.Gold", 250); } 
    public static int getMiningXPDiamondOre() { return config.getInt("Experience.Mining.Diamond", 750); }
    public static int getMiningXPIronOre() { return config.getInt("Experience.Mining.Iron", 250); }
    public static int getMiningXPRedstoneOre() { return config.getInt("Experience.Mining.Redstone", 150); }
    public static int getMiningXPLapisOre() { return config.getInt("Experience.Mining.Lapis", 400); }
    public static int getMiningXPObsidian() { return config.getInt("Experience.Mining.Obsidian", 150); }
    public static int getMiningXPNetherrack() { return config.getInt("Experience.Mining.Netherrack", 30); }
    public static int getMiningXPGlowstone() { return config.getInt("Experience.Mining.Glowstone", 30); }
    public static int getMiningXPCoalOre() { return config.getInt("Experience.Mining.Coal", 100); }
    public static int getMiningXPStone() { return config.getInt("Experience.Mining.Stone", 30); }
    public static int getMiningXPSandstone() { return config.getInt("Experience.Mining.Sandstone", 30); }
    public static int getMiningXPEndStone() { return config.getInt("Experience.Mining.End_Stone", 150); }
    public static int getMiningXPMossyStone() { return config.getInt("Experience.Mining.Moss_Stone", 30); }
    
    public static int detonatorID;

    /* Repair */
    public static Boolean repairArmor, repairTools;
    public static Boolean anvilmessages;
    public static int rWood, rStone, rIron, rGold, rDiamond, rString, rLeather;
    public static int anvilID;
    public static int repairStoneLevel, repairIronLevel, repairGoldLevel, repairDiamondLevel, repairStringLevel;

    /* Taming */
    public static int mtameWolf, mtameOcelot;
    public static int bonesConsumedByCOTW, fishConsumedByCOTW;

    /* Woodcutting */
    public static int moak, mbirch, mspruce, mjungle;

    /* Arcane Forging */
    public static Boolean mayDowngradeEnchants, mayLoseEnchants;
    public static int arcaneRank1, arcaneRank2, arcaneRank3, arcaneRank4;
    public static int downgradeRank1, downgradeRank2, downgradeRank3, downgradeRank4;
    public static int keepEnchantsRank1, keepEnchantsRank2, keepEnchantsRank3, keepEnchantsRank4;

    /* Level Caps */
    public static int getLevelCapAcrobatics() { return config.getInt("Skills.Acrobatics.Level_Cap", 0); }
    public static int getLevelCapArchery() { return config.getInt("Skills.Archery.Level_Cap", 0); }
    public static int getLevelCapAxes() { return config.getInt("Skills.Axes.Level_Cap", 0); }
    public static int getLevelCapExcavation() { return config.getInt("Skills.Excavation.Level_Cap", 0); }
    public static int getLevelCapFishing() { return config.getInt("Skills.Fishing.Level_Cap", 0); }
    public static int getLevelCapHerbalism() { return config.getInt("Skills.Herbalism.Level_Cap", 0); }
    public static int getLevelCapMining() { return config.getInt("Skills.Mining.Level_Cap", 0); }
    public static int getLevelCapRepair() { return config.getInt("Skills.Repair.Level_Cap", 0); }
    public static int getLevelCapSwords() { return config.getInt("Skills.Swords.Level_Cap", 0); }
    public static int getLevelCapTaming() { return config.getInt("Skills.Taming.Level_Cap", 0); }
    public static int getLevelCapUnarmed() { return config.getInt("Skills.Unarmed.Level_Cap", 0); }
    public static int getLevelCapWoodcutting() { return config.getInt("Skills.Woodcutting.Level_Cap", 0); }
    public static int getPowerLevelCap() { return config.getInt("General.Power_Level_Cap", 0); }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public static Boolean xpGainsMobSpawners, pvpxp;
    public static int xpGainMultiplier;

    /* Combat XP Multipliers */
    public static double getPlayerVersusPlayerXP() { return config.getDouble("Experience.Gains.Multiplier.PVP", 1.0); }
    
    public static double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }
    public static double getCreeperXP() { return config.getDouble("Experience.Combat.Multiplier.Creeper", 4.0); }
    public static double getSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Skeleton", 2.0); }
    public static double getSpiderXP() { return config.getDouble("Experience.Combat.Multiplier.Spider", 3.0); }
    public static double getGhastXP() { return config.getDouble("Experience.Combat.Multiplier.Ghast", 3.0); }
    public static double getSlimeXP() { return config.getDouble("Experience.Combat.Multiplier.Slime", 2.0); }
    public static double getZombieXP() { return config.getDouble("Experience.Combat.Multiplier.Zombie", 2.0); }
    public static double getPigZombieXP() { return config.getDouble("Experience.Combat.Multiplier.Pig_Zombie", 3.0); }
    public static double getEndermanXP() { return config.getDouble("Experience.Combat.Multiplier.Enderman", 2.0); }
    public static double getCaveSpiderXP() { return config.getDouble("Experience.Combat.Multiplier.Cave_Spider", 3.0); }
    public static double getSilverfishXP() { return config.getDouble("Experience.Combat.Multiplier.Silverfish", 3.0); }
    public static double getBlazeXP() { return config.getDouble("Experience.Combat.Multiplier.Blaze", 3.0); }
    public static double getMagmaCubeXP() { return config.getDouble("Experience.Combat.Multiplier.Magma_Cube", 2.0); }
    public static double getEnderDragonXP() { return config.getDouble("Experience.Combat.Multiplier.Ender_Dragon", 8.0); }
    public static double getIronGolemXP() { return config.getDouble("Experience.Combat.Multiplier.Iron_Golem", 2.0); }

    /* XP Formula Multiplier */
    public static double getFormulaMultiplierTaming() { return config.getDouble("Experience.Formula.Multiplier.Taming", 1.0); }
    public static double getFormulaMultiplierMining() { return config.getDouble("Experience.Formula.Multiplier.Mining", 1.0); }
    public static double getFormulaMultiplierRepair() { return config.getDouble("Experience.Formula.Multiplier.Repair", 1.0); }
    public static double getFormulaMultiplierWoodcutting() { return config.getDouble("Experience.Formula.Multiplier.Woodcutting", 1.0); }
    public static double getFormulaMultiplierUnarmed() { return config.getDouble("Experience.Formula.Multiplier.Unarmed", 1.0); }
    public static double getFormulaMultiplierHerbalism() { return config.getDouble("Experience.Formula.Multiplier.Herbalism", 1.0); }
    public static double getFormulaMultiplierExcavation() { return config.getDouble("Experience.Formula.Multiplier.Excavation", 1.0); }
    public static double getFormulaMultiplierArchery() { return config.getDouble("Experience.Formula.Multiplier.Archery", 1.0); }
    public static double getFormulaMultiplierSwords() { return config.getDouble("Experience.Formula.Multiplier.Swords", 1.0); }
    public static double getFormulaMultiplierAxes() { return config.getDouble("Experience.Formula.Multiplier.Axes", 1.0); }
    public static double getFormulaMultiplierAcrobatics() { return config.getDouble("Experience.Formula.Multiplier.Acrobatics", 1.0); }
    public static double getFormulaMultiplierFishing() { return config.getDouble("Experience.Formula.Multiplier.Fishing", 1.0); }

    /*
     * SPOUT SETTINGS
     */

    public static boolean spoutEnabled;
    public static boolean getShowPowerLevelForSpout() { return config.getBoolean("Spout.HUD.Show_Power_Level", true); }

    /* Spout Party HUD */
    public static Boolean showDisplayName, showFaces, partybar;

    /* Spout XP Bar */
    public static Boolean xpbar, xpicon;
    public static int xpbar_x, xpbar_y, xpicon_x, xpicon_y;

    /* Spout HUD Colors */
    public static double getSpoutRetroHUDXPBorderRed() { return config.getDouble("Spout.HUD.Retro.Colors.Border.RED", 0.0); }
    public static double getSpoutRetroHUDXPBorderGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Border.GREEN", 0.0); }
    public static double getSpoutRetroHUDXPBorderBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Border.BLUE", 0.0); }
    public static double getSpoutRetroHUDXPBackgroundRed() { return config.getDouble("Spout.HUD.Retro.Colors.Background.RED", 0.75); }
    public static double getSpoutRetroHUDXPBackgroundGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Background.GREEN", 0.75); }
    public static double getSpoutRetroHUDXPBackgroundBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Background.BLUE", 0.75); }
    
    public static double getSpoutRetroHUDAcrobaticsRed() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3); }
    public static double getSpoutRetroHUDAcrobaticsGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3); }
    public static double getSpoutRetroHUDAcrobaticsBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75); }
    public static double getSpoutRetroHUDArcheryRed() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.RED", 0.3); }
    public static double getSpoutRetroHUDArcheryGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3); }
    public static double getSpoutRetroHUDArcheryBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75); }
    public static double getSpoutRetroHUDAxesRed() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.RED", 0.3); }
    public static double getSpoutRetroHUDAxesGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3); }
    public static double getSpoutRetroHUDAxesBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75); }
    public static double getSpoutRetroHUDExcavationRed() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.RED", 0.3); }
    public static double getSpoutRetroHUDExcavationGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3); }
    public static double getSpoutRetroHUDExcavationBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75); }
    public static double getSpoutRetroHUDHerbalismRed() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3); }
    public static double getSpoutRetroHUDHerbalismGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3); }
    public static double getSpoutRetroHUDHerbalismBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75); }
    public static double getSpoutRetroHUDMiningRed() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.RED", 0.3); }
    public static double getSpoutRetroHUDMiningGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3); }
    public static double getSpoutRetroHUDMiningBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75); }
    public static double getSpoutRetroHUDRepairRed() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.RED", 0.3); }
    public static double getSpoutRetroHUDRepairGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3); }
    public static double getSpoutRetroHUDRepairBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75); }
    public static double getSpoutRetroHUDSwordsRed() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.RED", 0.3); }
    public static double getSpoutRetroHUDSwordsGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3); }
    public static double getSpoutRetroHUDSwordsBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75); }
    public static double getSpoutRetroHUDTamingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.RED", 0.3); }
    public static double getSpoutRetroHUDTamingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3); }
    public static double getSpoutRetroHUDTamingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75); }
    public static double getSpoutRetroHUDUnarmedRed() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3); }
    public static double getSpoutRetroHUDUnarmedGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3); }
    public static double getSpoutRetroHUDUnarmedBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75); }
    public static double getSpoutRetroHUDWoodcuttingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3); }
    public static double getSpoutRetroHUDWoodcuttingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3); }
    public static double getSpoutRetroHUDWoodcuttingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75); }
    public static double getSpoutRetroHUDFishingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.RED", 0.3); }
    public static double getSpoutRetroHUDFishingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.GREEN", 0.3); }
    public static double getSpoutRetroHUDFishingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.BLUE", 0.75); }

    /*
     * CONFIG LOADING
     */

    public static HUDType defaulthud;

    public Config(mcMMO plugin) {
        super(plugin, "config.yml");
        config = plugin.getConfig();
    }

    @Override
    public void load() {

        // If it doesn't exist, copy it from the .jar
        if (!configFile.exists()) {
            dataFolder.mkdir();
            plugin.saveDefaultConfig();
        }

        addDefaults();
        loadKeys();
    }

    @Override
    protected void loadKeys() {
        plugin.getLogger().info("Loading mcMMO config.yml File...");

        // Setup default HUD
        String temp = config.getString("Spout.HUD.Default", "STANDARD");
        for (HUDType x : HUDType.values()) {
            if (x.toString().equalsIgnoreCase(temp)) {
                defaulthud = x;
            }
        }

        enableAbilityMessages = config.getBoolean("Abilities.Messages", true);
        enableAbilities = config.getBoolean("Abilities.Enabled", true);

        donateMessage = config.getBoolean("Commands.mcmmo.Donate_Message", true);
        xpGainsMobSpawners = config.getBoolean("Experience.Gains.Mobspawners.Enabled", false);

        bonesConsumedByCOTW = config.getInt("Skills.Taming.Call_Of_The_Wild.Bones_Required", 10);
        fishConsumedByCOTW = config.getInt("Skills.Taming.Call_Of_The_Wild.Fish_Required", 10);

        xpbar = config.getBoolean("Spout.XP.Bar.Enabled", true);
        xpicon = config.getBoolean("Spout.XP.Icon.Enabled", true);
        xpbar_x = config.getInt("Spout.XP.Bar.X_POS", 95);
        xpbar_y = config.getInt("Spout.XP.Bar.Y_POS", 6);
        xpicon_x = config.getInt("Spout.XP.Icon.X_POS", 78);
        xpicon_y = config.getInt("Spout.XP.Icon.Y_POS", 2);

        showFaces = config.getBoolean("Spout.Party.HUD.Show_Faces", true);
        showDisplayName = config.getBoolean("Spout.Party.HUD.Show_Display_Name", false);
        partybar = config.getBoolean("Spout.Party.HUD.Enabled", true);
        
        mbase = config.getInt("Experience.Excavation.Base", 40);

        msugar = config.getInt("Experience.Herbalism.Sugar_Cane", 30);
        mwheat = config.getInt("Experience.Herbalism.Wheat", 50);
        mcactus = config.getInt("Experience.Herbalism.Cactus", 30);
        mpumpkin = config.getInt("Experience.Herbalism.Pumpkin", 20);
        mflower = config.getInt("Experience.Herbalism.Flowers", 100);
        mmushroom = config.getInt("Experience.Herbalism.Mushrooms", 150);
        mmelon = config.getInt("Experience.Herbalism.Melon", 20);
        mnetherwart = config.getInt("Experience.Herbalism.Nether_Wart", 50);
        mlilypad = config.getInt("Experience.Herbalism.Lily_Pads", 100);
        mvines = config.getInt("Experience.Herbalism.Vines", 10);
        herbalismHungerBonus = config.getBoolean("Skills.Herbalism.Hunger_Bonus", true);
        wheatRegrowth = config.getBoolean("Skills.Herbalism.Instant_Wheat_Regrowth", true);

        moak = config.getInt("Experience.Woodcutting.Oak", 70);
        mbirch = config.getInt("Experience.Woodcutting.Birch", 90);
        mspruce = config.getInt("Experience.Woodcutting.Spruce", 80);
        mjungle = config.getInt("Experience.Woodcutting.Jungle", 100);

        

        mtameWolf = config.getInt("Experience.Taming.Animal_Taming.Wolf", 250);
        mtameOcelot = config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500);

        

        enableOnlyActivateWhenSneaking = config.getBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false);

        greenTerraCooldown = config.getInt("Abilities.Cooldowns.Green_Terra", 240);
        superBreakerCooldown = config.getInt("Abilities.Cooldowns.Super_Breaker", 240);
        gigaDrillBreakerCooldown = config.getInt("Abilities.Cooldowns.Giga_Drill_Breaker", 240);
        treeFellerThreshold = config.getInt("Abilities.Limits.Tree_Feller_Threshold", 500);
        treeFellerCooldown = config.getInt("Abilities.Cooldowns.Tree_Feller", 240);
        berserkCooldown = config.getInt("Abilities.Cooldowns.Berserk", 240);
        serratedStrikeCooldown = config.getInt("Abilities.Cooldowns.Serrated_Strikes", 240);
        skullSplitterCooldown = config.getInt("Abilities.Cooldowns.Skull_Splitter", 240);
        blastMiningCooldown = config.getInt("Abilities.Cooldowns.Blast_Mining", 60);

        locale = config.getString("General.Locale", "en_us");
        enableMotd = config.getBoolean("General.MOTD_Enabled", true);
        saveInterval = config.getInt("General.Save_Interval", 10);
        statsTracking = config.getBoolean("General.Stats_Tracking", true);
        eventCallback = config.getBoolean("General.Event_Callback", true);
        perLevelTools = config.getBoolean("General.Per_Level_Tools", false);

        enableCobbleToMossy = config.getBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true);
        enableSmoothToMossy = config.getBoolean("Skills.Herbalism.Green_Thumb.SmoothBrick_To_MossyBrick", true);
        enableDirtToGrass = config.getBoolean("Skills.Herbalism.Green_Thumb.Dirt_To_Grass", true);

        xpGainMultiplier = config.getInt("Experience.Gains.Multiplier.Global", 1);
        toolsLoseDurabilityFromAbilities = config.getBoolean("Abilities.Tools.Durability_Loss_Enabled", true);
        abilityDurabilityLoss = config.getInt("Abilities.Tools.Durability_Loss", 2);

        feathersConsumedByChimaeraWing = config.getInt("Items.Chimaera_Wing.Feather_Cost", 10);
        chimaeraId = config.getInt("Items.Chimaera_Wing.Item_ID", 288);
        chimaeraWingEnable = config.getBoolean("Items.Chimaera_Wing.Enabled", true);
        dAxe = config.getInt("Items.Diamond.Axe", 750);
        dHoe = config.getInt("Items.Diamond.Hoe", 750);
        dShovel = config.getInt("Items.Diamond.Shovel", 750);
        dSword = config.getInt("Items.Diamond.Sword", 750);
        dPickaxe = config.getInt("Items.Diamond.Pickaxe", 750);
        gAxe = config.getInt("Items.Gold.Axe", 500);
        gHoe = config.getInt("Items.Gold.Hoe", 500);
        gShovel = config.getInt("Items.Gold.Shovel", 500);
        gSword = config.getInt("Items.Gold.Sword", 500);
        gPickaxe = config.getInt("Items.Gold.Pickaxe", 500);
        iAxe = config.getInt("Items.Iron.Axe", 250);
        iHoe = config.getInt("Items.Iron.Hoe", 250);
        iShovel = config.getInt("Items.Iron.Shovel", 250);
        iSword = config.getInt("Items.Iron.Sword", 250);
        iPickaxe = config.getInt("Items.Iron.Pickaxe", 250);
        sAxe = config.getInt("Items.Stone.Axe", 0);
        sHoe = config.getInt("Items.Stone.Hoe", 0);
        sShovel = config.getInt("Items.Stone.Shovel", 0);
        sSword = config.getInt("Items.Stone.Sword", 0);
        sPickaxe = config.getInt("Items.Stone.Pickaxe", 0);

        pvpxp = config.getBoolean("Experience.PVP.Rewards", true);

        miningrequirespickaxe = config.getBoolean("Skills.Mining.Requires_Pickaxe", true);
        excavationRequiresShovel = config.getBoolean("Skills.Excavation.Requires_Shovel", true);
        woodcuttingrequiresaxe = config.getBoolean("Skills.Woodcutting.Requires_Axe", true);
        repairArmor = config.getBoolean("Skills.Repair.Can_Repair_Armor", true);
        repairTools = config.getBoolean("Skills.Repair.Can_Repair_Tools", true);
        repairDiamondLevel = config.getInt("Skills.Repair.Diamond.Level_Required", 50);
        repairIronLevel = config.getInt("Skills.Repair.Iron.Level_Required", 0);
        repairGoldLevel = config.getInt("Skills.Repair.Gold.Level_Required", 0);
        repairStoneLevel = config.getInt("Skills.Repair.Stone.Level_Required", 0);
        repairStringLevel = config.getInt("Skills.Repair.String.Level_Required", 0);

        anvilmessages = config.getBoolean("Skills.Repair.Anvil_Messages", true);
        anvilID = config.getInt("Skills.Repair.Anvil_ID", 42);

        rGold = config.getInt("Skills.Repair.Gold.ID", 266);
        rStone = config.getInt("Skills.Repair.Stone.ID", 4);
        rWood = config.getInt("Skills.Repair.Wood.ID", 5);
        rDiamond = config.getInt("Skills.Repair.Diamond.ID", 264);
        rIron = config.getInt("Skills.Repair.Iron.ID", 265);
        rString = config.getInt("Skills.Repair.String.ID", 287);
        rLeather = config.getInt("Skills.Repair.Leather.ID", 334);

        mayDowngradeEnchants = config.getBoolean("Arcane_Forging.Downgrades.Enabled", true);
        downgradeRank1 = config.getInt("Arcane_Forging.Downgrades.Chance.Rank_1", 75);
        downgradeRank2 = config.getInt("Arcane_Forging.Downgrades.Chance.Rank_2", 50);
        downgradeRank3 = config.getInt("Arcane_Forging.Downgrades.Chance.Rank_3", 25);
        downgradeRank4 = config.getInt("Arcane_Forging.Downgrades.Chance.Rank_4", 15);
        mayLoseEnchants = config.getBoolean("Arcane_Forging.May_Lose_Enchants", true);
        keepEnchantsRank1 = config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10);
        keepEnchantsRank2 = config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20);
        keepEnchantsRank3 = config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30);
        keepEnchantsRank4 = config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40);
        arcaneRank1 = config.getInt("Arcane_Forging.Rank_Levels.Rank_1", 100);
        arcaneRank2 = config.getInt("Arcane_Forging.Rank_Levels.Rank_2", 250);
        arcaneRank3 = config.getInt("Arcane_Forging.Rank_Levels.Rank_3", 500);
        arcaneRank4 = config.getInt("Arcane_Forging.Rank_Levels.Rank_4", 750);
        
        ptpCommandCooldown = config.getInt("Commands.ptp.Cooldown", 30);

        detonatorID = config.getInt("Skills.Mining.Detonator_ID", 259);
    }
}