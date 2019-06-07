package com.gmail.nossr50.skills.archery;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.meta.TrackedArrowMeta;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Archery {
    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public static void arrowRetrievalCheck(LivingEntity livingEntity) {
        if(livingEntity.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
            Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).get(0).asInt());
        }
    }

    public static void incrementArrowCount(LivingEntity livingEntity) {
        if(livingEntity.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
            int arrowCount = livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).get(0).asInt();
            livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).set(0, new FixedMetadataValue(mcMMO.p, arrowCount + 1));
        } else {
            livingEntity.setMetadata(MetadataConstants.ARROW_TRACKER_METAKEY, new TrackedArrowMeta(mcMMO.p, 1));
        }
    }

    public static double getSkillShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage, getSkillShotDamageCap());
    }

    public static double getDamageBonusPercent(Player player) {
        return ((RankUtils.getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)) * mcMMO.getConfigManager().getConfigArchery().getSkillShotDamageMultiplier()) / 100.0D;
    }

    public static double getSkillShotDamageCap() {
        return mcMMO.getConfigManager().getConfigArchery().getSkillShotDamageCeiling();
    }

    public static double getDazeBonusDamage() {
        return mcMMO.getConfigManager().getConfigArchery().getDaze().getDazeBonusDamage();
    }

    public static double getDistanceXpMultiplier() {
        return mcMMO.getConfigManager().getConfigExperience().getExperienceArchery().getDistanceMultiplier();
    }
}
