package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Archery {
    private static List<TrackedEntity> trackedEntities = new ArrayList<TrackedEntity>();

    public static int    skillShotIncreaseLevel      = AdvancedConfig.getInstance().getSkillShotIncreaseLevel();
    public static double skillShotIncreasePercentage = AdvancedConfig.getInstance().getSkillShotRankDamageMultiplier();
    public static double skillShotMaxBonusDamage     = AdvancedConfig.getInstance().getSkillShotDamageMax();

    public static double dazeBonusDamage = AdvancedConfig.getInstance().getDazeBonusDamage();

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
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        for (Iterator<TrackedEntity> entityIterator = trackedEntities.iterator(); entityIterator.hasNext();) {
            TrackedEntity trackedEntity = entityIterator.next();

            if (trackedEntity.getID() == livingEntity.getUniqueId()) {
                Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), trackedEntity.getArrowCount());
                entityIterator.remove();
                return;
            }
        }
    }

    /**
     * Every rank we increase Skill Shot's bonus damage % by the IncreaseDamage percentage value from advanced.yml
     * Divide end result by 100.0D to get proper scale
     * Damage is capped in advanced.yml by Archery.SkillShot.MaxDamage
     *
     * @param player The target player
     * @param oldDamage The raw damage of the arrow before we add bonus damage
     * @return The damage that the arrow will deal after we've added bonus damage, damage is capped by Archery.SkillShot.MaxDamage
     */
    public static double getSkillShotBonusDamage(Player player, double oldDamage)
    {
        double damageBonusPercent = ((RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)) * Archery.skillShotIncreasePercentage) / 100.0D;
        return Math.min(oldDamage * damageBonusPercent, Archery.skillShotMaxBonusDamage);
    }
}
