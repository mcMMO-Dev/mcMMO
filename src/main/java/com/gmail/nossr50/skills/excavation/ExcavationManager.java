package com.gmail.nossr50.skills.excavation;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

import static java.util.Objects.requireNonNull;

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
        requireNonNull(blockState, "excavationBlockCheck: blockState cannot be null");
        if (Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            List<ExcavationTreasure> treasures = getTreasures(blockState);

            if (!treasures.isEmpty()) {
                int skillLevel = getSkillLevel();
                Location location = Misc.getBlockCenter(blockState);

                for (ExcavationTreasure treasure : treasures) {
                    if (skillLevel >= treasure.getDropLevel()
                            && ProbabilityUtil.isStaticSkillRNGSuccessful(
                                    PrimarySkillType.EXCAVATION, mmoPlayer, treasure.getDropProbability())) {
                        processExcavationBonusesOnBlock(blockState, treasure, location);
                    }
                }
            }
        }

        applyXpGain(xp, XPGainReason.PVE, XPGainSource.SELF);
    }

    @VisibleForTesting
    public List<ExcavationTreasure> getTreasures(@NotNull BlockState blockState) {
        requireNonNull(blockState, "blockState cannot be null");
        return Excavation.getTreasures(blockState);
    }

    @VisibleForTesting
    public void processExcavationBonusesOnBlock(BlockState blockState, ExcavationTreasure treasure, Location location) {
        //Spawn Vanilla XP orbs if a dice roll succeeds
        if (ProbabilityUtil.isStaticSkillRNGSuccessful(
                PrimarySkillType.EXCAVATION, mmoPlayer, getArchaelogyExperienceOrbChance())) {
            Misc.spawnExperienceOrb(location, getExperienceOrbsReward());
        }

        int xp = 0;
        xp += treasure.getXp();
        ItemUtils.spawnItem(getPlayer(), location, treasure.getDrop(), ItemSpawnReason.EXCAVATION_TREASURE);
        if (xp > 0) {
            applyXpGain(xp, XPGainReason.PVE, XPGainSource.SELF);
        }
    }

    public int getExperienceOrbsReward() {
        return getArchaeologyRank();
    }

    public double getArchaelogyExperienceOrbChance() {
        return getArchaeologyRank() * 2;
    }

    public int getArchaeologyRank() {
        return RankUtils.getRank(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    public void printExcavationDebug(Player player, BlockState blockState) {
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

        SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), mcMMO.p.getGeneralConfig().getAbilityToolDamage());
    }
}
