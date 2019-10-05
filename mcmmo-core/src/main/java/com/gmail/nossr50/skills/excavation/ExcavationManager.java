package com.gmail.nossr50.skills.excavation;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.behaviours.ExcavationBehaviour;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class ExcavationManager extends SkillManager {

    private final ExcavationBehaviour excavationBehaviour;

    public ExcavationManager(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.EXCAVATION);
        this.excavationBehaviour = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getExcavationBehaviour();
    }

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void excavationBlockCheck(BlockState blockState) {
        int xp = excavationBehaviour.getBlockXP(blockState);

//        if (pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
//            List<ExcavationTreasure> treasures = excavationBehaviour.getTreasures(blockState);
//
//            if (!treasures.isEmpty()) {
//                int skillLevel = getSkillLevel();
//                Location location = Misc.getBlockCenter(blockState);
//
//                for (ExcavationTreasure treasure : treasures) {
//                    if (skillLevel >= treasure.getDropLevel()
//                            && pluginRef.getRandomChanceTools().checkRandomChanceExecutionSuccess(getPlayer(), PrimarySkillType.EXCAVATION, treasure.getDropChance())) {
//
//                        //Spawn Vanilla XP orbs if a dice roll succeeds
//                        if(pluginRef.getRandomChanceTools().rollDice(getArchaelogyExperienceOrbChance(), 100)) {
//                            ExperienceOrb experienceOrb = (ExperienceOrb) getPlayer().getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB);
//                            experienceOrb.setExperience(getExperienceOrbsReward());
//                        }
//
//                        xp += treasure.getXp();
//                        Misc.dropItem(location, treasure.getDrop());
//                    }
//                }
//            }
//        }

        applyXpGain(xp, XPGainReason.PVE);
    }

    public int getExperienceOrbsReward() {
        return 1 * getArchaeologyRank();
    }

    public double getArchaelogyExperienceOrbChance() {
        return getArchaeologyRank() * 2;
    }

    public int getArchaeologyRank() {
        return pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY);
    }

    public void printExcavationDebug(Player player, BlockState blockState)
    {
//        if (pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
//            List<ExcavationTreasure> treasures = excavationBehaviour.getTreasures(blockState);
//
//            if (!treasures.isEmpty()) {
//                for (ExcavationTreasure treasure : treasures) {
//                    player.sendMessage("|||||||||||||||||||||||||||||||||");
//                    player.sendMessage("[mcMMO DEBUG] Treasure found: (" + treasure.getDrop().getType().toString() + ")");
//                    player.sendMessage("[mcMMO DEBUG] Drop Chance for Treasure: " + treasure.getDropChance());
//                    player.sendMessage("[mcMMO DEBUG] Skill Level Required: " + treasure.getDropLevel());
//                    player.sendMessage("[mcMMO DEBUG] XP for Treasure: " + treasure.getXp());
//                }
//            } else {
//                player.sendMessage("[mcMMO DEBUG] No treasures found for this block.");
//            }
//        }
    }

    /**
     * Process the Giga Drill Breaker ability.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    public void gigaDrillBreaker(BlockState blockState) {
        excavationBlockCheck(blockState);
        excavationBlockCheck(blockState);

        pluginRef.getSkillTools().handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(), pluginRef.getConfigManager().getConfigSuperAbilities().getSuperAbilityLimits().getToolDurabilityDamage());
    }
}
