package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.api.ItemSpawnReason;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Archery {
    private static final Map<UUID, TrackedEntity> trackedEntities = new ConcurrentHashMap<>();

    public static double skillShotMaxBonusDamage = mcMMO.p.getAdvancedConfig()
            .getSkillShotDamageMax();

    public static double dazeBonusDamage = mcMMO.p.getAdvancedConfig().getDazeBonusDamage();

    public static final double DISTANCE_XP_MULTIPLIER = ExperienceConfig.getInstance()
            .getArcheryDistanceMultiplier();

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        final TrackedEntity trackedEntity = trackedEntities.computeIfAbsent(livingEntity.getUniqueId(), k -> new TrackedEntity(livingEntity));
        trackedEntity.incrementArrowCount();
    }

    protected static void removeFromTracker(TrackedEntity trackedEntity) {
        trackedEntities.remove(trackedEntity.getID());
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(@NotNull LivingEntity livingEntity) {
        final TrackedEntity trackedEntity = trackedEntities.remove(livingEntity.getUniqueId());
        if (trackedEntity != null) {
            ItemUtils.spawnItems(null, livingEntity.getLocation(),
                    new ItemStack(Material.ARROW), trackedEntity.getArrowCount(),
                    ItemSpawnReason.ARROW_RETRIEVAL_ACTIVATED);
        }
    }

    public static double getSkillShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage, (oldDamage + Archery.skillShotMaxBonusDamage));
    }

    public static double getDamageBonusPercent(Player player) {
        return ((RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT))
                * (mcMMO.p.getAdvancedConfig().getSkillShotRankDamageMultiplier()) / 100.0D);
    }
}
