package com.gmail.nossr50.util.random;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Probability {
    /**
     * The value of this Probability
     * Should return a result between 0 and 1 (inclusive)
     * 1 should represent something that will always succeed
     * 0.5 should represent something that succeeds around half the time
     * etc
     *
     * @return the value of probability
     */
    double getValue();

    static @NotNull Probability ofSubSkill(@Nullable Player player,
                                           @NotNull SubSkillType subSkillType,
                                           @NotNull SkillProbabilityType skillProbabilityType) {

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
                probabilityCeiling = mcMMO.p.getAdvancedConfig().getMaximumProbability(subSkillType);
                //The xCeiling is configurable in this type
                xCeiling = mcMMO.p.getAdvancedConfig().getMaxBonusLevel(subSkillType);
                return new ProbabilityImpl(xPos, xCeiling, probabilityCeiling);
            case STATIC_CONFIGURABLE:
                try {
                    return ofPercentageValue(getStaticRandomChance(subSkillType));
                } catch (InvalidStaticChance invalidStaticChance) {
                    invalidStaticChance.printStackTrace();
                }
            default:
                throw new RuntimeException("No case in switch statement for Skill Probability Type!");
        }
    }

    static @NotNull Probability ofPercentageValue(double percentageValue) {
        return new ProbabilityImpl(percentageValue / 100);
    }

    static double getStaticRandomChance(@NotNull SubSkillType subSkillType) throws InvalidStaticChance {
        return switch (subSkillType) {
            case AXES_ARMOR_IMPACT -> mcMMO.p.getAdvancedConfig().getImpactChance();
            case AXES_GREATER_IMPACT -> mcMMO.p.getAdvancedConfig().getGreaterImpactChance();
            case TAMING_FAST_FOOD_SERVICE -> mcMMO.p.getAdvancedConfig().getFastFoodChance();
            default -> throw new InvalidStaticChance();
        };
    }
}
