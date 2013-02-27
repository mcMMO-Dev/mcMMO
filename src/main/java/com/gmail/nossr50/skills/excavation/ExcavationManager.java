package com.gmail.nossr50.skills.excavation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;

public class ExcavationManager extends SkillManager {
    public ExcavationManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.EXCAVATION);
    }

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void excavationBlockCheck(BlockState blockState) {
        int xp = Excavation.getBlockXP(blockState);

        if (Permissions.excavationTreasureHunter(getPlayer())) {
            List<ExcavationTreasure> treasures = Excavation.getTreasures(blockState);

            if (!treasures.isEmpty()) {
                int skillLevel = getSkillLevel();
                Location location = blockState.getLocation();

                for (ExcavationTreasure treasure : treasures) {
                    if (skillLevel >= treasure.getDropLevel() && SkillTools.treasureDropSuccessful(treasure.getDropChance(), activationChance)) {
                        xp += treasure.getXp();
                        Misc.dropItem(location, treasure.getDrop());
                    }
                }
            }
        }

        applyXpGain(xp);
    }

    /**
     * Process the Giga Drill Breaker ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void gigaDrillBreaker(BlockState blockState) {
        excavationBlockCheck(blockState);
        excavationBlockCheck(blockState);
   }
}
