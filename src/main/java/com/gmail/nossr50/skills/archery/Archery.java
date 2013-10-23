package com.gmail.nossr50.skills.archery;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.Misc;

public class Archery {
    private static Map<UUID, TrackedEntity> trackedEntities = new HashMap<UUID, TrackedEntity>();

    public static int    retrieveMaxBonusLevel = AdvancedConfig.getInstance().getRetrieveMaxBonusLevel();
    public static double retrieveMaxChance     = AdvancedConfig.getInstance().getRetrieveChanceMax();

    public static int    skillShotIncreaseLevel      = AdvancedConfig.getInstance().getSkillShotIncreaseLevel();
    public static double skillShotIncreasePercentage = AdvancedConfig.getInstance().getSkillShotIncreasePercentage();
    public static double skillShotMaxBonusPercentage = AdvancedConfig.getInstance().getSkillShotBonusMax();
    public static double skillShotMaxBonusDamage     = AdvancedConfig.getInstance().getSkillShotDamageMax();

    public static int    dazeMaxBonusLevel = AdvancedConfig.getInstance().getDazeMaxBonusLevel();
    public static double dazeModifier      = AdvancedConfig.getInstance().getDazeModifier();
    public static double dazeMaxBonus      = AdvancedConfig.getInstance().getDazeBonusMax();

    public static final double DISTANCE_XP_MULTIPLIER = 0.025;

    protected static void incrementTrackerValue(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = trackedEntities.get(livingEntity.getUniqueId());

        if (trackedEntity != null) {
            trackedEntity.incrementArrowCount();
            return;
        }

        addToTracker(livingEntity); // If the entity isn't tracked yet
    }

    protected static void addToTracker(LivingEntity livingEntity) {
        TrackedEntity trackedEntity = new TrackedEntity(livingEntity);

        trackedEntity.incrementArrowCount();
        trackedEntities.put(livingEntity.getUniqueId(), trackedEntity);
    }

    protected static void removeFromTracker(UUID id) {
        trackedEntities.remove(id);
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        UUID id = livingEntity.getUniqueId();
        TrackedEntity trackedEntity = trackedEntities.get(id);

        if (trackedEntity == null) {
            return;
        }

        Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount());
        removeFromTracker(id);
    }
}
