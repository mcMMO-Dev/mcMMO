package com.gmail.nossr50.config;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.gmail.nossr50.mcMMO;

public class Config extends ConfigLoader {
    private static Config instance;

    private Config() {
        super("config.yml");
        loadKeys();
    }

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {}

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public String getLocale() { return config.getString("General.Locale", "en_us"); }
    public boolean getMOTDEnabled() { return config.getBoolean("General.MOTD_Enabled", true); }
    public boolean getDonateMessageEnabled() { return config.getBoolean("Commands.mcmmo.Donate_Message", true); }
    public int getSaveInterval() { return config.getInt("General.Save_Interval", 10); }
    public boolean getStatsTrackingEnabled() { return config.getBoolean("General.Stats_Tracking", true); }
    public boolean getEventCallbackEnabled() { return config.getBoolean("General.Event_Callback", true); }
    public boolean getBackupsEnabled() { return config.getBoolean("General.Generate_Backups", true); }
    public boolean getPartyDisplayNames() { return config.getBoolean("Commands.p.Use_Display_Names", true); }
    public boolean getAdminDisplayNames() { return config.getBoolean("Commands.a.Use_Display_Names", true); }

    /* Database Purging */
    public int getPurgeInterval() { return config.getInt("Database_Purging.Purge_Interval", -1); }
    public int getOldUsersCutoff() { return config.getInt("Database_Purging.Old_User_Cutoff", 6); }

    /* mySQL */
    public boolean getUseMySQL() { return config.getBoolean("MySQL.Enabled", false); }
    public String getMySQLTablePrefix() { return config.getString("MySQL.Database.TablePrefix", "mcmmo_"); }
    public String getMySQLDatabaseName() { return getStringIncludingInts(config, "MySQL.Database.Name"); }
    public String getMySQLUserName() { return getStringIncludingInts(config, "MySQL.Database.User_Name"); }
    public int getMySQLServerPort() { return config.getInt("MySQL.Server.Port", 3306); }
    public String getMySQLServerName() { return config.getString("MySQL.Server.Address", "localhost"); }

    public String getMySQLUserPassword() {
        if (getStringIncludingInts(config, "MySQL.Database.User_Password") != null) {
            return getStringIncludingInts(config, "MySQL.Database.User_Password");
        }

        return "";
    }

    private static String getStringIncludingInts(ConfigurationSection cfg, String key) {
        String str = cfg.getString(key);
        if (str == null)
            str = String.valueOf(cfg.getInt(key));
        if (str == null)
            str = "No value set for '" + key + "'";
        return str;
    }

    /* Hardcore Mode */
    public boolean getHardcoreEnabled() { return config.getBoolean("Hardcore.Enabled", false); }
    public void setHardcoreEnabled(boolean enabled) { config.set("Hardcore.Enabled", enabled); }

    public double getHardcoreDeathStatPenaltyPercentage() { return config.getDouble("Hardcore.Death_Stat_Loss_Penalty_Percentage", 75.0); }
    public void setHardcoreDeathStatPenaltyPercentage(double value) { config.set("Hardcore.Death_Stat_Loss_Penalty_Percentage", value); }

    public double getHardcoreVampirismStatLeechPercentage() { return config.getDouble("Hardcore.Vampirism_Stat_Leech_Percentage", 5.0); }
    public void setHardcoreVampirismStatLeechPercentage(double value) { config.set("Hardcore.Vampirism_Stat_Leech_Percentage", value); }

    public boolean getHardcoreVampirismEnabled() { return config.getBoolean("Hardcore.Vampirism", false); }
    public void setHardcoreVampirismEnabled(boolean enabled) { config.set("Hardcore.Vampirism", enabled); }

    /* SMP Mods */
    public boolean getToolModsEnabled() { return config.getBoolean("Mods.Tool_Mods_Enabled", false); }
    public boolean getArmorModsEnabled() { return config.getBoolean("Mods.Tool_Mods_Enabled", false); }
    public boolean getBlockModsEnabled() { return config.getBoolean("Mods.Block_Mods_Enabled", false); }
    public boolean getEntityModsEnabled() { return config.getBoolean("Mods.Entity_Mods_Enabled", false); }

