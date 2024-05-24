package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Archery {
    private static final List<TrackedEntity> trackedEntities = new ArrayList<>();

    public static double skillShotMaxBonusDamage     = mcMMO.p.getAdvancedConfig().getSkillShotDamageMax();

    public static double dazeBonusDamage = mcMMO.p.getAdvancedConfig().getDazeBonusDamage();

    public static final double DISTANCE_XP_MULTIPLIER = ExperienceConfig.getInstance().getArcheryDistanceMultiplier();

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        for (TrackedEntity trackedEntity : trackedEntities) {
            if (trackedEntity.getLivingEntity().getEntityId() == livingEntity.getEntityId()) {
                trackedEntity.incrementArrowCount();
                return;
            }
        }

        addToTracker(livingEntity); // If the entity isn't tracked yet
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = new TrackedEntity(livingEntity);

        trackedEntity.incrementArrowCount();
        trackedEntities.add(trackedEntity);
    }

    protected static void removeFromTracker(TrackedEntity trackedEntity) {
        trackedEntities.remove(trackedEntity);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(@NotNull LivingEntity livingEntity) {
        for (Iterator<TrackedEntity> entityIterator = trackedEntities.iterator(); entityIterator.hasNext();) {
            TrackedEntity trackedEntity = entityIterator.next();

            if (trackedEntity.getID() == livingEntity.getUniqueId()) {
                ItemUtils.spawnItems(null, livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount(), ItemSpawnReason.ARROW_RETRIEVAL_ACTIVATED);
                entityIterator.remove();
                return;
            }
        }
    }

    public static double getSkillShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage, (oldDamage + Archery.skillShotMaxBonusDamage));
    }

    public static double getDamageBonusPercent(Player player) {
        return ((RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)) * (mcMMO.p.getAdvancedConfig().getSkillShotRankDamageMultiplier()) / 100.0D);
    }
}
