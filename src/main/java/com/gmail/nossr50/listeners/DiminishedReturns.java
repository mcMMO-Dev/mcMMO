package com.gmail.nossr50.listeners;

import org.jetbrains.annotations.NotNull;

/**
 * Pure math for the diminished-returns XP throttle, extracted from the XP gain listener so the
 * formula is testable. Once a player's recent registered XP for a skill exceeds the configured
 * threshold, further gains shrink proportionally to how far past the threshold they are, with
 * an optional guaranteed minimum fraction of the original gain.
 */
final class DiminishedReturns {

    /**
     * @param rawXp the XP value the gain should proceed with (meaningful when not cancelled)
     * @param changed whether the gain must be updated to {@link #rawXp()}
     * @param cancelled whether the gain should be cancelled outright
     */
    record Result(float rawXp, boolean changed, boolean cancelled) {
        static final Result UNCHANGED = new Result(0F, false, false);

        static @NotNull Result changedTo(float rawXp) {
            return new Result(rawXp, true, false);
        }

        static final Result CANCELLED = new Result(0F, false, true);
    }

    private DiminishedReturns() {
    }

    /**
     * Applies the diminished-returns formula to one XP gain.
     *
     * @param rawXp the raw XP of this gain, must be positive
     * @param registeredXpGain the player's recently registered XP for the skill
     * @param threshold the configured diminished-returns threshold for the skill
     * @param formulaSkillModifier the skill's formula modifier from experience.yml
     * @param globalMultiplier the global XP multiplier from experience.yml
     * @param guaranteedMinimumFraction the cap setting: the fraction of the raw gain a player
     * always keeps, or 0 or less for no guaranteed minimum
     */
    static @NotNull Result apply(float rawXp, float registeredXpGain, int threshold,
            double formulaSkillModifier, double globalMultiplier,
            float guaranteedMinimumFraction) {
        final float guaranteedMinimum = guaranteedMinimumFraction * rawXp;
        final float modifiedThreshold =
                (float) (threshold / formulaSkillModifier * globalMultiplier);
        final float difference = (registeredXpGain - modifiedThreshold) / modifiedThreshold;

        if (difference <= 0) {
            return Result.UNCHANGED;
        }

        final float newValue = rawXp - (rawXp * difference);

        // Players always keep the guaranteed minimum when one is configured
        if (guaranteedMinimum > 0 && newValue <= guaranteedMinimum) {
            return Result.changedTo(guaranteedMinimum);
        }

        if (newValue > 0) {
            return Result.changedTo(newValue);
        }

        return Result.CANCELLED;
    }
}
