package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomChanceSkillStatic extends RandomChanceSkill {
    private final double xPos;

    public RandomChanceSkillStatic(double xPos, @Nullable Player player, @NotNull SubSkillType subSkillType) {
        super(player, subSkillType);

        this.xPos = xPos;
    }

    public RandomChanceSkillStatic(double xPos, @Nullable Player player, @NotNull SubSkillType subSkillType, boolean luckyOverride) {
        super(player, subSkillType, false, luckyOverride);

        this.xPos = xPos;
    }

    public RandomChanceSkillStatic(double xPos, @Nullable Player player, @NotNull SubSkillType subSkillType, double resultModifier) {
        super(player, subSkillType, resultModifier);

        this.xPos = xPos;
    }

    /**
     * Gets the XPos used in the formula for success
     *
     * @return value of x for our success probability graph
     */
    @Override
    public double getXPos() {
        return xPos;
    }

    /**
     * The maximum odds for this RandomChanceExecution
     * For example, if this value is 10, then 10% odds would be the maximum and would be achieved only when xPos equaled the LinearCurvePeak
     *
     * @return maximum probability odds from 0.00 (no chance of ever happened) to 100.0 (probability can be guaranteed)
     */
    @Override
    public double getProbabilityCap() {
        return probabilityCap;
    }

    /**
     * The maximum bonus level for this skill
     * This is when the skills level no longer increases the odds of success
     * For example, a value of 25 will mean the success chance no longer grows after skill level 25
     *
     * @return the maximum bonus from skill level for this skill
     */
    @Override
    public double getMaximumBonusLevelCap() {
        return 100;
    }
}
