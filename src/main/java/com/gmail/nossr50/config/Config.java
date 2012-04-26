package com.gmail.nossr50.config;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDType;

public class Config extends ConfigLoader{
    
    public static int xpGainMultiplier = getExperienceGainsGlobalMultiplier();

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public static String getLocale() { return config.getString("General.Locale", "en_us"); }
    public static boolean getMOTDEnabled() { return config.getBoolean("General.MOTD_Enabled", true); }
    public static int getSaveInterval() { return config.getInt("General.Save_Interval", 10); }
    public static boolean getStatsTrackingEnabled() { return config.getBoolean("General.Stats_Tracking", true); }
    public static boolean getEventCallbackEnabled() { return config.getBoolean("General.Event_Callback", true); }

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

    public static int getPTPCommandCooldown() { return config.getInt("Commands.ptp.Cooldown", 30); }
    public static boolean getDonateMessageEnabled() { return config.getBoolean("Commands.mcmmo.Donate_Message", true); }

    /* Items */
    public static int getChimaeraCost() { return config.getInt("Items.Chimaera_Wing.Feather_Cost", 10); }
    public static int getChimaeraItemId() { return config.getInt("Items.Chimaera_Wing.Item_ID", 288); }
    public static boolean getChimaeraEnabled() { return config.getBoolean("Items.Chimaera_Wing.Enabled", true); }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public static boolean getAbilityMessagesEnabled() { return config.getBoolean("Abilities.Messages", true); }
    public static boolean getAbilitiesEnabled() { return config.getBoolean("Abilities.Enabled", true); }
    public static boolean getAbilitiesOnlyActivateWhenSneaking() { return config.getBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false); }

    /* Durability Settings */
    public static boolean getAbilitiesDamageTools() { return config.getBoolean("Abilities.Tools.Durability_Loss_Enabled", true); }
    public static int getAbilityToolDamage() { return config.getInt("Abilities.Tools.Durability_Loss", 2); }

    /* Cooldowns */
    public static int getAbilityCooldownGreenTerra() { return config.getInt("Abilities.Cooldowns.Green_Terra", 240); }
    public static int getAbilityCooldownSuperBreaker() { return config.getInt("Abilities.Cooldowns.Super_Breaker", 240); }
    public static int getAbilityCooldownGigaDrillBreaker() { return config.getInt("Abilities.Cooldowns.Giga_Drill_Breaker", 240); }
    public static int getAbilityCooldownTreeFeller() { return config.getInt("Abilities.Cooldowns.Tree_Feller", 240); }
    public static int getAbilityCooldownBerserk() { return config.getInt("Abilities.Cooldowns.Berserk", 240); }
    public static int getAbilityCooldownSerratedStrikes() { return config.getInt("Abilities.Cooldowns.Serrated_Strikes", 240); }
    public static int getAbilityCooldownSkullSplitter() { return config.getInt("Abilities.Cooldowns.Skull_Splitter", 240); }
    public static int getAbilityCooldownBlastMining() { return config.getInt("Abilities.Cooldowns.Blast_Mining", 60); }

    /* Thresholds */
    public static int getTreeFellerThreshold() { return config.getInt("Abilities.Limits.Tree_Feller_Threshold", 500); }

    /*
     * SKILL SETTINGS
     */

    /* Tool Requirements */
    public static boolean getMiningRequiresTool() { return config.getBoolean("Skills.Mining.Requires_Pickaxe", true); }
    public static boolean getExcavationRequiresTool() { return config.getBoolean("Skills.Excavation.Requires_Shovel", true); }
    public static boolean getWoodcuttingRequiresTool() { return config.getBoolean("Skills.Woodcutting.Requires_Axe", true); }

    /* Excavation */
    public static int getExcavationBaseXP() { return config.getInt("Experience.Excavation.Base", 40); }

    /* Fishing */
    public static int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }
    public static boolean getFishingDropsEnabled() { return config.getBoolean("Fishing.Drops_Enabled", true); }
    public static int getFishingTierLevelsTier1() { return config.getInt("Fishing.Tier_Levels.Tier1", 0); }
    public static int getFishingTierLevelsTier2() { return config.getInt("Fishing.Tier_Levels.Tier2", 200); }
    public static int getFishingTierLevelsTier3() { return config.getInt("Fishing.Tier_Levels.Tier3", 400); }
    public static int getFishingTierLevelsTier4() { return config.getInt("Fishing.Tier_Levels.Tier4", 600); }
    public static int getFishingTierLevelsTier5() { return config.getInt("Fishing.Tier_Levels.Tier5", 800); }

    /* Herbalism */
    public static int getHerbalismXPSugarCane() { return config.getInt("Experience.Herbalism.Sugar_Cane", 30); }
    public static int getHerbalismXPWheat() { return config.getInt("Experience.Herbalism.Wheat", 50); }
    public static int getHerbalismXPCactus() { return config.getInt("Experience.Herbalism.Cactus", 30); }
    public static int getHerbalismXPPumpkin() { return config.getInt("Experience.Herbalism.Pumpkin", 20); }
    public static int getHerbalismXPFlowers() { return config.getInt("Experience.Herbalism.Flowers", 100); }
    public static int getHerbalismXPMushrooms() { return config.getInt("Experience.Herbalism.Mushrooms", 150); }
    public static int getHerbalismXPMelon() { return config.getInt("Experience.Herbalism.Melon", 20); }
    public static int getHerbalismXPNetherWart() { return config.getInt("Experience.Herbalism.Nether_Wart", 50); }
    public static int getHerbalismXPLilyPads() { return config.getInt("Experience.Herbalism.Lily_Pads", 100); }
    public static int getHerbalismXPVines() { return config.getInt("Experience.Herbalism.Vines", 10); }
    public static boolean getHerbalismHungerBonusEnabled() { return config.getBoolean("Skills.Herbalism.Hunger_Bonus", true); }
    public static boolean getHerbalismWheatRegrowth() { return config.getBoolean("Skills.Herbalism.Instant_Wheat_Regrowth", true); }
    public static boolean getHerbalismGreenThumbCobbleToMossy() { return config.getBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true); }
    public static boolean getHerbalismGreenThumbSmoothbrickToMossy() { return config.getBoolean("Skills.Herbalism.Green_Thumb.SmoothBrick_To_MossyBrick", true); }
    public static boolean getHerbalismGreenThumbDirtToGrass() { return config.getBoolean("Skills.Herbalism.Green_Thumb.Dirt_To_Grass", true); }

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
    
    public static int getDetonatorItemID() { return config.getInt("Skills.Mining.Detonator_ID", 259); }

    /* Repair */
    public static boolean getRepairAnvilMessagesEnabled() { return config.getBoolean("Skills.Repair.Anvil_Messages", true); }
    public static int getRepairAnvilId() { return config.getInt("Skills.Repair.Anvil_ID", 42); }

    public static int getRepairGoldMaterial() { return config.getInt("Skills.Repair.Gold.ID", 266); }
    public static int getRepairStoneMaterial() { return config.getInt("Skills.Repair.Stone.ID", 4); }
    public static int getRepairWoodMaterial() { return config.getInt("Skills.Repair.Wood.ID", 5); }
    public static int getRepairDiamondMaterial() { return config.getInt("Skills.Repair.Diamond.ID", 264); }
    public static int getRepairIronMaterial() { return config.getInt("Skills.Repair.Iron.ID", 265); }
    public static int getRepairStringMaterial() { return config.getInt("Skills.Repair.String.ID", 287); }
    public static int getRepairLeatherMaterial() { return config.getInt("Skills.Repair.Leather.ID", 334); }
    
    public static boolean getRepairArmorAllowed() { return config.getBoolean("Skills.Repair.Can_Repair_Armor", true); }
    public static boolean getRepairToolsAllowed() { return config.getBoolean("Skills.Repair.Can_Repair_Tools", true); }
    public static int getRepairDiamondLevelRequirement() { return config.getInt("Skills.Repair.Diamond.Level_Required", 50); }
    public static int getRepairIronLevelRequirement() { return config.getInt("Skills.Repair.Iron.Level_Required", 0); }
    public static int getRepairGoldLevelRequirement() { return config.getInt("Skills.Repair.Gold.Level_Required", 0); }
    public static int getRepairStoneLevelRequirement() { return config.getInt("Skills.Repair.Stone.Level_Required", 0); }
    public static int getRepairStringLevelRequirement() { return config.getInt("Skills.Repair.String.Level_Required", 0); }

    /* Taming */
    public static int getTamingXPWolf() { return config.getInt("Experience.Taming.Animal_Taming.Wolf", 250); }
    public static int getTamingXPOcelot() { return config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500); }
    public static int getTamingCOTWWolfCost() { return config.getInt("Skills.Taming.Call_Of_The_Wild.Bones_Required", 10); }
    public static int getTamingCOTWOcelotCost() { return config.getInt("Skills.Taming.Call_Of_The_Wild.Fish_Required", 10); }

    /* Woodcutting */
    public static int getWoodcuttingXPOak() { return config.getInt("Experience.Woodcutting.Oak", 70); }
    public static int getWoodcuttingXPBirch() { return config.getInt("Experience.Woodcutting.Birch", 90); }
    public static int getWoodcuttingXPSpruce() { return config.getInt("Experience.Woodcutting.Spruce", 80); }
    public static int getWoodcuttingXPJungle() { return config.getInt("Experience.Woodcutting.Jungle", 100); }

    /* Arcane Forging */
    public static boolean getArcaneForgingDowngradeEnabled() { return config.getBoolean("Arcane_Forging.Downgrades.Enabled", true); }
    public static int getArcaneForgingDowngradeChanceRank1() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_1", 75); }
    public static int getArcaneForgingDowngradeChanceRank2() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_2", 50); }
    public static int getArcaneForgingDowngradeChanceRank3() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_3", 25); }
    public static int getArcaneForgingDowngradeChanceRank4() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_4", 15); }
    public static boolean getArcaneForgingEnchantLossEnabled() { return config.getBoolean("Arcane_Forging.May_Lose_Enchants", true); }
    public static int getArcaneForgingKeepEnchantsChanceRank1() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10); }
    public static int getArcaneForgingKeepEnchantsChanceRank2() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20); }
    public static int getArcaneForgingKeepEnchantsChanceRank3() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30); }
    public static int getArcaneForgingKeepEnchantsChanceRank4() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40); }
    public static int getArcaneForgingRankLevels1() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_1", 100); }
    public static int getArcaneForgingRankLevels2() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_2", 250); }
    public static int getArcaneForgingRankLevels3() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_3", 500); }
    public static int getArcaneForgingRankLevels4() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_4", 750); }

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
    public static boolean getExperienceGainsMobspawnersEnabled() { return config.getBoolean("Experience.Gains.Mobspawners.Enabled", false); }
    public static boolean getExperienceGainsPlayerVersusPlayerEnabled() { return config.getBoolean("Experience.PVP.Rewards", true); }
    public static int getExperienceGainsGlobalMultiplier() { return config.getInt("Experience.Gains.Multiplier.Global", 1); }

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

    /* Spout XP Bar */
    public static boolean getSpoutXPBarEnabled() { return config.getBoolean("Spout.XP.Bar.Enabled", true); }
    public static boolean getSpoutXPBarIconEnabled() { return config.getBoolean("Spout.XP.Icon.Enabled", true); }
    public static int getSpoutXPBarXPosition() { return config.getInt("Spout.XP.Bar.X_POS", 95); }
    public static int getSpoutXPBarYPosition() { return config.getInt("Spout.XP.Bar.Y_POS", 6); }
    public static int getSpoutXPIconXPosition() { return config.getInt("Spout.XP.Icon.X_POS", 78); }
    public static int getSpoutXPIconYPosition() { return config.getInt("Spout.XP.Icon.Y_POS", 2); }

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
    }
}