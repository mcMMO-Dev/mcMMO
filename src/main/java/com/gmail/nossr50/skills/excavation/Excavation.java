package com.gmail.nossr50.skills.excavation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.config.TreasuresConfig;
import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mods.ModChecks;
import com.gmail.nossr50.skills.utilities.PerksUtils;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.Users;

public class Excavation {

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     */
    public static void excavationBlockCheck(BlockState blockState, Player player) {
        McMMOPlayer mcMMOPlayer = Users.getPlayer(player);
        int xp = Config.getInstance().getXp(SkillType.EXCAVATION, blockState.getType());

        if (xp == 0 && ModChecks.isCustomExcavationBlock(blockState)) {
            xp = ModChecks.getCustomBlock(blockState).getXpGain();
        }

        if (Permissions.excavationTreasureHunter(player)) {
            List<ExcavationTreasure> treasures = getTreasures(blockState);

            if (!treasures.isEmpty()) {
                int skillLevel = mcMMOPlayer.getProfile().getSkillLevel(SkillType.EXCAVATION);
                int activationChance = PerksUtils.handleLuckyPerks(player, SkillType.EXCAVATION);
                Location location = blockState.getLocation();

                for (ExcavationTreasure treasure : treasures) {
                    if (skillLevel >= treasure.getDropLevel() && Misc.getRandom().nextDouble() * activationChance <= treasure.getDropChance()) {
                        xp += treasure.getXp();
                        Misc.dropItem(location, treasure.getDrop());
                    }
                }
            }
        }

        mcMMOPlayer.beginXpGain(SkillType.EXCAVATION, xp);
    }

    /**
     * Process the Giga Drill Breaker ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     * @param player The {@link Player} using this ability
     */
    public static void gigaDrillBreaker(BlockState blockState, Player player) {
        Excavation.excavationBlockCheck(blockState, player);
        Excavation.excavationBlockCheck(blockState, player);
   }

    /**
     * Get the list of possible {@link ExcavationTreasure|ExcavationTreasures} obtained from a given block.
     *
     * @param blockState The {@link BlockState} of the block to check.
     * @return the list of treasures that could be found
     */
    private static List<ExcavationTreasure> getTreasures(BlockState blockState) {
        switch (blockState.getType()) {
        case DIRT:
            return TreasuresConfig.getInstance().excavationFromDirt;

        case GRASS:
            return TreasuresConfig.getInstance().excavationFromGrass;

        case SAND:
            return TreasuresConfig.getInstance().excavationFromSand;

        case GRAVEL:
            return TreasuresConfig.getInstance().excavationFromGravel;

        case CLAY:
            return TreasuresConfig.getInstance().excavationFromClay;

        case MYCEL:
            return TreasuresConfig.getInstance().excavationFromMycel;

        case SOUL_SAND:
            return TreasuresConfig.getInstance().excavationFromSoulSand;

        default:
            return new ArrayList<ExcavationTreasure>();
        }
    }
}
