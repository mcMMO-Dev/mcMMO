//package com.gmail.nossr50.config;
//
//import com.gmail.nossr50.mcMMO;
//import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@ConfigSerializable
//public class AdvancedConfig extends ConfigValidated {
//
//    public static final String SKILLS = "Skills";
//    public static final String GENERAL = "General";
//    public static final String ABILITY = "Ability";
//    public static final String LENGTH = "Length";
//    public static final String INCREASE_LEVEL = "IncreaseLevel";
//    public static final String ENCHANT_BUFF = "EnchantBuff";
//    public static final String ACROBATICS = "Acrobatics";
//    public static final String DODGE = "Dodge";
//    public static final String CHANCE = "Chance";
//    public static final String CHANCE_MAX = CHANCE + "Max";
//    public static final String BONUS = "Bonus";
//    public static final String MAX_BONUS_LEVEL = "Max" + BONUS + "Level";
//    public static final String MODIFIER = "Modifier";
//    public static final String DAMAGE_MODIFIER = "Damage" + MODIFIER;
//    public static final String DAMAGE_THRESHOLD = "DamageThreshold";
//    public static final String ALCHEMY = "Alchemy";
//    public static final String CATALYSIS = "Catalysis";
//    public static final String MIN_SPEED = "MinSpeed";
//    public static final String MAX_SPEED = "MaxSpeed";
//    public static final String ARCHERY = "Archery";
//    public static final String SKILL_SHOT = "SkillShot";
//    public static final String MULTIPLIER = "Multiplier";
//    public static final String RANK_DAMAGE_MULTIPLIER = "RankDamage" + MULTIPLIER;
//    public static final String BONUS_DAMAGE = BONUS + "Damage";
//    public static final String FORCE_MULTIPLIER = "Force" + MULTIPLIER;
//    public static final String AXES = "Axes";
//    public static final String STANDARD = "Standard";
//    public static final String RETRO_MODE = "RetroMode";
//    public static final String CAP_LEVEL = "CapLevel";
//    public static final String KNOCKBACK_MODIFIER = "Knockback" + MODIFIER;
//    public static final String PVP_MODIFIER = "PVP_" + MODIFIER;
//    public static final String PVE_MODIFIER = "PVE_" + MODIFIER;
//    public static final String FISHING = "Fishing";
//    public static final String MASTER_ANGLER = "MasterAngler";
//    public static final String BOAT_MODIFIER = "Boat" + MODIFIER;
//    public static final String BIOME_MODIFIER = "Biome" + MODIFIER;
//    public static final String XP = "XP";
//    public static final String VANILLA_XPMULTIPLIER = "Vanilla" + XP + MULTIPLIER;
//    public static final String RANK = "Rank_";
//    public static final String TAMING = "Taming";
//    public static final String CALL_OF_THE_WILD = "CallOfTheWild";
//    public static final String MIN_HORSE_JUMP_STRENGTH = "MinHorseJumpStrength";
//    public static final String MAX_HORSE_JUMP_STRENGTH = "MaxHorseJumpStrength";
//    public static final String SHOCK_PROOF = "ShockProof";
//    public static final String UNARMED = "Unarmed";
//    public static final String STARTING_LEVEL = "StartingLevel";
//    public static final String AXE_MASTERY = "AxeMastery";
//    public static final String CRITICAL_STRIKES = "CriticalStrikes";
//    public static final String GREATER_IMPACT = "GreaterImpact";
//    public static final String ARMOR_IMPACT = "ArmorImpact";
//    public static final String SKULL_SPLITTER = "SkullSplitter";
//    public static final String MAX_PERCENTAGE_DURABILITY_DAMAGE = "MaxPercentageDurabilityDamage";
//    public static final String SHAKE = "Shake";
//    public static final String MINING = "Mining";
//    public static final String BLAST_MINING = "BlastMining";
//    public static final String LEVELS = "Levels";
//    public static final String BLAST_DAMAGE_DECREASE = "BlastDamageDecrease";
//    public static final String ORE_BONUS = "Ore" + BONUS;
//    public static final String DEBRIS_REDUCTION = "DebrisReduction";
//    public static final String DROP_MULTIPLIER = "Drop" + MULTIPLIER;
//    public static final String BLAST_RADIUS = "BlastRadius";
//    public static final String REPAIR = "Repair";
//    public static final String REPAIR_MASTERY = "RepairMastery";
//    public static final String MAX_BONUS_PERCENTAGE = "Max" + BONUS + "Percentage";
//    public static final String ARCANE_FORGING = "ArcaneForging";
//    public static final String MAY_LOSE_ENCHANTS = "May_Lose_Enchants";
//    public static final String KEEP_ENCHANTS = "Keep_Enchants_";
//    public static final String DOWNGRADES = "Downgrades_";
//    public static final String ENABLED = "Enabled";
//    public static final String DOWNGRADES_ENABLED = DOWNGRADES + ENABLED;
//    public static final String SALVAGE = "Salvage";
//    public static final String ARCANE_SALVAGE = "ArcaneSalvage";
//    public static final String ENCHANT_DOWNGRADE_ENABLED = "EnchantDowngrade" + ENABLED;
//    public static final String ENCHANT_LOSS_ENABLED = "EnchantLoss" + ENABLED;
//    public static final String EXTRACT_FULL_ENCHANT = "ExtractFullEnchant";
//    public static final String EXTRACT_PARTIAL_ENCHANT = "ExtractPartialEnchant";
//    public static final String SMELTING = "Smelting";
//    public static final String FUEL_EFFICIENCY = "FuelEfficiency";
//    public static final String SWORDS = "Swords";
//    public static final String RUPTURE = "Rupture";
//    public static final String DAMAGE_PLAYER = "DamagePlayer";
//    public static final String DAMAGE_MOBS = "DamageMobs";
//    public static final String MAX_TICKS = "MaxTicks";
//    public static final String BASE_TICKS = "BaseTicks";
//    public static final String COUNTER_ATTACK = "CounterAttack";
//    public static final String SERRATED_STRIKES = "SerratedStrikes";
//    public static final String TICKS = "Ticks";
//    public static final String GORE = "Gore";
//    public static final String FAST_FOOD = "FastFood";
//    public static final String FAST_FOOD_SERVICE = FAST_FOOD + "Service";
//    public static final String PUMMEL = "Pummel";
//    public static final String THICK_FUR = "ThickFur";
//    public static final String SHARPENED_CLAWS = "SharpenedClaws";
//    public static final String DISARM = "Disarm";
//    public static final String ANTI_THEFT = "AntiTheft";
//    public static final String DAZE = "Daze";
//    public static final String MAX_DAMAGE = "MaxDamage";
//    public static final String ROLL = "Roll";
//    public static final String GRACEFUL_ROLL = "Graceful" + ROLL;
//    public static final String ARROW_DEFLECT = "ArrowDeflect";
//    public static final String IRON_GRIP = "IronGrip";
//    public static final String WOODCUTTING = "Woodcutting";
//    public static final String HARVEST_LUMBER = "HarvestLumber";
//    public static final String FEEDBACK = "Feedback";
//    public static final String SKILL_COMMAND = "SkillCommand";
//    public static final String BLANK_LINES_ABOVE_HEADER = "BlankLinesAboveHeader";
//    public static final String ACTION_BAR_NOTIFICATIONS = "ActionBarNotifications";
//    public static final String SEND_COPY_OF_MESSAGE_TO_CHAT = "SendCopyOfMessageToChat";
//    public static final String EVENTS = "Events";
//    public static final String SEND_TITLES = "SendTitles";
//    //private static AdvancedConfig instance;
//
//    public AdvancedConfig() {
//        //super(mcMMO.getDataFolderPath().getAbsoluteFile(), "advanced.yml", true);
//        super("advanced", pluginRef.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, true);
//    }
//
//    /**
//     * This grabs an instance of this config class from the Config Manager
//     * This method is deprecated and will be removed in the future
//     *
//     * @return the instance of this config
//     * @see mcMMO#getConfigManager()
//     * @deprecated Please use mcMMO.getConfigManager() to grab a specific config instead
//     */
//    @Deprecated
//    public static AdvancedConfig getInstance() {
//        return pluginRef.getConfigManager().getAdvancedConfig();
//    }
//
//    /**
//     * The version of this config
//     *
//     * @return
//     */
//    @Override
//    public double getConfigVersion() {
//        return 1;
//    }
//
//    @Override
//    public List<String> validateKeys() {
//        // Validate all the settings!
//        List<String> reason = new ArrayList<>();
//        return reason;
//    }
//
//    /* GENERAL */
//
//    /* Notification Settings */
//
//    /* FISHING */
//    public double getShakeChance(int rank) {
//        return getDoubleValue(SKILLS, FISHING, SHAKE, CHANCE, RANK, String.valueOf(rank));
//    }
//
//    public double getMasterAnglerBoatModifier() {
//        return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BOAT_MODIFIER);
//    }
//
//    public double getMasterAnglerBiomeModifier() {
//        return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BIOME_MODIFIER);
//    }
//
//    /* TAMING */
//    public double getGoreModifier() {
//        return getDoubleValue(SKILLS, TAMING, GORE, MODIFIER);
//    }
//
//    public double getFastFoodChance() {
//        return getDoubleValue(SKILLS, TAMING, FAST_FOOD_SERVICE, CHANCE);
//    }
//
//    public double getPummelChance() {
//        return getDoubleValue(SKILLS, TAMING, PUMMEL, CHANCE);
//    }
//
//    public double getThickFurModifier() {
//        return getDoubleValue(SKILLS, TAMING, THICK_FUR, MODIFIER);
//    }
//
//    public double getShockProofModifier() {
//        return getDoubleValue(SKILLS, TAMING, SHOCK_PROOF, MODIFIER);
//    }
//
//    public double getSharpenedClawsBonus() {
//        return getDoubleValue(SKILLS, TAMING, SHARPENED_CLAWS, BONUS);
//    }
//
//    public double getMinHorseJumpStrength() {
//        return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MIN_HORSE_JUMP_STRENGTH);
//    }
//
//    public double getMaxHorseJumpStrength() {
//        return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MAX_HORSE_JUMP_STRENGTH);
//    }
//
//}
