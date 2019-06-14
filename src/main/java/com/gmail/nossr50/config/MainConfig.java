package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
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
     *
     * @return the instance of this config
     * @see mcMMO#getConfigManager()
     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
     */
    @Deprecated
    public static MainConfig getInstance() {
        return mcMMO.getConfigManager().getMainConfig();
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
        List<String> reason = new ArrayList<>();
        return reason;
    }

    /*
     * GENERAL SETTINGS
     */

    public boolean getShowProfileLoadedMessage() {
        return getBooleanValue(GENERAL, SHOW_PROFILE_LOADED);
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
