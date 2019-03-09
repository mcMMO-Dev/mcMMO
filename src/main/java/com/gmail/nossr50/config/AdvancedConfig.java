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

    @Override
    public void unload() {
        //do nothing
    }

    /**
     * This grabs an instance of this config class from the Config Manager
     * This method is deprecated and will be removed in the future
     * @see mcMMO#getConfigManager()
     * @return the instance of this config
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
        List<String> reason = new ArrayList<String>();

        /* GENERAL */
        if (getAbilityLength() < 1) {
            reason.add(SKILLS + "." + GENERAL + "." + ABILITY + "." + LENGTH + ".<mode>." + INCREASE_LEVEL + " should be at least 1!");
        }

        if (getEnchantBuff() < 1) {
            reason.add(SKILLS + "." + GENERAL + "." + ABILITY + "." + ENCHANT_BUFF + " should be at least 1!");
        }

        /* ACROBATICS */
        if (getMaximumProbability(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_DODGE) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getDodgeDamageModifier() <= 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + DODGE + "." + DAMAGE_MODIFIER + " should be greater than 1!");
        }

        if (getMaximumProbability(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + ROLL + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL) < 1) {
            reason.add(SKILLS + "." + ACROBATICS + "." + ROLL + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getRollDamageThreshold() < 0) {
            reason.add(SKILLS + "." + ACROBATICS + "." + ROLL + "." + DAMAGE_THRESHOLD + " should be at least 0!");
        }

        if (getGracefulRollDamageThreshold() < 0) {
            reason.add(SKILLS + "." + ACROBATICS + "." + GRACEFUL_ROLL + "." + DAMAGE_THRESHOLD + " should be at least 0!");
        }

        if (getCatalysisMinSpeed() <= 0) {
            reason.add(SKILLS + "." + ALCHEMY + "." + CATALYSIS + "." + MIN_SPEED + " must be greater than 0!");
        }

        if (getCatalysisMaxSpeed() < getCatalysisMinSpeed()) {
            reason.add(SKILLS + "." + ALCHEMY + "." + CATALYSIS + "." + MAX_SPEED + " should be at least Skills.Alchemy.Catalysis." + MIN_SPEED + "!");
        }

        /* ARCHERY */

        if (getSkillShotRankDamageMultiplier() <= 0) {
            reason.add(SKILLS + "." + ARCHERY + "." + SKILL_SHOT + "." + RANK_DAMAGE_MULTIPLIER + " should be greater than 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add(SKILLS + "." + ARCHERY + "." + DAZE + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_DAZE) < 1) {
            reason.add(SKILLS + "." + ARCHERY + "." + DAZE + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getDazeBonusDamage() < 0) {
            reason.add(SKILLS + "." + ARCHERY + "." + DAZE + "." + BONUS_DAMAGE + " should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Retrieve." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.ARCHERY_ARROW_RETRIEVAL) < 1) {
            reason.add(SKILLS + "." + ARCHERY + ".Retrieve." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getForceMultiplier() < 0) {
            reason.add(SKILLS + "." + ARCHERY + "." + FORCE_MULTIPLIER + " should be at least 0!");
        }

        /* AXES */
        if(getAxeMasteryRankDamageMultiplier() < 0)
        {
            reason.add(SKILLS + "." + AXES + "." + AXE_MASTERY + "." + RANK_DAMAGE_MULTIPLIER + " should be at least 0!");
        }

        if (getMaximumProbability(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalHit." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.AXES_CRITICAL_STRIKES) < 1) {
            reason.add(SKILLS + "." + AXES + ".CriticalHit." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add(SKILLS + "." + AXES + "." + CRITICAL_STRIKES + "." + PVP_MODIFIER + " should be at least 1!");
        }

        if (getCriticalStrikesPVPModifier() < 1) {
            reason.add(SKILLS + "." + AXES + "." + CRITICAL_STRIKES + "." + PVE_MODIFIER + " should be at least 1!");
        }

        if (getGreaterImpactChance() < 1) {
            reason.add(SKILLS + "." + AXES + "." + GREATER_IMPACT + "." + CHANCE + " should be at least 1!");
        }

        if (getGreaterImpactModifier() < 1) {
            reason.add(SKILLS + "." + AXES + "." + GREATER_IMPACT + "." + KNOCKBACK_MODIFIER + " should be at least 1!");
        }

        if (getGreaterImpactBonusDamage() < 1) {
            reason.add(SKILLS + "." + AXES + "." + GREATER_IMPACT + "." + BONUS_DAMAGE + " should be at least 1!");
        }

        if (getArmorImpactIncreaseLevel() < 1) {
            reason.add(SKILLS + "." + AXES + "." + ARMOR_IMPACT + "." + INCREASE_LEVEL + " should be at least 1!");
        }

        if (getImpactChance() < 1) {
            reason.add(SKILLS + "." + AXES + "." + ARMOR_IMPACT + "." + CHANCE + " should be at least 1!");
        }

        if (getArmorImpactMaxDurabilityDamage() < 1) {
            reason.add(SKILLS + "." + AXES + "." + ARMOR_IMPACT + "." + MAX_PERCENTAGE_DURABILITY_DAMAGE + " should be at least 1!");
        }

        if (getSkullSplitterModifier() < 1) {
            reason.add(SKILLS + "." + AXES + "." + SKULL_SPLITTER + DAMAGE_MODIFIER + " should be at least 1!");
        }

        /*if (getFishermanDietRankChange() < 1) {
            reason.add(SKILLS + "." + FISHING + ".FishermansDiet.RankChange should be at least 1!");
        }*/

        if (getMasterAnglerBoatModifier() < 1) {
            reason.add(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BOAT_MODIFIER + " should be at least 1!");
        }

        if (getMasterAnglerBiomeModifier() < 1) {
            reason.add(SKILLS + "." + FISHING + "." + MASTER_ANGLER + "." + BIOME_MODIFIER + " should be at least 1!");
        }

        /* HERBALISM */
        /*if (getFarmerDietRankChange() < 1) {
            reason.add(SKILLS + ".Herbalism.FarmersDiet.RankChange should be at least 1!");
        }

        if (getGreenThumbStageChange() < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb.StageChange should be at least 1!");
        }*/

        if (getMaximumProbability(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_GREEN_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.GreenThumb." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Herbalism.DoubleDrops." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + ".Herbalism.DoubleDrops." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add(SKILLS + ".Herbalism.HylianLuck." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_HYLIAN_LUCK) < 1) {
            reason.add(SKILLS + ".Herbalism.HylianLuck." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.ShroomThumb." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.HERBALISM_SHROOM_THUMB) < 1) {
            reason.add(SKILLS + ".Herbalism.ShroomThumb." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* MINING */
        if (getMaximumProbability(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + "." + MINING + ".DoubleDrops." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.MINING_DOUBLE_DROPS) < 1) {
            reason.add(SKILLS + "." + MINING + ".DoubleDrops." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* REPAIR */
        /*
        if (getRepairMasteryMaxLevel() < 1) {
            reason.add(SKILLS + "." + REPAIR + "." + REPAIR_MASTERY + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }*/

        if (getRepairMasteryMaxBonus() < 1) {
            reason.add(SKILLS + "." + REPAIR + "." + REPAIR_MASTERY + "." + MAX_BONUS_PERCENTAGE + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add(SKILLS + "." + REPAIR + ".SuperRepair." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.REPAIR_SUPER_REPAIR) < 1) {
            reason.add(SKILLS + "." + REPAIR + ".SuperRepair." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* SMELTING */
        if (getMaxBonusLevel(SubSkillType.SMELTING_FUEL_EFFICIENCY) < 1) {
            reason.add(SKILLS + "." + SMELTING + "." + FUEL_EFFICIENCY + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add(SKILLS + "." + SMELTING + ".SecondSmelt." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SMELTING_SECOND_SMELT) < 1) {
            reason.add(SKILLS + "." + SMELTING + ".SecondSmelt." + CHANCE_MAX + " should be at least 1!");
        }

        /* SWORDS */
        if (getMaximumProbability(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + RUPTURE + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_RUPTURE) < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + RUPTURE + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getRuptureMaxTicks() < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + RUPTURE + "." + MAX_TICKS + " should be at least 1!");
        }

        if (getRuptureMaxTicks() < getRuptureBaseTicks()) {
            reason.add(SKILLS + "." + SWORDS + "." + RUPTURE + "." + MAX_TICKS + " should be at least Skills.Swords.Rupture." + BASE_TICKS + "!");
        }

        if (getRuptureBaseTicks() < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + RUPTURE + "." + BASE_TICKS + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + COUNTER_ATTACK + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.SWORDS_COUNTER_ATTACK) < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + COUNTER_ATTACK + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getCounterAttackModifier() < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + COUNTER_ATTACK + "." + DAMAGE_MODIFIER + " should be at least 1!");
        }

        if (getSerratedStrikesModifier() < 1) {
            reason.add(SKILLS + "." + SWORDS + "." + SERRATED_STRIKES + "." + DAMAGE_MODIFIER + " should be at least 1!");
        }

        /* TAMING */

        if (getMaximumProbability(SubSkillType.TAMING_GORE) < 1) {
            reason.add(SKILLS + "." + TAMING + "." + GORE + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.TAMING_GORE) < 1) {
            reason.add(SKILLS + "." + TAMING + "." + GORE + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getGoreModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + GORE + "." + MODIFIER + " should be at least 1!");
        }

        if (getFastFoodChance() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + FAST_FOOD + "." + CHANCE + " should be at least 1!");
        }

        if (getThickFurModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + THICK_FUR + "." + MODIFIER + " should be at least 1!");
        }

        if (getShockProofModifier() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + SHOCK_PROOF + "." + MODIFIER + " should be at least 1!");
        }

        if (getSharpenedClawsBonus() < 1) {
            reason.add(SKILLS + "." + TAMING + "." + SHARPENED_CLAWS + "." + BONUS + " should be at least 1!");
        }

        if (getMaxHorseJumpStrength() < 0 || getMaxHorseJumpStrength() > 2) {
            reason.add(SKILLS + "." + TAMING + "." + CALL_OF_THE_WILD + "." + MAX_HORSE_JUMP_STRENGTH + " should be between 0 and 2!");
        }

        /* UNARMED */
        if (getMaximumProbability(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + DISARM + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_DISARM) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + DISARM + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + ARROW_DEFLECT + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_ARROW_DEFLECT) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + ARROW_DEFLECT + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        if (getMaximumProbability(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + IRON_GRIP + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.UNARMED_IRON_GRIP) < 1) {
            reason.add(SKILLS + "." + UNARMED + "." + IRON_GRIP + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        /* WOODCUTTING */

        /*if (getLeafBlowUnlockLevel() < 0) {
            reason.add("Skills.Woodcutting.LeafBlower.UnlockLevel should be at least 0!");
        }*/

        if (getMaximumProbability(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add(SKILLS + "." + WOODCUTTING + "." + HARVEST_LUMBER + "." + CHANCE_MAX + " should be at least 1!");
        }

        if (getMaxBonusLevel(SubSkillType.WOODCUTTING_HARVEST_LUMBER) < 1) {
            reason.add(SKILLS + "." + WOODCUTTING + "." + HARVEST_LUMBER + "." + MAX_BONUS_LEVEL + " should be at least 1!");
        }

        return reason;
    }

    /* GENERAL */
    public int getStartingLevel() { return getIntValue(SKILLS, GENERAL, STARTING_LEVEL); }

    /**
     * This returns the maximum level at which superabilities will stop lengthening from scaling alongside skill level.
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the level at which abilities stop increasing in length
     */
    public int getAbilityLengthCap() {
        if(!mcMMO.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, CAP_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, CAP_LEVEL);
    }

    /**
     * This returns the frequency at which abilities will increase in length
     * It returns a different value depending on whether or not the server is in retro mode
     * @return the number of levels required per ability length increase
     */
    public int getAbilityLength() {
        if(!mcMMO.isRetroModeEnabled())
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, STANDARD, INCREASE_LEVEL);
        else
            return getIntValue(SKILLS, GENERAL, ABILITY, LENGTH, RETRO_MODE, INCREASE_LEVEL);
    }

    public int getEnchantBuff() { return getIntValue(SKILLS, GENERAL, ABILITY, ENCHANT_BUFF); }

    /**
     * Grabs the max bonus level for a skill used in RNG calculations
     * All max level values in the config are multiplied by 10 if the server is in retro mode as the values in the config are based around the new 1-100 skill system scaling
     * A value of 10 in the file will be returned as 100 for retro mode servers to accommodate the change in scaling
     * @param subSkillType target subskill
     * @return the level at which this skills max benefits will be reached on the curve
     */
    public int getMaxBonusLevel(SubSkillType subSkillType) {
        String[] category = subSkillType.getAdvConfigAddress();
        
        if(!mcMMO.isRetroModeEnabled())
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

    public double getMaximumProbability(AbstractSubSkill abstractSubSkill)
    {
        return getMaximumProbability(abstractSubSkill.getSubSkillType());
    }

    /* Notification Settings */

    public boolean doesSkillCommandSendBlankLines()
    {
        return getBooleanValue(FEEDBACK,  SKILL_COMMAND,  BLANK_LINES_ABOVE_HEADER);
    }

    public boolean doesNotificationUseActionBar(NotificationType notificationType)
    {
        return getBooleanValue(FEEDBACK,  ACTION_BAR_NOTIFICATIONS, notificationType.toString(),  ENABLED);
    }

    public boolean doesNotificationSendCopyToChat(NotificationType notificationType)
    {
        return getBooleanValue(FEEDBACK,  ACTION_BAR_NOTIFICATIONS, notificationType.toString(),  SEND_COPY_OF_MESSAGE_TO_CHAT);
    }

    public boolean useTitlesForXPEvent()
    {
        return getBooleanValue(FEEDBACK,  EVENTS,  XP,  SEND_TITLES);
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
    public double getDodgeDamageModifier() { return getDoubleValue(SKILLS, ACROBATICS, DODGE, DAMAGE_MODIFIER); }

    public double getRollDamageThreshold() { return getDoubleValue(SKILLS, ACROBATICS, ROLL, DAMAGE_THRESHOLD); }

    public double getGracefulRollDamageThreshold() { return getDoubleValue(SKILLS, ACROBATICS, GRACEFUL_ROLL, DAMAGE_THRESHOLD); }

    /* ALCHEMY */
    public int getCatalysisMaxBonusLevel() { return getIntValue(SKILLS, ALCHEMY, CATALYSIS, MAX_BONUS_LEVEL); }

    public double getCatalysisMinSpeed() { return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MIN_SPEED); }
    public double getCatalysisMaxSpeed() { return getDoubleValue(SKILLS, ALCHEMY, CATALYSIS, MAX_SPEED); }

    /* ARCHERY */
    public double getSkillShotRankDamageMultiplier() { return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT, RANK_DAMAGE_MULTIPLIER); }
    public double getSkillShotDamageMax() { return getDoubleValue(SKILLS, ARCHERY, SKILL_SHOT, MAX_DAMAGE); }

    public double getDazeBonusDamage() { return getDoubleValue(SKILLS, ARCHERY, DAZE, BONUS_DAMAGE); }

    public double getForceMultiplier() { return getDoubleValue(SKILLS, ARCHERY, FORCE_MULTIPLIER); }

    /* AXES */
    public double getAxeMasteryRankDamageMultiplier() { return getDoubleValue(SKILLS, AXES, AXE_MASTERY, RANK_DAMAGE_MULTIPLIER); }

    public double getCriticalStrikesPVPModifier() { return getDoubleValue(SKILLS, AXES, CRITICAL_STRIKES, PVP_MODIFIER); }
    public double getCriticalStrikesPVEModifier() { return getDoubleValue(SKILLS, AXES, CRITICAL_STRIKES, PVE_MODIFIER); }

    public double getGreaterImpactChance() { return getDoubleValue(SKILLS, AXES, GREATER_IMPACT, CHANCE); }
    public double getGreaterImpactModifier() { return getDoubleValue(SKILLS, AXES, GREATER_IMPACT, KNOCKBACK_MODIFIER); }
    public double getGreaterImpactBonusDamage() { return getDoubleValue(SKILLS, AXES, GREATER_IMPACT, BONUS_DAMAGE); }

    public int getArmorImpactIncreaseLevel() {
        int increaseLevel = getIntValue(SKILLS, AXES, ARMOR_IMPACT, INCREASE_LEVEL);

        if(mcMMO.isRetroModeEnabled())
            return increaseLevel * 10;

        return increaseLevel;
    }

    public double getImpactChance() { return getDoubleValue(SKILLS, AXES, ARMOR_IMPACT, CHANCE); }
    public double getArmorImpactMaxDurabilityDamage() { return getDoubleValue(SKILLS, AXES, ARMOR_IMPACT, MAX_PERCENTAGE_DURABILITY_DAMAGE); }

    public double getSkullSplitterModifier() { return getDoubleValue(SKILLS, AXES, SKULL_SPLITTER, DAMAGE_MODIFIER); }

    /* EXCAVATION */
    //Nothing to configure, everything is already configurable in config.yml

    /* FISHING */
    public double getShakeChance(int rank) { return getDoubleValue(SKILLS, FISHING, SHAKE, CHANCE, RANK, String.valueOf(rank)); }
    public int getFishingVanillaXPModifier(int rank) { return getIntValue(SKILLS, FISHING, VANILLA_XPMULTIPLIER, RANK, String.valueOf(rank)); }
    public double getMasterAnglerBoatModifier() {return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BOAT_MODIFIER); }
    public double getMasterAnglerBiomeModifier() {return getDoubleValue(SKILLS, FISHING, MASTER_ANGLER, BIOME_MODIFIER); }

    /* HERBALISM */
    //public int getFarmerDietRankChange() { return getIntValue(SKILLS, ".Herbalism.FarmersDiet.RankChange"); }

    //public int getGreenThumbStageChange() { return getIntValue(SKILLS, ".Herbalism.GreenThumb.StageChange"); }

    /* MINING */
    public boolean getDoubleDropSilkTouchEnabled() { return getBooleanValue(SKILLS, MINING, "DoubleDrops", "SilkTouch"); }
    public int getBlastMiningRankLevel(int rank) { return getIntValue(SKILLS, MINING, BLAST_MINING, RANK, LEVELS, RANK, String.valueOf(rank)); }
    public double getBlastDamageDecrease(int rank) { return getDoubleValue(SKILLS, MINING, BLAST_MINING, BLAST_DAMAGE_DECREASE, RANK, String.valueOf(rank)); }
    public double getOreBonus(int rank) { return getDoubleValue(SKILLS, MINING, BLAST_MINING, ORE_BONUS, RANK, String.valueOf(rank)); }
    public double getDebrisReduction(int rank) { return getDoubleValue(SKILLS, MINING, BLAST_MINING, DEBRIS_REDUCTION, RANK, String.valueOf(rank)); }
    public int getDropMultiplier(int rank) { return getIntValue(SKILLS, MINING, BLAST_MINING, DROP_MULTIPLIER, RANK, String.valueOf(rank)); }
    public double getBlastRadiusModifier(int rank) { return getDoubleValue(SKILLS, MINING, BLAST_MINING, BLAST_RADIUS, MODIFIER, RANK, String.valueOf(rank)); }

    /* REPAIR */
    public double getRepairMasteryMaxBonus() { return getDoubleValue(SKILLS, REPAIR, REPAIR_MASTERY, MAX_BONUS_PERCENTAGE); }
    //public int getRepairMasteryMaxLevel() { return getIntValue(SKILLS, REPAIR, REPAIR_MASTERY, MAX_BONUS_LEVEL); }

    /* Arcane Forging */
    public boolean getArcaneForgingEnchantLossEnabled() { return getBooleanValue(SKILLS, REPAIR, ARCANE_FORGING, MAY_LOSE_ENCHANTS); }
    public double getArcaneForgingKeepEnchantsChance(int rank) { return getDoubleValue(SKILLS, REPAIR, ARCANE_FORGING, KEEP_ENCHANTS, CHANCE, RANK, String.valueOf(rank)); }

    public boolean getArcaneForgingDowngradeEnabled() { return getBooleanValue(SKILLS, REPAIR, ARCANE_FORGING, DOWNGRADES_ENABLED); }
    public double getArcaneForgingDowngradeChance(int rank) { return getDoubleValue(SKILLS, REPAIR, ARCANE_FORGING, DOWNGRADES, CHANCE, RANK, String.valueOf(rank)); }

    /* SALVAGE */

    public boolean getArcaneSalvageEnchantDowngradeEnabled() { return getBooleanValue(SKILLS, SALVAGE, ARCANE_SALVAGE, ENCHANT_DOWNGRADE_ENABLED); }
    public boolean getArcaneSalvageEnchantLossEnabled() { return getBooleanValue(SKILLS, SALVAGE, ARCANE_SALVAGE, ENCHANT_LOSS_ENABLED); }

    public double getArcaneSalvageExtractFullEnchantsChance(int rank) { return getDoubleValue(SKILLS, SALVAGE, ARCANE_SALVAGE, EXTRACT_FULL_ENCHANT, RANK, String.valueOf(rank)); }
    public double getArcaneSalvageExtractPartialEnchantsChance(int rank) { return getDoubleValue(SKILLS, SALVAGE, ARCANE_SALVAGE, EXTRACT_PARTIAL_ENCHANT, RANK, String.valueOf(rank)); }

    /* SMELTING */
    //public int getBurnModifierMaxLevel() { return getIntValue(SKILLS, SMELTING, FUEL_EFFICIENCY, MAX_BONUS_LEVEL); }
    public double getBurnTimeMultiplier() { return getDoubleValue(SKILLS, SMELTING, FUEL_EFFICIENCY, MULTIPLIER); }

    public int getSmeltingRankLevel(int rank) { return getIntValue(SKILLS, SMELTING, RANK, LEVELS, RANK, String.valueOf(rank)); }

    public int getSmeltingVanillaXPBoostMultiplier(int rank) { return getIntValue(SKILLS, SMELTING, VANILLA_XPMULTIPLIER, RANK, String.valueOf(rank)); }

    /* SWORDS */
    public double getRuptureDamagePlayer() { return getDoubleValue(SKILLS, SWORDS, RUPTURE, DAMAGE_PLAYER); }
    public double getRuptureDamageMobs() { return getDoubleValue(SKILLS, SWORDS, RUPTURE, DAMAGE_MOBS); }

    public int getRuptureMaxTicks() { return getIntValue(SKILLS, SWORDS, RUPTURE, MAX_TICKS); }
    public int getRuptureBaseTicks() { return getIntValue(SKILLS, SWORDS, RUPTURE, BASE_TICKS); }

    public double getCounterAttackModifier() { return getDoubleValue(SKILLS, SWORDS, COUNTER_ATTACK, DAMAGE_MODIFIER); }

    public double getSerratedStrikesModifier() { return getDoubleValue(SKILLS, SWORDS, SERRATED_STRIKES, DAMAGE_MODIFIER); }
    //public int getSerratedStrikesTicks() { return getIntValue(SKILLS, SWORDS, SERRATED_STRIKES, RUPTURE, TICKS); }

    /* TAMING */
    public double getGoreModifier() { return getDoubleValue(SKILLS, TAMING, GORE, MODIFIER); }
    public double getFastFoodChance() { return getDoubleValue(SKILLS, TAMING, FAST_FOOD_SERVICE, CHANCE); }
    public double getPummelChance() { return getDoubleValue(SKILLS, TAMING, PUMMEL, CHANCE); }
    public double getThickFurModifier() { return getDoubleValue(SKILLS, TAMING, THICK_FUR, MODIFIER); }
    public double getShockProofModifier() { return getDoubleValue(SKILLS, TAMING, SHOCK_PROOF, MODIFIER); }

    public double getSharpenedClawsBonus() { return getDoubleValue(SKILLS, TAMING, SHARPENED_CLAWS, BONUS); }

    public double getMinHorseJumpStrength() { return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MIN_HORSE_JUMP_STRENGTH); }
    public double getMaxHorseJumpStrength() { return getDoubleValue(SKILLS, TAMING, CALL_OF_THE_WILD, MAX_HORSE_JUMP_STRENGTH); }

    /* UNARMED */
    public boolean getDisarmProtected() { return getBooleanValue(SKILLS, UNARMED, DISARM, ANTI_THEFT); }

    /* WOODCUTTING */
}
