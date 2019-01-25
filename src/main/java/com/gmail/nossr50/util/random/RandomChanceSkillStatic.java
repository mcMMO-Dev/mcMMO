package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import org.bukkit.entity.Player;

public class RandomChanceSkillStatic extends RandomChanceSkill {
    private final double xPos;

    public RandomChanceSkillStatic(double xPos, Player player, SubSkillType subSkillType)
    {
        super(player, subSkillType);

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
}
