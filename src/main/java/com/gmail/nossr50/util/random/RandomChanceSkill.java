package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RandomChanceSkill implements RandomChanceExecution {
    protected final double probabilityCap;
    protected final boolean isLucky;
    protected int skillLevel;
    protected final double resultModifier;
    protected final double maximumBonusLevelCap;

    public RandomChanceSkill(@Nullable Player player, @NotNull SubSkillType subSkillType, double resultModifier) {
        this.probabilityCap = RandomChanceUtil.LINEAR_CURVE_VAR;

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (player != null && mcMMOPlayer != null) {
            this.skillLevel = mcMMOPlayer.getSkillLevel(subSkillType.getParentSkill());
        } else {
            this.skillLevel = 0;
        }

        if (player != null)
            isLucky = Permissions.lucky(player, subSkillType.getParentSkill());
        else
            isLucky = false;

        this.resultModifier = resultModifier;
        this.maximumBonusLevelCap = RandomChanceUtil.getMaxBonusLevelCap(subSkillType);
    }

    public RandomChanceSkill(@Nullable Player player, @NotNull SubSkillType subSkillType) {
        this.probabilityCap = RandomChanceUtil.LINEAR_CURVE_VAR;

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (player != null && mcMMOPlayer != null) {
            this.skillLevel = mcMMOPlayer.getSkillLevel(subSkillType.getParentSkill());
        } else {
            this.skillLevel = 0;
        }

        if (player != null)
            isLucky = Permissions.lucky(player, subSkillType.getParentSkill());
        else
            isLucky = false;

        this.resultModifier = 1.0D;
        this.maximumBonusLevelCap = RandomChanceUtil.getMaxBonusLevelCap(subSkillType);
    }

    public RandomChanceSkill(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap) {
        if (hasCap)
            this.probabilityCap = RandomChanceUtil.getMaximumProbability(subSkillType);
        else
            this.probabilityCap = RandomChanceUtil.LINEAR_CURVE_VAR;

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (player != null && mcMMOPlayer != null) {
            this.skillLevel = mcMMOPlayer.getSkillLevel(subSkillType.getParentSkill());
        } else {
            this.skillLevel = 0;
        }

        if (player != null)
            isLucky = Permissions.lucky(player, subSkillType.getParentSkill());
        else
            isLucky = false;

        this.resultModifier = 1.0D;
        this.maximumBonusLevelCap = RandomChanceUtil.getMaxBonusLevelCap(subSkillType);
    }

    public RandomChanceSkill(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap, boolean luckyOverride) {
        if (hasCap)
            this.probabilityCap = RandomChanceUtil.getMaximumProbability(subSkillType);
        else
            this.probabilityCap = RandomChanceUtil.LINEAR_CURVE_VAR;

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (player != null && mcMMOPlayer != null) {
            this.skillLevel = mcMMOPlayer.getSkillLevel(subSkillType.getParentSkill());
        } else {
            this.skillLevel = 0;
        }

        isLucky = luckyOverride;

        this.resultModifier = 1.0D;
        this.maximumBonusLevelCap = RandomChanceUtil.getMaxBonusLevelCap(subSkillType);
    }

    public RandomChanceSkill(@Nullable Player player, @NotNull SubSkillType subSkillType, boolean hasCap, double resultModifier) {
        if (hasCap)
            this.probabilityCap = RandomChanceUtil.getMaximumProbability(subSkillType);
        else
            this.probabilityCap = RandomChanceUtil.LINEAR_CURVE_VAR;

        final McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
        if (player != null && mcMMOPlayer != null) {
            this.skillLevel = mcMMOPlayer.getSkillLevel(subSkillType.getParentSkill());
        } else {
            this.skillLevel = 0;
        }

        if (player != null)
            isLucky = Permissions.lucky(player, subSkillType.getParentSkill());
        else
            isLucky = false;

        this.resultModifier = resultModifier;
        this.maximumBonusLevelCap = RandomChanceUtil.getMaxBonusLevelCap(subSkillType);
    }

    /**
     * Gets the skill level of the player who owns this RandomChanceSkill
     *
     * @return the current skill level relating to this RandomChanceSkill
     */
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Modify the skill level used for this skill's RNG calculations
     *
     * @param newSkillLevel new skill level
     */
    public void setSkillLevel(int newSkillLevel) {
        skillLevel = newSkillLevel;
    }

    /**
     * The maximum bonus level for this skill
     * This is when the skills level no longer increases the odds of success
     * For example, a value of 25 will mean the success chance no longer grows after skill level 25
     *
     * @return the maximum bonus from skill level for this skill
     */
    public double getMaximumBonusLevelCap() {
        return maximumBonusLevelCap;
    }

    /**
     * Gets the XPos used in the formula for success
     *
     * @return value of x for our success probability graph
     */
    @Override
    public double getXPos() {
        return getSkillLevel();
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

    public boolean isLucky() {
        return isLucky;
    }

    public double getResultModifier() {
        return resultModifier;
    }
}
