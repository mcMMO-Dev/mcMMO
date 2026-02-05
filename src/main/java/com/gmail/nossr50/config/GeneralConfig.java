package com.gmail.nossr50.config;

import static com.gmail.nossr50.util.text.ConfigStringUtils.getConfigPartyFeatureString;
import static com.gmail.nossr50.util.text.ConfigStringUtils.getMaterialConfigString;

import com.gmail.nossr50.database.SQLDatabaseManager.PoolIdentifier;
import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.text.StringUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneralConfig extends BukkitConfig {

    public GeneralConfig(@NotNull File dataFolder) {
        super("config.yml", dataFolder);
        validate();
    }

    @Override
    protected void loadKeys() {

    }

    @Override
    protected boolean validateKeys() {
        // Validate all the settings!
        List<String> reason = new ArrayList<>();

        /* General Settings */
        if (getSaveInterval() <= 0) {
            reason.add("General.Save_Interval should be greater than 0!");
        }

        /* MySQL Settings */
        for (PoolIdentifier identifier : PoolIdentifier.values()) {
            if (getMySQLMaxConnections(identifier) <= 0) {
                reason.add("MySQL.Database.MaxConnections." + StringUtils.getCapitalized(
                        identifier.toString()) + " should be greater than 0!");
            }
            if (getMySQLMaxPoolSize(identifier) <= 0) {
                reason.add("MySQL.Database.MaxPoolSize." + StringUtils.getCapitalized(
                        identifier.toString()) + " should be greater than 0!");
            }
        }

        /* Mob Healthbar */
        if (getMobHealthbarTime() == 0) {
            reason.add(
                    "Mob_Healthbar.Display_Time cannot be 0! Set to -1 to disable or set a valid value.");
        }

        /* Database Purging */
        if (getPurgeInterval() < -1) {
            reason.add("Database_Purging.Purge_Interval should be greater than, or equal to -1!");
        }

        if (getOldUsersCutoff() != -1 && getOldUsersCutoff() <= 0) {
            reason.add("Database_Purging.Old_User_Cutoff should be greater than 0 or -1!");
        }

        /* Hardcore Mode */
        if (getHardcoreDeathStatPenaltyPercentage() < 0.01
                || getHardcoreDeathStatPenaltyPercentage() > 100) {
            reason.add(
                    "Hardcore.Death_Stat_Loss.Penalty_Percentage only accepts values from 0.01 to 100!");
        }

        if (getHardcoreVampirismStatLeechPercentage() < 0.01
                || getHardcoreVampirismStatLeechPercentage() > 100) {
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
                reason.add("Party.Leveling." + getConfigPartyFeatureString(
                        partyFeature) + "_UnlockLevel should be at least 0!");
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

        if (getRepairAnvilMaterial() == null) {
            reason.add("Skills.Repair.Anvil_Type is invalid!!");
        }

        if (getSalvageAnvilMaterial() == null) {
            reason.add("Skills.Repair.Salvage_Anvil_Type is invalid!");
        }

        if (getRepairAnvilMaterial() == getSalvageAnvilMaterial()) {
            reason.add("Cannot use the same item for Repair and Salvage anvils!");
        }

        return noErrorsInConfig(reason);
    }

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public boolean getIsMetricsEnabled() {
        return config.getBoolean("Metrics.bstats", true);
    }

    //Retro mode will default the value to true if the config file doesn't contain the entry (server is from a previous mcMMO install)
    public boolean getIsRetroMode() {
        return config.getBoolean("General.RetroMode.Enabled", true);
    }

    public String getLocale() {
        return config.getString("General.Locale", "en_US");
    }

    public boolean getMOTDEnabled() {
        return config.getBoolean("General.MOTD_Enabled", true);
    }

    public boolean getShowProfileLoadedMessage() {
        return config.getBoolean("General.Show_Profile_Loaded", true);
    }

    public boolean getDonateMessageEnabled() {
        return config.getBoolean("Commands.mcmmo.Donate_Message", true);
    }

    public int getSaveInterval() {
        return config.getInt("General.Save_Interval", 10);
    }

    public boolean getStatsTrackingEnabled() {
        return config.getBoolean("General.Stats_Tracking", true);
    }

    public boolean getUpdateCheckEnabled() {
        return config.getBoolean("General.Update_Check", true);
    }

    public boolean getPreferBeta() {
        return config.getBoolean("General.Prefer_Beta", false);
    }

    public boolean getVerboseLoggingEnabled() {
        return config.getBoolean("General.Verbose_Logging", false);
    }


    public boolean getMatchOfflinePlayers() {
        return config.getBoolean("Commands.Generic.Match_OfflinePlayers", false);
    }

    public long getDatabasePlayerCooldown() {
        return config.getLong("Commands.Database.Player_Cooldown", 1750);
    }

    public boolean getLevelUpSoundsEnabled() {
        return config.getBoolean("General.LevelUp_Sounds", true);
    }

    public boolean getRefreshChunksEnabled() {
        return config.getBoolean("General.Refresh_Chunks", false);
    }

    public boolean getMobHealthbarEnabled() {
        return config.getBoolean("Mob_Healthbar.Enabled", true);
    }

    /* Mob Healthbar */
    public MobHealthbarType getMobHealthbarDefault() {
        try {
            return MobHealthbarType.valueOf(config.getString("Mob_Healthbar.Display_Type", "HEARTS")
                    .toUpperCase(Locale.ENGLISH)
                    .trim());
        } catch (IllegalArgumentException ex) {
            return MobHealthbarType.HEARTS;
        }
    }

    public int getMobHealthbarTime() {
        return Math.max(1, config.getInt("Mob_Healthbar.Display_Time", 3));
    }

    /* Scoreboards */
    public boolean getScoreboardsEnabled() {
        return config.getBoolean("Scoreboard.UseScoreboards", true);
    }

    public boolean getPowerLevelTagsEnabled() {
        return config.getBoolean("Scoreboard.Power_Level_Tags", false);
    }

    public boolean getAllowKeepBoard() {
        return config.getBoolean("Scoreboard.Allow_Keep", true);
    }

    public int getTipsAmount() {
        return config.getInt("Scoreboard.Tips_Amount", 5);
    }

    public boolean getShowStatsAfterLogin() {
        return config.getBoolean("Scoreboard.Show_Stats_After_Login", false);
    }

    public boolean getScoreboardRainbows() {
        return config.getBoolean("Scoreboard.Rainbows", false);
    }

    public boolean getShowAbilityNames() {
        return config.getBoolean("Scoreboard.Ability_Names", true);
    }

    public boolean getRankUseChat() {
        return config.getBoolean("Scoreboard.Types.Rank.Print", false);
    }

    public boolean getRankUseBoard() {
        return config.getBoolean("Scoreboard.Types.Rank.Board", true);
    }

    public int getRankScoreboardTime() {
        return config.getInt("Scoreboard.Types.Rank.Display_Time", 10);
    }

    public boolean getTopUseChat() {
        return config.getBoolean("Scoreboard.Types.Top.Print", true);
    }

    public boolean getTopUseBoard() {
        return config.getBoolean("Scoreboard.Types.Top.Board", true);
    }

    public int getTopScoreboardTime() {
        return config.getInt("Scoreboard.Types.Top.Display_Time", 15);
    }

    public boolean getStatsUseChat() {
        return config.getBoolean("Scoreboard.Types.Stats.Print", true);
    }

    public boolean getStatsUseBoard() {
        return config.getBoolean("Scoreboard.Types.Stats.Board", true);
    }

    public int getStatsScoreboardTime() {
        return config.getInt("Scoreboard.Types.Stats.Display_Time", 10);
    }

    public boolean getInspectUseChat() {
        return config.getBoolean("Scoreboard.Types.Inspect.Print", true);
    }

    public boolean getInspectUseBoard() {
        return config.getBoolean("Scoreboard.Types.Inspect.Board", true);
    }

    public int getInspectScoreboardTime() {
        return config.getInt("Scoreboard.Types.Inspect.Display_Time", 25);
    }

    public boolean getCooldownUseChat() {
        return config.getBoolean("Scoreboard.Types.Cooldown.Print", false);
    }

    public boolean getCooldownUseBoard() {
        return config.getBoolean("Scoreboard.Types.Cooldown.Board", true);
    }

    public int getCooldownScoreboardTime() {
        return config.getInt("Scoreboard.Types.Cooldown.Display_Time", 41);
    }

    public boolean getSkillUseBoard() {
        return config.getBoolean("Scoreboard.Types.Skill.Board", true);
    }

    public int getSkillScoreboardTime() {
        return config.getInt("Scoreboard.Types.Skill.Display_Time", 30);
    }

    public boolean getSkillLevelUpBoard() {
        return config.getBoolean("Scoreboard.Types.Skill.LevelUp_Board", true);
    }

    public int getSkillLevelUpTime() {
        return config.getInt("Scoreboard.Types.Skill.LevelUp_Time", 5);
    }

    /* Database Purging */
    public int getPurgeInterval() {
        return config.getInt("Database_Purging.Purge_Interval", -1);
    }

    public int getOldUsersCutoff() {
        return config.getInt("Database_Purging.Old_User_Cutoff", 6);
    }

    /* Backups */
    public boolean getBackupsEnabled() {
        return config.getBoolean("Backups.Enabled", true);
    }

    public boolean getKeepLast24Hours() {
        return config.getBoolean("Backups.Keep.Last_24_Hours", true);
    }

    public boolean getKeepDailyLastWeek() {
        return config.getBoolean("Backups.Keep.Daily_Last_Week", true);
    }

    public boolean getKeepWeeklyPastMonth() {
        return config.getBoolean("Backups.Keep.Weekly_Past_Months", true);
    }

    /* mySQL */
    public boolean getUseMySQL() {
        return config.getBoolean("MySQL.Enabled", false);
    }

    public String getMySQLTablePrefix() {
        return config.getString("MySQL.Database.TablePrefix", "mcmmo_");
    }

    public String getMySQLDatabaseName() {
        return getStringIncludingInts("MySQL.Database.Name");
    }

    public String getMySQLUserName() {
        return getStringIncludingInts("MySQL.Database.User_Name");
    }

    public int getMySQLServerPort() {
        return config.getInt("MySQL.Server.Port", 3306);
    }

    public String getMySQLServerName() {
        return config.getString("MySQL.Server.Address", "localhost");
    }

    public String getMySQLUserPassword() {
        return getStringIncludingInts("MySQL.Database.User_Password");
    }

    public int getMySQLMaxConnections(PoolIdentifier identifier) {
        return config.getInt("MySQL.Database.MaxConnections." + StringUtils.getCapitalized(
                identifier.toString()), 30);
    }

    public int getMySQLMaxPoolSize(PoolIdentifier identifier) {
        return config.getInt(
                "MySQL.Database.MaxPoolSize." + StringUtils.getCapitalized(identifier.toString()),
                10);
    }

    public boolean getMySQLSSL() {
        return config.getBoolean("MySQL.Server.SSL", true);
    }

    public boolean getMySQLPublicKeyRetrieval() {
        return config.getBoolean("MySQL.Server.allowPublicKeyRetrieval", true);
    }

    private String getStringIncludingInts(String key) {
        String str = config.getString(key);

        if (str == null) {
            str = String.valueOf(config.getInt(key));
        }

        if (str.equals("0")) {
            str = "No value set for '" + key + "'";
        }
        return str;
    }

    /* Hardcore Mode */
    public boolean getHardcoreStatLossEnabled(PrimarySkillType primarySkillType) {
        return config.getBoolean(
                "Hardcore.Death_Stat_Loss.Enabled." + StringUtils.getCapitalized(
                        primarySkillType.toString()), false);
    }

    public void setHardcoreStatLossEnabled(PrimarySkillType primarySkillType, boolean enabled) {
        config.set(
                "Hardcore.Death_Stat_Loss.Enabled." + StringUtils.getCapitalized(
                        primarySkillType.toString()),
                enabled);
    }

    public double getHardcoreDeathStatPenaltyPercentage() {
        return config.getDouble("Hardcore.Death_Stat_Loss.Penalty_Percentage", 75.0D);
    }

    public void setHardcoreDeathStatPenaltyPercentage(double value) {
        config.set("Hardcore.Death_Stat_Loss.Penalty_Percentage", value);
    }

    public int getHardcoreDeathStatPenaltyLevelThreshold() {
        return config.getInt("Hardcore.Death_Stat_Loss.Level_Threshold", 0);
    }

    public boolean getHardcoreVampirismEnabled(PrimarySkillType primarySkillType) {
        return config.getBoolean(
                "Hardcore.Vampirism.Enabled." + StringUtils.getCapitalized(
                        primarySkillType.toString()), false);
    }

    public void setHardcoreVampirismEnabled(PrimarySkillType primarySkillType, boolean enabled) {
        config.set("Hardcore.Vampirism.Enabled." + StringUtils.getCapitalized(
                primarySkillType.toString()), enabled);
    }

    public double getHardcoreVampirismStatLeechPercentage() {
        return config.getDouble("Hardcore.Vampirism.Leech_Percentage", 5.0D);
    }

    public void setHardcoreVampirismStatLeechPercentage(double value) {
        config.set("Hardcore.Vampirism.Leech_Percentage", value);
    }

    public int getHardcoreVampirismLevelThreshold() {
        return config.getInt("Hardcore.Vampirism.Level_Threshold", 0);
    }

    /* SMP Mods */
    public boolean getToolModsEnabled() {
        return config.getBoolean("Mods.Tool_Mods_Enabled", false);
    }

    public boolean getArmorModsEnabled() {
        return config.getBoolean("Mods.Armor_Mods_Enabled", false);
    }

    public boolean getBlockModsEnabled() {
        return config.getBoolean("Mods.Block_Mods_Enabled", false);
    }

    public boolean getEntityModsEnabled() {
        return config.getBoolean("Mods.Entity_Mods_Enabled", false);
    }

    /* Items */
    public int getChimaeraUseCost() {
        return config.getInt("Items.Chimaera_Wing.Use_Cost", 1);
    }

    public int getChimaeraRecipeCost() {
        return config.getInt("Items.Chimaera_Wing.Recipe_Cost", 5);
    }

    public Material getChimaeraItem() {
        return Material.matchMaterial(config.getString("Items.Chimaera_Wing.Item_Name", "Feather"));
    }

    public boolean getChimaeraEnabled() {
        return config.getBoolean("Items.Chimaera_Wing.Enabled", true);
    }

    public boolean getChimaeraPreventUseUnderground() {
        return config.getBoolean("Items.Chimaera_Wing.Prevent_Use_Underground", true);
    }

    public boolean getChimaeraUseBedSpawn() {
        return config.getBoolean("Items.Chimaera_Wing.Use_Bed_Spawn", true);
    }

    public int getChimaeraCooldown() {
        return config.getInt("Items.Chimaera_Wing.Cooldown", 240);
    }

    public int getChimaeraWarmup() {
        return config.getInt("Items.Chimaera_Wing.Warmup", 5);
    }

    public int getChimaeraRecentlyHurtCooldown() {
        return config.getInt("Items.Chimaera_Wing.RecentlyHurt_Cooldown", 60);
    }

    public boolean getChimaeraSoundEnabled() {
        return config.getBoolean("Items.Chimaera_Wing.Sound_Enabled", true);
    }

    public boolean getFluxPickaxeSoundEnabled() {
        return config.getBoolean("Items.Flux_Pickaxe.Sound_Enabled", true);
    }

    /* Particles */
    public boolean getAbilityActivationEffectEnabled() {
        return config.getBoolean("Particles.Ability_Activation", true);
    }

    public boolean getAbilityDeactivationEffectEnabled() {
        return config.getBoolean("Particles.Ability_Deactivation", true);
    }

    public boolean getBleedEffectEnabled() {
        return config.getBoolean("Particles.Bleed", true);
    }

    public boolean getCrippleEffectEnabled() {
        return config.getBoolean("Particles.Cripple", true);
    }

    public boolean getDodgeEffectEnabled() {
        return config.getBoolean("Particles.Dodge", true);
    }

    public boolean getFluxEffectEnabled() {
        return config.getBoolean("Particles.Flux", true);
    }

    public boolean getGreaterImpactEffectEnabled() {
        return config.getBoolean("Particles.Greater_Impact", true);
    }

    public boolean getCallOfTheWildEffectEnabled() {
        return config.getBoolean("Particles.Call_of_the_Wild", true);
    }

    public boolean getLevelUpEffectsEnabled() {
        return config.getBoolean("Particles.LevelUp_Enabled", true);
    }

    public int getLevelUpEffectsTier() {
        return config.getInt("Particles.LevelUp_Tier", 100);
    }
    //    public boolean getLargeFireworks() { return config.getBoolean("Particles.LargeFireworks", true); }

    /* PARTY SETTINGS */
    public boolean getPartyFriendlyFire() {
        return config.getBoolean("Party.FriendlyFire", false);
    }

    public int getPartyMaxSize() {
        return config.getInt("Party.MaxSize", -1);
    }

    public int getAutoPartyKickInterval() {
        return config.getInt("Party.AutoKick_Interval", 12);
    }

    public int getAutoPartyKickTime() {
        return config.getInt("Party.Old_Party_Member_Cutoff", 7);
    }

    public double getPartyShareBonusBase() {
        return config.getDouble("Party.Sharing.ExpShare_bonus_base", 1.1D);
    }

    public double getPartyShareBonusIncrease() {
        return config.getDouble("Party.Sharing.ExpShare_bonus_increase", 0.05D);
    }

    public double getPartyShareBonusCap() {
        return config.getDouble("Party.Sharing.ExpShare_bonus_cap", 1.5D);
    }

    public double getPartyShareRange() {
        return config.getDouble("Party.Sharing.Range", 75.0D);
    }

    public int getPartyLevelCap() {
        int cap = config.getInt("Party.Leveling.Level_Cap", 10);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    public int getPartyXpCurveMultiplier() {
        return config.getInt("Party.Leveling.Xp_Curve_Modifier", 3);
    }

    public boolean getPartyXpNearMembersNeeded() {
        return config.getBoolean("Party.Leveling.Near_Members_Needed", false);
    }

    public boolean getPartyInformAllMembers() {
        return config.getBoolean("Party.Leveling.Inform_All_Party_Members_On_LevelUp", false);
    }

    public int getPartyFeatureUnlockLevel(PartyFeature partyFeature) {
        return config.getInt(
                "Party.Leveling." + getConfigPartyFeatureString(partyFeature) + "_UnlockLevel", 0);
    }

    /* Party Teleport Settings */
    public int getPTPCommandCooldown() {
        return config.getInt("Commands.ptp.Cooldown", 120);
    }

    public int getPTPCommandWarmup() {
        return config.getInt("Commands.ptp.Warmup", 5);
    }

    public int getPTPCommandRecentlyHurtCooldown() {
        return config.getInt("Commands.ptp.RecentlyHurt_Cooldown", 60);
    }

    public int getPTPCommandTimeout() {
        return config.getInt("Commands.ptp.Request_Timeout", 300);
    }

    public boolean getPTPCommandConfirmRequired() {
        return config.getBoolean("Commands.ptp.Accept_Required", true);
    }

    public boolean getPTPCommandWorldPermissions() {
        return config.getBoolean("Commands.ptp.World_Based_Permissions", false);
    }

    /* Inspect command distance */
    public double getInspectDistance() {
        return config.getDouble("Commands.inspect.Max_Distance", 30.0D);
    }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public boolean getUrlLinksEnabled() {
        return config.getBoolean("Commands.Skills.URL_Links");
    }

    public boolean getAbilityMessagesEnabled() {
        return config.getBoolean("Abilities.Messages", true);
    }

    public boolean getAbilitiesEnabled() {
        return config.getBoolean("Abilities.Enabled", true);
    }

    public boolean getAbilitiesOnlyActivateWhenSneaking() {
        return config.getBoolean("Abilities.Activation.Only_Activate_When_Sneaking", false);
    }

    public boolean getAbilitiesGateEnabled() {
        return config.getBoolean("Abilities.Activation.Level_Gate_Abilities");
    }

    public int getCooldown(SuperAbilityType ability) {
        return config.getInt("Abilities.Cooldowns." + ability.toString());
    }

    public int getMaxLength(SuperAbilityType ability) {
        return config.getInt("Abilities.Max_Seconds." + ability.toString());
    }

    /* Durability Settings */
    public int getAbilityToolDamage() {
        return config.getInt("Abilities.Tools.Durability_Loss", 1);
    }

    /* Thresholds */
    public int getTreeFellerThreshold() {
        return config.getInt("Abilities.Limits.Tree_Feller_Threshold", 1000);
    }

    /*
     * SKILL SETTINGS
     */
    public boolean getDoubleDropsEnabled(PrimarySkillType skill, Material material) {
        //TODO: Temporary measure to fix an exploit caused by a yet to be fixed Spigot bug (as of 7/3/2020)
        if (material.toString().equalsIgnoreCase("LILY_PAD")) {
            return false;
        }

        return config.getBoolean(
                "Bonus_Drops." + StringUtils.getCapitalized(skill.toString()) + "."
                        + getMaterialConfigString(
                        material).replace(" ", "_"));
    }

    public boolean getDoubleDropsDisabled(PrimarySkillType skill) {
        String skillName = StringUtils.getCapitalized(skill.toString());
        ConfigurationSection section = config.getConfigurationSection("Bonus_Drops." + skillName);
        if (section == null) {
            return false;
        }
        Set<String> keys = section.getKeys(false);
        boolean disabled = true;

        for (String key : keys) {
            if (config.getBoolean("Bonus_Drops." + skillName + "." + key)) {
                disabled = false;
                break;
            }
        }

        return disabled;
    }

    /* Axes */
    public int getAxesGate() {
        return config.getInt("Skills.Axes.Ability_Activation_Level_Gate", 10);
    }

    /* Acrobatics */
    public boolean getDodgeLightningDisabled() {
        return config.getBoolean("Skills.Acrobatics.Prevent_Dodge_Lightning", false);
    }

    public int getXPAfterTeleportCooldown() {
        return config.getInt("Skills.Acrobatics.XP_After_Teleport_Cooldown", 5);
    }

    /* Alchemy */
    public boolean getEnabledForHoppers() {
        return config.getBoolean("Skills.Alchemy.Enabled_for_Hoppers", true);
    }

    public boolean getPreventHopperTransferIngredients() {
        return config.getBoolean("Skills.Alchemy.Prevent_Hopper_Transfer_Ingredients", false);
    }

    public boolean getPreventHopperTransferBottles() {
        return config.getBoolean("Skills.Alchemy.Prevent_Hopper_Transfer_Bottles", false);
    }

    /* Fishing */
    public boolean getFishingDropsEnabled() {
        return config.getBoolean("Skills.Fishing.Drops_Enabled", true);
    }

    public boolean getFishingOverrideTreasures() {
        return config.getBoolean("Skills.Fishing.Override_Vanilla_Treasures", true);
    }

    public boolean getFishingExtraFish() {
        return config.getBoolean("Skills.Fishing.Extra_Fish", true);
    }

    public double getFishingLureModifier() {
        return config.getDouble("Skills.Fishing.Lure_Modifier", 4.0D);
    }

    /* Mining */
    public Material getDetonatorItem() {
        return Material.matchMaterial(
                config.getString("Skills.Mining.Detonator_Name", "FLINT_AND_STEEL"));
    }

    /* Excavation */
    public int getExcavationGate() {
        return config.getInt("Skills.Excavation.Ability_Activation_Level_Gate", 10);
    }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() {
        return config.getBoolean("Skills.Repair.Anvil_Messages", true);
    }

    public boolean getRepairAnvilPlaceSoundsEnabled() {
        return config.getBoolean("Skills.Repair.Anvil_Placed_Sounds", true);
    }

    public boolean getRepairAnvilUseSoundsEnabled() {
        return config.getBoolean("Skills.Repair.Anvil_Use_Sounds", true);
    }

    public @Nullable Material getRepairAnvilMaterial() {
        return Material.matchMaterial(
                config.getString("Skills.Repair.Anvil_Material", "IRON_BLOCK"));
    }

    public boolean getRepairConfirmRequired() {
        return config.getBoolean("Skills.Repair.Confirm_Required", true);
    }

    public boolean getAllowVanillaInventoryRepair() {
        return config.getBoolean("Skills.Repair.Allow_Vanilla_Anvil_Repair", false);
    }

    public boolean getAllowVanillaAnvilRepair() {
        return config.getBoolean("Skills.Repair.Allow_Vanilla_Inventory_Repair", false);
    }

    public boolean getAllowVanillaGrindstoneRepair() {
        return config.getBoolean("Skills.Repair.Allow_Vanilla_Grindstone_Repair", false);
    }

    /* Salvage */
    public boolean getSalvageAnvilMessagesEnabled() {
        return config.getBoolean("Skills.Salvage.Anvil_Messages", true);
    }

    public boolean getSalvageAnvilPlaceSoundsEnabled() {
        return config.getBoolean("Skills.Salvage.Anvil_Placed_Sounds", true);
    }

    public boolean getSalvageAnvilUseSoundsEnabled() {
        return config.getBoolean("Skills.Salvage.Anvil_Use_Sounds", true);
    }

    public @Nullable Material getSalvageAnvilMaterial() {
        return Material.matchMaterial(
                config.getString("Skills.Salvage.Anvil_Material", "GOLD_BLOCK"));
    }

    public boolean getSalvageConfirmRequired() {
        return config.getBoolean("Skills.Salvage.Confirm_Required", true);
    }

    /* Unarmed */
    public boolean isBlockCrackerAllowed() {
        return config.getBoolean("Skills.Unarmed.Block_Cracker.Allow_Block_Cracker", true);
    }

    public boolean getUnarmedItemPickupDisabled() {
        return config.getBoolean("Skills.Unarmed.Item_Pickup_Disabled_Full_Inventory", true);
    }

    public boolean getUnarmedItemsAsUnarmed() {
        return config.getBoolean("Skills.Unarmed.Items_As_Unarmed", false);
    }

    public int getUnarmedGate() {
        return config.getInt("Skills.Unarmed.Ability_Activation_Level_Gate", 10);
    }

    /* Swords */
    public int getSwordsGate() {
        return config.getInt("Skills.Swords.Ability_Activation_Level_Gate", 10);
    }

    /* Taming */
    //    public Material getTamingCOTWMaterial(EntityType type) { return Material.matchMaterial(config.getString("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Item_Material")); }
    //    public int getTamingCOTWCost(EntityType type) { return config.getInt("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Item_Amount"); }
    //    public int getTamingCOTWAmount(EntityType type) { return config.getInt("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type) + ".Summon_Amount"); }
    //    public int getTamingCOTWLength(EntityType type) { return config.getInt("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type)+ ".Summon_Length"); }
    //    public int getTamingCOTWMaxAmount(EntityType type) { return config.getInt("Skills.Taming.Call_Of_The_Wild." + StringUtils.getPrettyEntityTypeString(type)+ ".Summon_Max_Amount"); }

    public Material getTamingCOTWMaterial(String cotwEntity) {
        return Material.matchMaterial(
                config.getString(
                        "Skills.Taming.Call_Of_The_Wild." + cotwEntity + ".Item_Material"));
    }

    public int getTamingCOTWCost(String cotwEntity) {
        return config.getInt("Skills.Taming.Call_Of_The_Wild." + cotwEntity + ".Item_Amount");
    }

    public int getTamingCOTWAmount(String cotwEntity) {
        return config.getInt("Skills.Taming.Call_Of_The_Wild." + cotwEntity + ".Summon_Amount");
    }

    public int getTamingCOTWLength(String cotwEntity) {
        return config.getInt("Skills.Taming.Call_Of_The_Wild." + cotwEntity + ".Summon_Length");
    }

    public int getTamingCOTWMaxAmount(String cotwEntity) {
        return config.getInt("Skills.Taming.Call_Of_The_Wild." + cotwEntity + ".Per_Player_Limit",
                1);
    }

    /* Woodcutting */
    public boolean getWoodcuttingDoubleDropsEnabled(BlockData blockData) {
        return config.getBoolean(
                "Bonus_Drops.Woodcutting." + getMaterialConfigString(blockData.getMaterial()));
    }

    public boolean getTreeFellerSoundsEnabled() {
        return config.getBoolean("Skills.Woodcutting.Tree_Feller_Sounds", true);
    }

    public int getWoodcuttingGate() {
        return config.getInt("Skills.Woodcutting.Ability_Activation_Level_Gate", 10);
    }

    /* AFK Leveling */
    public boolean getHerbalismPreventAFK() {
        return config.getBoolean("Skills.Herbalism.Prevent_AFK_Leveling", true);
    }

    /* Level Caps */
    public int getPowerLevelCap() {
        int cap = config.getInt("General.Power_Level_Cap", 0);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    public int getLevelCap(PrimarySkillType skill) {
        int cap = config.getInt(
                "Skills." + StringUtils.getCapitalized(skill.toString()) + ".Level_Cap");
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }


    /*public int isSuperAbilityUnlocked(PrimarySkillType skill) {
        return config.getInt("Skills." + StringUtils.getCapitalized(skill.toString()) + ".Ability_Activation_Level_Gate");
    }*/

    public boolean getTruncateSkills() {
        return config.getBoolean("General.TruncateSkills", false);
    }

    /* PVP & PVE Settings */
    public boolean getPVPEnabled(PrimarySkillType skill) {
        return config.getBoolean(
                "Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVP",
                true);
    }

    public boolean getPVEEnabled(PrimarySkillType skill) {
        return config.getBoolean(
                "Skills." + StringUtils.getCapitalized(skill.toString()) + ".Enabled_For_PVE",
                true);
    }

    //public float getMasterVolume() { return (float) config.getDouble("Sounds.MasterVolume", 1.0); }

    public boolean broadcastEventMessages() {
        return config.getBoolean("General.EventBroadcasts", true);
    }

    public boolean playerJoinEventInfo() {
        return config.getBoolean("General.EventInfoOnPlayerJoin", true);
    }

    public boolean adminNotifications() {
        return config.getBoolean("General.AdminNotifications", true);
    }

    public boolean shouldLevelUpBroadcasts() {
        return config.getBoolean("General.Level_Up_Chat_Broadcasts.Enabled", true);
    }

    public boolean shouldLevelUpBroadcastToConsole() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Targets.Send_To_Console", true);
    }

    public boolean isLevelUpBroadcastsPartyMembersOnly() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Targets.Only_Party_Members", false);
    }

    public boolean isLevelUpBroadcastsSameWorldOnly() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Targets.Only_Same_World", false);
    }

    public boolean shouldLevelUpBroadcastsRestrictDistance() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Targets.Distance_Restrictions.Restrict_Distance",
                false);
    }

    public int getLevelUpBroadcastRadius() {
        return config.getInt(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Targets.Distance_Restrictions.Restricted_Radius",
                100);
    }

    public int getLevelUpBroadcastInterval() {
        return config.getInt("General.Level_Up_Chat_Broadcasts.Milestone_Interval", 100);
    }

    public boolean shouldPowerLevelUpBroadcasts() {
        return config.getBoolean("General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Enabled",
                true);
    }

    public boolean shouldPowerLevelUpBroadcastToConsole() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Broadcast_Targets.Send_To_Console",
                true);
    }

    public boolean isPowerLevelUpBroadcastsPartyMembersOnly() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Broadcast_Targets.Only_Party_Members",
                false);
    }

    public boolean isPowerLevelUpBroadcastsSameWorldOnly() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Broadcast_Targets.Only_Same_World",
                false);
    }

    public boolean shouldPowerLevelUpBroadcastsRestrictDistance() {
        return config.getBoolean(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Broadcast_Targets.Distance_Restrictions.Restrict_Distance",
                false);
    }

    public int getPowerLevelUpBroadcastRadius() {
        return config.getInt(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Broadcast_Targets.Distance_Restrictions.Restricted_Radius",
                100);
    }

    public int getPowerLevelUpBroadcastInterval() {
        return config.getInt(
                "General.Level_Up_Chat_Broadcasts.Broadcast_Powerlevels.Milestone_Interval", 100);
    }

    public boolean isGreenThumbReplantableCrop(@NotNull Material material) {
        return config.getBoolean(
                "Green_Thumb_Replanting_Crops." + StringUtils.getCapitalized(material.toString()),
                true);
    }

    public boolean useVerboseLogging() {
        return config.getBoolean("General.Verbose_Logging", false);
    }

    public boolean isMasterySystemEnabled() {
        return config.getBoolean("General.PowerLevel.Skill_Mastery.Enabled");
    }
}
