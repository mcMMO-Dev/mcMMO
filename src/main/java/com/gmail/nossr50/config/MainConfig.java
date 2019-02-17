package com.gmail.nossr50.config;

import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainConfig extends ConfigValidated {

    public MainConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "config.yml", true);
        super(mcMMO.p.getDataFolder().getAbsoluteFile(), "config.yml", true);
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static MainConfig getInstance() {
        return mcMMO.getConfigManager().getMainConfig();
    }

    @Override
    public void unload() {
        //do nothing
    }

    /**
     * The version of this config
     *
     * @return
     */
    @Override
    public double getConfigVersion() {
        return 1;
    }

    @Override
    public List<String> validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<String>();

        /* General Settings */
        if (getSaveInterval() <= 0) {
            reason.add("General.Save_Interval should be greater than 0!");
        }

        /* MySQL Settings */
        for (SQLDatabaseManager.PoolIdentifier identifier : SQLDatabaseManager.PoolIdentifier.values()) {
            if (getMySQLMaxConnections(identifier) <= 0) {
                reason.add("MySQL.Database.MaxConnections." + StringUtils.getCapitalized(identifier.toString()) + " should be greater than 0!");
            }
            if (getMySQLMaxPoolSize(identifier) <= 0) {
                reason.add("MySQL.Database.MaxPoolSize." + StringUtils.getCapitalized(identifier.toString()) + " should be greater than 0!");
            }
        }

        /* Mob Healthbar */
        if (getMobHealthbarTime() == 0) {
            reason.add("Mob_Healthbar.Display_Time cannot be 0! Set to -1 to disable or set a valid value.");
        }

        /* Scoreboards */
        /*if (getRankScoreboardTime() != -1 && getRankScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Rank.Display_Time should be greater than 0, or -1!");
        }

        if (getStatsScoreboardTime() != -1 && getStatsScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Stats.Display_Time should be greater than 0, or -1!");
        }

        if (getTopScoreboardTime() != -1 && getTopScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Top.Display_Time should be greater than 0, or -1!");
        }

        if (getInspectScoreboardTime() != -1 && getInspectScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Inspect.Display_Time should be greater than 0, or -1!");
        }

        if (getSkillScoreboardTime() != -1 && getSkillScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Skill.Display_Time should be greater than 0, or -1!");
        }

        if (getSkillLevelUpTime() != -1 && getSkillScoreboardTime() <= 0) {
            reason.add("Scoreboard.Types.Skill.Display_Time should be greater than 0, or -1!");
        }

        if (!(getRankUseChat() || getRankUseBoard())) {
            reason.add("Either Board or Print in Scoreboard.Types.Rank must be true!");
        }

        if (!(getTopUseChat() || getTopUseBoard())) {
            reason.add("Either Board or Print in Scoreboard.Types.Top must be true!");
        }

        if (!(getStatsUseChat() || getStatsUseBoard())) {
            reason.add("Either Board or Print in Scoreboard.Types.Stats must be true!");
        }

        if (!(getInspectUseChat() || getInspectUseBoard())) {
            reason.add("Either Board or Print in Scoreboard.Types.Inspect must be true!");
        }*/

        /* Database Purging */
        if (getPurgeInterval() < -1) {
            reason.add("Database_Purging.Purge_Interval should be greater than, or equal to -1!");
        }

        if (getOldUsersCutoff() != -1 && getOldUsersCutoff() <= 0) {
            reason.add("Database_Purging.Old_User_Cutoff should be greater than 0 or -1!");
        }

        /* Hardcore Mode */
        if (getHardcoreDeathStatPenaltyPercentage() < 0.01 || getHardcoreDeathStatPenaltyPercentage() > 100) {
            reason.add("Hardcore.Death_Stat_Loss.Penalty_Percentage only accepts values from 0.01 to 100!");
        }

        if (getHardcoreVampirismStatLeechPercentage() < 0.01 || getHardcoreVampirismStatLeechPercentage() > 100) {
            reason.add("Hardcore.Vampirism.Leech_Percentage only accepts values from 0.01 to 100!");
        }

        /* Items */
        if (getChimaeraUseCost() < 1 || getChimaeraUseCost() > 64) {
            reason.add("Items.Chimaera_Wing.Use_Cost only accepts values from 1 to 64!");
        }

        if (getChimaeraRecipeCost() < 1 || getChimaeraRecipeCost() > 9) {
            reason.add("Items.Chimaera_Wing.Recipe_Cost only accepts values from 1 to 9!");
        }

        if (getChimaeraItem() == null) {
            reason.add("Items.Chimaera_Wing.Item_Name is invalid!");
        }

        /* Particles */
        if (getLevelUpEffectsTier() < 1) {
            reason.add("Particles.LevelUp_Tier should be at least 1!");
        }

        /* PARTY SETTINGS */
        if (getAutoPartyKickInterval() < -1) {
            reason.add("Party.AutoKick_Interval should be at least -1!");
        }

        if (getAutoPartyKickTime() < 0) {
            reason.add("Party.Old_Party_Member_Cutoff should be at least 0!");
        }

        if (getPartyShareBonusBase() <= 0) {
            reason.add("Party.Sharing.ExpShare_bonus_base should be greater than 0!");
        }

        if (getPartyShareBonusIncrease() < 0) {
            reason.add("Party.Sharing.ExpShare_bonus_increase should be at least 0!");
        }

        if (getPartyShareBonusCap() <= 0) {
            reason.add("Party.Sharing.ExpShare_bonus_cap should be greater than 0!");
        }

        if (getPartyShareRange() <= 0) {
            reason.add("Party.Sharing.Range should be greater than 0!");
        }

        if (getPartyXpCurveMultiplier() < 1) {
            reason.add("Party.Leveling.Xp_Curve_Modifier should be at least 1!");
        }

        for (PartyFeature partyFeature : PartyFeature.values()) {
            if (getPartyFeatureUnlockLevel(partyFeature) < 0) {
                reason.add("Party.Leveling." + StringUtils.getPrettyPartyFeatureString(partyFeature).replace(" ", "") + "_UnlockLevel should be at least 0!");
            }
        }

        /* Inspect command distance */
        if (getInspectDistance() <= 0) {
            reason.add("Commands.inspect.Max_Distance should be greater than 0!");
        }

        if (getTreeFellerThreshold() <= 0) {
            reason.add("Abilities.Limits.Tree_Feller_Threshold should be greater than 0!");
        }

        if (getFishingLureModifier() < 0) {
            reason.add("Abilities.Fishing.Lure_Modifier should be at least 0!");
        }

        if (getDetonatorItem() == null) {
            reason.add("Skills.Mining.Detonator_Item is invalid!");
        }

        if (getRepairAnvilMaterial() == null) {
            reason.add("Skills.Repair.Anvil_Type is invalid!!");
        }

        if (getSalvageAnvilMaterial() == null) {
            reason.add("Skills.Repair.Salvage_Anvil_Type is invalid!");
        }

        if (getRepairAnvilMaterial() == getSalvageAnvilMaterial()) {
            reason.add("Cannot use the same item for Repair and Salvage anvils!");
        }

        if (getTamingCOTWMaterial(EntityType.WOLF) == null) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Wolf.Item_Material is invalid!!");
        }

        if (getTamingCOTWMaterial(EntityType.OCELOT) == null) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Ocelot.Item_Material is invalid!!");
        }

        if (getTamingCOTWMaterial(EntityType.HORSE) == null) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Horse.Item_Material is invalid!!");
        }

        if (getTamingCOTWCost(EntityType.WOLF) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Wolf.Item_Amount should be greater than 0!");
        }

        if (getTamingCOTWCost(EntityType.OCELOT) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Ocelot.Item_Amount should be greater than 0!");
        }

        if (getTamingCOTWCost(EntityType.HORSE) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Horse.Item_Amount should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.WOLF) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Wolf.Summon_Amount should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.OCELOT) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Ocelot.Summon_Amount should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.HORSE) <= 0) {
            reason.add("Skills.Taming.Call_Of_The_Wild.Horse.Summon_Amount should be greater than 0!");
        }

        return reason;
    }

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public boolean getIsMetricsEnabled() {
        return getBooleanValue("Metrics.bstats", true);
    }

    //Retro mode will default the value to true if the config file doesn't contain the entry (server is from a previous mcMMO install)
    public boolean getIsRetroMode() {
        return getBooleanValue("General.RetroMode.Enabled", true);
    }

    public String getLocale() {
        return getStringValue("General.Locale", "en_us");
    }

    public boolean getMOTDEnabled() {
        return getBooleanValue("General.MOTD_Enabled", true);
    }

    public boolean getShowProfileLoadedMessage() {
        return getBooleanValue("General.Show_Profile_Loaded", true);
    }

    public boolean getDonateMessageEnabled() {
        return getBooleanValue("Commands.mcmmo.Donate_Message", true);
    }

    public int getSaveInterval() {
        return getIntValue("General.Save_Interval", 10);
    }

    public boolean getStatsTrackingEnabled() {
        return getBooleanValue("General.Stats_Tracking", true);
    }

    public boolean getUpdateCheckEnabled() {
        return getBooleanValue("General.Update_Check", true);
    }

    public boolean getPreferBeta() {
        return getBooleanValue("General.Prefer_Beta", false);
    }

    public boolean getVerboseLoggingEnabled() {
        return getBooleanValue("General.Verbose_Logging", false);
    }

    public String getPartyChatPrefix() {
        return getStringValue("Commands.partychat.Chat_Prefix_Format", "[[GREEN]]([[WHITE]]{0}[[GREEN]])");
    }

    public boolean getPartyChatColorLeaderName() {
        return getBooleanValue("Commands.partychat.Gold_Leader_Name", true);
    }

    public boolean getPartyDisplayNames() {
        return getBooleanValue("Commands.partychat.Use_Display_Names", true);
    }

    public String getPartyChatPrefixAlly() {
        return getStringValue("Commands.partychat.Chat_Prefix_Format_Ally", "[[GREEN]](A)[[RESET]]");
    }

    public String getAdminChatPrefix() {
        return getStringValue("Commands.adminchat.Chat_Prefix_Format", "[[AQUA]][[[WHITE]]{0}[[AQUA]]]");
    }

    public boolean getAdminDisplayNames() {
        return getBooleanValue("Commands.adminchat.Use_Display_Names", true);
    }

    public boolean getMatchOfflinePlayers() {
        return getBooleanValue("Commands.Generic.Match_OfflinePlayers", false);
    }

    public long getDatabasePlayerCooldown() {
        return config.getLong("Commands.Database.Player_Cooldown", 1750);
    }

    public boolean getLevelUpSoundsEnabled() {
        return getBooleanValue("General.LevelUp_Sounds", true);
    }

    public boolean getRefreshChunksEnabled() {
        return getBooleanValue("General.Refresh_Chunks", false);
    }

    public boolean getMobHealthbarEnabled() {
        return getBooleanValue("Mob_Healthbar.Enabled", true);
    }

    /* Mob Healthbar */
    public MobHealthbarType getMobHealthbarDefault() {
        try {
            return MobHealthbarType.valueOf(getStringValue("Mob_Healthbar.Display_Type", "HEARTS").toUpperCase().trim());
        } catch (IllegalArgumentException ex) {
            return MobHealthbarType.HEARTS;
        }
    }

    public int getMobHealthbarTime() {
        return getIntValue("Mob_Healthbar.Display_Time", 3);
    }

    /* Scoreboards */
    public boolean getScoreboardsEnabled() {
        return getBooleanValue("Scoreboard.UseScoreboards", true);
    }

    public boolean getPowerLevelTagsEnabled() {
        return getBooleanValue("Scoreboard.Power_Level_Tags", false);
    }

    public boolean getAllowKeepBoard() {
        return getBooleanValue("Scoreboard.Allow_Keep", true);
    }

    public int getTipsAmount() {
        return getIntValue("Scoreboard.Tips_Amount", 5);
    }

    public boolean getShowStatsAfterLogin() {
        return getBooleanValue("Scoreboard.Show_Stats_After_Login", false);
    }

    public boolean getScoreboardRainbows() {
        return getBooleanValue("Scoreboard.Rainbows", false);
    }

    public boolean getShowAbilityNames() {
        return getBooleanValue("Scoreboard.Ability_Names", true);
    }

    public boolean getRankUseChat() {
        return getBooleanValue("Scoreboard.Types.Rank.Print", false);
    }

    public boolean getRankUseBoard() {
        return getBooleanValue("Scoreboard.Types.Rank.Board", true);
    }

    public int getRankScoreboardTime() {
        return getIntValue("Scoreboard.Types.Rank.Display_Time", 10);
    }

    public boolean getTopUseChat() {
        return getBooleanValue("Scoreboard.Types.Top.Print", true);
    }

    public boolean getTopUseBoard() {
        return getBooleanValue("Scoreboard.Types.Top.Board", true);
    }

    public int getTopScoreboardTime() {
        return getIntValue("Scoreboard.Types.Top.Display_Time", 15);
    }

    public boolean getStatsUseChat() {
        return getBooleanValue("Scoreboard.Types.Stats.Print", true);
    }

    public boolean getStatsUseBoard() {
        return getBooleanValue("Scoreboard.Types.Stats.Board", true);
    }

    public int getStatsScoreboardTime() {
        return getIntValue("Scoreboard.Types.Stats.Display_Time", 10);
    }

    public boolean getInspectUseChat() {
        return getBooleanValue("Scoreboard.Types.Inspect.Print", true);
    }

    public boolean getInspectUseBoard() {
        return getBooleanValue("Scoreboard.Types.Inspect.Board", true);
    }

    public int getInspectScoreboardTime() {
        return getIntValue("Scoreboard.Types.Inspect.Display_Time", 25);
    }

    public boolean getCooldownUseChat() {
        return getBooleanValue("Scoreboard.Types.Cooldown.Print", false);
    }

    public boolean getCooldownUseBoard() {
        return getBooleanValue("Scoreboard.Types.Cooldown.Board", true);
    }

    public int getCooldownScoreboardTime() {
        return getIntValue("Scoreboard.Types.Cooldown.Display_Time", 41);
    }

    public boolean getSkillUseBoard() {
        return getBooleanValue("Scoreboard.Types.Skill.Board", true);
    }

    public int getSkillScoreboardTime() {
        return getIntValue("Scoreboard.Types.Skill.Display_Time", 30);
    }

    public boolean getSkillLevelUpBoard() {
        return getBooleanValue("Scoreboard.Types.Skill.LevelUp_Board", true);
    }

    public int getSkillLevelUpTime() {
        return getIntValue("Scoreboard.Types.Skill.LevelUp_Time", 5);
    }

    /* Database Purging */
    public int getPurgeInterval() {
        return getIntValue("Database_Purging.Purge_Interval", -1);
    }

    public int getOldUsersCutoff() {
        return getIntValue("Database_Purging.Old_User_Cutoff", 6);
    }

    /* Backups */
    public boolean getBackupsEnabled() {
        return getBooleanValue("Backups.Enabled", true);
    }

    public boolean getKeepLast24Hours() {
        return getBooleanValue("Backups.Keep.Last_24_Hours", true);
    }

    public boolean getKeepDailyLastWeek() {
        return getBooleanValue("Backups.Keep.Daily_Last_Week", true);
    }

    public boolean getKeepWeeklyPastMonth() {
        return getBooleanValue("Backups.Keep.Weekly_Past_Months", true);
    }

    /* mySQL */
    public boolean getUseMySQL() {
        return getBooleanValue("MySQL.Enabled", false);
    }

    public String getMySQLTablePrefix() {
        return getStringValue("MySQL.Database.TablePrefix", "mcmmo_");
    }

    public String getMySQLDatabaseName() {
        return getStringIncludingInts("MySQL.Database.Name");
    }

    public String getMySQLUserName() {
        return getStringIncludingInts("MySQL.Database.User_Name");
    }

    public int getMySQLServerPort() {
        return getIntValue("MySQL.Server.Port", 3306);
    }

    public String getMySQLServerName() {
        return getStringValue("MySQL.Server.Address", "localhost");
    }

    public String getMySQLUserPassword() {
        return getStringIncludingInts("MySQL.Database.User_Password");
    }

    public int getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier identifier) {
        return getIntValue("MySQL.Database.MaxConnections." + StringUtils.getCapitalized(identifier.toString()), 30);
    }

    public int getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier identifier) {
        return getIntValue("MySQL.Database.MaxPoolSize." + StringUtils.getCapitalized(identifier.toString()), 10);
    }

    public boolean getMySQLSSL() {
        return getBooleanValue("MySQL.Server.SSL", true);
    }

    private String getStringIncludingInts(String key) {
        String str = getStringValue(key);

        if (str == null) {
            str = String.valueOf(getIntValue(key));
        }

        if (str.equals("0")) {
            str = "No value set for '" + key + "'";
        }
        return str;
    }

    /* Hardcore Mode */
    public boolean getHardcoreStatLossEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue("Hardcore.Death_Stat_Loss.Enabled." + StringUtils.getCapitalized(primarySkillType.toString()), false);
    }

    public void setHardcoreStatLossEnabled(PrimarySkillType primarySkillType, boolean enabled) {
        config.set("Hardcore.Death_Stat_Loss.Enabled." + StringUtils.getCapitalized(primarySkillType.toString()), enabled);
    }

    public double getHardcoreDeathStatPenaltyPercentage() {
        return getDoubleValue("Hardcore.Death_Stat_Loss.Penalty_Percentage", 75.0D);
    }

    public void setHardcoreDeathStatPenaltyPercentage(double value) {
        config.set("Hardcore.Death_Stat_Loss.Penalty_Percentage", value);
    }

    public int getHardcoreDeathStatPenaltyLevelThreshold() {
        return getIntValue("Hardcore.Death_Stat_Loss.Level_Threshold", 0);
    }

    public boolean getHardcoreVampirismEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue("Hardcore.Vampirism.Enabled." + StringUtils.getCapitalized(primarySkillType.toString()), false);
    }

    public void setHardcoreVampirismEnabled(PrimarySkillType primarySkillType, boolean enabled) {
        config.set("Hardcore.Vampirism.Enabled." + StringUtils.getCapitalized(primarySkillType.toString()), enabled);
    }

    public double getHardcoreVampirismStatLeechPercentage() {
        return getDoubleValue("Hardcore.Vampirism.Leech_Percentage", 5.0D);
    }

    public void setHardcoreVampirismStatLeechPercentage(double value) {
        config.set("Hardcore.Vampirism.Leech_Percentage", value);
    }

    public int getHardcoreVampirismLevelThreshold() {
        return getIntValue("Hardcore.Vampirism.Level_Threshold", 0);
    }

    /* SMP Mods */
    public boolean getToolModsEnabled() {
        return getBooleanValue("Mods.Tool_Mods_Enabled", false);
    }

    public boolean getArmorModsEnabled() {
        return getBooleanValue("Mods.Armor_Mods_Enabled", false);
    }

    public boolean getBlockModsEnabled() {
        return getBooleanValue("Mods.Block_Mods_Enabled", false);
    }

    public boolean getEntityModsEnabled() {
        return getBooleanValue("Mods.Entity_Mods_Enabled", false);
    }

    /* Items */
    public int getChimaeraUseCost() {
        return getIntValue("Items.Chimaera_Wing.Use_Cost", 1);
    }

    public int getChimaeraRecipeCost() {
        return getIntValue("Items.Chimaera_Wing.Recipe_Cost", 5);
    }

    public Material getChimaeraItem() {
        return Material.matchMaterial(getStringValue("Items.Chimaera_Wing.Item_Name", "Feather"));
    }

    public boolean getChimaeraEnabled() {
        return getBooleanValue("Items.Chimaera_Wing.Enabled", true);
    }

    public boolean getChimaeraPreventUseUnderground() {
        return getBooleanValue("Items.Chimaera_Wing.Prevent_Use_Underground", true);
    }

    public boolean getChimaeraUseBedSpawn() {
        return getBooleanValue("Items.Chimaera_Wing.Use_Bed_Spawn", true);
    }

    public int getChimaeraCooldown() {
        return getIntValue("Items.Chimaera_Wing.Cooldown", 240);
    }

    public int getChimaeraWarmup() {
        return getIntValue("Items.Chimaera_Wing.Warmup", 5);
    }

    public int getChimaeraRecentlyHurtCooldown() {
        return getIntValue("Items.Chimaera_Wing.RecentlyHurt_Cooldown", 60);
    }

    public boolean getChimaeraSoundEnabled() {
        return getBooleanValue("Items.Chimaera_Wing.Sound_Enabled", true);
    }

    public boolean getFluxPickaxeSoundEnabled() {
        return getBooleanValue("Items.Flux_Pickaxe.Sound_Enabled", true);
    }

    /* Particles */
    public boolean getAbilityActivationEffectEnabled() {
        return getBooleanValue("Particles.Ability_Activation", true);
    }

    public boolean getAbilityDeactivationEffectEnabled() {
        return getBooleanValue("Particles.Ability_Deactivation", true);
    }

    public boolean getBleedEffectEnabled() {
        return getBooleanValue("Particles.Bleed", true);
    }

    public boolean getDodgeEffectEnabled() {
        return getBooleanValue("Particles.Dodge", true);
    }

    public boolean getFluxEffectEnabled() {
        return getBooleanValue("Particles.Flux", true);
    }

    public boolean getGreaterImpactEffectEnabled() {
        return getBooleanValue("Particles.Greater_Impact", true);
    }

    public boolean getCallOfTheWildEffectEnabled() {
        return getBooleanValue("Particles.Call_of_the_Wild", true);
    }

    public boolean getLevelUpEffectsEnabled() {
        return getBooleanValue("Particles.LevelUp_Enabled", true);
    }

    public int getLevelUpEffectsTier() {
        return getIntValue("Particles.LevelUp_Tier", 100);
    }

    public boolean getLargeFireworks() {
        return getBooleanValue("Particles.LargeFireworks", true);
    }

    /* PARTY SETTINGS */
    public boolean getPartyFriendlyFire() {
        return getBooleanValue("Party.FriendlyFire", false);
    }

    public int getPartyMaxSize() {
        return getIntValue("Party.MaxSize", -1);
    }

    public int getAutoPartyKickInterval() {
        return getIntValue("Party.AutoKick_Interval", 12);
    }

    public int getAutoPartyKickTime() {
        return getIntValue("Party.Old_Party_Member_Cutoff", 7);
    }

    public double getPartyShareBonusBase() {
        return getDoubleValue("Party.Sharing.ExpShare_bonus_base", 1.1D);
    }

    public double getPartyShareBonusIncrease() {
        return getDoubleValue("Party.Sharing.ExpShare_bonus_increase", 0.05D);
    }

    public double getPartyShareBonusCap() {
        return getDoubleValue("Party.Sharing.ExpShare_bonus_cap", 1.5D);
    }

    public double getPartyShareRange() {
        return getDoubleValue("Party.Sharing.Range", 75.0D);
    }

    public int getPartyLevelCap() {
        int cap = getIntValue("Party.Leveling.Level_Cap", 10);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    public int getPartyXpCurveMultiplier() {
        return getIntValue("Party.Leveling.Xp_Curve_Modifier", 3);
    }

    public boolean getPartyXpNearMembersNeeded() {
        return getBooleanValue("Party.Leveling.Near_Members_Needed", false);
    }

    public boolean getPartyInformAllMembers() {
        return getBooleanValue("Party.Leveling.Inform_All_Party_Members_On_LevelUp", false);
    }

    public int getPartyFeatureUnlockLevel(PartyFeature partyFeature) {
        return getIntValue("Party.Leveling." + StringUtils.getPrettyPartyFeatureString(partyFeature).replace(" ", "") + "_UnlockLevel", 0);
    }

    /* Party Teleport Settings */
    public int getPTPCommandCooldown() {
        return getIntValue("Commands.ptp.Cooldown", 120);
    }

    public int getPTPCommandWarmup() {
        return getIntValue("Commands.ptp.Warmup", 5);
    }

    public int getPTPCommandRecentlyHurtCooldown() {
        return getIntValue("Commands.ptp.RecentlyHurt_Cooldown", 60);
    }

    public int getPTPCommandTimeout() {
        return getIntValue("Commands.ptp.Request_Timeout", 300);
    }

    public boolean getPTPCommandConfirmRequired() {
        return getBooleanValue("Commands.ptp.Accept_Required", true);
    }

    public boolean getPTPCommandWorldPermissions() {
        return getBooleanValue("Commands.ptp.World_Based_Permissions", false);
    }

    /* Inspect command distance */
    public double getInspectDistance() {
        return getDoubleValue("Commands.inspect.Max_Distance", 30.0D);
    }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public boolean getUrlLinksEnabled() {
        return getBooleanValue("Commands.Skills.URL_Links");
    }

    public boolean getAbilityMessagesEnabled() {
        return getBooleanValue("Abilities.Messages", true);
    }

    public boolean getAbilitiesEnabled() {
        return getBooleanValue("Abilities.Enabled", true);
    }

    public boolean getAbilitiesOnlyActivateWhenSneaking() {
        return getBooleanValue("Abilities.Activation.Only_Activate_When_Sneaking", false);
    }

    public boolean getAbilitiesGateEnabled() {
        return getBooleanValue("Abilities.Activation.Level_Gate_Abilities");
    }

    public int getCooldown(SuperAbilityType ability) {
        return getIntValue("Abilities.Cooldowns." + ability.toString());
    }

    public int getMaxLength(SuperAbilityType ability) {
        return getIntValue("Abilities.Max_Seconds." + ability.toString());
    }

    /* Durability Settings */
    public int getAbilityToolDamage() {
        return getIntValue("Abilities.Tools.Durability_Loss", 1);
    }

    /* Thresholds */
    public int getTreeFellerThreshold() {
        return getIntValue("Abilities.Limits.Tree_Feller_Threshold", 500);
    }

    /*
     * SKILL SETTINGS
     */
    public boolean getDoubleDropsEnabled(PrimarySkillType skill, Material material) {
        return getBooleanValue("Double_Drops." + StringUtils.getCapitalized(skill.toString()) + "." + StringUtils.getPrettyItemString(material).replace(" ", "_"));
    }

    public boolean getDoubleDropsDisabled(PrimarySkillType skill) {
        String skillName = StringUtils.getCapitalized(skill.toString());
        ConfigurationSection section = config.getConfigurationSection("Double_Drops." + skillName);
        if (section == null)
            return false;
        Set<String> keys = section.getKeys(false);
        boolean disabled = true;

        for (String key : keys) {
            if (getBooleanValue("Double_Drops." + skillName + "." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    /* Axes */
    public int getAxesGate() {
        return getIntValue("Skills.Axes.Ability_Activation_Level_Gate", 10);
    }

    /* Acrobatics */
    public boolean getDodgeLightningDisabled() {
        return getBooleanValue("Skills.Acrobatics.Prevent_Dodge_Lightning", false);
    }

    public int getXPAfterTeleportCooldown() {
        return getIntValue("Skills.Acrobatics.XP_After_Teleport_Cooldown", 5);
    }

    /* Alchemy */
    public boolean getEnabledForHoppers() {
        return getBooleanValue("Skills.Alchemy.Enabled_for_Hoppers", true);
    }

    public boolean getPreventHopperTransferIngredients() {
        return getBooleanValue("Skills.Alchemy.Prevent_Hopper_Transfer_Ingredients", false);
    }

    public boolean getPreventHopperTransferBottles() {
        return getBooleanValue("Skills.Alchemy.Prevent_Hopper_Transfer_Bottles", false);
    }

    /* Fishing */
    public boolean getFishingDropsEnabled() {
        return getBooleanValue("Skills.Fishing.Drops_Enabled", true);
    }

    public boolean getFishingOverrideTreasures() {
        return getBooleanValue("Skills.Fishing.Override_Vanilla_Treasures", true);
    }

    public boolean getFishingExtraFish() {
        return getBooleanValue("Skills.Fishing.Extra_Fish", true);
    }

    public double getFishingLureModifier() {
        return getDoubleValue("Skills.Fishing.Lure_Modifier", 4.0D);
    }

    /* Mining */
    public Material getDetonatorItem() {
        return Material.matchMaterial(getStringValue("Skills.Mining.Detonator_Name", "FLINT_AND_STEEL"));
    }

    public int getMiningGate() {
        return getIntValue("Skills.Mining.Ability_Activation_Level_Gate", 10);
    }

    /* Excavation */
    public int getExcavationGate() {
        return getIntValue("Skills.Excavation.Ability_Activation_Level_Gate", 10);
    }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() {
        return getBooleanValue("Skills.Repair.Anvil_Messages", true);
    }

    public boolean getRepairAnvilPlaceSoundsEnabled() {
        return getBooleanValue("Skills.Repair.Anvil_Placed_Sounds", true);
    }

    public boolean getRepairAnvilUseSoundsEnabled() {
        return getBooleanValue("Skills.Repair.Anvil_Use_Sounds", true);
    }

    public Material getRepairAnvilMaterial() {
        return Material.matchMaterial(getStringValue("Skills.Repair.Anvil_Material", "IRON_BLOCK"));
    }

    public boolean getRepairConfirmRequired() {
        return getBooleanValue("Skills.Repair.Confirm_Required", true);
    }

    /* Salvage */
    public boolean getSalvageAnvilMessagesEnabled() {
        return getBooleanValue("Skills.Salvage.Anvil_Messages", true);
    }

    public boolean getSalvageAnvilPlaceSoundsEnabled() {
        return getBooleanValue("Skills.Salvage.Anvil_Placed_Sounds", true);
    }

    public boolean getSalvageAnvilUseSoundsEnabled() {
        return getBooleanValue("Skills.Salvage.Anvil_Use_Sounds", true);
    }

    public Material getSalvageAnvilMaterial() {
        return Material.matchMaterial(getStringValue("Skills.Salvage.Anvil_Material", "GOLD_BLOCK"));
    }

    public boolean getSalvageConfirmRequired() {
        return getBooleanValue("Skills.Salvage.Confirm_Required", true);
    }

    /* Unarmed */
    public boolean getUnarmedBlockCrackerSmoothbrickToCracked() {
        return getBooleanValue("Skills.Unarmed.Block_Cracker.SmoothBrick_To_CrackedBrick", true);
    }

    public boolean getUnarmedItemPickupDisabled() {
        return getBooleanValue("Skills.Unarmed.Item_Pickup_Disabled_Full_Inventory", true);
    }

    public boolean getUnarmedItemsAsUnarmed() {
        return getBooleanValue("Skills.Unarmed.Items_As_Unarmed", false);
    }

    public int getUnarmedGate() {
        return getIntValue("Skills.Unarmed.Ability_Activation_Level_Gate", 10);
    }

    /* Swords */
    public int getSwordsGate() {
        return getIntValue("Skills.Swords.Ability_Activation_Level_Gate", 10);
    }

    /* Taming */
    public Material getTamingCOTWMaterial(EntityType type) {
        return Material.matchMaterial(getStringValue("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Item_Material"));
    }

    public int getTamingCOTWCost(EntityType type) {
        return getIntValue("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Item_Amount");
    }

    public int getTamingCOTWAmount(EntityType type) {
        return getIntValue("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Summon_Amount");
    }

    public int getTamingCOTWLength(EntityType type) {
        return getIntValue("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Summon_Length");
    }

    public int getTamingCOTWMaxAmount(EntityType type) {
        return getIntValue("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Summon_Max_Amount");
    }

    public double getTamingCOTWRange() {
        return getDoubleValue("Skills.Taming.Call_Of_The_Wild.Range", 40.0D);
    }

    /* Woodcutting */
    public boolean getWoodcuttingDoubleDropsEnabled(BlockData material) {
        return getBooleanValue("Double_Drops.Woodcutting." + StringUtils.getFriendlyConfigBlockDataString(material));
    }

    public boolean getTreeFellerSoundsEnabled() {
        return getBooleanValue("Skills.Woodcutting.Tree_Feller_Sounds", true);
    }

    public int getWoodcuttingGate() {
        return getIntValue("Skills.Woodcutting.Ability_Activation_Level_Gate", 10);
    }

    /* AFK Leveling */
    public boolean getAcrobaticsPreventAFK() {
        return getBooleanValue("Skills.Acrobatics.Prevent_AFK_Leveling", true);
    }

    public int getAcrobaticsAFKMaxTries() {
        return getIntValue("Skills.Acrobatics.Max_Tries_At_Same_Location", 3);
    }

    public boolean getHerbalismPreventAFK() {
        return getBooleanValue("Skills.Herbalism.Prevent_AFK_Leveling", true);
    }

    /* Level Caps */
    public int getPowerLevelCap() {
        int cap = getIntValue("General.Power_Level_Cap", 0);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    public int getLevelCap(PrimarySkillType skill) {
        int cap = getIntValue("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Level_Cap");
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }


    /*public int isSuperAbilityUnlocked(PrimarySkillType skill) {
        return getIntValue("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Ability_Activation_Level_Gate");
    }*/

    public boolean getTruncateSkills() {
        return getBooleanValue("General.TruncateSkills", false);
    }

    /* PVP & PVE Settings */
    public boolean getPVPEnabled(PrimarySkillType skill) {
        return getBooleanValue("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVP", true);
    }

    public boolean getPVEEnabled(PrimarySkillType skill) {
        return getBooleanValue("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVE", true);
    }

    //public float getMasterVolume() { return (float) getDoubleValue("Sounds.MasterVolume", 1.0); }
}
