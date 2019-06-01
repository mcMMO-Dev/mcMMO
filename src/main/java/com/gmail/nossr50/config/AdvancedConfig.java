package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.mcMMO;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class AdvancedConfig extends ConfigValidated {

    public static final String SKILLS = "Skills";
    public static final String GENERAL = "General";
    public static final String ABILITY = "Ability";
    public static final String LENGTH = "Length";
    public static final String INCREASE_LEVEL = "IncreaseLevel";
    public static final String ENCHANT_BUFF = "EnchantBuff";
    public static final String ACROBATICS = "Acrobatics";
    public static final String DODGE = "Dodge";
    public static final String CHANCE = "Chance";
    public static final String CHANCE_MAX = CHANCE + "Max";
    public static final String BONUS = "Bonus";
    public static final String MAX_BONUS_LEVEL = "Max" + BONUS + "Level";
    public static final String MODIFIER = "Modifier";
    public static final String DAMAGE_MODIFIER = "Damage" + MODIFIER;
    public static final String DAMAGE_THRESHOLD = "DamageThreshold";
    public static final String ALCHEMY = "Alchemy";
    public static final String CATALYSIS = "Catalysis";
    public static final String MIN_SPEED = "MinSpeed";
    public static final String MAX_SPEED = "MaxSpeed";
    public static final String ARCHERY = "Archery";
    public static final String SKILL_SHOT = "SkillShot";
    public static final String MULTIPLIER = "Multiplier";
    public static final String RANK_DAMAGE_MULTIPLIER = "RankDamage" + MULTIPLIER;
    public static final String BONUS_DAMAGE = BONUS + "Damage";
    public static final String FORCE_MULTIPLIER = "Force" + MULTIPLIER;
    public static final String AXES = "Axes";
    public static final String STANDARD = "Standard";
    public static final String RETRO_MODE = "RetroMode";
    public static final String CAP_LEVEL = "CapLevel";
    public static final String KNOCKBACK_MODIFIER = "Knockback" + MODIFIER;
    public static final String PVP_MODIFIER = "PVP_" + MODIFIER;
    public static final String PVE_MODIFIER = "PVE_" + MODIFIER;
    public static final String FISHING = "Fishing";
    public static final String MASTER_ANGLER = "MasterAngler";
    public static final String BOAT_MODIFIER = "Boat" + MODIFIER;
    public static final String BIOME_MODIFIER = "Biome" + MODIFIER;
    public static final String XP = "XP";
    public static final String VANILLA_XPMULTIPLIER = "Vanilla" + XP + MULTIPLIER;
    public static final String RANK = "Rank_";
    public static final String TAMING = "Taming";
    public static final String CALL_OF_THE_WILD = "CallOfTheWild";
    public static final String MIN_HORSE_JUMP_STRENGTH = "MinHorseJumpStrength";
    public static final String MAX_HORSE_JUMP_STRENGTH = "MaxHorseJumpStrength";
    public static final String SHOCK_PROOF = "ShockProof";
    public static final String UNARMED = "Unarmed";
    public static final String STARTING_LEVEL = "StartingLevel";
    public static final String AXE_MASTERY = "AxeMastery";
    public static final String CRITICAL_STRIKES = "CriticalStrikes";
    public static final String GREATER_IMPACT = "GreaterImpact";
    public static final String ARMOR_IMPACT = "ArmorImpact";
    public static final String SKULL_SPLITTER = "SkullSplitter";
    public static final String MAX_PERCENTAGE_DURABILITY_DAMAGE = "MaxPercentageDurabilityDamage";
    public static final String SHAKE = "Shake";
    public static final String MINING = "Mining";
    public static final String BLAST_MINING = "BlastMining";
    public static final String LEVELS = "Levels";
    public static final String BLAST_DAMAGE_DECREASE = "BlastDamageDecrease";
    public static final String ORE_BONUS = "Ore" + BONUS;
    public static final String DEBRIS_REDUCTION = "DebrisReduction";
    public static final String DROP_MULTIPLIER = "Drop" + MULTIPLIER;
    public static final String BLAST_RADIUS = "BlastRadius";
    public static final String REPAIR = "Repair";
    public static final String REPAIR_MASTERY = "RepairMastery";
    public static final String MAX_BONUS_PERCENTAGE = "Max" + BONUS + "Percentage";
    public static final String ARCANE_FORGING = "ArcaneForging";
    public static final String MAY_LOSE_ENCHANTS = "May_Lose_Enchants";
    public static final String KEEP_ENCHANTS = "Keep_Enchants_";
    public static final String DOWNGRADES = "Downgrades_";
    public static final String ENABLED = "Enabled";
    public static final String DOWNGRADES_ENABLED = DOWNGRADES + ENABLED;
    public static final String SALVAGE = "Salvage";
    public static final String ARCANE_SALVAGE = "ArcaneSalvage";
    public static final String ENCHANT_DOWNGRADE_ENABLED = "EnchantDowngrade" + ENABLED;
    public static final String ENCHANT_LOSS_ENABLED = "EnchantLoss" + ENABLED;
    public static final String EXTRACT_FULL_ENCHANT = "ExtractFullEnchant";
    public static final String EXTRACT_PARTIAL_ENCHANT = "ExtractPartialEnchant";
    public static final String SMELTING = "Smelting";
    public static final String FUEL_EFFICIENCY = "FuelEfficiency";
    public static final String SWORDS = "Swords";
    public static final String RUPTURE = "Rupture";
    public static final String DAMAGE_PLAYER = "DamagePlayer";
    public static final String DAMAGE_MOBS = "DamageMobs";
    public static final String MAX_TICKS = "MaxTicks";
    public static final String BASE_TICKS = "BaseTicks";
    public static final String COUNTER_ATTACK = "CounterAttack";
    public static final String SERRATED_STRIKES = "SerratedStrikes";
    public static final String TICKS = "Ticks";
    public static final String GORE = "Gore";
    public static final String FAST_FOOD = "FastFood";
    public static final String FAST_FOOD_SERVICE = FAST_FOOD + "Service";
    public static final String PUMMEL = "Pummel";
    public static final String THICK_FUR = "ThickFur";
    public static final String SHARPENED_CLAWS = "SharpenedClaws";
    public static final String DISARM = "Disarm";
    public static final String ANTI_THEFT = "AntiTheft";
    public static final String DAZE = "Daze";
    public static final String MAX_DAMAGE = "MaxDamage";
    public static final String ROLL = "Roll";
    public static final String GRACEFUL_ROLL = "Graceful" + ROLL;
    public static final String ARROW_DEFLECT = "ArrowDeflect";
    public static final String IRON_GRIP = "IronGrip";
    public static final String WOODCUTTING = "Woodcutting";
    public static final String HARVEST_LUMBER = "HarvestLumber";
    public static final String FEEDBACK = "Feedback";
    public static final String SKILL_COMMAND = "SkillCommand";
    public static final String BLANK_LINES_ABOVE_HEADER = "BlankLinesAboveHeader";
    public static final String ACTION_BAR_NOTIFICATIONS = "ActionBarNotifications";
    public static final String SEND_COPY_OF_MESSAGE_TO_CHAT = "SendCopyOfMessageToChat";
    public static final String EVENTS = "Events";
    public static final String SEND_TITLES = "SendTitles";
    //private static AdvancedConfig instance;

    public AdvancedConfig() {
        //super(mcMMO.getDataFolderPath().getAbsoluteFile(), "advanced.yml", true);
        super("advanced", mcMMO.p.getDataFolder().getAbsoluteFile(), ConfigConstants.RELATIVE_PATH_CONFIG_DIR, true, true, true, true);
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
    public static AdvancedConfig getInstance() {
        return mcMMO.getConfigManager().getAdvancedConfig();
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

    /* GENERAL */

    /**
     * This returns the maximum level at which superabilities will stop lengthening from scaling alongside skill level.
     * It returns a different value depending on whether or not the server is in retro mode
     *
     * @return the level at which abilities stop increasing in length
     */
    public int getAbilityLengthCap() {
        if (!mcMMO.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, CAP_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, CAP_LEVEL);
    }

    /**
     * This returns the frequency at which abilities will increase in length
     * It returns a different value depending on whether or not the server is in retro mode
     *
     * @return the number of levels required per ability length increase
     */
    public int getAbilityLength() {
        if (!mcMMO.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, INCREASE_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, INCREASE_LEVEL);
    }

    public int getEnchantBuff() {
        return getIntValue(SKILLS, GENERAL, ABILITY, ENCHANT_BUFF);
    }

    /**
     * Grabs the max bonus level for a skill used in RNG calculations
     * All max level values in the config are multiplied by 10 if the server is in retro mode as the values in the config are based around the new 1-100 skill system scaling
     * A value of 10 in the file will be returned as 100 for retro mode servers to accommodate the change in scaling
     *
     * @param subSkillType target subskill
     * @return the level at which this skills max benefits will be reached on the curve
     */
    public int getMaxBonusLevel(SubSkillType subSkillType) {
        String[] category = subSkillType.getAdvConfigAddress();

        if (!mcMMO.isRetroModeEnabled())
            return getIntValue(category[0], category[1], category[2], MAX_BONUS_LEVEL, STANDARD);
        else
            return getIntValue(category[0], category[1], category[2], MAX_BONUS_LEVEL, RETRO_MODE);
    }

    public int getMaxBonusLevel(AbstractSubSkill abstractSubSkill) {
        return getMaxBonusLevel(abstractSubSkill.getSubSkillType());
    }

    public double getMaximumProbability(SubSkillType subSkillType) {
        String[] category = subSkillType.getAdvConfigAddress();

        double maximumProbability = getDoubleValue(category[0], category[1], category[2], CHANCE_MAX);

        return maximumProbability;
    }

    public double getMaximumProbability(AbstractSubSkill abstractSubSkill) {
        return getMaximumProbability(abstractSubSkill.getSubSkillType());
    }

    /* Notification Settings */

    public boolean doesSkillCommandSendBlankLines() {
        return getBooleanValue(FEEDBACK, SKILL_COMMAND, BLANK_LINES_ABOVE_HEADER);
    }

    public boolean doesNotificationUseActionBar(NotificationType notificationType) {
        return getBooleanValue(FEEDBACK, ACTION_BAR_NOTIFICATIONS, notificationType.toString(), ENABLED);
    }

    public boolean doesNotificationSendCopyToChat(NotificationType notificationType) {
        return getBooleanValue(FEEDBACK, ACTION_BAR_NOTIFICATIONS, notificationType.toString(), SEND_COPY_OF_MESSAGE_TO_CHAT);
    }

    public boolean useTitlesForXPEvent() {
        return getBooleanValue(FEEDBACK, EVENTS, XP, SEND_TITLES);
    }

    private ChatColor getChatColorFromKey(String keyLocation) {
        String colorName = getStringValue(keyLocation);

        return getChatColor(colorName);
    }

    private ChatColor getChatColor(String configColor) {
        for (ChatColor chatColor : ChatColor.values()) {
            if (configColor.equalsIgnoreCase(chatColor.toString()))
                return chatColor;
        }

        //Invalid Color
        System.out.println("[mcMMO] " + configColor + " is an invalid color value");
        return ChatColor.WHITE;
    }

    /* ACROBATICS */
    public double getDodgeDamageModifier() {
        return getDoubleValue(SKILLS, ACROBATICS, DODGE, DAMAGE_MODIFIER);
    }

    public double getRollDamageThreshold() {
        return getDoubleValue(SKILLS, ACROBATICS, ROLL, DAMAGE_THRESHOLD);
    }

    public double getGracefulRollDamageThreshold() {
        return getDoubleValue(SKILLS, ACROBATICS, GRACEFUL_ROLL, DAMAGE_THRESHOLD);
    }

    /* ALCHEMY */
    public int getCatalysisMaxBonusLevel() {
        return getIntValue(SKILLS, ALCHEMY, CATALYSIS, MAX_BONUS_LEVEL);
    }

    public double getCatalysisMinSpeed() {
        return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MIN_SPEED);
    }

    public double getCatalysisMaxSpeed() {
        return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MAX_SPEED);
    }

    /* ARCHERY */
    public double getSkillShotRankDamageMultiplier() {
        return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT, RANK_DAMAGE_MULTIPLIER);
    }

    public double getSkillShotDamageMax() {
        return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT, MAX_DAMAGE);
    }

    public double getDazeBonusDamage() {
        return getDoubleValue(SKILLS, ARCHERY, DAZE, BONUS_DAMAGE);
    }

    public double getForceMultiplier() {
        return getDoubleValue(SKILLS, ARCHERY, FORCE_MULTIPLIER);
    }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public double getShakeChance(int rank) {
        return getDoubleValue(SKILLS, FISHING, SHAKE, CHANCE, RANK, String.valueOf(rank));
    }

    public double getMasterAnglerBoatModifier() {
        return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BOAT_MODIFIER);
    }

    public double getMasterAnglerBiomeModifier() {
        return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BIOME_MODIFIER);
    }

    /* HERBALISM */
    //public int getFarmerDietRankChange() { return getIntValue(SKILLS, ".Herbalism.FarmersDiet.RankChange"); }

    //public int getGreenThumbStageChange() { return getIntValue(SKILLS, ".Herbalism.GreenThumb.StageChange"); }

    /* MINING */
    public boolean getDoubleDropSilkTouchEnabled() {
        return getBooleanValue(SKILLS, MINING, "DoubleDrops", "SilkTouch");
    }

    public int getBlastMiningRankLevel(int rank) {
        return getIntValue(SKILLS, MINING, BLAST_MINING, RANK, LEVELS, RANK, String.valueOf(rank));
    }

    public double getBlastDamageDecrease(int rank) {
        return getDoubleValue(SKILLS, MINING, BLAST_MINING, BLAST_DAMAGE_DECREASE, RANK, String.valueOf(rank));
    }

    public double getOreBonus(int rank) {
        return getDoubleValue(SKILLS, MINING, BLAST_MINING, ORE_BONUS, RANK, String.valueOf(rank));
    }

    public double getDebrisReduction(int rank) {
        return getDoubleValue(SKILLS, MINING, BLAST_MINING, DEBRIS_REDUCTION, RANK, String.valueOf(rank));
    }

    public int getDropMultiplier(int rank) {
        return getIntValue(SKILLS, MINING, BLAST_MINING, DROP_MULTIPLIER, RANK, String.valueOf(rank));
    }

    public double getBlastRadiusModifier(int rank) {
        return getDoubleValue(SKILLS, MINING, BLAST_MINING, BLAST_RADIUS, MODIFIER, RANK, String.valueOf(rank));
    }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() {
        return getDoubleValue(SKILLS, REPAIR, REPAIR_MASTERY, MAX_BONUS_PERCENTAGE);
    }
    //public int getRepairMasteryMaxLevel() { return getIntValue(SKILLS, REPAIR, REPAIR_MASTERY, MAX_BONUS_LEVEL); }

    /* Arcane Forging */
    public boolean getArcaneForgingEnchantLossEnabled() {
        return getBooleanValue(SKILLS, REPAIR, ARCANE_FORGING, MAY_LOSE_ENCHANTS);
    }

    public double getArcaneForgingKeepEnchantsChance(int rank) {
        return getDoubleValue(SKILLS, REPAIR, ARCANE_FORGING, KEEP_ENCHANTS, CHANCE, RANK, String.valueOf(rank));
    }

    public boolean getArcaneForgingDowngradeEnabled() {
        return getBooleanValue(SKILLS, REPAIR, ARCANE_FORGING, DOWNGRADES_ENABLED);
    }

    public double getArcaneForgingDowngradeChance(int rank) {
        return getDoubleValue(SKILLS, REPAIR, ARCANE_FORGING, DOWNGRADES, CHANCE, RANK, String.valueOf(rank));
    }

    /* SALVAGE */

    public boolean getArcaneSalvageEnchantDowngradeEnabled() {
        return getBooleanValue(SKILLS, SALVAGE, ARCANE_SALVAGE, ENCHANT_DOWNGRADE_ENABLED);
    }

    public boolean getArcaneSalvageEnchantLossEnabled() {
        return getBooleanValue(SKILLS, SALVAGE, ARCANE_SALVAGE, ENCHANT_LOSS_ENABLED);
    }

    public double getArcaneSalvageExtractFullEnchantsChance(int rank) {
        return getDoubleValue(SKILLS, SALVAGE, ARCANE_SALVAGE, EXTRACT_FULL_ENCHANT, RANK, String.valueOf(rank));
    }

    public double getArcaneSalvageExtractPartialEnchantsChance(int rank) {
        return getDoubleValue(SKILLS, SALVAGE, ARCANE_SALVAGE, EXTRACT_PARTIAL_ENCHANT, RANK, String.valueOf(rank));
    }

    /* SMELTING */
    //public int getBurnModifierMaxLevel() { return getIntValue(SKILLS, SMELTING, FUEL_EFFICIENCY, MAX_BONUS_LEVEL); }
    public double getBurnTimeMultiplier() {
        return getDoubleValue(SKILLS, SMELTING, FUEL_EFFICIENCY, MULTIPLIER);
    }

    public int getSmeltingRankLevel(int rank) {
        return getIntValue(SKILLS, SMELTING, RANK, LEVELS, RANK, String.valueOf(rank));
    }

    public int getSmeltingVanillaXPBoostMultiplier(int rank) {
        return getIntValue(SKILLS, SMELTING, VANILLA_XPMULTIPLIER, RANK, String.valueOf(rank));
    }

    /* SWORDS */
    public double getRuptureDamagePlayer() {
        return getDoubleValue(SKILLS, SWORDS, RUPTURE, DAMAGE_PLAYER);
    }

    public double getRuptureDamageMobs() {
        return getDoubleValue(SKILLS, SWORDS, RUPTURE, DAMAGE_MOBS);
    }

    public int getRuptureMaxTicks() {
        return getIntValue(SKILLS, SWORDS, RUPTURE, MAX_TICKS);
    }

    public int getRuptureBaseTicks() {
        return getIntValue(SKILLS, SWORDS, RUPTURE, BASE_TICKS);
    }

    public double getCounterAttackModifier() {
        return getDoubleValue(SKILLS, SWORDS, COUNTER_ATTACK, DAMAGE_MODIFIER);
    }

    public double getSerratedStrikesModifier() {
        return getDoubleValue(SKILLS, SWORDS, SERRATED_STRIKES, DAMAGE_MODIFIER);
    }
    //public int getSerratedStrikesTicks() { return getIntValue(SKILLS, SWORDS, SERRATED_STRIKES, RUPTURE, TICKS); }

    /* TAMING */
    public double getGoreModifier() {
        return getDoubleValue(SKILLS, TAMING, GORE, MODIFIER);
    }

    public double getFastFoodChance() {
        return getDoubleValue(SKILLS, TAMING, FAST_FOOD_SERVICE, CHANCE);
    }

    public double getPummelChance() {
        return getDoubleValue(SKILLS, TAMING, PUMMEL, CHANCE);
    }

    public double getThickFurModifier() {
        return getDoubleValue(SKILLS, TAMING, THICK_FUR, MODIFIER);
    }

    public double getShockProofModifier() {
        return getDoubleValue(SKILLS, TAMING, SHOCK_PROOF, MODIFIER);
    }

    public double getSharpenedClawsBonus() {
        return getDoubleValue(SKILLS, TAMING, SHARPENED_CLAWS, BONUS);
    }

    public double getMinHorseJumpStrength() {
        return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MIN_HORSE_JUMP_STRENGTH);
    }

    public double getMaxHorseJumpStrength() {
        return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MAX_HORSE_JUMP_STRENGTH);
    }

    /* UNARMED */
    public boolean getDisarmProtected() {
        return getBooleanValue(SKILLS, UNARMED, DISARM, ANTI_THEFT);
    }

    /* WOODCUTTING */
}
