package com.gmail.nossr50.config;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.HUDType;

public class Config extends ConfigLoader {
    public int xpGainMultiplier = 1;
    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config(mcMMO.p);
        }

        return instance;
    }

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public String getLocale() { return config.getString("General.Locale", "en_us"); }
    public boolean getMOTDEnabled() { return config.getBoolean("General.MOTD_Enabled", true); }
    public int getSaveInterval() { return config.getInt("General.Save_Interval", 10); }
    public boolean getStatsTrackingEnabled() { return config.getBoolean("General.Stats_Tracking", true); }
    public boolean getEventCallbackEnabled() { return config.getBoolean("General.Event_Callback", true); }

    /* mySQL */
    public boolean getUseMySQL() { return config.getBoolean("MySQL.Enabled", false); }
    public String getMySQLTablePrefix() { return config.getString("MySQL.Database.TablePrefix", "mcmmo_"); }
    public String getMySQLDatabaseName() { return config.getString("MySQL.Database.Name", "DatabaseName"); }
    public String getMySQLUserName() { return config.getString("MySQL.Database.User_Name", "UserName"); } //Really should be labeled under MySQL.User_Name instead...
    public int getMySQLServerPort() { return config.getInt("MySQL.Server.Port", 3306); }
    public String getMySQLServerName() { return config.getString("MySQL.Server.Address", "localhost"); }

    public String getMySQLUserPassword() {
        if (config.getString("MySQL.Database.User_Password", null) != null) {
            return config.getString("MySQL.Database.User_Password", null);
        }
        else {
            return "";
        }
    }

    /* Hardcore Mode */
    public boolean getHardcoreEnabled() { return config.getBoolean("Hardcore.Enabled", false); }
    public double getHardcoreDeathStatPenaltyPercentage() { return config.getDouble("Hardcore.Death_Stat_Loss_Penalty_Percentage", 75); }
    public double getHardcoreVampirismStatLeechPercentage() { return config.getDouble("Hardcore.Vampirism_Stat_Leech_Percentage", 5); }
    public boolean getHardcoreVampirismEnabled() { return config.getBoolean("Hardcore.Vampirism", false); }
    

    /* Commands */
    public boolean getCommandXPLockEnabled() { return config.getBoolean("Commands.xplock.Enabled", true); }
    public boolean getCommandXPRateEnabled() { return config.getBoolean("Commands.xprate.Enabled", true); }
    public boolean getCommandMCTopEnabled() { return config.getBoolean("Commands.mctop.Enabled", true); }
    public boolean getCommandAddXPEnabled() { return config.getBoolean("Commands.addxp.Enabled", true); }
    public boolean getCommandAddLevelsEnabled() { return config.getBoolean("Commands.addlevels.Enabled", true); }
    public boolean getCommandMCAbilityEnabled() { return config.getBoolean("Commands.mcability.Enabled", true); }
    public boolean getCommandMCRefreshEnabled() { return config.getBoolean("Commands.mcrefresh.Enabled", true); }
    public boolean getCommandmcMMOEnabled() { return config.getBoolean("Commands.mcmmo.Enabled", true); }
    public boolean getCommandMCCEnabled() { return config.getBoolean("Commands.mcc.Enabled", true); }
    public boolean getCommandMCGodEnabled() { return config.getBoolean("Commands.mcgod.Enabled", true); }
    public boolean getCommandMCStatsEnabled() { return config.getBoolean("Commands.mcstats.Enabled", true); }
    public boolean getCommandMmoeditEnabled() { return config.getBoolean("Commands.mmoedit.Enabled", true); }
    public boolean getCommandMCRemoveEnabled() { return config.getBoolean("Commands.mcremove.Enable", true); }
    public boolean getCommandPTPEnabled() { return config.getBoolean("Commands.ptp.Enabled", true); }
    public boolean getCommandPartyEnabled() { return config.getBoolean("Commands.party.Enabled", true); }
    public boolean getCommandInspectEnabled() { return config.getBoolean("Commands.inspect.Enabled", true); }
    public boolean getCommandInviteEnabled() { return config.getBoolean("Commands.invite.Enabled", true); }
    public boolean getCommandAcceptEnabled() { return config.getBoolean("Commands.accept.Enabled", true); }
    public boolean getCommandAdminChatAEnabled() { return config.getBoolean("Commands.a.Enabled", true); }
    public boolean getCommandPartyChatPEnabled() { return config.getBoolean("Commands.p.Enabled", true); }

    public int getPTPCommandCooldown() { return config.getInt("Commands.ptp.Cooldown", 30); }
    public boolean getDonateMessageEnabled() { return config.getBoolean("Commands.mcmmo.Donate_Message", true); }

    /* Items */
    public int getChimaeraCost() { return config.getInt("Items.Chimaera_Wing.Feather_Cost", 10); }
    public int getChimaeraItemId() { return config.getInt("Items.Chimaera_Wing.Item_ID", 288); }
    public boolean getChimaeraEnabled() { return config.getBoolean("Items.Chimaera_Wing.Enabled", true); }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public boolean getAbilityMessagesEnabled() { return config.getBoolean("Abilities.Messages", true); }
    public boolean getAbilitiesEnabled() { return config.getBoolean("Abilities.Enabled", true); }
    public boolean getAbilitiesOnlyActivateWhenSneaking() { return config.getBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false); }

    /* Durability Settings */
    public boolean getAbilitiesDamageTools() { return config.getBoolean("Abilities.Tools.Durability_Loss_Enabled", true); }
    public int getAbilityToolDamage() { return config.getInt("Abilities.Tools.Durability_Loss", 2); }

    /* Cooldowns */
    public int getAbilityCooldownGreenTerra() { return config.getInt("Abilities.Cooldowns.Green_Terra", 240); }
    public int getAbilityCooldownSuperBreaker() { return config.getInt("Abilities.Cooldowns.Super_Breaker", 240); }
    public int getAbilityCooldownGigaDrillBreaker() { return config.getInt("Abilities.Cooldowns.Giga_Drill_Breaker", 240); }
    public int getAbilityCooldownTreeFeller() { return config.getInt("Abilities.Cooldowns.Tree_Feller", 240); }
    public int getAbilityCooldownBerserk() { return config.getInt("Abilities.Cooldowns.Berserk", 240); }
    public int getAbilityCooldownSerratedStrikes() { return config.getInt("Abilities.Cooldowns.Serrated_Strikes", 240); }
    public int getAbilityCooldownSkullSplitter() { return config.getInt("Abilities.Cooldowns.Skull_Splitter", 240); }
    public int getAbilityCooldownBlastMining() { return config.getInt("Abilities.Cooldowns.Blast_Mining", 60); }

    /* Thresholds */
    public int getTreeFellerThreshold() { return config.getInt("Abilities.Limits.Tree_Feller_Threshold", 500); }

    /*
     * SKILL SETTINGS
     */

    /* Tool Requirements */
    public boolean getMiningRequiresTool() { return config.getBoolean("Skills.Mining.Requires_Pickaxe", true); }
    public boolean getExcavationRequiresTool() { return config.getBoolean("Skills.Excavation.Requires_Shovel", true); }
    public boolean getWoodcuttingRequiresTool() { return config.getBoolean("Skills.Woodcutting.Requires_Axe", true); }

    /* Excavation */
    public int getExcavationBaseXP() { return config.getInt("Experience.Excavation.Base", 40); }

    /* Fishing */
    public int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }
    public boolean getFishingDropsEnabled() { return config.getBoolean("Fishing.Drops_Enabled", true); }
    public int getFishingTierLevelsTier1() { return config.getInt("Fishing.Tier_Levels.Tier1", 0); }
    public int getFishingTierLevelsTier2() { return config.getInt("Fishing.Tier_Levels.Tier2", 200); }
    public int getFishingTierLevelsTier3() { return config.getInt("Fishing.Tier_Levels.Tier3", 400); }
    public int getFishingTierLevelsTier4() { return config.getInt("Fishing.Tier_Levels.Tier4", 600); }
    public int getFishingTierLevelsTier5() { return config.getInt("Fishing.Tier_Levels.Tier5", 800); }

    /* Herbalism */
    public int getHerbalismXPSugarCane() { return config.getInt("Experience.Herbalism.Sugar_Cane", 30); }
    public int getHerbalismXPWheat() { return config.getInt("Experience.Herbalism.Wheat", 50); }
    public int getHerbalismXPCactus() { return config.getInt("Experience.Herbalism.Cactus", 30); }
    public int getHerbalismXPPumpkin() { return config.getInt("Experience.Herbalism.Pumpkin", 20); }
    public int getHerbalismXPFlowers() { return config.getInt("Experience.Herbalism.Flowers", 100); }
    public int getHerbalismXPMushrooms() { return config.getInt("Experience.Herbalism.Mushrooms", 150); }
    public int getHerbalismXPMelon() { return config.getInt("Experience.Herbalism.Melon", 20); }
    public int getHerbalismXPNetherWart() { return config.getInt("Experience.Herbalism.Nether_Wart", 50); }
    public int getHerbalismXPLilyPads() { return config.getInt("Experience.Herbalism.Lily_Pads", 100); }
    public int getHerbalismXPVines() { return config.getInt("Experience.Herbalism.Vines", 10); }

    public boolean getHerbalismGreenThumbCobbleToMossy() { return config.getBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true); }
    public boolean getHerbalismGreenThumbSmoothbrickToMossy() { return config.getBoolean("Skills.Herbalism.Green_Thumb.SmoothBrick_To_MossyBrick", true); }
    public boolean getHerbalismGreenThumbDirtToGrass() { return config.getBoolean("Skills.Herbalism.Green_Thumb.Dirt_To_Grass", true); }

    public boolean getBrownMushroomsDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Brown_Mushrooms", true); }
    public boolean getCactiDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Cacti", true); }
    public boolean getWheatDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Wheat", true); }
    public boolean getMelonsDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Melons", true); }
    public boolean getNetherWartsDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Nether_Warts", true); }
    public boolean getPumpkinsDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Pumpkins", true); }
    public boolean getRedMushroomsDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Red_Mushrooms", true); }
    public boolean getRedRosesDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Red_Roses", true); }
    public boolean getSugarCaneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Sugar_Cane", true); }
    public boolean getVinesDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Vines", true); }
    public boolean getWaterLiliesDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Water_Lilies", true); }
    public boolean getYellowFlowersDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Yellow_Flowers", true); }

    public boolean herbalismDoubleDropsDisabled() {
        ConfigurationSection section = config.getConfigurationSection("Double_Drops.Herbalism");
        Set<String> keys = section.getKeys(false);
        Iterator<String> iterator = keys.iterator();

        boolean disabled = true;

        while (iterator.hasNext()) {
            String key = iterator.next();

            if (config.getBoolean("Double_Drops.Herbalism." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    /* Mining */
    public int getMiningXPGoldOre() { return config.getInt("Experience.Mining.Gold", 250); } 
    public int getMiningXPDiamondOre() { return config.getInt("Experience.Mining.Diamond", 750); }
    public int getMiningXPIronOre() { return config.getInt("Experience.Mining.Iron", 250); }
    public int getMiningXPRedstoneOre() { return config.getInt("Experience.Mining.Redstone", 150); }
    public int getMiningXPLapisOre() { return config.getInt("Experience.Mining.Lapis", 400); }
    public int getMiningXPObsidian() { return config.getInt("Experience.Mining.Obsidian", 150); }
    public int getMiningXPNetherrack() { return config.getInt("Experience.Mining.Netherrack", 30); }
    public int getMiningXPGlowstone() { return config.getInt("Experience.Mining.Glowstone", 30); }
    public int getMiningXPCoalOre() { return config.getInt("Experience.Mining.Coal", 100); }
    public int getMiningXPStone() { return config.getInt("Experience.Mining.Stone", 30); }
    public int getMiningXPSandstone() { return config.getInt("Experience.Mining.Sandstone", 30); }
    public int getMiningXPEndStone() { return config.getInt("Experience.Mining.End_Stone", 150); }
    public int getMiningXPMossyStone() { return config.getInt("Experience.Mining.Moss_Stone", 30); }

    public boolean getCoalDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Coal", true); }
    public boolean getDiamondDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Diamond", true); }
    public boolean getEndStoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.End_Stone", true); }
    public boolean getGlowstoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Glowstone", true); }
    public boolean getGoldDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Gold", true); }
    public boolean getIronDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Iron", true); }
    public boolean getLapisDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Lapis", true); }
    public boolean getMossyCobblestoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Moss_Stone", true); }
    public boolean getNetherrackDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Netherrack", true); }
    public boolean getObsidianDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Obsidian", true); }
    public boolean getRedstoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Redstone", true); }
    public boolean getSandstoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Sandstone", true); }
    public boolean getStoneDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Stone", true); }

    public boolean miningDoubleDropsDisabled() {
        ConfigurationSection section = config.getConfigurationSection("Double_Drops.Mining");
        Set<String> keys = section.getKeys(false);
        Iterator<String> iterator = keys.iterator();

        boolean disabled = true;

        while (iterator.hasNext()) {
            String key = iterator.next();

            if (config.getBoolean("Double_Drops.Mining." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    public int getDetonatorItemID() { return config.getInt("Skills.Mining.Detonator_ID", 259); }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() { return config.getBoolean("Skills.Repair.Anvil_Messages", true); }
    public int getRepairAnvilId() { return config.getInt("Skills.Repair.Anvil_ID", 42); }

    public int getRepairGoldMaterial() { return config.getInt("Skills.Repair.Gold.ID", 266); }
    public int getRepairStoneMaterial() { return config.getInt("Skills.Repair.Stone.ID", 4); }
    public int getRepairWoodMaterial() { return config.getInt("Skills.Repair.Wood.ID", 5); }
    public int getRepairDiamondMaterial() { return config.getInt("Skills.Repair.Diamond.ID", 264); }
    public int getRepairIronMaterial() { return config.getInt("Skills.Repair.Iron.ID", 265); }
    public int getRepairStringMaterial() { return config.getInt("Skills.Repair.String.ID", 287); }
    public int getRepairLeatherMaterial() { return config.getInt("Skills.Repair.Leather.ID", 334); }

    public int getRepairDiamondLevelRequirement() { return config.getInt("Skills.Repair.Diamond.Level_Required", 50); }
    public int getRepairIronLevelRequirement() { return config.getInt("Skills.Repair.Iron.Level_Required", 0); }
    public int getRepairGoldLevelRequirement() { return config.getInt("Skills.Repair.Gold.Level_Required", 0); }
    public int getRepairStoneLevelRequirement() { return config.getInt("Skills.Repair.Stone.Level_Required", 0); }

    /* Taming */
    public int getTamingXPWolf() { return config.getInt("Experience.Taming.Animal_Taming.Wolf", 250); }
    public int getTamingXPOcelot() { return config.getInt("Experience.Taming.Animal_Taming.Ocelot", 500); }
    public int getTamingCOTWWolfCost() { return config.getInt("Skills.Taming.Call_Of_The_Wild.Bones_Required", 10); }
    public int getTamingCOTWOcelotCost() { return config.getInt("Skills.Taming.Call_Of_The_Wild.Fish_Required", 10); }

    /* Woodcutting */
    public int getWoodcuttingXPOak() { return config.getInt("Experience.Woodcutting.Oak", 70); }
    public int getWoodcuttingXPBirch() { return config.getInt("Experience.Woodcutting.Birch", 90); }
    public int getWoodcuttingXPSpruce() { return config.getInt("Experience.Woodcutting.Spruce", 80); }
    public int getWoodcuttingXPJungle() { return config.getInt("Experience.Woodcutting.Jungle", 100); }

    public boolean getOakDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Oak", true); }
    public boolean getBirchDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Birch", true); }
    public boolean getSpruceDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Spruce", true); }
    public boolean getJungleDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Jungle", true); }

    public boolean woodcuttingDoubleDropsDisabled() {
        ConfigurationSection section = config.getConfigurationSection("Double_Drops.Woodcutting");
        Set<String> keys = section.getKeys(false);
        Iterator<String> iterator = keys.iterator();

        boolean disabled = true;

        while (iterator.hasNext()) {
            String key = iterator.next();

            if (config.getBoolean("Double_Drops.Woodcutting." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    /* Arcane Forging */
    public boolean getArcaneForgingDowngradeEnabled() { return config.getBoolean("Arcane_Forging.Downgrades.Enabled", true); }
    public int getArcaneForgingDowngradeChanceRank1() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_1", 75); }
    public int getArcaneForgingDowngradeChanceRank2() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_2", 50); }
    public int getArcaneForgingDowngradeChanceRank3() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_3", 25); }
    public int getArcaneForgingDowngradeChanceRank4() { return config.getInt("Arcane_Forging.Downgrades.Chance.Rank_4", 15); }
    public boolean getArcaneForgingEnchantLossEnabled() { return config.getBoolean("Arcane_Forging.May_Lose_Enchants", true); }
    public int getArcaneForgingKeepEnchantsChanceRank1() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_1", 10); }
    public int getArcaneForgingKeepEnchantsChanceRank2() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_2", 20); }
    public int getArcaneForgingKeepEnchantsChanceRank3() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_3", 30); }
    public int getArcaneForgingKeepEnchantsChanceRank4() { return config.getInt("Arcane_Forging.Keep_Enchants.Chance.Rank_4", 40); }
    public int getArcaneForgingRankLevels1() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_1", 100); }
    public int getArcaneForgingRankLevels2() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_2", 250); }
    public int getArcaneForgingRankLevels3() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_3", 500); }
    public int getArcaneForgingRankLevels4() { return config.getInt("Arcane_Forging.Rank_Levels.Rank_4", 750); }

    /* Level Caps */
    public int getLevelCapAcrobatics() { return config.getInt("Skills.Acrobatics.Level_Cap", 0); }
    public int getLevelCapArchery() { return config.getInt("Skills.Archery.Level_Cap", 0); }
    public int getLevelCapAxes() { return config.getInt("Skills.Axes.Level_Cap", 0); }
    public int getLevelCapExcavation() { return config.getInt("Skills.Excavation.Level_Cap", 0); }
    public int getLevelCapFishing() { return config.getInt("Skills.Fishing.Level_Cap", 0); }
    public int getLevelCapHerbalism() { return config.getInt("Skills.Herbalism.Level_Cap", 0); }
    public int getLevelCapMining() { return config.getInt("Skills.Mining.Level_Cap", 0); }
    public int getLevelCapRepair() { return config.getInt("Skills.Repair.Level_Cap", 0); }
    public int getLevelCapSwords() { return config.getInt("Skills.Swords.Level_Cap", 0); }
    public int getLevelCapTaming() { return config.getInt("Skills.Taming.Level_Cap", 0); }
    public int getLevelCapUnarmed() { return config.getInt("Skills.Unarmed.Level_Cap", 0); }
    public int getLevelCapWoodcutting() { return config.getInt("Skills.Woodcutting.Level_Cap", 0); }
    public int getPowerLevelCap() { return config.getInt("General.Power_Level_Cap", 0); }

    /* PVP & PVE Settings */
    public boolean getArcheryPVP() { return config.getBoolean("Skills.Archery.Enabled_For_PVP", true); }
    public boolean getArcheryPVE() { return config.getBoolean("Skills.Archery.Enabled_For_PVE", true); }
    public boolean getAxesPVP() { return config.getBoolean("Skills.Axes.Enabled_For_PVP", true); }
    public boolean getAxesPVE() { return config.getBoolean("Skills.Axes.Enabled_For_PVE", true); }
    public boolean getSwordsPVP() { return config.getBoolean("Skills.Swords.Enabled_For_PVP", true); }
    public boolean getSwordsPVE() { return config.getBoolean("Skills.Swords.Enabled_For_PVE", true); }
    public boolean getTamingPVP() { return config.getBoolean("Skills.Taming.Enabled_For_PVP", true); }
    public boolean getTamingPVE() { return config.getBoolean("Skills.Taming.Enabled_For_PVE", true); }
    public boolean getUnarmedPVP() { return config.getBoolean("Skills.Unarmed.Enabled_For_PVP", true); }
    public boolean getUnarmedPVE() { return config.getBoolean("Skills.Unarmed.Enabled_For_PVE", true); }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsMobspawnersEnabled() { return config.getBoolean("Experience.Gains.Mobspawners.Enabled", false); }
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() { return config.getBoolean("Experience.PVP.Rewards", true); }
    public int getExperienceGainsGlobalMultiplier() { return config.getInt("Experience.Gains.Multiplier.Global", 1); }

    /* Combat XP Multipliers */
    public double getPlayerVersusPlayerXP() { return config.getDouble("Experience.Gains.Multiplier.PVP", 1.0); }
    
    public double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }
    public double getCreeperXP() { return config.getDouble("Experience.Combat.Multiplier.Creeper", 4.0); }
    public double getSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Skeleton", 2.0); }
    public double getSpiderXP() { return config.getDouble("Experience.Combat.Multiplier.Spider", 3.0); }
    public double getGhastXP() { return config.getDouble("Experience.Combat.Multiplier.Ghast", 3.0); }
    public double getSlimeXP() { return config.getDouble("Experience.Combat.Multiplier.Slime", 2.0); }
    public double getZombieXP() { return config.getDouble("Experience.Combat.Multiplier.Zombie", 2.0); }
    public double getPigZombieXP() { return config.getDouble("Experience.Combat.Multiplier.Pig_Zombie", 3.0); }
    public double getEndermanXP() { return config.getDouble("Experience.Combat.Multiplier.Enderman", 2.0); }
    public double getCaveSpiderXP() { return config.getDouble("Experience.Combat.Multiplier.Cave_Spider", 3.0); }
    public double getSilverfishXP() { return config.getDouble("Experience.Combat.Multiplier.Silverfish", 3.0); }
    public double getBlazeXP() { return config.getDouble("Experience.Combat.Multiplier.Blaze", 3.0); }
    public double getMagmaCubeXP() { return config.getDouble("Experience.Combat.Multiplier.Magma_Cube", 2.0); }
    public double getEnderDragonXP() { return config.getDouble("Experience.Combat.Multiplier.Ender_Dragon", 8.0); }
    public double getIronGolemXP() { return config.getDouble("Experience.Combat.Multiplier.Iron_Golem", 2.0); }

    /* XP Formula Multiplier */
    public double getFormulaMultiplierTaming() { return config.getDouble("Experience.Formula.Multiplier.Taming", 1.0); }
    public double getFormulaMultiplierMining() { return config.getDouble("Experience.Formula.Multiplier.Mining", 1.0); }
    public double getFormulaMultiplierRepair() { return config.getDouble("Experience.Formula.Multiplier.Repair", 1.0); }
    public double getFormulaMultiplierWoodcutting() { return config.getDouble("Experience.Formula.Multiplier.Woodcutting", 1.0); }
    public double getFormulaMultiplierUnarmed() { return config.getDouble("Experience.Formula.Multiplier.Unarmed", 1.0); }
    public double getFormulaMultiplierHerbalism() { return config.getDouble("Experience.Formula.Multiplier.Herbalism", 1.0); }
    public double getFormulaMultiplierExcavation() { return config.getDouble("Experience.Formula.Multiplier.Excavation", 1.0); }
    public double getFormulaMultiplierArchery() { return config.getDouble("Experience.Formula.Multiplier.Archery", 1.0); }
    public double getFormulaMultiplierSwords() { return config.getDouble("Experience.Formula.Multiplier.Swords", 1.0); }
    public double getFormulaMultiplierAxes() { return config.getDouble("Experience.Formula.Multiplier.Axes", 1.0); }
    public double getFormulaMultiplierAcrobatics() { return config.getDouble("Experience.Formula.Multiplier.Acrobatics", 1.0); }
    public double getFormulaMultiplierFishing() { return config.getDouble("Experience.Formula.Multiplier.Fishing", 1.0); }

    /*
     * SPOUT SETTINGS
     */

    public boolean spoutEnabled;
    public boolean getShowPowerLevelForSpout() { return config.getBoolean("Spout.HUD.Show_Power_Level", true); }

    /* Spout XP Bar */
    public boolean getSpoutXPBarEnabled() { return config.getBoolean("Spout.XP.Bar.Enabled", true); }
    public boolean getSpoutXPBarIconEnabled() { return config.getBoolean("Spout.XP.Icon.Enabled", true); }
    public int getSpoutXPBarXPosition() { return config.getInt("Spout.XP.Bar.X_POS", 95); }
    public int getSpoutXPBarYPosition() { return config.getInt("Spout.XP.Bar.Y_POS", 6); }
    public int getSpoutXPIconXPosition() { return config.getInt("Spout.XP.Icon.X_POS", 78); }
    public int getSpoutXPIconYPosition() { return config.getInt("Spout.XP.Icon.Y_POS", 2); }

    /* Spout HUD Colors */
    public double getSpoutRetroHUDXPBorderRed() { return config.getDouble("Spout.HUD.Retro.Colors.Border.RED", 0.0); }
    public double getSpoutRetroHUDXPBorderGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Border.GREEN", 0.0); }
    public double getSpoutRetroHUDXPBorderBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Border.BLUE", 0.0); }
    public double getSpoutRetroHUDXPBackgroundRed() { return config.getDouble("Spout.HUD.Retro.Colors.Background.RED", 0.75); }
    public double getSpoutRetroHUDXPBackgroundGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Background.GREEN", 0.75); }
    public double getSpoutRetroHUDXPBackgroundBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Background.BLUE", 0.75); }
    
    public double getSpoutRetroHUDAcrobaticsRed() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.RED", 0.3); }
    public double getSpoutRetroHUDAcrobaticsGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.GREEN", 0.3); }
    public double getSpoutRetroHUDAcrobaticsBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Acrobatics.BLUE", 0.75); }
    public double getSpoutRetroHUDArcheryRed() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.RED", 0.3); }
    public double getSpoutRetroHUDArcheryGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.GREEN", 0.3); }
    public double getSpoutRetroHUDArcheryBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Archery.BLUE", 0.75); }
    public double getSpoutRetroHUDAxesRed() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.RED", 0.3); }
    public double getSpoutRetroHUDAxesGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.GREEN", 0.3); }
    public double getSpoutRetroHUDAxesBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Axes.BLUE", 0.75); }
    public double getSpoutRetroHUDExcavationRed() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.RED", 0.3); }
    public double getSpoutRetroHUDExcavationGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.GREEN", 0.3); }
    public double getSpoutRetroHUDExcavationBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Excavation.BLUE", 0.75); }
    public double getSpoutRetroHUDHerbalismRed() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.RED", 0.3); }
    public double getSpoutRetroHUDHerbalismGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.GREEN", 0.3); }
    public double getSpoutRetroHUDHerbalismBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Herbalism.BLUE", 0.75); }
    public double getSpoutRetroHUDMiningRed() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.RED", 0.3); }
    public double getSpoutRetroHUDMiningGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.GREEN", 0.3); }
    public double getSpoutRetroHUDMiningBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Mining.BLUE", 0.75); }
    public double getSpoutRetroHUDRepairRed() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.RED", 0.3); }
    public double getSpoutRetroHUDRepairGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.GREEN", 0.3); }
    public double getSpoutRetroHUDRepairBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Repair.BLUE", 0.75); }
    public double getSpoutRetroHUDSwordsRed() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.RED", 0.3); }
    public double getSpoutRetroHUDSwordsGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.GREEN", 0.3); }
    public double getSpoutRetroHUDSwordsBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Swords.BLUE", 0.75); }
    public double getSpoutRetroHUDTamingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.RED", 0.3); }
    public double getSpoutRetroHUDTamingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.GREEN", 0.3); }
    public double getSpoutRetroHUDTamingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Taming.BLUE", 0.75); }
    public double getSpoutRetroHUDUnarmedRed() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.RED", 0.3); }
    public double getSpoutRetroHUDUnarmedGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.GREEN", 0.3); }
    public double getSpoutRetroHUDUnarmedBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Unarmed.BLUE", 0.75); }
    public double getSpoutRetroHUDWoodcuttingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.RED", 0.3); }
    public double getSpoutRetroHUDWoodcuttingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.GREEN", 0.3); }
    public double getSpoutRetroHUDWoodcuttingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Woodcutting.BLUE", 0.75); }
    public double getSpoutRetroHUDFishingRed() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.RED", 0.3); }
    public double getSpoutRetroHUDFishingGreen() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.GREEN", 0.3); }
    public double getSpoutRetroHUDFishingBlue() { return config.getDouble("Spout.HUD.Retro.Colors.Fishing.BLUE", 0.75); }

    /*
     * CONFIG LOADING
     */

    public HUDType defaulthud;

    private Config(mcMMO plugin) {
        super(plugin, "config.yml");
        config = plugin.getConfig();
        xpGainMultiplier = getExperienceGainsGlobalMultiplier();
        load();
    }

    @Override
    protected void load() {
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
            if (x.toString().toLowerCase().equalsIgnoreCase(temp.toString().toLowerCase())) {
                defaulthud = x;
            }
        }

        if(defaulthud == null)
            defaulthud = HUDType.STANDARD;
    }
}
