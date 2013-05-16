package com.gmail.nossr50.config;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.StringUtils;

public class Config extends AutoUpdateConfigLoader {
    private static Config instance;

    private Config() {
        super("config.yml");
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
    public boolean getUpdateCheckEnabled() { return config.getBoolean("General.Update_Check", true); }
    public boolean getPreferBeta() { return config.getBoolean("General.Prefer_Beta", false); }
    public boolean getEventCallbackEnabled() { return config.getBoolean("General.Event_Callback", true); }
    public boolean getBackupsEnabled() { return config.getBoolean("General.Generate_Backups", true); }
    public boolean getVerboseLoggingEnabled() { return config.getBoolean("General.Verbose_Logging", false); }
    public boolean getConfigOverwriteEnabled() { return config.getBoolean("General.Config_Update_Overwrite", true); }

    public boolean getPotatoEnabled() { return config.getBoolean("General.Potato_Explosion_Enabled", false); }
    public int getPotatoChance() { return config.getInt("General.Potato_Explosion_Chance", 1); }

    public boolean getPartyDisplayNames() { return config.getBoolean("Commands.p.Use_Display_Names", true); }
    public boolean getAdminDisplayNames() { return config.getBoolean("Commands.a.Use_Display_Names", true); }

    /* Mob Healthbar */
    public MobHealthbarType getMobHealthbarDefault() {
        try {
            return MobHealthbarType.valueOf(config.getString("Mob_Healthbar.Display_Type", "HEARTS").toUpperCase().trim());
        }
        catch (IllegalArgumentException ex) {
            return MobHealthbarType.HEARTS;
        }
    }

    public int getMobHealthbarTime() { return config.getInt("Mob_Healthbar.Display_Time", 3); }

    /* Scoreboards */
    public boolean getMcrankScoreboardEnabled() { return config.getBoolean("Scoreboards.Mcrank.Use", true); }
    public int getMcrankScoreboardTime() { return config.getInt("Scoreboards.Mcrank.Display_Time", 10); }

    public boolean getMcstatsScoreboardsEnabled() { return config.getBoolean("Scoreboards.Mcstats.Use", true); }
    public int getMcstatsScoreboardTime() { return config.getInt("Scoreboards.Mcstats.Display_Time", 10); }

    public boolean getMctopScoreboardEnabled() { return config.getBoolean("Scoreboards.Mctop.Use", true); }
    public int getMctopScoreboardTime() { return config.getInt("Scoreboards.Mctop.Display_Time", 10); }

    public boolean getInspectScoreboardEnabled() { return config.getBoolean("Scoreboards.Inspect.Use", true); }
    public int getInspectScoreboardTime() { return config.getInt("Scoreboards.Inspect.Display_Time", 10); }

    public boolean getSkillScoreboardEnabled() { return config.getBoolean("Scoreboards.Skillname.Use", true); }
    public int getSkillScoreboardTime() { return config.getInt("Scoreboards.Skillname.Display_Time", 10); }

    public boolean getPowerLevelsEnabled() { return config.getBoolean("Scoreboards.Power_Level.Use", true); }

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

        if (str == null) {
            str = String.valueOf(cfg.getInt(key));
        }

        if (str == "0") {
            str = "No value set for '" + key + "'";
        }
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
    public int getChimaeraUseCost() { return config.getInt("Items.Chimaera_Wing.Use_Cost", 1); }
    public int getChimaeraRecipeCost() { return config.getInt("Items.Chimaera_Wing.Recipe_Cost", 5); }
    public int getChimaeraItemId() { return config.getInt("Items.Chimaera_Wing.Item_ID", 288); }
    public boolean getChimaeraEnabled() { return config.getBoolean("Items.Chimaera_Wing.Enabled", true); }
    public boolean getChimaeraPreventUseUnderground() { return config.getBoolean("Items.Chimaera_Wing.Prevent_Use_Underground", true); }
    public int getChimaeraCooldown() { return config.getInt("Items.Chimaera_Wing.Cooldown", 240); }
    public int getChimaeraWarmup() { return config.getInt("Items.Chimaera_Wing.Warmup", 5); }
    public int getChimaeraRecentlyHurtCooldown() { return config.getInt("Items.Chimaera_Wing.RecentlyHurt_Cooldown", 60); }

    /* Particles */
    public boolean getAbilityActivationEffectEnabled() { return config.getBoolean("Particles.Ability_Activation", true); }
    public boolean getAbilityDeactivationEffectEnabled() { return config.getBoolean("Particles.Ability_Deactivation", true); }
    public boolean getDodgeEffectEnabled() { return config.getBoolean("Particles.Dodge", true); }
    public boolean getBleedEffectEnabled() { return config.getBoolean("Particles.Bleed", true); }
    public boolean getGreaterImpactEffectEnabled() { return config.getBoolean("Particles.Greater_Impact", true); }
    public boolean getLevelUpEffectsEnabled() { return config.getBoolean("Particles.LevelUp_Enabled", true); }
    public int getLevelUpEffectsTier() { return config.getInt("Particles.LevelUp_Tier", 100); }
    public boolean getLargeFireworks() { return config.getBoolean("Particles.LargeFireworks", true); }

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
    public int getPTPCommandCooldown() { return config.getInt("Commands.ptp.Cooldown", 120); }
    public int getPTPCommandWarmup() { return config.getInt("Commands.ptp.Warmup", 5); }
    public int getPTPCommandRecentlyHurtCooldown() { return config.getInt("Commands.ptp.RecentlyHurt_Cooldown", 60); }
    public int getPTPCommandTimeout() { return config.getInt("Commands.ptp.Request_Timeout", 300); }
    public boolean getPTPCommandConfirmRequired() { return config.getBoolean("Commands.ptp.Confirm_Required", true); }
    public boolean getPTPCommandWorldPermissions() { return config.getBoolean("Commands.ptp.World_Based_Permissions", false); }

    /* Inspect command distance */
    public double getInspectDistance() { return config.getDouble("Commands.inspect.Max_Distance", 30); }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public boolean getAbilityMessagesEnabled() { return config.getBoolean("Abilities.Messages", true); }
    public boolean getAbilitiesEnabled() { return config.getBoolean("Abilities.Enabled", true); }
    public boolean getAbilitiesOnlyActivateWhenSneaking() { return config.getBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false); }

