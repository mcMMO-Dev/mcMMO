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
import org.jetbrains.annotations.VisibleForTesting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static java.util.Objects.requireNonNull;

public class ProbabilityUtil {
    public static final @NotNull DecimalFormat percent = new DecimalFormat("##0.00%", DecimalFormatSymbols.getInstance(Locale.US));
    public static final double LUCKY_MODIFIER = 1.333D;

    /**
     * Return a chance of success in "percentage" format, shown to the player in UI elements
     *
     * @param player target player
     * @param subSkillType target subskill
     * @param isLucky whether to apply luck modifiers
     *
     * @return "percentage" representation of success
     * @deprecated use {@link #chanceOfSuccessPercentage(McMMOPlayer, SubSkillType, boolean)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static double chanceOfSuccessPercentage(@Nullable Player player,
                                                   @NotNull SubSkillType subSkillType,
                                                   boolean isLucky) {
        return chanceOfSuccessPercentage(requireNonNull(UserManager.getPlayer(player)), subSkillType, isLucky);
    }

    public static double chanceOfSuccessPercentage(@Nullable McMMOPlayer mmoPlayer,
                                                   @NotNull SubSkillType subSkillType,
                                                   boolean isLucky) {
        final Probability probability = getSubSkillProbability(subSkillType, mmoPlayer);
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue(); //Doesn't need to be scaled

        //Apply lucky modifier
        if (isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

    /**
     * Return a chance of success as a double representing a "percentage".
     *
     * @param probability the probability of success
     * @param isLucky whether to apply luck modifiers
     * @return a double as a "percentage" representation of success
     */
    public static double chanceOfSuccessPercentage(@NotNull Probability probability, boolean isLucky) {
        //Probability values are on a 0-1 scale and need to be "transformed" into a 1-100 scale
        double percentageValue = probability.getValue();

        //Apply lucky modifier
        if (isLucky) {
            percentageValue *= LUCKY_MODIFIER;
        }

        return percentageValue;
    }

    @VisibleForTesting
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

        if (subSkillType == SubSkillType.TAMING_FAST_FOOD_SERVICE
                || subSkillType == SubSkillType.AXES_ARMOR_IMPACT
                || subSkillType == SubSkillType.AXES_GREATER_IMPACT)
            skillProbabilityType = SkillProbabilityType.STATIC_CONFIGURABLE;

