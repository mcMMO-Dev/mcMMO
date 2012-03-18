package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDType;

public class LoadProperties extends ConfigLoader{

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public static String locale;
    public static Boolean enableMotd, statsTracking, eventCallback;
    public static int saveInterval;

    /* mySQL */
    public static Boolean useMySQL;
    public static String MySQLtablePrefix, MySQLuserName, MySQLserverName, MySQLdbName, MySQLdbPass;
    public static int MySQLport;

    /* Commands */
    public static Boolean xplockEnable, xprateEnable, mccEnable, mcmmoEnable,
                          partyEnable, inviteEnable, acceptEnable, inspectEnable,
                          mcstatsEnable, addxpEnable, ptpEnable, mmoeditEnable,
                          mcremoveEnable, mcgodEnable, mcabilityEnable, mctopEnable,
                          addlevelsEnable, mcrefreshEnable, aEnable, pEnable;
    public static Boolean aDisplayNames, pDisplayNames;
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
    public static Boolean fishingDrops;
    public static int fishingTier1, fishingTier2, fishingTier3, fishingTier4, fishingTier5;
    public static int mfishing;

    /* Herbalism */
    public static Boolean herbalismHungerBonus, wheatRegrowth;
    public static int mmelon, mcactus, mmushroom, mflower, msugar, mpumpkin, mwheat, mvines, mlilypad, mnetherwart;
    public static Boolean enableCobbleToMossy, enableSmoothToMossy, enableDirtToGrass;

    /* Mining */
    public static int msandstone, mgold, mdiamond, miron, mredstone, mlapis, mobsidian, mnetherrack, mglowstone, mcoal, mstone, mendstone, mmossstone;
    public static int detonatorID;

    /* Repair */
    public static Boolean repairArmor, repairTools;
    public static Boolean anvilmessages;
    public static int rWood, rStone, rIron, rGold, rDiamond, rString, rLeather;
    public static int anvilID;
    public static int repairStoneLevel, repairIronLevel, repairGoldLevel, repairdiamondlevel;

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
    public static int levelCapAcrobatics, levelCapArchery, levelCapAxes, levelCapExcavation,
                      levelCapFishing, levelCapHerbalism, levelCapMining, levelCapRepair,
                      levelCapSwords, levelCapTaming, levelCapUnarmed, levelCapWoodcutting;

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public static Boolean xpGainsMobSpawners, pvpxp;
    public static int xpGainMultiplier;

    /* Combat XP Multipliers */
    public static double pvpxprewardmodifier;
    public static double animalXP, creeperXP, skeletonXP, spiderXP, ghastXP, slimeXP,
                         zombieXP, pigzombieXP, endermanXP, cavespiderXP, silverfishXP,
                         blazeXP, magmacubeXP, enderdragonXP;

    /* XP Formula Multiplier */
    public static double tamingxpmodifier, miningxpmodifier, repairxpmodifier, woodcuttingxpmodifier,
                         fishingxpmodifier, unarmedxpmodifier, herbalismxpmodifier, excavationxpmodifier,
                         archeryxpmodifier, swordsxpmodifier, axesxpmodifier, acrobaticsxpmodifier;

    /*
     * SPOUT SETTINGS
     */

    public static Boolean spoutEnabled;

    /* Spout Party HUD */
    public static Boolean showDisplayName, showFaces, partybar;

    /* Spout XP Bar */
    public static Boolean xpbar, xpicon;
    public static int xpbar_x, xpbar_y, xpicon_x, xpicon_y;

    /* Spout HUD Colors */
    public static double xpbackground_r, xpbackground_g, xpbackground_b;
    public static double xpborder_r, xpborder_g, xpborder_b;
    public static double fishing_r, fishing_g, fishing_b;
    public static double acrobatics_r, acrobatics_g, acrobatics_b;
    public static double archery_r, archery_g, archery_b;
    public static double axes_r, axes_g, axes_b;
    public static double excavation_r, excavation_g, excavation_b;
    public static double herbalism_r, herbalism_g, herbalism_b;
    public static double mining_r, mining_g, mining_b;
    public static double repair_r, repair_g, repair_b;
    public static double swords_r, swords_g, swords_b;
    public static double taming_r, taming_g, taming_b;
    public static double unarmed_r, unarmed_g, unarmed_b;
    public static double woodcutting_r, woodcutting_g, woodcutting_b;