    public int getCooldown(AbilityType ability) { return config.getInt("Abilities.Cooldowns." + ability.toString()); }
    public int getMaxTicks(AbilityType ability) { return config.getInt("Abilities.Max_Seconds." + ability.toString()); }

    /* Durability Settings */
    public boolean getAbilitiesDamageTools() { return config.getBoolean("Abilities.Tools.Durability_Loss_Enabled", true); }
    public int getAbilityToolDamage() { return config.getInt("Abilities.Tools.Durability_Loss", 2); }

    /* Thresholds */
    public int getTreeFellerThreshold() { return config.getInt("Abilities.Limits.Tree_Feller_Threshold", 500); }

    /*
     * SKILL SETTINGS
     */

    public int getXp(SkillType skill, Material material) { return config.getInt("Experience." + StringUtils.getCapitalized(skill.toString()) + "." + StringUtils.getPrettyItemString(material).replace(" ", "_")); }
    public boolean getDoubleDropsEnabled(SkillType skill, Material material) { return config.getBoolean("Double_Drops." + StringUtils.getCapitalized(skill.toString()) + "." + StringUtils.getPrettyItemString(material).replace(" ", "_")); }

    public boolean getDoubleDropsDisabled(SkillType skill) {
        String skillName = StringUtils.getCapitalized(skill.toString());
        ConfigurationSection section = config.getConfigurationSection("Double_Drops." + skillName);
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

    /* Acrobatics */
    public boolean getDodgeLightningDisabled() { return config.getBoolean("Skills.Acrobatics.Prevent_Dodge_Lightning", false); }

    /* Fishing */
    public int getFishingBaseXP() { return config.getInt("Experience.Fishing.Base", 800); }
    public boolean getFishingDropsEnabled() { return config.getBoolean("Skills.Fishing.Drops_Enabled", true); }

    /* Mining */
    public int getDetonatorItemID() { return config.getInt("Skills.Mining.Detonator_ID", 259); }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() { return config.getBoolean("Skills.Repair.Anvil_Messages", true); }
    public int getRepairAnvilId() { return config.getInt("Skills.Repair.Anvil_ID", 42); }
    public int getSalvageAnvilId() { return config.getInt("Skills.Repair.Salvage_Anvil_ID", 41); }
    public boolean getSalvageTools() { return config.getBoolean("Skills.Repair.Salvage_tools", true); }
    public boolean getSalvageArmor() { return config.getBoolean("Skills.Repair.Salvage_armor", true); }
    public boolean getRepairConfirmRequired() { return config.getBoolean("Skills.Repair.Confirm_Required", true); }

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

    /* AFK Leveling */
    public boolean getAcrobaticsAFKDisabled() { return config.getBoolean("Skills.Acrobatics.Prevent_AFK_Leveling", true); }
    public boolean getHerbalismAFKDisabled() { return config.getBoolean("Skills.Herbalism.Prevent_AFK_Leveling", true); }

    /* Level Caps */
    public int getPowerLevelCap() {
        int cap = config.getInt("General.Power_Level_Cap", 0);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    public int getLevelCap(SkillType skill) {
        int cap = config.getInt("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Level_Cap");
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    /* PVP & PVE Settings */
    public boolean getPVPEnabled(SkillType skill) { return config.getBoolean("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVP", true); }
    public boolean getPVEEnabled(SkillType skill) { return config.getBoolean("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVE", true); }

    /*
     * XP SETTINGS
     */

    /* General Settings */
    public boolean getExperienceGainsPlayerVersusPlayerEnabled() { return config.getBoolean("Experience.PVP.Rewards", true); }

    public double getExperienceGainsGlobalMultiplier() { return config.getDouble("Experience.Gains.Multiplier.Global", 1.0); }
    public void setExperienceGainsGlobalMultiplier(double value) { config.set("Experience.Gains.Multiplier.Global", value); }

    /* Combat XP Multipliers */
    public double getPlayerVersusPlayerXP() { return config.getDouble("Experience.Gains.Multiplier.PVP", 1.0); }
    public double getCombatXP(EntityType entity) { return config.getDouble("Experience.Combat.Multiplier." + StringUtils.getPrettyEntityTypeString(entity).replace(" ", "_")); }

    public double getAnimalsXP() { return config.getDouble("Experience.Combat.Multiplier.Animals", 1.0); }
    public double getWitherSkeletonXP() { return config.getDouble("Experience.Combat.Multiplier.Wither_Skeleton", 4.0); }

    public double getSpawnedMobXpMultiplier() { return config.getDouble("Experience.Gains.MobSpawners.Multiplier", 0.0); }

    /* XP Formula Multiplier */
    public int getFormulaMultiplierCurve() { return config.getInt("Experience.Formula.Curve_Modifier", 20); }
    public double getForumulaMultiplier(SkillType skill) { return config.getDouble("Experience.Formula.Modifier." + StringUtils.getCapitalized(skill.toString())); }
}
