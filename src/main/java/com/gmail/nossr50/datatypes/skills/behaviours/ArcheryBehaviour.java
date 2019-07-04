package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.datatypes.meta.TrackedArrowMeta;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.Misc;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class ArcheryBehaviour {

    private final mcMMO pluginRef;

    public ArcheryBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    /**
     * Check for arrow retrieval.
     *
     * @param livingEntity The entity hit by the arrows
     */
    public void arrowRetrievalCheck(LivingEntity livingEntity) {
        if(livingEntity.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
            Misc.dropItems(livingEntity.getLocation(), new ItemStack(Material.ARROW), livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).get(0).asInt());
        }
    }

    public void incrementArrowCount(LivingEntity livingEntity) {
        if(livingEntity.hasMetadata(MetadataConstants.ARROW_TRACKER_METAKEY)) {
            int arrowCount = livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).get(0).asInt();
            livingEntity.getMetadata(MetadataConstants.ARROW_TRACKER_METAKEY).set(0, new FixedMetadataValue(pluginRef, arrowCount + 1));
        } else {
            livingEntity.setMetadata(MetadataConstants.ARROW_TRACKER_METAKEY, new TrackedArrowMeta(pluginRef, 1));
        }
    }

    public double getSkillShotBonusDamage(Player player, double oldDamage) {
        double damageBonusPercent = getDamageBonusPercent(player);
        double newDamage = oldDamage + (oldDamage * damageBonusPercent);
        return Math.min(newDamage, getSkillShotDamageCap());
    }

    public double getDamageBonusPercent(Player player) {
        return ((pluginRef.getRankTools().getRank(player, SubSkillType.ARCHERY_SKILL_SHOT)) * pluginRef.getConfigManager().getConfigArchery().getSkillShotDamageMultiplier()) / 100.0D;
    }

    public double getSkillShotDamageCap() {
        return pluginRef.getConfigManager().getConfigArchery().getSkillShotDamageCeiling();
    }

    public double getDazeBonusDamage() {
        return pluginRef.getConfigManager().getConfigArchery().getDaze().getDazeBonusDamage();
    }

    public double getDistanceXpMultiplier() {
        return pluginRef.getConfigManager().getConfigExperience().getExperienceArchery().getDistanceMultiplier();
    }
}
