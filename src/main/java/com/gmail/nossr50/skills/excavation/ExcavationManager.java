package com.gmail.nossr50.skills.excavation;

import static java.util.Objects.requireNonNull;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.treasure.ExcavationTreasure;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class ExcavationManager extends SkillManager {
    public ExcavationManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.EXCAVATION);
    }

    /**
     * Process treasure drops & XP gain for Excavation.
     *
     * @param blockState The {@link BlockState} to check ability activation for
     */
    @Deprecated(forRemoval = true, since = "2.2.024")
    public void excavationBlockCheck(BlockState blockState) {
        excavationBlockCheck(blockState.getBlock());
    }

    public void excavationBlockCheck(Block block) {
        requireNonNull(block, "excavationBlockCheck: block cannot be null");
        final int xp = ExperienceConfig.getInstance().getXp(PrimarySkillType.EXCAVATION, block.getType());
        applyXpGain(xp, XPGainReason.PVE, XPGainSource.SELF);
        // Treasure drops are rolled inside BlockDropItemEvent via rollAndCollectTreasureDrops()
    }

    @Deprecated(forRemoval = true, since = "2.2.024")
    public List<ExcavationTreasure> getTreasures(@NotNull BlockState blockState) {
        requireNonNull(blockState, "blockState cannot be null");
        return getTreasures(blockState.getType());
    }

    public List<ExcavationTreasure> getTreasures(@NotNull Block block) {
        requireNonNull(block, "block cannot be null");
        return getTreasures(block.getType());
    }

    public List<ExcavationTreasure> getTreasures(@NotNull Material material) {
        requireNonNull(material, "material cannot be null");
        return Excavation.getTreasures(material);
    }

    @VisibleForTesting
    @Deprecated(forRemoval = true, since = "2.2.024")
    public void processExcavationBonusesOnBlock(BlockState ignored, ExcavationTreasure treasure,
            Location location) {
        processExcavationBonusesOnBlock(treasure, location);
    }

    /**
     * Rolls for excavation treasures. This is the primary production entry point, called from
     * {@link org.bukkit.event.block.BlockDropItemEvent}.
     *
     * <p>{@code block.getType()} is AIR by event time because the block has already been removed
     * from the world. Pass {@code material} from {@code event.getBlockState().getType()} (the
     * pre-break snapshot) so treasure lookup uses the correct material.
     *
     * @param block    the broken block — used only for spawn location
     * @param material the material of the block before it was broken
     * @return list of treasure {@link ItemStack}s from all successful rolls
     */
    public @NotNull List<ItemStack> rollAndCollectTreasureDrops(
            @NotNull Block block, @NotNull Material material) {
        requireNonNull(block, "block cannot be null");
        requireNonNull(material, "material cannot be null");
        return doRollAndCollectTreasureDrops(material, Misc.getBlockCenter(block));
    }

    /**
     * @deprecated Use {@link #rollAndCollectTreasureDrops(Block, Material)} instead. When called
     *     during {@link org.bukkit.event.block.BlockDropItemEvent}, {@code block.getType()}
     *     returns AIR, causing treasure lookup to fail silently.
     */
    @Deprecated(forRemoval = true, since = "2.2.053")
    public @NotNull List<ItemStack> rollAndCollectTreasureDrops(@NotNull Block block) {
        requireNonNull(block, "block cannot be null");
        return doRollAndCollectTreasureDrops(block.getType(), Misc.getBlockCenter(block));
    }

    private @NotNull List<ItemStack> doRollAndCollectTreasureDrops(
            @NotNull Material material, @NotNull Location centerOfBlock) {
        if (!Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            return List.of();
        }

        final List<ExcavationTreasure> treasures = getTreasures(material);
        if (treasures.isEmpty()) {
            return List.of();
        }

        final int skillLevel = getSkillLevel();

        // GDB called excavationBlockCheck 3 times (1 regular + 2 via gigaDrillBreaker),
        // giving 3 independent treasure rolls for the centre block. Mirror that here.
        final int rollCount = mmoPlayer.getAbilityMode(SuperAbilityType.GIGA_DRILL_BREAKER) ? 3 : 1;

        final List<ItemStack> drops = new ArrayList<>();
        for (int roll = 0; roll < rollCount; roll++) {
            for (final ExcavationTreasure treasure : treasures) {
                if (skillLevel >= treasure.getDropLevel()
                        && ProbabilityUtil.isStaticSkillRNGSuccessful(
                        PrimarySkillType.EXCAVATION, mmoPlayer,
                        treasure.getDropProbability())) {
                    if (ProbabilityUtil.isStaticSkillRNGSuccessful(
                            PrimarySkillType.EXCAVATION, mmoPlayer,
                            getArchaelogyExperienceOrbChance())) {
                        Misc.spawnExperienceOrb(centerOfBlock, getExperienceOrbsReward());
                    }
                    final int treasureXp = treasure.getXp();
                    if (treasureXp > 0) {
                        applyXpGain(treasureXp, XPGainReason.PVE, XPGainSource.SELF);
                    }
                    drops.add(treasure.getDrop().clone());
                }
            }
        }
        return drops;
    }

    /**
     * @deprecated Treasure drops are now handled via
     *     {@link #rollAndCollectTreasureDrops(Block)} inside
     *     {@link org.bukkit.event.block.BlockDropItemEvent} so they are visible to
     *     Telekinesis-style enchant plugins.
     */
    public void processExcavationBonusesOnBlock(ExcavationTreasure treasure, Location location) {
        //Spawn Vanilla XP orbs if a dice roll succeeds
        if (ProbabilityUtil.isStaticSkillRNGSuccessful(
                PrimarySkillType.EXCAVATION, mmoPlayer, getArchaelogyExperienceOrbChance())) {
            Misc.spawnExperienceOrb(location, getExperienceOrbsReward());
        }

        int xp = treasure.getXp();
        ItemUtils.spawnItem(getPlayer(), location, treasure.getDrop(),
                ItemSpawnReason.EXCAVATION_TREASURE);
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

    public void printExcavationDebug(Player player, Block block) {
        if (Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.EXCAVATION_ARCHAEOLOGY)) {
            List<ExcavationTreasure> treasures = Excavation.getTreasures(block);

            if (!treasures.isEmpty()) {
                for (ExcavationTreasure treasure : treasures) {
                    player.sendMessage("|||||||||||||||||||||||||||||||||");
                    player.sendMessage(
                            "[mcMMO DEBUG] Treasure found: (" + treasure.getDrop().getType() + ")");
                    player.sendMessage(
                            "[mcMMO DEBUG] Drop Chance for Treasure: " + treasure.getDropChance());
                    player.sendMessage(
                            "[mcMMO DEBUG] Skill Level Required: " + treasure.getDropLevel());
                    player.sendMessage("[mcMMO DEBUG] XP for Treasure: " + treasure.getXp());
                }
            } else {
                player.sendMessage("[mcMMO DEBUG] No treasures found for this block.");
            }
        }
    }

    /**
     * Process the Giga Drill Breaker ability.
     *
     * @param block The {@link Block} to check ability activation for
     */
    public void gigaDrillBreaker(Block block) {
        excavationBlockCheck(block);
        excavationBlockCheck(block);

        SkillUtils.handleDurabilityChange(getPlayer().getInventory().getItemInMainHand(),
                mcMMO.p.getGeneralConfig().getAbilityToolDamage());
    }
}
