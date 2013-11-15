package com.gmail.nossr50.skills.alchemy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.runnables.skills.AlchemyBrewTask;

public final class Alchemy {
    public enum Tier {
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2),
        ONE(1);

        int numerical;

        private Tier(int numerical) {
            this.numerical = numerical;
        }

        public int toNumerical() {
            return numerical;
        }
        
        public static Tier fromNumerical(int numerical) {
            for (Tier tier : Tier.values()) {
                if (tier.toNumerical() == numerical) {
                    return tier;
                }
            }
            return null;
        }

        protected int getLevel() {
            return AdvancedConfig.getInstance().getConcoctionsTierLevel(this);
        }
    }
    
    public static int    catalysisUnlockLevel   = AdvancedConfig.getInstance().getCatalysisUnlockLevel();
    public static int    catalysisMaxBonusLevel = AdvancedConfig.getInstance().getCatalysisMaxBonusLevel();
    public static double catalysisMinSpeed      = AdvancedConfig.getInstance().getCatalysisMinSpeed();
    public static double catalysisMaxSpeed      = AdvancedConfig.getInstance().getCatalysisMaxSpeed();

    public static double potionXp = ExperienceConfig.getInstance().getPotionXP();
    
    public static Map<Block, AlchemyBrewTask> brewingStandMap = new HashMap<Block, AlchemyBrewTask>();

    private Alchemy() {};
    
    /**
     * Calculate base brewing speed, given a skill level and ignoring all perks.
     * 
     * @param skillLevel Skill level used for calculation.
     * @return Base brewing speed for the level.
     */
    public static double calculateBrewSpeed(int skillLevel) {
        if (skillLevel < catalysisUnlockLevel) {
            return catalysisMinSpeed;
        }

        return Math.min(catalysisMaxSpeed, catalysisMinSpeed + (catalysisMaxSpeed - catalysisMinSpeed) * (skillLevel - catalysisUnlockLevel) / (catalysisMaxBonusLevel - catalysisUnlockLevel));
    }
    
    /**
     * Finish all active brews.  Used upon Disable to prevent vanilla potions from being brewed upon next Enable.
     */
    public static void finishAllBrews() {
        mcMMO.p.getLogger().info("Completing " + brewingStandMap.size() + " unfinished Alchemy brews.");
        for (Entry<Block, AlchemyBrewTask> entry : brewingStandMap.entrySet()) {
            entry.getValue().finishImmediately();
        }
    }
}