        return skillProbabilityType;
    }

    @Deprecated(forRemoval = true, since = "2.2.010")
    private static @NotNull Probability ofSubSkill(@Nullable Player player, @NotNull SubSkillType subSkillType) {
        // no null check needed here
        return ofSubSkill(UserManager.getPlayer(player), subSkillType);
    }

    private static @NotNull Probability ofSubSkill(@Nullable McMMOPlayer mmoPlayer, @NotNull SubSkillType subSkillType) {
        switch (getProbabilityType(subSkillType)) {
            case DYNAMIC_CONFIGURABLE:
                double probabilityCeiling;
                double skillLevel;
                double maxBonusLevel; // If a skill level is equal to the cap, it has the full probability

                if (mmoPlayer != null) {
                    skillLevel = mmoPlayer.getSkillLevel(subSkillType.getParentSkill());
                } else {
                    skillLevel = 0;
                }

                //Probability ceiling is configurable in this type
                probabilityCeiling = mcMMO.p.getAdvancedConfig().getMaximumProbability(subSkillType);
                //The xCeiling is configurable in this type
                maxBonusLevel = mcMMO.p.getAdvancedConfig().getMaxBonusLevel(subSkillType);
                return calculateCurrentSkillProbability(skillLevel, 0, probabilityCeiling, maxBonusLevel);
            case STATIC_CONFIGURABLE:
                try {
                    return getStaticRandomChance(subSkillType);
                } catch (InvalidStaticChance invalidStaticChance) {
                    throw new RuntimeException(invalidStaticChance);
                }
            default:
                throw new IllegalStateException("No case in switch statement for Skill Probability Type!");
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
     * @param player target player
     *              can be null (null players are given odds equivalent to a player with no levels or luck)
     * @return true if the Skill RNG succeeds, false if it fails
     * @deprecated use {@link #isSkillRNGSuccessful(SubSkillType, McMMOPlayer)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static boolean isSkillRNGSuccessful(@NotNull SubSkillType subSkillType, @Nullable Player player) {
        return isSkillRNGSuccessful(subSkillType, UserManager.getPlayer(player));
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is for specific {@link SubSkillType},
     * which help mcMMO understand where the RNG values used in our calculations come from this {@link SubSkillType}
     * <p>
     * 1) Determine where the RNG values come from for the passed {@link SubSkillType}
     *  NOTE: In the config file, there are values which are static and which are more dynamic,
     *  this is currently a bit hardcoded and will need to be updated manually
     * <p>
     * 2) Determine whether to use Lucky multiplier and influence the outcome
     * <p>
     * 3)
     * Creates a {@link Probability} and pipes it to {@link ProbabilityUtil} which processes the result and returns it
     * <p>
     * This also calls a {@link SubSkillEvent} which can be cancelled, if it is cancelled this will return false
     * The outcome of the probability can also be modified by this event that is called
     *
     * @param subSkillType target subskill
     * @param mmoPlayer target player
     *                  can be null (null players are given odds equivalent to a player with no levels or luck)
     * @return true if the Skill RNG succeeds, false if it fails
     */
    public static boolean isSkillRNGSuccessful(@NotNull SubSkillType subSkillType, @Nullable McMMOPlayer mmoPlayer) {
        final Probability probability = getSkillProbability(subSkillType, mmoPlayer);

        //Luck
        boolean isLucky = mmoPlayer != null && Permissions.lucky(mmoPlayer.getPlayer(), subSkillType.getParentSkill());

        if (isLucky) {
            return probability.evaluate(LUCKY_MODIFIER);
        } else {
            return probability.evaluate();
        }
    }

    /**
     * This is one of several Skill RNG evaluation methods.
     * This one specifically allows for a probability multiplier to be passed in.
     * This probability multiplier is applied after any lucky modifiers, affecting the final result.
     * <p>
     * This helper method is for specific {@link SubSkillType},
     * which help mcMMO understand where the RNG values used in our calculations come from this {@link SubSkillType}
     * <p>
     * 1) Determine where the RNG values come from for the passed {@link SubSkillType}
     *  NOTE: In the config file, there are values which are static and which are more dynamic,
     *  this is currently a bit hardcoded and will need to be updated manually
     * <p>
     * 2) Determine whether to use Lucky multiplier and influence the outcome
     * <p>
     * 3)
     * Creates a {@link Probability} and pipes it to {@link ProbabilityUtil} which processes the result and returns it
     * <p>
     * This also calls a {@link SubSkillEvent} which can be cancelled, if it is cancelled this will return false
     * The outcome of the probability can also be modified by this event that is called
     *
     * @param subSkillType target subskill
     * @param mmoPlayer target player
     *                  can be null (null players are given odds equivalent to a player with no levels or luck)
     * @return true if the Skill RNG succeeds, false if it fails
     */
    public static boolean isSkillRNGSuccessful(@NotNull SubSkillType subSkillType, @Nullable McMMOPlayer mmoPlayer,
                                               double probabilityMultiplier) {
        final Probability probability = getSkillProbability(subSkillType, mmoPlayer);

        //Luck
        boolean isLucky = mmoPlayer != null && Permissions.lucky(mmoPlayer.getPlayer(), subSkillType.getParentSkill());

        if (isLucky) {
            return probability.evaluate(LUCKY_MODIFIER, probabilityMultiplier);
        } else {
            return probability.evaluate();
        }
    }

    /**
     * Returns the {@link Probability} for a specific {@link SubSkillType} for a specific {@link Player}.
     * This does not take into account perks such as lucky for the player.
     * This is affected by other plugins who can listen to the {@link SubSkillEvent} and cancel it or mutate it.
     * Null players will be treated as zero skill players.
     *
     * @param subSkillType the target subskill
     * @param player the target player
     *               can be null (null players have the worst odds)
     * @return the probability for this skill
     * @deprecated use {@link #getSkillProbability(SubSkillType, McMMOPlayer)} instead
     */
    @Deprecated(forRemoval = true)
    public static Probability getSkillProbability(@NotNull SubSkillType subSkillType, @Nullable Player player) {
        return getSkillProbability(subSkillType, UserManager.getPlayer(player));
    }

    /**
     * Returns the {@link Probability} for a specific {@link SubSkillType} for a specific {@link Player}.
     * This does not take into account perks such as lucky for the player.
     * This is affected by other plugins who can listen to the {@link SubSkillEvent} and cancel it or mutate it.
     * Null players will be treated as zero skill players.
     *
     * @param subSkillType the target subskill
     * @param mmoPlayer the target player
     *               can be null (null players have the worst odds)
     * @return the probability for this skill
     */
    public static Probability getSkillProbability(@NotNull SubSkillType subSkillType, @Nullable McMMOPlayer mmoPlayer) {
        // Process probability
        Probability probability = getSubSkillProbability(subSkillType, mmoPlayer);

        // Send out event
        if (mmoPlayer != null) {
            SubSkillEvent subSkillEvent = EventUtils.callSubSkillEvent(mmoPlayer, subSkillType);

            if (subSkillEvent.isCancelled()) {
                return Probability.ALWAYS_FAILS;
            }

            // Result modifier
            double resultModifier = subSkillEvent.getResultModifier();

            // Mutate probability
            if (resultModifier != 1.0D)
                probability = Probability.ofPercent(probability.getValue() * resultModifier);
        }

        return probability;
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player can be null (null players have the worst odds)
     * @param probabilityPercentage the probability of this player succeeding in "percentage" format (0-100 inclusive)
     * @return true if the RNG succeeds, false if it fails
     * @deprecated use {@link #isStaticSkillRNGSuccessful(PrimarySkillType, McMMOPlayer, double)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType, @Nullable Player player, double probabilityPercentage) {
        return isStaticSkillRNGSuccessful(primarySkillType, player, Probability.ofPercent(probabilityPercentage));
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param mmoPlayer the target player can be null (null players have the worst odds)
     * @param probabilityPercentage the probability of this player succeeding in "percentage" format (0-100 inclusive)
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType,
                                                     @Nullable McMMOPlayer mmoPlayer, double probabilityPercentage) {
        //Grab a probability converted from a "percentage" value
        final Probability probability = Probability.ofPercent(probabilityPercentage);

        return isStaticSkillRNGSuccessful(primarySkillType, mmoPlayer, probability);
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a player's Luck
     *
     * @param primarySkillType the related primary skill
     * @param player the target player
     *              can be null (null players have the worst odds)
     * @param probability the probability of this player succeeding
     * @return true if the RNG succeeds, false if it fails
     * @deprecated use {@link #isStaticSkillRNGSuccessful(PrimarySkillType, McMMOPlayer, Probability)} instead, this
     * method is redundant and will be removed.
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType,
                                                     @Nullable Player player, @NotNull Probability probability) {
        return isStaticSkillRNGSuccessful(primarySkillType, UserManager.getPlayer(player), probability);
    }

    /**
     * This is one of several Skill RNG check methods
     * This helper method is specific to static value RNG, which can be influenced by a mmoPlayer's Luck
     *
     * @param primarySkillType the related primary skill
     * @param mmoPlayer the target mmoPlayer
     *              can be null (null players have the worst odds)
     * @param probability the probability of this mmoPlayer succeeding
     * @return true if the RNG succeeds, false if it fails
     */
    public static boolean isStaticSkillRNGSuccessful(@NotNull PrimarySkillType primarySkillType,
                                                     @Nullable McMMOPlayer mmoPlayer, @NotNull Probability probability) {
        boolean isLucky = mmoPlayer != null && Permissions.lucky(mmoPlayer.getPlayer(), primarySkillType);

        if (isLucky) {
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
     * @deprecated use {@link #isNonRNGSkillActivationSuccessful(SubSkillType, McMMOPlayer)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static boolean isNonRNGSkillActivationSuccessful(@NotNull SubSkillType subSkillType, @NotNull Player player) {
        return isNonRNGSkillActivationSuccessful(subSkillType, requireNonNull(UserManager.getPlayer(player)));
    }

    /**
     * Skills activate without RNG, this allows other plugins to prevent that activation
     * @param subSkillType target subskill
     * @param mmoPlayer target player
     * @return true if the skill succeeds (wasn't cancelled by any other plugin)
     */
    public static boolean isNonRNGSkillActivationSuccessful(@NotNull SubSkillType subSkillType,
                                                            @NotNull McMMOPlayer mmoPlayer) {
        return !EventUtils.callSubSkillEvent(mmoPlayer, subSkillType).isCancelled();
    }

    /**
     * Retrieves the {@link Probability} of success for a specified {@link SubSkillType} for a given {@link Player}.
     *
     * @param subSkillType The targeted subskill.
     * @param player The player in question.
     *               If null, the method treats it as a player with no levels or luck and calculates the probability
     *               accordingly.
     * @return The probability that the specified skill will succeed.
     * @deprecated use {@link #getSubSkillProbability(SubSkillType, McMMOPlayer)} instead
     */
    @Deprecated(forRemoval = true, since = "2.2.010")
    public static @NotNull Probability getSubSkillProbability(@NotNull SubSkillType subSkillType,
                                                              @Nullable Player player) {
        return ProbabilityUtil.ofSubSkill(player, subSkillType);
    }

    /**
     * Retrieves the {@link Probability} of success for a specified {@link SubSkillType} for a given {@link Player}.
     *
     * @param subSkillType The targeted subskill.
     * @param mmoPlayer The player in question.
     *               If null, the method treats it as a player with no levels or luck and calculates the probability
     *               accordingly.
     * @return The probability that the specified skill will succeed.
     */
    public static @NotNull Probability getSubSkillProbability(@NotNull SubSkillType subSkillType,
                                                              @Nullable McMMOPlayer mmoPlayer) {
        return ProbabilityUtil.ofSubSkill(mmoPlayer, subSkillType);
    }

    public static @NotNull String[] getRNGDisplayValues(@Nullable McMMOPlayer mmoPlayer, @NotNull SubSkillType subSkill) {
        double firstValue = chanceOfSuccessPercentage(mmoPlayer, subSkill, false);
        double secondValue = chanceOfSuccessPercentage(mmoPlayer, subSkill, true);

        return new String[]{percent.format(firstValue), percent.format(secondValue)};
    }

    public static @NotNull String[] getRNGDisplayValues(@NotNull Probability probability) {
        double firstValue = chanceOfSuccessPercentage(probability, false);
        double secondValue = chanceOfSuccessPercentage(probability, true);

        return new String[]{percent.format(firstValue), percent.format(secondValue)};
    }

    /**
     * Helper function to calculate what probability a given skill has at a certain level
     * @param skillLevel the skill level currently between the floor and the ceiling
     * @param floor the minimum odds this skill can have
     * @param ceiling the maximum odds this skill can have
     * @param maxBonusLevel the maximum level this skill can have to reach the ceiling
     *
     * @return the probability of success for this skill at this level
     */
    public static Probability calculateCurrentSkillProbability(double skillLevel, double floor,
                                                               double ceiling, double maxBonusLevel) {
        // The odds of success are between the value of the floor and the value of the ceiling.
        // If the skill has a maxBonusLevel of 500 on this skill, then at skill level 500 you would have the full odds,
        // at skill level 250 it would be half odds.

        if (skillLevel >= maxBonusLevel || maxBonusLevel <= 0) {
            // Avoid divide by zero bugs
            // Max benefit has been reached, should always succeed
            return Probability.ofPercent(ceiling);
        }

        double odds = ((skillLevel / maxBonusLevel) * ceiling);

        // make sure the odds aren't lower or higher than the floor or ceiling
        return Probability.ofPercent(Math.min(Math.max(floor, odds), ceiling));
    }
}
