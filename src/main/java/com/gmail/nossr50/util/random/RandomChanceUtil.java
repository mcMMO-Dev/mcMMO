package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

//TODO: Normalize chance values
//TODO: Update calls to this class and its members
public class RandomChanceUtil {
    public static final @NotNull DecimalFormat percent = new DecimalFormat("##0.00%");
    public static final double LUCKY_MODIFIER = 1.333D;

    /**
     * Simulate an outcome on a probability and return true or false for the result of that outcome
     *
     * @param probability target probability
     * @return true if the probability succeeded, false if it failed
     */
    public static boolean processProbability(@NotNull Probability probability) {
        return isSuccessfulRoll(probability.getValue());
    }

    /**
     * Modify and then Simulate an outcome on a probability and return true or false for the result of that outcome
     *
     * @param probability target probability
     * @param probabilityMultiplier probability will be multiplied by this before success is checked
     * @return true if the probability succeeded, false if it failed
     */
    public static boolean processProbability(@NotNull Probability probability, double probabilityMultiplier) {
        double probabilityValue = probability.getValue() * probabilityMultiplier;
        return isSuccessfulRoll(probabilityValue);
    }

    /**
     * Simulates a "roll of the dice"
     * If the value passed is higher than the "random" value, than it is a successful roll
     *
     * @param probabilityValue probability value
     * @return true for succeeding, false for failing
     */
    private static boolean isSuccessfulRoll(double probabilityValue) {
        return probabilityValue >= ThreadLocalRandom.current().nextDouble(1.0D);
    }

    /**
     * Return a chance of success in "percentage" format, show to the player in UI elements
     *
     * @param player target player
     * @param subSkillType target subskill
     * @param isLucky whether or not to apply luck modifiers
     *
     * @return "percentage" representation of success
     */
    public static double chanceOfSuccessPercentage(@NotNull Player player, @NotNull SubSkillType subSkillType, boolean isLucky) {
        Probability probability = SkillUtils.getSubSkillProbability(subSkillType, player);
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue() * 100;

        //Apply lucky modifier
        if(isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

    public static double chanceOfSuccessPercentage(@NotNull Probability probability, boolean isLucky) {
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue() * 100;

        //Apply lucky modifier
        if(isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

}
