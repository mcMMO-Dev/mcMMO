package com.gmail.nossr50.skills.alchemy;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.skills.AlchemyBrewTask;
import com.gmail.nossr50.util.LogUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Alchemy {
    public static final int INGREDIENT_SLOT = 3;

    public static int catalysisMaxBonusLevel = mcMMO.p.getAdvancedConfig().getCatalysisMaxBonusLevel();
    public static double catalysisMinSpeed = mcMMO.p.getAdvancedConfig().getCatalysisMinSpeed();
    public static double catalysisMaxSpeed = mcMMO.p.getAdvancedConfig().getCatalysisMaxSpeed();

    public static Map<Location, AlchemyBrewTask> brewingStandMap = new HashMap<>();

    private Alchemy() {}

    /**
     * Finish all active brews.  Used upon Disable to prevent vanilla potions from being brewed upon next Enable.
     */
    public static void finishAllBrews() {
        LogUtils.debug(mcMMO.p.getLogger(), "Completing " + brewingStandMap.size() + " unfinished Alchemy brews.");

        List<AlchemyBrewTask> toFinish = new ArrayList<>(brewingStandMap.values());

        for (AlchemyBrewTask alchemyBrewTask : toFinish) {
            alchemyBrewTask.finishImmediately();
        }
    }
}