    /* Items */
    public int getChimaeraCost() { return config.getInt("Items.Chimaera_Wing.Feather_Cost", 10); }
    public int getChimaeraItemId() { return config.getInt("Items.Chimaera_Wing.Item_ID", 288); }
    public boolean getChimaeraEnabled() { return config.getBoolean("Items.Chimaera_Wing.Enabled", true); }

    /* PARTY SETTINGS */
    public int getAutoPartyKickInterval() { return config.getInt("Party.AutoKick_Interval", 12); }
    public int getAutoPartyKickTime() { return config.getInt("Party.Old_Party_Member_Cutoff", 7); }
    public boolean getExpShareEnabled() { return config.getBoolean("Party.Sharing.ExpShare_enabled", true); }
    public double getPartyShareBonusBase() { return config.getDouble("Party.Sharing.ExpShare_bonus_base", 1.1); }
    public double getPartyShareBonusIncrease() { return config.getDouble("Party.Sharing.ExpShare_bonus_increase", 0.05); }
    public double getPartyShareBonusCap() { return config.getDouble("Party.Sharing.ExpShare_bonus_cap", 1.5); }
    public boolean getItemShareEnabled() { return config.getBoolean("Party.Sharing.ItemShare_enabled", true); }
    public double getPartyShareRange() { return config.getDouble("Party.Sharing.Range", 75.0); }

    /* Party Teleport Settings */
    public int getPTPCommandCooldown() { return config.getInt("Commands.ptp.Cooldown", 30); }
    public int getPTPCommandTimeout() { return config.getInt("Commands.ptp.Request_Timeout", 300); }
    public boolean getPTPCommandConfirmRequired() { return config.getBoolean("Commands.ptp.Confirm_Required", true); }
    public boolean getPTPCommandWorldPermissions() { return config.getBoolean("Commands.ptp.World_Based_Permissions", false); }

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

    /* Max ticks */
    public int getAbilityMaxTicksGreenTerra() { return config.getInt("Abilities.Max_Seconds.Green_Terra", 0); }
    public int getAbilityMaxTicksSuperBreaker() { return config.getInt("Abilities.Max_Seconds.Super_Breaker", 0); }
    public int getAbilityMaxTicksGigaDrillBreaker() { return config.getInt("Abilities.Max_Seconds.Giga_Drill_Breaker", 0); }
    public int getAbilityMaxTicksTreeFeller() { return config.getInt("Abilities.Max_Seconds.Tree_Feller", 0); }
    public int getAbilityMaxTicksBerserk() { return config.getInt("Abilities.Max_Seconds.Berserk", 0); }
    public int getAbilityMaxTicksSerratedStrikes() { return config.getInt("Abilities.Max_Seconds.Serrated_Strikes", 0); }
    public int getAbilityMaxTicksSkullSplitter() { return config.getInt("Abilities.Max_Seconds.Skull_Splitter", 0); }
    public int getAbilityMaxTicksBlastMining() { return config.getInt("Abilities.Max_Seconds.Blast_Mining", 0); }

    /* Thresholds */
    public int getTreeFellerThreshold() { return config.getInt("Abilities.Limits.Tree_Feller_Threshold", 500); }

    /*
     * SKILL SETTINGS
     */

    /* Tool Requirements */
    public boolean getWoodcuttingRequiresTool() { return config.getBoolean("Skills.Woodcutting.Requires_Axe", true); }

    /* Excavation */
    public int getExcavationClayXP() { return config.getInt("Experience.Excavation.Clay", 40); }
    public int getExcavationDirtXP() { return config.getInt("Experience.Excavation.Dirt", 40); }
    public int getExcavationGrassXP() { return config.getInt("Experience.Excavation.Grass", 40); }
    public int getExcavationGravelXP() { return config.getInt("Experience.Excavation.Gravel", 40); }
    public int getExcavationMycelXP() { return config.getInt("Experience.Excavation.Mycel", 40); }
    public int getExcavationSandXP() { return config.getInt("Experience.Excavation.Sand", 40); }
    public int getExcavationSoulSandXP() { return config.getInt("Experience.Excavation.SoulSand", 40); }

