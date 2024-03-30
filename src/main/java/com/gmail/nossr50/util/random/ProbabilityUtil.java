package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.events.skills.secondaryabilities.SubSkillEvent;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class ProbabilityUtil {
    public static final @NotNull DecimalFormat percent = new DecimalFormat("##0.00%");
    public static final double LUCKY_MODIFIER = 1.333D;

    /**
     * Return a chance of success in "percentage" format, show to the player in UI elements
     *
     * @param player target player
     * @param subSkillType target subskill
     * @param isLucky whether to apply luck modifiers
     *
     * @return "percentage" representation of success
     */
    public static double chanceOfSuccessPercentage(@NotNull Player player,
                                                   @NotNull SubSkillType subSkillType,
                                                   boolean isLucky) {
        Probability probability = getSubSkillProbability(subSkillType, player);
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue(); //Doesn't need to be scaled

        //Apply lucky modifier
        if(isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

    public static double chanceOfSuccessPercentage(@NotNull Probability probability, boolean isLucky) {
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue();

        //Apply lucky modifier
        if(isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

    static Probability getStaticRandomChance(@NotNull SubSkillType subSkillType) throws InvalidStaticChance {
        return switch (subSkillType) {
            case AXES_ARMOR_IMPACT -> Probability.ofPercent(mcMMO.p.getAdvancedConfig().getImpactChance());
            case AXES_GREATER_IMPACT -> Probability.ofPercent(mcMMO.p.getAdvancedConfig().getGreaterImpactChance());
            case TAMING_FAST_FOOD_SERVICE -> Probability.ofPercent(mcMMO.p.getAdvancedConfig().getFastFoodChance());
            default -> throw new InvalidStaticChance();
        };
    }

    static SkillProbabilityType getProbabilityType(@NotNull SubSkillType subSkillType) {
        SkillProbabilityType skillProbabilityType = SkillProbabilityType.DYNAMIC_CONFIGURABLE;

        if(subSkillType == SubSkillType.TAMING_FAST_FOOD_SERVICE
                || subSkillType == SubSkillType.AXES_ARMOR_IMPACT
                || subSkillType == SubSkillType.AXES_GREATER_IMPACT)
            skillProbabilityType = SkillProbabilityType.STATIC_CONFIGURABLE;

        return skillProbabilityType;
    }

    static @NotNull Probability ofSubSkill(@Nullable Player player,
                                           @NotNull SubSkillType subSkillType) {
        switch (getProbabilityType(subSkillType)) {
            case DYNAMIC_CONFIGURABLE:
                double probabilityCeiling;
                double xCeiling;
                double xPos;

                if (player != null) {
                    McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
                    if (mmoPlayer == null) {
                        return Probability.ofPercent(0);
                    }
                    xPos = mmoPlayer.getSkillLevel(subSkillType.getParentSkill());
                } else {
                    xPos = 0;
                }

                //Probability ceiling is configurable in this type
                probabilityCeiling = mcMMO.p.getAdvancedConfig().getMaximumProbability(subSkillType);
                //The xCeiling is configurable in this type
                xCeiling = mcMMO.p.getAdvancedConfig().getMaxBonusLevel(subSkillType);
                return new ProbabilityImpl(xPos, xCeiling, probabilityCeiling);
            case STATIC_CONFIGURABLE:
                try {
                    return getStaticRandomChance(subSkillType);
                } catch (InvalidStaticChance invalidStaticChance) {
                    invalidStaticChance.printStackTrace();
                }
            default:
                throw new RuntimeException("No case in switch statement for Skill Probability Type!");
        }
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is for specific {@link SubSkillType}, which help mcMMO understand where the RNG values used in our calculations come from this {@link SubSkillType}
     * <p>
     * 1) Determine where the RNG values come from for the passed {@link SubSkillType}
     *  NOTE: In the config file, there are values which are static and which are more dynamic, this is currently a bit hardcoded and will need to be updated manually
     * <p>
     * 2) Determine whether to use Lucky multiplier and influence the outcome
     * <p>
     * 3) Creates a {@link Probability} and pipes it to {@link ProbabilityUtil} which processes the result and returns it
     * <p>
     * This also calls a {@link SubSkillEvent} which can be cancelled, if it is cancelled this will return false
     * The outcome of the probability can also be modified by this event that is called
     *
     * @param subSkillType target subskill
     * @param player target player, can be null (null players are given odds equivalent to a player with no levels or luck)
     * @return true if the Skill RNG succeeds, false if it fails
     */
    public static boolean isSkillRNGSuccessful(@NotNull SubSkillType subSkillType, @NotNull Player player) {
        //Process probability
        Probability probability = getSubSkillProbability(subSkillType, player);

        //Send out event
        SubSkillEvent subSkillEvent = EventUtils.callSubSkillEvent(player, subSkillType);

        if(subSkillEvent.isCancelled()) {
            return false; //Event got cancelled so this doesn't succeed
        }

        //Result modifier
        double resultModifier = subSkillEvent.getResultModifier();

        //Mutate probability
        if(resultModifier != 1.0D)
            probability = Probability.ofPercent(probability.getValue() * resultModifier);

        //Luck
        boolean isLucky = Permissions.lucky(player, subSkillType.getParentSkill());

        if(isLucky) {
            return probability.evaluate(LUCKY_MODIFIER);
        } else {
            return probability.evaluate();
        }
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player, can be null (null players have the worst odds)
     * @param probabilityPercentage the probability of this player succeeding in "percentage" format (0-100 inclusive)
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType, @Nullable Player player, double probabilityPercentage) {
        //Grab a probability converted from a "percentage" value
        Probability probability = Probability.ofPercent(probabilityPercentage);

        return isStaticSkillRNGSuccessful(primarySkillType, player, probability);
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player, can be null (null players have the worst odds)
     * @param probability the probability of this player succeeding
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType, @Nullable Player player, @NotNull Probability probability) {
        boolean isLucky = player != null && Permissions.lucky(player, primarySkillType);

        if(isLucky) {
            return probability.evaluate(LUCKY_MODIFIER);
        } else {
            return probability.evaluate();
        }
    }

    /**
     * Skills activate without RNG, this allows other plugins to prevent that activation
     * @param subSkillType target subskill
     * @param player target player
     * @return true if the skill succeeds (wasn't cancelled by any other plugin)
     */
    public static boolean isNonRNGSkillActivationSuccessful(@NotNull SubSkillType subSkillType, @NotNull Player player) {
        return !EventUtils.callSubSkillEvent(player, subSkillType).isCancelled();
    }

    /**
     * Grab the {@link Probability} for a specific {@link SubSkillType} for a specific {@link Player}
     *
     * @param subSkillType target subskill
     * @param player target player
     * @return the Probability of this skill succeeding
     */
    public static @NotNull Probability getSubSkillProbability(@NotNull SubSkillType subSkillType, @Nullable Player player) {
        return ProbabilityUtil.ofSubSkill(player, subSkillType);
    }

    public static @NotNull String[] getRNGDisplayValues(@NotNull Player player, @NotNull SubSkillType subSkill) {
        double firstValue = chanceOfSuccessPercentage(player, subSkill, false);
        double secondValue = chanceOfSuccessPercentage(player, subSkill, true);

        return new String[]{percent.format(firstValue), percent.format(secondValue)};
    }

    public static @NotNull String[] getRNGDisplayValues(@NotNull Probability probability) {
        double firstValue = chanceOfSuccessPercentage(probability, false);
        double secondValue = chanceOfSuccessPercentage(probability, true);

        return new String[]{percent.format(firstValue), percent.format(secondValue)};
    }
}
