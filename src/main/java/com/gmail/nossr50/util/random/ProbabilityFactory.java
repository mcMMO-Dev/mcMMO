package com.gmail.nossr50.util.random;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProbabilityFactory {

    public static @NotNull Probability ofPercentageValue(double percentageValue) {
        return new ProbabilityImpl(probabilityFromPercent(percentageValue));
    }

    public static @NotNull Probability ofSubSkill(@Nullable Player player,
                                                  @NotNull SubSkillType subSkillType,
                                                  @NotNull SkillProbabilityType skillProbabilityType) throws InvalidStaticChance, RuntimeException {

        switch (skillProbabilityType) {
            case DYNAMIC_CONFIGURABLE:
                double probabilityCeiling;
                double xCeiling;
                double xPos;

                if (player != null) {
                    McMMOPlayer mmoPlayer = UserManager.getPlayer(player);
                    if(mmoPlayer != null)
                        xPos = mmoPlayer.getSkillLevel(subSkillType.getParentSkill());
                    else
                        xPos = 0;
                } else {
                    xPos = 0;
                }

                //Probability ceiling is configurable in this type
                probabilityCeiling = AdvancedConfig.getInstance().getMaximumProbability(subSkillType);
                //The xCeiling is configurable in this type
                xCeiling = AdvancedConfig.getInstance().getMaxBonusLevel(subSkillType);
                return new ProbabilityImpl(xPos, xCeiling, probabilityCeiling);
            case STATIC_CONFIGURABLE:
                return ofPercentageValue(getStaticRandomChance(subSkillType));
            default:
                throw new RuntimeException("No case in switch statement for Skill Probability Type!");
        }
    }

    /**
     * Convert a probability from a percentage
     * @param percentage value to convert
     * @return 0 -> 1 inclusive representation of probability
     */
    public static double probabilityFromPercent(double percentage) {
        return percentage / 100;
    }

    /**
     * Grabs static activation rolls for Secondary Abilities
     *
     * @param subSkillType The secondary ability to grab properties of
     * @return The static activation roll involved in the RNG calculation
     * @throws InvalidStaticChance if the skill has no defined static chance this exception will be thrown and you should know you're a naughty boy
     */
    private static double getStaticRandomChance(@NotNull SubSkillType subSkillType) throws InvalidStaticChance {
        switch (subSkillType) {
            case AXES_ARMOR_IMPACT:
                return AdvancedConfig.getInstance().getImpactChance();
            case AXES_GREATER_IMPACT:
                return AdvancedConfig.getInstance().getGreaterImpactChance();
            case TAMING_FAST_FOOD_SERVICE:
                return AdvancedConfig.getInstance().getFastFoodChance();
            default:
                throw new InvalidStaticChance();
        }
    }
}