    /*
     * CONFIG LOADING
     */

    public static HUDType defaulthud;

    public LoadProperties(mcMMO plugin) {
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

        acrobatics_r = config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3);
        acrobatics_g = config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3);
        acrobatics_b = config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75);
        archery_r = config.getDouble("Spout.HUD.Retro.Colors.Archery.RED", 0.3);
        archery_g = config.getDouble("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3);
        archery_b = config.getDouble("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75);
        axes_r = config.getDouble("Spout.HUD.Retro.Colors.Axes.RED", 0.3);
        axes_g = config.getDouble("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3);
        axes_b = config.getDouble("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75);
        excavation_r = config.getDouble("Spout.HUD.Retro.Colors.Excavation.RED", 0.3);
        excavation_g = config.getDouble("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3);
        excavation_b = config.getDouble("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75);
        herbalism_r = config.getDouble("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3);
        herbalism_g = config.getDouble("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3);
        herbalism_b = config.getDouble("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75);
        mining_r = config.getDouble("Spout.HUD.Retro.Colors.Mining.RED", 0.3);
        mining_g = config.getDouble("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3);
        mining_b = config.getDouble("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75);
        repair_r = config.getDouble("Spout.HUD.Retro.Colors.Repair.RED", 0.3);
        repair_g = config.getDouble("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3);
        repair_b = config.getDouble("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75);
        swords_r = config.getDouble("Spout.HUD.Retro.Colors.Swords.RED", 0.3);
        swords_g = config.getDouble("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3);
        swords_b = config.getDouble("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75);
        taming_r = config.getDouble("Spout.HUD.Retro.Colors.Taming.RED", 0.3);
        taming_g = config.getDouble("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3);
        taming_b = config.getDouble("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75);
        unarmed_r = config.getDouble("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3);
        unarmed_g = config.getDouble("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3);
        unarmed_b = config.getDouble("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75);
        woodcutting_r = config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3);
        woodcutting_g = config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3);
        woodcutting_b = config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75);
        fishing_r = config.getDouble("Spout.HUD.Retro.Colors.Fishing.RED", 0.3);
        fishing_g = config.getDouble("Spout.HUD.Retro.Colors.Fishing.GREEN", 0.3);
        fishing_b = config.getDouble("Spout.HUD.Retro.Colors.Fishing.BLUE", 0.75);

        xpborder_r = config.getDouble("Spout.HUD.Retro.Colors.Border.RED", 0.0);
        xpborder_g = config.getDouble("Spout.HUD.Retro.Colors.Border.GREEN", 0.0);
        xpborder_b = config.getDouble("Spout.HUD.Retro.Colors.Border.BLUE", 0.0);
        xpbackground_r = config.getDouble("Spout.HUD.Retro.Colors.Background.RED", 0.75);
        xpbackground_g = config.getDouble("Spout.HUD.Retro.Colors.Background.GREEN", 0.75);
        xpbackground_b = config.getDouble("Spout.HUD.Retro.Colors.Background.BLUE", 0.75);

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

        mgold = config.getInt("Experience.Mining.Gold", 250);
        mdiamond = config.getInt("Experience.Mining.Diamond", 750);
        miron = config.getInt("Experience.Mining.Iron", 250);
        mredstone = config.getInt("Experience.Mining.Redstone", 150);
        mlapis = config.getInt("Experience.Mining.Lapis", 400);
        mobsidian = config.getInt("Experience.Mining.Obsidian", 150);
        mnetherrack = config.getInt("Experience.Mining.Netherrack", 30);
        mglowstone = config.getInt("Experience.Mining.Glowstone", 30);
        mcoal = config.getInt("Experience.Mining.Coal", 100);
        mstone = config.getInt("Experience.Mining.Stone", 30);
        msandstone = config.getInt("Experience.Mining.Sandstone", 30);
        mendstone = config.getInt("Experience.Mining.End_Stone", 150);
        mmossstone = config.getInt("Experience.Mining.Moss_Stone", 30);

        mtameWolf = config.getInt("Experience.Taming.Animal_Taming.Wolf", 250);
        mtameOcelot = config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500);

        mfishing = config.getInt("Experience.Fishing.Base", 800);

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

        MySQLserverName = config.getString("MySQL.Server.Address", "localhost");

        if (config.getString("MySQL.Database.User_Password", null) != null) {
            MySQLdbPass = config.getString("MySQL.Database.User_Password", null);
        }
        else {
            MySQLdbPass = "";
        }

        MySQLdbName = config.getString("MySQL.Database.Name", "DatabaseName");
        MySQLuserName = config.getString("MySQL.Database.User_Name", "UserName");
        MySQLtablePrefix = config.getString("MySQL.Database.TablePrefix", "mcmmo_");
        MySQLport = config.getInt("MySQL.Server.Port", 3306);
        useMySQL = config.getBoolean("MySQL.Enabled", false);

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
        pvpxprewardmodifier = config.getDouble("Experience.Gains.Multiplier.PVP", 1.0);

        miningrequirespickaxe = config.getBoolean("Skills.Mining.Requires_Pickaxe", true);
        excavationRequiresShovel = config.getBoolean("Skills.Excavation.Requires_Shovel", true);
        woodcuttingrequiresaxe = config.getBoolean("Skills.Woodcutting.Requires_Axe", true);
        repairArmor = config.getBoolean("Skills.Repair.Can_Repair_Armor", true);
        repairTools = config.getBoolean("Skills.Repair.Can_Repair_Tools", true);
        repairdiamondlevel = config.getInt("Skills.Repair.Diamond.Level_Required", 50);
        repairIronLevel = config.getInt("Skills.Repair.Iron.Level_Required", 0);
        repairGoldLevel = config.getInt("Skills.Repair.Gold.Level_Required", 0);
        repairStoneLevel = config.getInt("Skills.Repair.Stone.Level_Required", 0);

        tamingxpmodifier = config.getDouble("Experience.Formula.Multiplier.Taming", 1.0);
        miningxpmodifier = config.getDouble("Experience.Formula.Multiplier.Mining", 1.0);
        repairxpmodifier = config.getDouble("Experience.Formula.Multiplier.Repair", 1.0);
        woodcuttingxpmodifier = config.getDouble("Experience.Formula.Multiplier.Woodcutting", 1.0);
        unarmedxpmodifier = config.getDouble("Experience.Formula.Multiplier.Unarmed", 1.0);
        herbalismxpmodifier = config.getDouble("Experience.Formula.Multiplier.Herbalism", 1.0);
        excavationxpmodifier = config.getDouble("Experience.Formula.Multiplier.Excavation", 1.0);
        archeryxpmodifier = config.getDouble("Experience.Formula.Multiplier.Archery", 1.0);
        swordsxpmodifier = config.getDouble("Experience.Formula.Multiplier.Swords", 1.0);
        axesxpmodifier = config.getDouble("Experience.Formula.Multiplier.Axes", 1.0);
        acrobaticsxpmodifier = config.getDouble("Experience.Formula.Multiplier.Acrobatics", 1.0);
        fishingxpmodifier = config.getDouble("Experience.Forumla.Multiplier.Fishing", 1.0);

        anvilmessages = config.getBoolean("Skills.Repair.Anvil_Messages", true);
        anvilID = config.getInt("Skills.Repair.Anvil_ID", 42);

        rGold = config.getInt("Skills.Repair.Gold.ID", 266);
        rStone = config.getInt("Skills.Repair.Stone.ID", 4);
        rWood = config.getInt("Skills.Repair.Wood.ID", 5);
        rDiamond = config.getInt("Skills.Repair.Diamond.ID", 264);
        rIron = config.getInt("Skills.Repair.Iron.ID", 265);
        rString = config.getInt("Skills.Repair.String.ID", 287);
        rLeather = config.getInt("Skills.Repair.Leather.ID", 334);

        levelCapAcrobatics = config.getInt("Skills.Acrobatics.Level_Cap", 0);
        levelCapArchery = config.getInt("Skills.Archery.Level_Cap", 0);
        levelCapAxes = config.getInt("Skills.Axes.Level_Cap", 0);
        levelCapExcavation = config.getInt("Skills.Excavation.Level_Cap", 0);
        levelCapFishing = config.getInt("Skills.Fishing.Level_Cap", 0);
        levelCapHerbalism = config.getInt("Skills.Herbalism.Level_Cap", 0);
        levelCapMining = config.getInt("Skills.Mining.Level_Cap", 0);
        levelCapRepair = config.getInt("Skills.Repair.Level_Cap", 0);
        levelCapSwords = config.getInt("Skills.Swords.Level_Cap", 0);
        levelCapTaming = config.getInt("Skills.Taming.Level_Cap", 0);
        levelCapUnarmed = config.getInt("Skills.Unarmed.Level_Cap", 0);
        levelCapWoodcutting = config.getInt("Skills.Woodcutting.Level_Cap", 0);

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

        fishingDrops = config.getBoolean("Fishing.Drops_Enabled", true);
        fishingTier1 = config.getInt("Fishing.Tier_Levels.Tier1", 0);
        fishingTier2 = config.getInt("Fishing.Tier_Levels.Tier2", 200);
        fishingTier3 = config.getInt("Fishing.Tier_Levels.Tier3", 400);
        fishingTier4 = config.getInt("Fishing.Tier_Levels.Tier4", 600);
        fishingTier5 = config.getInt("Fishing.Tier_Levels.Tier5", 800);

        xplockEnable = config.getBoolean("Commands.xplock.Enabled", true);
        xprateEnable = config.getBoolean("Commands.xprate.Enabled", true);
        mctopEnable = config.getBoolean("Commands.mctop.Enabled", true);
        addxpEnable = config.getBoolean("Commands.addxp.Enabled", true);
        addlevelsEnable = config.getBoolean("Commands.addlevels.Enabled", true);
        mcabilityEnable = config.getBoolean("Commands.mcability.Enabled", true);
        mcrefreshEnable = config.getBoolean("Commands.mcrefresh.Enabled", true);
        mcmmoEnable = config.getBoolean("Commands.mcmmo.Enabled", true);
        mccEnable = config.getBoolean("Commands.mcc.Enabled", true);
        mcgodEnable = config.getBoolean("Commands.mcgod.Enabled", true);
        mcstatsEnable = config.getBoolean("Commands.mcstats.Enabled", true);
        mmoeditEnable = config.getBoolean("Commands.mmoedit.Enabled", true);
        mcremoveEnable = config.getBoolean("Commands.mcremove.Enable", true);
        ptpEnable = config.getBoolean("Commands.ptp.Enabled", true);
        partyEnable = config.getBoolean("Commands.party.Enabled", true);
        inspectEnable = config.getBoolean("Commands.inspect.Enabled", true);
        inviteEnable = config.getBoolean("Commands.invite.Enabled", true);
        acceptEnable = config.getBoolean("Commands.accept.Enabled", true);
        aEnable = config.getBoolean("Commands.a.Enabled", true);
        pEnable = config.getBoolean("Commands.p.Enabled", true);

        aDisplayNames = config.getBoolean("Commands.a.Display_Names", true);
        pDisplayNames = config.getBoolean("Commands.p.Display_Names", true);

        ptpCommandCooldown = config.getInt("Commands.ptp.Cooldown", 30);

        animalXP = config.getDouble("Experience.Combat.Multiplier.Animals", 1.0);
        creeperXP = config.getDouble("Experience.Combat.Multiplier.Creeper", 4.0);
        skeletonXP = config.getDouble("Experience.Combat.Multiplier.Skeleton", 2.0);
        spiderXP = config.getDouble("Experience.Combat.Multiplier.Spider", 3.0);
        ghastXP = config.getDouble("Experience.Combat.Multiplier.Ghast", 3.0);
        slimeXP = config.getDouble("Experience.Combat.Multiplier.Slime", 2.0);
        zombieXP = config.getDouble("Experience.Combat.Multiplier.Zombie", 2.0);
        pigzombieXP = config.getDouble("Experience.Combat.Multiplier.Pig_Zombie", 3.0);
        endermanXP = config.getDouble("Experience.Combat.Multiplier.Enderman", 2.0);
        cavespiderXP = config.getDouble("Experience.Combat.Multiplier.Cave_Spider", 3.0);
        silverfishXP = config.getDouble("Experience.Combat.Multiplier.Silverfish", 3.0);
        blazeXP = config.getDouble("Experience.Combat.Multiplier.Blaze", 3.0);
        magmacubeXP = config.getDouble("Experience.Combat.Multiplier.Magma_Cube", 2.0);
        enderdragonXP = config.getDouble("Experience.Combat.Multiplier.Ender_Dragon", 8.0);

        detonatorID = config.getInt("Skills.Mining.Detonator_ID", 259);
    }
}