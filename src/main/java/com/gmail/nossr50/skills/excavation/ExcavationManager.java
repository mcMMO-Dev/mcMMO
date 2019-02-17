package com.gmail.nossr50.skills.excavation;

import com.gmail.nossr50.config.MainConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.List;

public class ExcavationManager extends SkillManager {
    public ExcavationManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.EXCAVATION);
    }

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void excavationBlockCheck(BlockState blockState) {
        int xp = Excavation.getBlockXP(blockState);

        if (Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            List<ExcavationTreasure> treasures = Excavation.getTreasures(blockState);

            if (!treasures.isEmpty()) {
                int skillLevel = getSkillLevel();
                Location location = Misc.getBlockCenter(blockState);

                for (ExcavationTreasure treasure : treasures) {
                    if (skillLevel >= treasure.getDropLevel()
                            && RandomChanceUtil.checkRandomChanceExecutionSuccess(getPlayer(), PrimarySkillType.EXCAVATION, treasure.getDropChance())) {
                        xp += treasure.getXp();
                        Misc.dropItem(location, treasure.getDrop());
                    }
                }
            }
        }

        applyXpGain(xp, XPGainReason.PVE);
    }

    public void printExcavationDebug(Player player, BlockState blockState)
    {
        if (Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            List<ExcavationTreasure> treasures = Excavation.getTreasures(blockState);

            if (!treasures.isEmpty()) {
                for (ExcavationTreasure treasure : treasures) {
                    player.sendMessage("|||||||||||||||||||||||||||||||||");
                    player.sendMessage("[mcMMO DEBUG] Treasure found: ("+treasure.getDrop().getType().toString()+")");
                    player.sendMessage("[mcMMO DEBUG] Drop Chance for Treasure: "+treasure.getDropChance());
                    player.sendMessage("[mcMMO DEBUG] Skill Level Required: "+treasure.getDropLevel());
                    player.sendMessage("[mcMMO DEBUG] XP for Treasure: "+treasure.getXp());
                }
            } else {
                player.sendMessage("[mcMMO DEBUG] No treasures found for this block.");
            }
        }
    }

    /**
     * Process the Giga Drill Breaker ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void gigaDrillBreaker(BlockState blockState) {
        excavationBlockCheck(blockState);
        excavationBlockCheck(blockState);

        SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), MainConfig.getInstance().getAbilityToolDamage());
    }
}