    /* Fishing */
    public int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }
    public boolean getFishingDropsEnabled() { return config.getBoolean("Skills.Fishing.Drops_Enabled", true); }

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
    public int getHerbalismXPCocoa() { return config.getInt("Experience.Herbalism.Cocoa", 30); }
    public int getHerbalismXPCarrot() { return config.getInt("Experience.Herbalism.Carrot", 50); }
    public int getHerbalismXPPotato() { return config.getInt("Experience.Herbalism.Potato", 50); }

    public boolean getHerbalismGreenThumbCobbleToMossy() { return config.getBoolean("Skills.Herbalism.Green_Thumb.Cobble_To_Mossy", true); }
    public boolean getHerbalismGreenThumbCobbleWallToMossyWall() { return config.getBoolean("Skills.Herbalism.Green_Thumb.CobbleWall_To_MossyWall", true); }
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
    public boolean getCocoaDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Cocoa", true); }
    public boolean getCarrotDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Carrot", true); }
    public boolean getPotatoDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Herbalism.Potato", true); }

    public boolean herbalismDoubleDropsDisabled() {
        return doubleDropsDisabled("Herbalism");
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
    public int getMiningXPEmeraldOre() { return config.getInt("Experience.Mining.Emerald", 1000); }

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
    public boolean getEmeraldDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Mining.Emerald", true); }

    public boolean miningDoubleDropsDisabled() {
        return doubleDropsDisabled("Mining");
    }

    public int getDetonatorItemID() { return config.getInt("Skills.Mining.Detonator_ID", 259); }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() { return config.getBoolean("Skills.Repair.Anvil_Messages", true); }
    public int getRepairAnvilId() { return config.getInt("Skills.Repair.Anvil_ID", 42); }
    public boolean getSalvageEnabled() { return config.getBoolean("Skills.Repair.Salvage_enabled", true); }
    public int getSalvageAnvilId() { return config.getInt("Skills.Repair.Salvage_Anvil_ID", 41); }
    public int getSalvageUnlockLevel() { return config.getInt("Skills.Repair.Salvage_UnlockLevel", 600); }
    public boolean getSalvageTools() { return config.getBoolean("Skills.Repair.Salvage_tools", true); }
    public boolean getSalvageArmor() { return config.getBoolean("Skills.Repair.Salvage_armor", true); }

    /* Smelting */
    public int getSmeltingXPCoal() { return config.getInt("Experience.Smelting.Coal", 10); }
    public int getSmeltingXPRedstone() { return config.getInt("Experience.Smelting.Redstone", 15); }
    public int getSmeltingXPIron() { return config.getInt("Experience.Smelting.Iron", 25); }
    public int getSmeltingXPGold() { return config.getInt("Experience.Smelting.Gold", 35); }
    public int getSmeltingXPDiamond() { return config.getInt("Experience.Smelting.Diamond", 75); }
    public int getSmeltingXPLapis() { return config.getInt("Experience.Smelting.Lapis", 40); }
    public int getSmeltingXPEmerald() { return config.getInt("Experience.Smelting.Emerald", 100); }

    /* Unarmed */
    public boolean getUnarmedBlockCrackerSmoothbrickToCracked() { return config.getBoolean("Skills.Unarmed.Block_Cracker.SmoothBrick_To_CrackedBrick", true); }

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
    public int getWoodcuttingXPHugeBrownMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Brown", 70); }
    public int getWoodcuttingXPHugeRedMushroom() { return config.getInt("Experience.Woodcutting.Huge_Mushroom_Red", 70); }
    

    public boolean getOakDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Oak", true); }
    public boolean getBirchDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Birch", true); }
    public boolean getSpruceDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Spruce", true); }
    public boolean getJungleDoubleDropsEnabled() { return config.getBoolean("Double_Drops.Woodcutting.Jungle", true); }

    public boolean woodcuttingDoubleDropsDisabled() {
        return doubleDropsDisabled("Woodcutting");
    }

    /* AFK Leveling */
    public boolean getAcrobaticsAFKDisabled() { return config.getBoolean("Skills.Acrobatics.Prevent_AFK_Leveling", true); }
    public boolean getHerbalismAFKDisabled() { return config.getBoolean("Skills.Herbalism.Prevent_AFK_Leveling", true); }

    /* Dogde Lightning */
    public boolean getDodgeLightningDisabled() { return config.getBoolean("Skills.Acrobatics.Prevent_Dodge_Lightning", false); }

    /* Level Caps */
    public int getLevelCapAcrobatics() {
        return getLevelCap("Skills.Acrobatics.Level_Cap");
    }

    public int getLevelCapArchery() {
        return getLevelCap("Skills.Archery.Level_Cap");
    }

    public int getLevelCapAxes() {
        return getLevelCap("Skills.Axes.Level_Cap");
    }

    public int getLevelCapExcavation() {
        return getLevelCap("Skills.Excavation.Level_Cap");
    }

    public int getLevelCapFishing() {
        return getLevelCap("Skills.Fishing.Level_Cap");
    }

    public int getLevelCapHerbalism() {
        return getLevelCap("Skills.Herbalism.Level_Cap");
    }

    public int getLevelCapMining() {
        return getLevelCap("Skills.Mining.Level_Cap");
    }

    public int getLevelCapRepair() {
        return getLevelCap("Skills.Repair.Level_Cap");
    }

    public int getLevelCapSmelting() {
        return getLevelCap("Skills.Smelting.Level_Cap");
    }

    public int getLevelCapSwords() {
        return getLevelCap("Skills.Swords.Level_Cap");
    }

    public int getLevelCapTaming() {
        return getLevelCap("Skills.Taming.Level_Cap");
    }

    public int getLevelCapUnarmed() {
        return getLevelCap("Skills.Unarmed.Level_Cap");
    }

    public int getLevelCapWoodcutting() {
        return getLevelCap("Skills.Woodcutting.Level_Cap");
    }

    public int getPowerLevelCap() {
        return getLevelCap("General.Power_Level_Cap");
    }

    /* PVP & PVE Settings */
    public boolean getAcrobaticsPVP() { return config.getBoolean("Skills.Acrobatics.Enabled_For_PVP", true); }
    public boolean getAcrobaticsPVE() { return config.getBoolean("Skills.Acrobatics.Enabled_For_PVE", true); }
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
    public double getExperienceGainsGlobalMultiplier() { return config.getDouble("Experience.Gains.Multiplier.Global", 1.0); }
    public void setExperienceGainsGlobalMultiplier(double value) { config.set("Experience.Gains.Multiplier.Global", value); }

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
    public double getEndermanXP() { return config.getDouble("Experience.Combat.Multiplier.Enderman", 1.0); }
    public double getCaveSpiderXP() { return config.getDouble("Experience.Combat.Multiplier.Cave_Spider", 3.0); }
    public double getSilverfishXP() { return config.getDouble("Experience.Combat.Multiplier.Silverfish", 3.0); }
    public double getBlazeXP() { return config.getDouble("Experience.Combat.Multiplier.Blaze", 3.0); }
    public double getMagmaCubeXP() { return config.getDouble("Experience.Combat.Multiplier.Magma_Cube", 2.0); }
    public double getEnderDragonXP() { return config.getDouble("Experience.Combat.Multiplier.Ender_Dragon", 8.0); }
    public double getIronGolemXP() { return config.getDouble("Experience.Combat.Multiplier.Iron_Golem", 2.0); }
    public double getGiantXP() { return config.getDouble("Experience.Combat.Multiplier.Giant", 4.0); }
    public double getWitherXP() { return config.getDouble("Experience.Combat.Multiplier.Wither", 7.0); }
    public double getWitherSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Wither_Skeleton", 4.0); }
    public double getWitchXP() { return config.getDouble("Experience.Combat.Multiplier.Witch", 4.0); }

    /* XP Formula Multiplier */
    public int getFormulaMultiplierCurve() { return config.getInt("Experience.Formula.Curve_Modifier", 20); }
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

    private boolean doubleDropsDisabled(String skillName) {
        ConfigurationSection section = config.getConfigurationSection("Double_Drops." + skillName);

        if (section == null) {
            mcMMO.p.getLogger().warning("The configuration files are outdated!"); //TODO Locale and more descriptive message!
            return false;
        }

        Set<String> keys = section.getKeys(false);

        boolean disabled = true;

        for (String key : keys) {
            if (config.getBoolean("Double_Drops." + skillName + "." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    private int getLevelCap(String configString) {
        int cap = config.getInt(configString, 0);
        return ((cap <= 0) ? Integer.MAX_VALUE : cap);
    }
}
