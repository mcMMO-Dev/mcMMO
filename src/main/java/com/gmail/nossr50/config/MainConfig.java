package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.MobHealthbarType;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class MainConfig extends ConfigValidated {

    public static final String METRICS = "Metrics";
    public static final String BSTATS = "bstats";
    public static final String GENERAL = "General";
    public static final String RETRO_MODE = "RetroMode";
    public static final String ENABLED = "Enabled";
    public static final String LOCALE = "Locale";
    public static final String EN_US = "en_us";
    public static final String SHOW_PROFILE_LOADED = "Show_Profile_Loaded";
    public static final String DONATE_MESSAGE = "Donate_Message";
    public static final String MCMMO = "mcmmo";
    public static final String DATABASE_PREFIX = MCMMO + "_";
    public static final String COMMANDS = "Commands";
    public static final String SAVE_INTERVAL = "Save_Interval";
    public static final String STATS = "Stats";
    public static final String STATS_TRACKING = STATS + "_Tracking";
    public static final String UPDATE_CHECK = "Update_Check";
    public static final String PREFER_BETA = "Prefer_Beta";
    public static final String VERBOSE_LOGGING = "Verbose_Logging";
    public static final String PARTYCHAT = "partychat";
    public static final String CHAT_PREFIX_FORMAT = "Chat_Prefix_Format";
    public static final String NAME = "Name";
    public static final String GOLD_LEADER_NAME = "Gold_Leader_" + NAME;
    public static final String USE_DISPLAY_NAMES = "Use_Display_" + NAME + "s";
    public static final String ALLY = "_Ally";
    public static final String ADMINCHAT = "adminchat";
    public static final String GENERIC = "Generic";
    public static final String MATCH_OFFLINE_PLAYERS = "Match_OfflinePlayers";
    public static final String DATABASE = "Database";
    public static final String COOLDOWN = "Cooldown";
    public static final String PLAYER_COOLDOWN = "Player_" + COOLDOWN;
    public static final String LEVEL_UP = "LevelUp_";
    public static final String SOUND = "Sound";
    public static final String LEVEL_UP_SOUNDS = "LevelUp_Sounds";
    public static final String REFRESH_CHUNKS = "Refresh_Chunks";
    public static final String MOB_HEALTHBAR = "Mob_Healthbar";
    public static final String DISPLAY_TYPE = "Display_Type";
    public static final String HEARTS = "HEARTS";
    public static final String DISPLAY_TIME = "Display_Time";
    public static final String SCOREBOARD = "ConfigScoreboard";
    public static final String USE_SCOREBOARDS = "UseScoreboards";
    public static final String POWER = "Power_";
    public static final String POWER_LEVEL_TAGS = POWER + "Level_Tags";
    public static final String KEEP = "Keep";
    public static final String ALLOW_KEEP = "Allow_" + KEEP;
    public static final String TIPS_AMOUNT = "Tips_Amount";
    public static final String SHOW_STATS_AFTER_LOGIN = "Show_" + STATS + "_After_Login";
    public static final String RAINBOWS = "Rainbows";
    public static final String ABILITY_NAMES = "Ability_" + NAME + "s";
    public static final String TYPES = "Types";
    public static final String RANK = "Rank";
    public static final String PRINT = "Print";
    public static final String BOARD = "Board";
    public static final String TOP = "Top";
    public static final String INSPECT = "Inspect";
    public static final String SKILL = "Skill";
    public static final String TIME = "Time";
    public static final String PURGING = "_Purging";
    public static final String PURGE_INTERVAL = "Purge_Interval";
    public static final String OLD_USER_CUTOFF = "Old_User_Cutoff";
    public static final String BACKUPS = "Backups";
    public static final String KEEP_LAST_24_HOURS = KEEP + ".Last_24_Hours";
    public static final String DAILY_LAST_WEEK = "Daily_Last_Week";
    public static final String WEEKLY_PAST_MONTHS = "Weekly_Past_Months";
    public static final String MY_SQL = "MySQL";
    public static final String TABLE_PREFIX = "TablePrefix";
    public static final String USER_NAME = "User_" + NAME;
    public static final String SERVER = "Server";
    public static final String PORT = "Port";
    public static final String ADDRESS = "Address";
    public static final String LOCALHOST = "localhost";
    public static final String USER_PASSWORD = "User_Password";
    public static final String MAX_CONNECTIONS = "MaxConnections";
    public static final String MAX_POOL_SIZE = "MaxPoolSize";
    public static final String SSL = "SSL";
    public static final String HARDCORE = "Hardcore";
    public static final String DEATH_STAT_LOSS = "Death_Stat_Loss";
    public static final String PENALTY_PERCENTAGE = "Penalty_Percentage";
    public static final String LEVEL_THRESHOLD = "Level_Threshold";
    public static final String VAMPIRISM = "Vampirism";
    public static final String LEECH_PERCENTAGE = "Leech_Percentage";
    public static final String ITEMS = "Items";
    public static final String CHIMAERA_WING = "Chimaera_Wing";
    public static final String USE_COST = "Use_Cost";
    public static final String RECIPE_COST = "Recipe_Cost";
    public static final String ITEM = "Item_";
    public static final String FEATHER = "Feather";
    public static final String PREVENT = "Prevent_";
    public static final String PREVENT_USE_UNDERGROUND = PREVENT + "Use_Underground";
    public static final String USE_BED_SPAWN = "Use_Bed_Spawn";
    public static final String WARMUP = "Warmup";
    public static final String RECENTLY_HURT = "RecentlyHurt_";
    public static final String PARTICLES = "Particles";
    public static final String ACTIVATION = "Activation";
    public static final String ABILITY_ACTIVATION = "Ability_" + ACTIVATION;
    public static final String ABILITY_DEACTIVATION = "Ability_Deactivation";
    public static final String BLEED = "Bleed";
    public static final String DODGE = "Dodge";
    public static final String FLUX = "Flux";
    public static final String GREATER_IMPACT = "Greater_Impact";
    public static final String CALL_OF_THE_WILD = "Call_of_the_Wild";
    public static final String TIER = "Tier";
    public static final String LARGE_FIREWORKS = "LargeFireworks";
    public static final String PARTY = "Party";
    public static final String FRIENDLY_FIRE = "FriendlyFire";
    public static final String MAX_SIZE = "MaxSize";
    public static final String AUTO_KICK_INTERVAL = "AutoKick_Interval";
    public static final String OLD_PARTY_MEMBER_CUTOFF = "Old_Party_Member_Cutoff";
    public static final String SHARING = "Sharing";
    public static final String SHARING_EXP_SHARE_BONUS_BASE = SHARING + "ExpShare_bonus_base";
    public static final String EXP_SHARE_BONUS_INCREASE = "ExpShare_bonus_increase";
    public static final String EXP_SHARE_BONUS_CAP = "ExpShare_bonus_cap";
    public static final String RANGE = "Range";
    public static final String LEVELING = "Leveling";
    public static final String LEVEL_CAP = "Level_Cap";
    public static final String XP_CURVE_MODIFIER = "Xp_Curve_Modifier";
    public static final String NEAR_MEMBERS_NEEDED = "Near_Members_Needed";
    public static final String INFORM_ALL_PARTY_MEMBERS_ON_LEVEL_UP = "Inform_All_Party_Members_On_LevelUp";
    public static final String UNLOCK_LEVEL = "_UnlockLevel";
    public static final String PTP = "ptp";
    public static final String ACCEPT_REQUIRED = "Accept_Required";
    public static final String REQUEST_TIMEOUT = "Request_Timeout";
    public static final String WORLD_BASED_PERMISSIONS = "World_Based_Permissions";
    public static final String INSPECT1 = "inspect";
    public static final String MAX_DISTANCE = "Max_Distance";
    public static final String SKILLS = "Skills";
    public static final String URL_LINKS = "URL_Links";
    public static final String ABILITIES = "Abilities";
    public static final String MESSAGES = "Messages";
    public static final String ONLY_ACTIVATE_WHEN_SNEAKING = "Only_Activate_When_Sneaking";
    public static final String LEVEL_GATE_ABILITIES = "Level_Gate_Abilities";
    public static final String COOLDOWNS = "Cooldowns";
    public static final String MAX_SECONDS = "Max_Seconds";
    public static final String TOOLS = "Tools";
    public static final String DURABILITY_LOSS = "Durability_Loss";
    public static final String LIMITS = "Limits";
    public static final String TREE_FELLER = "Tree_Feller_";
    public static final String TREE_FELLER_THRESHOLD = TREE_FELLER + "Threshold";
    public static final String DOUBLE_DROPS = "Double_Drops";
    public static final String AXES = "Axes";
    public static final String TRUNCATE = "Truncate";
    public static final String FOR_PVP = "_For_PVP";
    public static final String FOR_PVE = "_For_PVE";
    public static final String ACROBATICS = "Acrobatics";
    public static final String PREVENT_AFK = PREVENT + "AFK_";
    public static final String WOODCUTTING = "Woodcutting";
    public static final String SOUNDS = "Sounds";
    public static final String MAX_TRIES_AT_SAME_LOCATION = "Max_Tries_At_Same_Location";
    public static final String HERBALISM = "Herbalism";
    public static final String TAMING = "Taming";
    public static final String CALL_OF_THE_WILD1 = "Call_Of_The_Wild";
    public static final String SUMMON_AMOUNT = "Summon_Amount";
    public static final String SUMMON_LENGTH = "Summon_Length";
    public static final String SUMMON_MAX_AMOUNT = "Summon_Max_Amount";
    public static final String AMOUNT = "Amount";
    public static final String MATERIAL = "Material";
    public static final String REPAIR = "Repair";
    public static final String CONFIRM_REQUIRED = "Confirm_Required";
    public static final String ANVIL = "Anvil_";
    public static final String ANVIL_MATERIAL = ANVIL + "Material";
    public static final String IRON_BLOCK = "IRON_BLOCK";
    public static final String ANVIL_USE = ANVIL + "Use_";
    public static final String ANVIL_PLACED = ANVIL + "Placed_";
    public static final String SALVAGE = "Salvage";
    public static final String UNARMED = "Unarmed";
    public static final String BLOCK_CRACKER = "Block_Cracker";
    public static final String SMOOTH_BRICK_TO_CRACKED_BRICK = "SmoothBrick_To_CrackedBrick";
    public static final String PICKUP_DISABLED_FULL_INVENTORY = "Pickup_Disabled_Full_Inventory";
    public static final String AS = "_As_";
    public static final String MINING = "Mining";
    public static final String DETONATOR = "Detonator_";
    public static final String FLINT_AND_STEEL = "FLINT_AND_STEEL";
    public static final String FISHING = "Fishing";
    public static final String LURE_MODIFIER = "Lure_Modifier";
    public static final String EXTRA_FISH = "Extra_Fish";
    public static final String OVERRIDE_VANILLA_TREASURES = "Override_Vanilla_Treasures";
    public static final String DROPS = "Drops_";
    public static final String ALCHEMY = "Alchemy";
    public static final String PREVENT_HOPPER_TRANSFER_BOTTLES = PREVENT + "Hopper_Transfer_Bottles";
    public static final String PREVENT_HOPPER_TRANSFER_INGREDIENTS = PREVENT + "Hopper_Transfer_Ingredients";
    public static final String FOR_HOPPERS = "_for_Hoppers";
    public static final String XP_AFTER_TELEPORT = "XP_After_Teleport_";
    public static final String LIGHTNING = "_Lightning";
    public static final String GOLD_BLOCK = "GOLD_BLOCK";
    public static final String PICKAXE = "_Pickaxe";
    public static final String EXP_SHARE_BONUS_BASE = "ExpShare_bonus_base";

    public MainConfig() {
        //super(McmmoCore.getDataFolderPath().getAbsoluteFile(), "config.yml", true);
        super("main", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, true);
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
            reason.add(GENERAL + SAVE_INTERVAL + " should be greater than 0!");
        }

        /* MySQL Settings */
        /*for (SQLDatabaseManager.PoolIdentifier identifier : SQLDatabaseManager.PoolIdentifier.values()) {
            if (getMySQLMaxConnections(identifier) <= 0) {
                reason.add(MY_SQL + "." + DATABASE + "." + MAX_CONNECTIONS + "." + StringUtils.getCapitalized(identifier.toString()) + " should be greater than 0!");
            }
            if (getMySQLMaxPoolSize(identifier) <= 0) {
                reason.add(MY_SQL + "." + DATABASE + "." + MAX_POOL_SIZE + "." + StringUtils.getCapitalized(identifier.toString()) + " should be greater than 0!");
            }
        }*/

        /* Mob Healthbar */
        if (getMobHealthbarTime() == 0) {
            reason.add(MOB_HEALTHBAR + "." + DISPLAY_TIME + " cannot be 0! Set to -1 to disable or set a valid value.");
        }

        /* Scoreboards */
        /*if (getRankScoreboardTime() != -1 && getRankScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Rank.Display_Time should be greater than 0, or -1!");
        }

        if (getStatsScoreboardTime() != -1 && getStatsScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Stats.Display_Time should be greater than 0, or -1!");
        }

        if (getTopScoreboardTime() != -1 && getTopScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Top.Display_Time should be greater than 0, or -1!");
        }

        if (getInspectScoreboardTime() != -1 && getInspectScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Inspect.Display_Time should be greater than 0, or -1!");
        }

        if (getSkillScoreboardTime() != -1 && getSkillScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Skill.Display_Time should be greater than 0, or -1!");
        }

        if (getSkillLevelUpTime() != -1 && getSkillScoreboardTime() <= 0) {
            reason.add("ConfigScoreboard.Types.Skill.Display_Time should be greater than 0, or -1!");
        }

        if (!(getRankUseChat() || getRankUseBoard())) {
            reason.add("Either Board or Print in ConfigScoreboard.Types.Rank must be true!");
        }

        if (!(getTopUseChat() || getTopUseBoard())) {
            reason.add("Either Board or Print in ConfigScoreboard.Types.Top must be true!");
        }

        if (!(getStatsUseChat() || getStatsUseBoard())) {
            reason.add("Either Board or Print in ConfigScoreboard.Types.Stats must be true!");
        }

        if (!(getInspectUseChat() || getInspectUseBoard())) {
            reason.add("Either Board or Print in ConfigScoreboard.Types.Inspect must be true!");
        }*/

        /* Hardcore Mode */
        if (getHardcoreDeathStatPenaltyPercentage() < 0.01 || getHardcoreDeathStatPenaltyPercentage() > 100) {
            reason.add(HARDCORE + "." + DEATH_STAT_LOSS + "." + PENALTY_PERCENTAGE + " only accepts values from 0.01 to 100!");
        }

        if (getHardcoreVampirismStatLeechPercentage() < 0.01 || getHardcoreVampirismStatLeechPercentage() > 100) {
            reason.add(HARDCORE + "." + VAMPIRISM + "." + LEECH_PERCENTAGE + " only accepts values from 0.01 to 100!");
        }

        /* Items */
        if (getChimaeraUseCost() < 1 || getChimaeraUseCost() > 64) {
            reason.add(ITEMS + "." + CHIMAERA_WING + "." + USE_COST + " only accepts values from 1 to 64!");
        }

        if (getChimaeraRecipeCost() < 1 || getChimaeraRecipeCost() > 9) {
            reason.add(ITEMS + "." + CHIMAERA_WING + "." + RECIPE_COST + " only accepts values from 1 to 9!");
        }

        if (getChimaeraItem() == null) {
            reason.add(ITEMS + "." + CHIMAERA_WING + "." + ITEM + NAME + " is invalid!");
        }

        /* Particles */
        if (getLevelUpEffectsTier() < 1) {
            reason.add(PARTICLES + "." + LEVEL_UP + "Tier should be at least 1!");
        }

        /* PARTY SETTINGS */
        if (getAutoPartyKickInterval() < -1) {
            reason.add(PARTY + "." + AUTO_KICK_INTERVAL + " should be at least -1!");
        }

        if (getAutoPartyKickTime() < 0) {
            reason.add(PARTY + "." + OLD_PARTY_MEMBER_CUTOFF + " should be at least 0!");
        }

        if (getPartyShareBonusBase() <= 0) {
            reason.add(PARTY + "." + SHARING_EXP_SHARE_BONUS_BASE + " should be greater than 0!");
        }

        if (getPartyShareBonusIncrease() < 0) {
            reason.add(PARTY + "." + SHARING + "." + EXP_SHARE_BONUS_INCREASE + " should be at least 0!");
        }

        if (getPartyShareBonusCap() <= 0) {
            reason.add(PARTY + "." + SHARING + "." + EXP_SHARE_BONUS_CAP + " should be greater than 0!");
        }

        if (getPartyShareRange() <= 0) {
            reason.add(PARTY + "." + SHARING + "." + RANGE + " should be greater than 0!");
        }

        if (getPartyXpCurveMultiplier() < 1) {
            reason.add(PARTY + "." + LEVELING + "." + XP_CURVE_MODIFIER + " should be at least 1!");
        }

        for (PartyFeature partyFeature : PartyFeature.values()) {
            if (getPartyFeatureUnlockLevel(partyFeature) < 0) {
                reason.add(PARTY + "." + LEVELING + "." + StringUtils.getPrettyPartyFeatureString(partyFeature).replace(" ", "") + "_UnlockLevel should be at least 0!");
            }
        }

        /* Inspect command distance */
        if (getInspectDistance() <= 0) {
            reason.add(COMMANDS + "." + INSPECT1 + "." + MAX_DISTANCE + " should be greater than 0!");
        }

        if (getTreeFellerThreshold() <= 0) {
            reason.add(ABILITIES + "." + LIMITS + "." + TREE_FELLER_THRESHOLD + " should be greater than 0!");
        }

        if (getFishingLureModifier() < 0) {
            reason.add(ABILITIES + "." + FISHING + "." + LURE_MODIFIER + " should be at least 0!");
        }

        if (getDetonatorItem() == null) {
            reason.add(SKILLS + "." + MINING + "." + DETONATOR + "Item is invalid!");
        }

        if (getRepairAnvilMaterial() == null) {
            reason.add(SKILLS + "." + REPAIR + "." + ANVIL + "Type is invalid!!");
        }

        if (getSalvageAnvilMaterial() == null) {
            reason.add(SKILLS + "." + REPAIR + "." + SALVAGE + "_" + ANVIL + "Type is invalid!");
        }

        if (getRepairAnvilMaterial() == getSalvageAnvilMaterial()) {
            reason.add("Cannot use the same item for " + REPAIR + " and " + SALVAGE + " anvils!");
        }

        if (getTamingCOTWMaterial(EntityType.WOLF) == null) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Wolf." + ITEM + "Material is invalid!!");
        }

        if (getTamingCOTWMaterial(EntityType.OCELOT) == null) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Ocelot." + ITEM + "Material is invalid!!");
        }

        if (getTamingCOTWMaterial(EntityType.HORSE) == null) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Horse." + ITEM + "Material is invalid!!");
        }

        if (getTamingCOTWCost(EntityType.WOLF) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Wolf." + ITEM + "Amount should be greater than 0!");
        }

        if (getTamingCOTWCost(EntityType.OCELOT) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Ocelot." + ITEM + "Amount should be greater than 0!");
        }

        if (getTamingCOTWCost(EntityType.HORSE) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Horse." + ITEM + "Amount should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.WOLF) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Wolf." + SUMMON_AMOUNT + " should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.OCELOT) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Ocelot." + SUMMON_AMOUNT + " should be greater than 0!");
        }

        if (getTamingCOTWAmount(EntityType.HORSE) <= 0) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD1 + ".Horse." + SUMMON_AMOUNT + " should be greater than 0!");
        }

        return reason;
    }

    /*
     * GENERAL SETTINGS
     */

    /* General Settings */
    public boolean getIsMetricsEnabled() {
        return getBooleanValue(METRICS, BSTATS);
    }

    //Retro mode will default the value to true if the config file doesn't contain the entry (server is from a previous mcMMO install)
    public boolean getIsRetroMode() {
        return getBooleanValue(GENERAL, RETRO_MODE, ENABLED);
    }

    public String getLocale() {
        if(hasNode(GENERAL, LOCALE))
            return getStringValue(GENERAL, LOCALE);
        else
            return "en_US";
    }

    public boolean getShowProfileLoadedMessage() {
        return getBooleanValue(GENERAL, SHOW_PROFILE_LOADED);
    }

    public boolean getDonateMessageEnabled() {
        return getBooleanValue(COMMANDS, MCMMO, DONATE_MESSAGE);
    }

    public int getSaveInterval() {
        return getIntValue(GENERAL, SAVE_INTERVAL);
    }

    public String getPartyChatPrefix() {
        return getStringValue(COMMANDS, PARTYCHAT, CHAT_PREFIX_FORMAT);
    }

    public boolean getPartyChatColorLeaderName() {
        return getBooleanValue(COMMANDS, PARTYCHAT, GOLD_LEADER_NAME);
    }

    public boolean getPartyDisplayNames() {
        return getBooleanValue(COMMANDS, PARTYCHAT, USE_DISPLAY_NAMES);
    }

    public String getPartyChatPrefixAlly() {
        return getStringValue(COMMANDS, PARTYCHAT, CHAT_PREFIX_FORMAT + ALLY);
    }

    public String getAdminChatPrefix() {
        return getStringValue(COMMANDS, ADMINCHAT, CHAT_PREFIX_FORMAT);
    }

    public boolean getAdminDisplayNames() {
        return getBooleanValue(COMMANDS, ADMINCHAT, USE_DISPLAY_NAMES);
    }

    public boolean getMatchOfflinePlayers() {
        return getBooleanValue(COMMANDS, GENERIC, MATCH_OFFLINE_PLAYERS);
    }

    public long getDatabasePlayerCooldown() {
        return getLongValue(COMMANDS, DATABASE, PLAYER_COOLDOWN);
    }

    public boolean getLevelUpSoundsEnabled() {
        return getBooleanValue(GENERAL, LEVEL_UP_SOUNDS);
    }

    public boolean getRefreshChunksEnabled() {
        return getBooleanValue(GENERAL, REFRESH_CHUNKS);
    }

    public boolean getMobHealthbarEnabled() {
        return getBooleanValue(MOB_HEALTHBAR, ENABLED);
    }

    /* Mob Healthbar */
    public MobHealthbarType getMobHealthbarDefault() {
        try {
            return MobHealthbarType.valueOf(getStringValue(MOB_HEALTHBAR, DISPLAY_TYPE, HEARTS).toUpperCase().trim());
        } catch (IllegalArgumentException ex) {
            return MobHealthbarType.HEARTS;
        }
    }

    public int getMobHealthbarTime() {
        return getIntValue(MOB_HEALTHBAR, DISPLAY_TIME);
    }

    /* Backups */
    public boolean getBackupsEnabled() {
        return getBooleanValue(BACKUPS, ENABLED);
    }

    public boolean getKeepLast24Hours() {
        return getBooleanValue(BACKUPS, KEEP_LAST_24_HOURS);
    }

    public boolean getKeepDailyLastWeek() {
        return getBooleanValue(BACKUPS, KEEP, DAILY_LAST_WEEK);
    }

    public boolean getKeepWeeklyPastMonth() {
        return getBooleanValue(BACKUPS, KEEP, WEEKLY_PAST_MONTHS);
    }

    /* mySQL */
    /*public boolean getUseMySQL() {
        return getBooleanValue(MY_SQL, ENABLED);
    }

    public String getMySQLTablePrefix() {
        return getStringValue(MY_SQL, DATABASE, TABLE_PREFIX, DATABASE_PREFIX);
    }

    public String getMySQLDatabaseName() {
        return getStringValue(MY_SQL, DATABASE, NAME);
    }

    public String getMySQLUserName() {
        return getStringValue(MY_SQL, DATABASE, USER_NAME);
    }

    public int getMySQLServerPort() {
        return getIntValue(MY_SQL, SERVER, PORT);
    }

    public String getMySQLServerName() {
        return getStringValue(MY_SQL, SERVER, ADDRESS, LOCALHOST);
    }

    public String getMySQLUserPassword() {
        return getStringValue(MY_SQL, DATABASE, USER_PASSWORD);
    }

    public int getMySQLMaxConnections(SQLDatabaseManager.PoolIdentifier identifier) {
        return getIntValue(MY_SQL, DATABASE, MAX_CONNECTIONS, StringUtils.getCapitalized(identifier.toString()));
    }

    public int getMySQLMaxPoolSize(SQLDatabaseManager.PoolIdentifier identifier) {
        return getIntValue(MY_SQL, DATABASE, MAX_POOL_SIZE, StringUtils.getCapitalized(identifier.toString()));
    }

    public boolean getMySQLSSL() {
        return getBooleanValue(MY_SQL, SERVER, SSL);
    }*/

    //TODO: Legit cannot tell what the point of this method was
    /*ssadprivate String getStringIncludingInts(String[] key) {
        String str = getStringValue(key);

        if (str == null) {
            str = String.valueOf(getIntValue(key));
        }

        if (str.equals("0")) {
            str = "No value set for '" + key + "'";
        }
        return str;
    }*/

    /* Hardcore Mode */
    public boolean getHardcoreStatLossEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue(HARDCORE, DEATH_STAT_LOSS, ENABLED, StringUtils.getCapitalized(primarySkillType.toString()));
    }

    public double getHardcoreDeathStatPenaltyPercentage() {
        return getDoubleValue(HARDCORE, DEATH_STAT_LOSS, PENALTY_PERCENTAGE);
    }

    public int getHardcoreDeathStatPenaltyLevelThreshold() {
        return getIntValue(HARDCORE, DEATH_STAT_LOSS, LEVEL_THRESHOLD);
    }

    public boolean getHardcoreVampirismEnabled(PrimarySkillType primarySkillType) {
        return getBooleanValue(HARDCORE, VAMPIRISM, ENABLED, StringUtils.getCapitalized(primarySkillType.toString()));
    }

    public double getHardcoreVampirismStatLeechPercentage() {
        return getDoubleValue(HARDCORE, VAMPIRISM, LEECH_PERCENTAGE);
    }

    public int getHardcoreVampirismLevelThreshold() {
        return getIntValue(HARDCORE, VAMPIRISM, LEVEL_THRESHOLD);
    }

    /* Items */
    public int getChimaeraUseCost() {
        return getIntValue(ITEMS, CHIMAERA_WING, USE_COST);
    }

    public int getChimaeraRecipeCost() {
        return getIntValue(ITEMS, CHIMAERA_WING, RECIPE_COST);
    }

    public Material getChimaeraItem() {
        return Material.matchMaterial(getStringValue(ITEMS, CHIMAERA_WING, ITEM + NAME));
    }

    public boolean getChimaeraEnabled() {
        return getBooleanValue(ITEMS, CHIMAERA_WING, ENABLED);
    }

    public boolean getChimaeraPreventUseUnderground() {
        return getBooleanValue(ITEMS, CHIMAERA_WING, PREVENT_USE_UNDERGROUND);
    }

    public boolean getChimaeraUseBedSpawn() {
        return getBooleanValue(ITEMS, CHIMAERA_WING, USE_BED_SPAWN);
    }

    public int getChimaeraCooldown() {
        return getIntValue(ITEMS, CHIMAERA_WING, COOLDOWN);
    }

    public int getChimaeraWarmup() {
        return getIntValue(ITEMS, CHIMAERA_WING, WARMUP);
    }

    public int getChimaeraRecentlyHurtCooldown() {
        return getIntValue(ITEMS, CHIMAERA_WING, RECENTLY_HURT + COOLDOWN);
    }

    public boolean getChimaeraSoundEnabled() {
        return getBooleanValue(ITEMS, CHIMAERA_WING, SOUND + "_" + ENABLED);
    }

    public boolean getFluxPickaxeSoundEnabled() {
        return getBooleanValue(ITEMS, FLUX + PICKAXE, SOUND + "_" + ENABLED);
    }

    /* Particles */
    public boolean getAbilityActivationEffectEnabled() {
        return getBooleanValue(PARTICLES, ABILITY_ACTIVATION);
    }

    public boolean getAbilityDeactivationEffectEnabled() {
        return getBooleanValue(PARTICLES, ABILITY_DEACTIVATION);
    }

    public boolean getBleedEffectEnabled() {
        return getBooleanValue(PARTICLES, BLEED);
    }

    public boolean getDodgeEffectEnabled() {
        return getBooleanValue(PARTICLES, DODGE);
    }

    public boolean getFluxEffectEnabled() {
        return getBooleanValue(PARTICLES, FLUX);
    }

    public boolean getGreaterImpactEffectEnabled() {
        return getBooleanValue(PARTICLES, GREATER_IMPACT);
    }

    public boolean getCallOfTheWildEffectEnabled() {
        return getBooleanValue(PARTICLES, CALL_OF_THE_WILD);
    }

    public boolean getLevelUpEffectsEnabled() {
        return getBooleanValue(PARTICLES, LEVEL_UP + ENABLED);
    }

    public int getLevelUpEffectsTier() {
        return getIntValue(PARTICLES, LEVEL_UP + TIER);
    }

    public boolean getLargeFireworks() {
        return getBooleanValue(PARTICLES, LARGE_FIREWORKS);
    }

    /* PARTY SETTINGS */
    public boolean getPartyFriendlyFire() {
        return getBooleanValue(PARTY, FRIENDLY_FIRE);
    }

    public int getPartyMaxSize() {
        return getIntValue(PARTY, MAX_SIZE);
    }

    public int getAutoPartyKickInterval() {
        return getIntValue(PARTY, AUTO_KICK_INTERVAL);
    }

    public int getAutoPartyKickTime() {
        return getIntValue(PARTY, OLD_PARTY_MEMBER_CUTOFF);
    }

    public double getPartyShareBonusBase() {
        return getDoubleValue(PARTY, SHARING, EXP_SHARE_BONUS_BASE);
    }

    public double getPartyShareBonusIncrease() {
        return getDoubleValue(PARTY, SHARING, EXP_SHARE_BONUS_INCREASE);
    }

    public double getPartyShareBonusCap() {
        return getDoubleValue(PARTY, SHARING, EXP_SHARE_BONUS_CAP);
    }

    public double getPartyShareRange() {
        return getDoubleValue(PARTY, SHARING, RANGE);
    }

    public int getPartyLevelCap() {
        int cap = getIntValue(PARTY, LEVELING, LEVEL_CAP);
        return (cap <= 0) ? Integer.MAX_VALUE : cap;
    }

    //TODO: Move this to Experience Config
    public int getPartyXpCurveMultiplier() {
        return getIntValue(PARTY, LEVELING, XP_CURVE_MODIFIER);
    }

    public boolean getPartyXpNearMembersNeeded() {
        return getBooleanValue(PARTY, LEVELING, NEAR_MEMBERS_NEEDED);
    }

    public boolean getPartyInformAllMembers() {
        return getBooleanValue(PARTY, LEVELING, INFORM_ALL_PARTY_MEMBERS_ON_LEVEL_UP);
    }

    public int getPartyFeatureUnlockLevel(PartyFeature partyFeature) {
        return getIntValue(PARTY, LEVELING, StringUtils.getPrettyPartyFeatureString(partyFeature).replace(" ", "") + UNLOCK_LEVEL);
    }

    /* Party Teleport Settings */
    public int getPTPCommandCooldown() {
        return getIntValue(COMMANDS, PTP, COOLDOWN);
    }

    public int getPTPCommandWarmup() {
        return getIntValue(COMMANDS, PTP, WARMUP);
    }

    public int getPTPCommandRecentlyHurtCooldown() {
        return getIntValue(COMMANDS, PTP, RECENTLY_HURT + COOLDOWN);
    }

    public int getPTPCommandTimeout() {
        return getIntValue(COMMANDS, PTP, REQUEST_TIMEOUT);
    }

    public boolean getPTPCommandConfirmRequired() {
        return getBooleanValue(COMMANDS, PTP, ACCEPT_REQUIRED);
    }

    public boolean getPTPCommandWorldPermissions() {
        return getBooleanValue(COMMANDS, PTP, WORLD_BASED_PERMISSIONS);
    }

    /* Inspect command distance */
    public double getInspectDistance() {
        return getDoubleValue(COMMANDS, INSPECT1, MAX_DISTANCE);
    }

    /*
     * ABILITY SETTINGS
     */

    /* General Settings */
    public boolean getAbilityMessagesEnabled() {
        return getBooleanValue(ABILITIES, MESSAGES);
    }

    public boolean getAbilitiesEnabled() {
        return getBooleanValue(ABILITIES, ENABLED);
    }

    public boolean getAbilitiesOnlyActivateWhenSneaking() {
        return getBooleanValue(ABILITIES, ACTIVATION, ONLY_ACTIVATE_WHEN_SNEAKING);
    }

    public int getCooldown(SuperAbilityType ability) {
        return getIntValue(ABILITIES, COOLDOWNS + ability.toString());
    }

    public int getMaxLength(SuperAbilityType ability) {
        return getIntValue(ABILITIES, MAX_SECONDS, ability.toString());
    }

    /* Durability Settings */
    public int getAbilityToolDamage() {
        return getIntValue(ABILITIES, TOOLS, DURABILITY_LOSS);
    }

    /* Thresholds */
    public int getTreeFellerThreshold() {
        return getIntValue(ABILITIES, LIMITS, TREE_FELLER_THRESHOLD);
    }

    /*
     * SKILL SETTINGS
     */
    public boolean getDoubleDropsEnabled(PrimarySkillType skill, Material material) {
        return getBooleanValue(DOUBLE_DROPS, StringUtils.getCapitalized(skill.toString()), StringUtils.getPrettyItemString(material).replace(" ", "_"));
    }

    /* Acrobatics */
    public boolean getDodgeLightningDisabled() {
        return getBooleanValue(SKILLS, ACROBATICS, PREVENT + DODGE + LIGHTNING);
    }

    public int getXPAfterTeleportCooldown() {
        return getIntValue(SKILLS, ACROBATICS, XP_AFTER_TELEPORT + COOLDOWN);
    }

    /* Alchemy */
    public boolean getEnabledForHoppers() {
        return getBooleanValue(SKILLS, ALCHEMY, ENABLED + FOR_HOPPERS);
    }

    public boolean getPreventHopperTransferIngredients() {
        return getBooleanValue(SKILLS, ALCHEMY, PREVENT_HOPPER_TRANSFER_INGREDIENTS);
    }

    public boolean getPreventHopperTransferBottles() {
        return getBooleanValue(SKILLS, ALCHEMY, PREVENT_HOPPER_TRANSFER_BOTTLES);
    }

    /* Fishing */
    public boolean getFishingDropsEnabled() {
        return getBooleanValue(SKILLS, FISHING, DROPS + ENABLED);
    }

    public boolean getFishingOverrideTreasures() {
        return getBooleanValue(SKILLS, FISHING, OVERRIDE_VANILLA_TREASURES);
    }

    public boolean getFishingExtraFish() {
        return getBooleanValue(SKILLS, FISHING, EXTRA_FISH);
    }

    public double getFishingLureModifier() {
        return getDoubleValue(SKILLS, FISHING, LURE_MODIFIER);
    }

    /* Mining */
    public Material getDetonatorItem() {
        //Flint and steel
        return Material.matchMaterial(getStringValue(SKILLS, MINING, DETONATOR + NAME));
    }

    /* Repair */
    public boolean getRepairAnvilMessagesEnabled() {
        return getBooleanValue(SKILLS, REPAIR, ANVIL + MESSAGES);
    }

    public boolean getRepairAnvilPlaceSoundsEnabled() {
        return getBooleanValue(SKILLS, REPAIR, ANVIL_PLACED + SOUNDS);
    }

    public boolean getRepairAnvilUseSoundsEnabled() {
        return getBooleanValue(SKILLS, REPAIR, ANVIL_USE + SOUNDS);
    }

    public Material getRepairAnvilMaterial() {
        //Iron block
        return Material.matchMaterial(getStringValue(SKILLS, REPAIR, ANVIL_MATERIAL));
    }

    public boolean getRepairConfirmRequired() {
        return getBooleanValue(SKILLS, REPAIR, CONFIRM_REQUIRED);
    }

    /* Salvage */
    public boolean getSalvageAnvilMessagesEnabled() {
        return getBooleanValue(SKILLS, SALVAGE, ANVIL + MESSAGES);
    }

    public boolean getSalvageAnvilPlaceSoundsEnabled() {
        return getBooleanValue(SKILLS, SALVAGE, ANVIL_PLACED + SOUNDS);
    }

    public boolean getSalvageAnvilUseSoundsEnabled() {
        return getBooleanValue(SKILLS, SALVAGE, ANVIL_USE + SOUNDS);
    }

    public Material getSalvageAnvilMaterial() {
        //Gold Block
        return Material.matchMaterial(getStringValue(SKILLS, SALVAGE, ANVIL_MATERIAL));
    }

    public boolean getSalvageConfirmRequired() {
        return getBooleanValue(SKILLS, SALVAGE, CONFIRM_REQUIRED);
    }

    /* Unarmed */
    public boolean getUnarmedBlockCrackerSmoothbrickToCracked() {
        return getBooleanValue(SKILLS, UNARMED, BLOCK_CRACKER, SMOOTH_BRICK_TO_CRACKED_BRICK);
    }

    public boolean getUnarmedItemPickupDisabled() {
        return getBooleanValue(SKILLS, UNARMED, ITEM + PICKUP_DISABLED_FULL_INVENTORY);
    }

    public boolean getUnarmedItemsAsUnarmed() {
        return getBooleanValue(SKILLS, UNARMED, ITEMS + AS + UNARMED);
    }

    /* Taming */
    public Material getTamingCOTWMaterial(EntityType type) {
        return Material.matchMaterial(getStringValue(SKILLS, TAMING, CALL_OF_THE_WILD1, StringUtils.getPrettyEntityTypeString(type), ITEM + MATERIAL));
    }

    public int getTamingCOTWCost(EntityType type) {
        return getIntValue(SKILLS, TAMING, CALL_OF_THE_WILD1, StringUtils.getPrettyEntityTypeString(type), ITEM + AMOUNT);
    }

    public int getTamingCOTWAmount(EntityType type) {
        return getIntValue(SKILLS, TAMING, CALL_OF_THE_WILD1, StringUtils.getPrettyEntityTypeString(type), SUMMON_AMOUNT);
    }

    public int getTamingCOTWLength(EntityType type) {
        return getIntValue(SKILLS, TAMING, CALL_OF_THE_WILD1, StringUtils.getPrettyEntityTypeString(type), SUMMON_LENGTH);
    }

    public int getTamingCOTWMaxAmount(EntityType type) {
        return getIntValue(SKILLS, TAMING, CALL_OF_THE_WILD1, StringUtils.getPrettyEntityTypeString(type), SUMMON_MAX_AMOUNT);
    }

    public double getTamingCOTWRange() {
        return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD1, RANGE);
    }

    /* Woodcutting */
    public boolean getWoodcuttingDoubleDropsEnabled(BlockData material) {
        return getBooleanValue(DOUBLE_DROPS, WOODCUTTING, StringUtils.getFriendlyConfigBlockDataString(material));
    }

    public boolean getTreeFellerSoundsEnabled() {
        return getBooleanValue(SKILLS, WOODCUTTING, TREE_FELLER + SOUNDS);
    }

    /* AFK Leveling */
    public boolean getAcrobaticsPreventAFK() {
        return getBooleanValue(SKILLS, ACROBATICS, PREVENT_AFK + LEVELING);
    }

    public int getAcrobaticsAFKMaxTries() {
        return getIntValue(SKILLS, ACROBATICS, MAX_TRIES_AT_SAME_LOCATION);
    }

    public boolean getHerbalismPreventAFK() {
        return getBooleanValue(SKILLS, HERBALISM, PREVENT_AFK + LEVELING);
    }

    /* PVP & PVE Settings */
    public boolean getPVPEnabled(PrimarySkillType skill) {
        return getBooleanValue(SKILLS, StringUtils.getCapitalized(skill.toString()), ENABLED + FOR_PVP);
    }

    public boolean getPVEEnabled(PrimarySkillType skill) {
        return getBooleanValue(SKILLS, StringUtils.getCapitalized(skill.toString()), ENABLED + FOR_PVE);
    }
}
