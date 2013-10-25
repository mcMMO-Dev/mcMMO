package com.gmail.nossr50.skills.excavation;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.events.skills.excavation.McMMOPlayerExcavationTreasureEvent;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.SkillUtils;

public class ExcavationManager extends SkillManager {
    public ExcavationManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.EXCAVATION);
    }

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void blockBreak(BlockState blockState) {
        int xp = Excavation.getBlockXP(blockState);

        if (Permissions.excavationTreasureHunter(getPlayer())) {
            List<ExcavationTreasure> treasures = Excavation.getTreasures(blockState);

            if (!treasures.isEmpty()) {
                int skillLevel = getSkillLevel();
                Location location = blockState.getLocation();

                Player player = getPlayer();
                Block block = blockState.getBlock();

                for (ExcavationTreasure treasure : treasures) {
                    if (skillLevel >= treasure.getDropLevel() && SkillUtils.treasureDropSuccessful(treasure.getDropChance(), activationChance)) {
                        McMMOPlayerExcavationTreasureEvent event = new McMMOPlayerExcavationTreasureEvent(player, treasure.getDrop(), treasure.getXp(), block);
                        mcMMO.p.getServer().getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            continue;
                        }

                        xp += event.getXpGained();
                        Misc.dropItem(location, event.getTreasure());
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
        blockBreak(blockState);
        blockBreak(blockState);

        SkillUtils.handleDurabilityChange(getPlayer().getItemInHand(), Config.getInstance().getAbilityToolDamage());
    }
}
