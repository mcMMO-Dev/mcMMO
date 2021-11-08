package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillRandomCheckEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class RandomChanceUtil {
    public static final @NotNull DecimalFormat percent = new DecimalFormat("##0.00%");
    //public static final DecimalFormat decimal = new DecimalFormat("##0.00");
    public static final double LINEAR_CURVE_VAR = 100.0D;
    public static final double LUCKY_MODIFIER = 1.333D;

    /**
     * This method is the final step in determining if a Sub-Skill / Secondary Skill in mcMMO successfully activates either from chance or otherwise
     * Random skills check for success based on numbers and then fire a cancellable event, if that event is not cancelled they succeed
     * non-RNG skills just fire the cancellable event and succeed if they go uncancelled
     *
     * @param skillActivationType this value represents what kind of activation procedures this sub-skill uses
     * @param subSkillType        The identifier for this specific sub-skill
     * @param player              The owner of this sub-skill
     * @return returns true if all conditions are met and the event is not cancelled
     */
    public static boolean isActivationSuccessful(@NotNull SkillActivationType skillActivationType, @NotNull SubSkillType subSkillType, @Nullable Player player) {
        switch (skillActivationType) {
            case RANDOM_LINEAR_100_SCALE_WITH_CAP:
                return checkRandomChanceExecutionSuccess(player, subSkillType, true);
            case RANDOM_STATIC_CHANCE:
                return checkRandomStaticChanceExecutionSuccess(player, subSkillType);
            case ALWAYS_FIRES:
                SubSkillEvent event = EventUtils.callSubSkillEvent(player, subSkillType);
                return !event.isCancelled();
            default:
                return false;
        }
    }

    public static double getActivationChance(@NotNull SkillActivationType skillActivationType, @NotNull SubSkillType subSkillType, @Nullable Player player, boolean luckyOverride) {
        switch (skillActivationType) {
            case RANDOM_LINEAR_100_SCALE_WITH_CAP:
                return getRandomChanceExecutionSuccess(player, subSkillType, true, luckyOverride);
            case RANDOM_STATIC_CHANCE:
                return getRandomStaticChanceExecutionSuccess(player, subSkillType, luckyOverride);
            default:
                return 0.1337;
        }
    }

    /**
     * Checks whether or not the random chance succeeds
     *
     * @return true if the random chance succeeds
     */
    public static boolean checkRandomChanceExecutionSuccess(@NotNull Player player, @NotNull PrimarySkillType primarySkillType, double chance) {
        //Check the odds
        chance *= 100;

        chance = addLuck(player, primarySkillType, chance);

        /*
         * Stuff like treasures can specify a drop chance from 0.05 to 100
         * Because of that we need to use a large int bound and multiply the chance by 100
         */
        return rollDice(chance, 10000);
    }

    public static boolean rollDice(double chanceOfSuccess, int bound) {
        return rollDice(chanceOfSuccess, bound, 1.0F);
    }

    public static boolean rollDice(double chanceOfSuccess, int bound, double resultModifier) {
        return chanceOfSuccess > (ThreadLocalRandom.current().nextInt(bound) * resultModifier);
    }

    /**
     * Used for stuff like Excavation, Fishing, etc...
     *
     * @param randomChance
     * @return
     */
    public static boolean checkRandomChanceExecutionSuccess(@NotNull RandomChanceSkillStatic randomChance, double resultModifier) {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);

        //Check the odds
        return rollDice(chanceOfSuccess, 100, resultModifier);
    }

    /**
     * Used for stuff like Excavation, Fishing, etc...
     *
     * @param randomChance
     * @return
     */
    public static boolean checkRandomChanceExecutionSuccess(@NotNull RandomChanceSkillStatic randomChance) {
        return checkRandomChanceExecutionSuccess(randomChance, 1.0F);
    }

    public static boolean checkRandomChanceExecutionSuccess(@NotNull RandomChanceSkill randomChance) {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);

        //Check the odds
        return rollDice(chanceOfSuccess, 100);
    }


    /*public static double getRandomChanceExecutionChance(RandomChanceSkill randomChance)
    {
        double chanceOfSuccess = calculateChanceOfSuccess(randomChance);
        return chanceOfSuccess;
    }*/

    /**
     * Gets the Static Chance for something to activate
     *
     * @param randomChance
     * @return
     */
    public static double getRandomChanceExecutionChance(@NotNull RandomChanceExecution randomChance) {
        return getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap(), LINEAR_CURVE_VAR);
    }

    public static double getRandomChanceExecutionChance(@NotNull RandomChanceExecution randomChance, boolean luckyOverride) {
        return getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap(), LINEAR_CURVE_VAR);
    }

    public static double getRandomChanceExecutionChance(@NotNull RandomChanceStatic randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap(), LINEAR_CURVE_VAR);

        chanceOfSuccess = addLuck(randomChance.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    /*private static double calculateChanceOfSuccess(RandomChanceStatic randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), randomChance.getProbabilityCap());
        return chanceOfSuccess;
    }*/

    public static double calculateChanceOfSuccess(@NotNull RandomChanceSkill randomChance) {
        double skillLevel = randomChance.getSkillLevel();
        double maximumProbability = randomChance.getProbabilityCap();
        double maximumBonusLevel = randomChance.getMaximumBonusLevelCap();

        double chanceOfSuccess;

        if (skillLevel >= maximumBonusLevel) {
            //Chance of success is equal to the maximum probability if the maximum bonus level has been reached
            chanceOfSuccess = maximumProbability;
        } else {
            //Get chance of success
            chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), maximumProbability, maximumBonusLevel);
        }

        //Add Luck
        chanceOfSuccess = addLuck(randomChance.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    public static double calculateChanceOfSuccess(@NotNull RandomChanceSkillStatic randomChance) {
        double chanceOfSuccess = getChanceOfSuccess(randomChance.getXPos(), 100, 100);

        //Add Luck
        chanceOfSuccess = addLuck(randomChance.isLucky(), chanceOfSuccess);

        return chanceOfSuccess;
    }

    /**
     * The formula for RNG success is determined like this
     * maximum probability * ( x / maxlevel )
     *
     * @return the chance of success from 0-100 (100 = guaranteed)
     */
    private static int getChanceOfSuccess(double skillLevel, double maxProbability, double maxLevel) {
        //return (int) (x / (y / LINEAR_CURVE_VAR));
        return (int) (maxProbability * (skillLevel / maxLevel));
        // max probability * (weight/maxlevel) = chance of success
    }

    private static int getChanceOfSuccess(double x, double y) {
        return (int) (x / (y / LINEAR_CURVE_VAR));
        // max probability * (weight/maxlevel) = chance of success
    }

    public static double getRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap) {
        RandomChanceSkill rcs = new RandomChanceSkill(player, subSkillType, hasCap);
        return calculateChanceOfSuccess(rcs);
    }

    public static double getRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap, boolean luckyOverride) {
        RandomChanceSkill rcs = new RandomChanceSkill(player, subSkillType, hasCap, luckyOverride);
        return calculateChanceOfSuccess(rcs);
    }

    public static double getRandomStaticChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean luckyOverride) {
        try {
            return getRandomChanceExecutionChance(new RandomChanceSkillStatic(getStaticRandomChance(subSkillType), player, subSkillType, luckyOverride));
        } catch (InvalidStaticChance invalidStaticChance) {
            //Catch invalid static skills
            invalidStaticChance.printStackTrace();
        }

        return 0.1337; //Puts on shades
    }

    public static boolean checkRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap) {
        return checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType, hasCap));
    }

    public static boolean checkRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType) {
        return checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType));
    }

    public static boolean checkRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap, double resultModifier) {
        return checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType, hasCap, resultModifier));
    }

    public static boolean checkRandomChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType, double resultModifier) {
        return checkRandomChanceExecutionSuccess(new RandomChanceSkill(player, subSkillType, resultModifier));
    }


    public static boolean checkRandomStaticChanceExecutionSuccess(@Nullable Player player, @NotNull SubSkillType subSkillType) {
        try {
            return checkRandomChanceExecutionSuccess(new RandomChanceSkillStatic(getStaticRandomChance(subSkillType), player, subSkillType));
        } catch (InvalidStaticChance invalidStaticChance) {
            //Catch invalid static skills
            invalidStaticChance.printStackTrace();
        }

        return false;
    }

    /**
     * Grabs static activation rolls for Secondary Abilities
     *
     * @param subSkillType The secondary ability to grab properties of
     * @return The static activation roll involved in the RNG calculation
     * @throws InvalidStaticChance if the skill has no defined static chance this exception will be thrown and you should know you're a naughty boy
     */
    public static double getStaticRandomChance(@NotNull SubSkillType subSkillType) throws InvalidStaticChance {
        switch (subSkillType) {
            case AXES_ARMOR_IMPACT:
                return mcMMO.p.getAdvancedConfig().getImpactChance();
            case AXES_GREATER_IMPACT:
                return mcMMO.p.getAdvancedConfig().getGreaterImpactChance();
            case TAMING_FAST_FOOD_SERVICE:
                return mcMMO.p.getAdvancedConfig().getFastFoodChance();
            default:
                throw new InvalidStaticChance();
        }
    }

    public static boolean sendSkillEvent(Player player, SubSkillType subSkillType, double activationChance) {
        SubSkillRandomCheckEvent event = new SubSkillRandomCheckEvent(player, subSkillType, activationChance);
        return !event.isCancelled();
    }

    public static String @NotNull [] calculateAbilityDisplayValues(@NotNull SkillActivationType skillActivationType, @NotNull Player player, @NotNull SubSkillType subSkillType) {
        double successChance = getActivationChance(skillActivationType, subSkillType, player, false);
        double successChanceLucky = getActivationChance(skillActivationType, subSkillType, player, true);

        String[] displayValues = new String[2];

        boolean isLucky = Permissions.lucky(player, subSkillType.getParentSkill());

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChanceLucky, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public static String @NotNull [] calculateAbilityDisplayValuesStatic(@NotNull Player player, @NotNull PrimarySkillType primarySkillType, double chance) {
        RandomChanceStatic rcs = new RandomChanceStatic(chance, LINEAR_CURVE_VAR, false);
        double successChance = getRandomChanceExecutionChance(rcs);

        RandomChanceStatic rcs_lucky = new RandomChanceStatic(chance, LINEAR_CURVE_VAR, true);
        double successChance_lucky = getRandomChanceExecutionChance(rcs_lucky);

        String[] displayValues = new String[2];

        boolean isLucky = Permissions.lucky(player, primarySkillType);

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChance_lucky, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public static String @NotNull [] calculateAbilityDisplayValuesCustom(@NotNull SkillActivationType skillActivationType, @NotNull Player player, @NotNull SubSkillType subSkillType, double multiplier) {
        double successChance = getActivationChance(skillActivationType, subSkillType, player, false);
        double successChanceLucky = getActivationChance(skillActivationType, subSkillType, player, true);
        //TODO: Most likely incorrectly displays the value for graceful roll but gonna ignore for now...
        successChance *= multiplier; //Currently only used for graceful roll
        String[] displayValues = new String[2];

        boolean isLucky = Permissions.lucky(player, subSkillType.getParentSkill());

        displayValues[0] = percent.format(Math.min(successChance, 100.0D) / 100.0D);
        displayValues[1] = isLucky ? percent.format(Math.min(successChanceLucky, 100.0D) / 100.0D) : null;

        return displayValues;
    }

    public static double addLuck(@NotNull Player player, @NotNull PrimarySkillType primarySkillType, double chance) {
        if (Permissions.lucky(player, primarySkillType))
            return chance * LUCKY_MODIFIER;
        else
            return chance;
    }

    public static double addLuck(boolean isLucky, double chance) {
        if (isLucky)
            return chance * LUCKY_MODIFIER;
        else
            return chance;
    }

    public static double getMaximumProbability(@NotNull SubSkillType subSkillType) {
        return mcMMO.p.getAdvancedConfig().getMaximumProbability(subSkillType);
    }

    public static double getMaxBonusLevelCap(@NotNull SubSkillType subSkillType) {
        return mcMMO.p.getAdvancedConfig().getMaxBonusLevel(subSkillType);
    }
}
