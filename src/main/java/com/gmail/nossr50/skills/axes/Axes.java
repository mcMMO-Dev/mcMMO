package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Axes {
    public static double axeMasteryRankDamageMultiplier = mcMMO.p.getAdvancedConfig()
            .getAxeMasteryRankDamageMultiplier();

    public static double criticalHitPVPModifier = mcMMO.p.getAdvancedConfig()
            .getCriticalStrikesPVPModifier();
    public static double criticalHitPVEModifier = mcMMO.p.getAdvancedConfig()
            .getCriticalStrikesPVEModifier();

    public static double impactChance = mcMMO.p.getAdvancedConfig().getImpactChance();

    public static double greaterImpactBonusDamage = mcMMO.p.getAdvancedConfig()
            .getGreaterImpactBonusDamage();
    public static double greaterImpactChance = mcMMO.p.getAdvancedConfig().getGreaterImpactChance();
    public static double greaterImpactKnockbackMultiplier = mcMMO.p.getAdvancedConfig()
            .getGreaterImpactModifier();

    public static double skullSplitterModifier = mcMMO.p.getAdvancedConfig()
            .getSkullSplitterModifier();

    protected static boolean hasArmor(LivingEntity target) {
        if (target == null || !target.isValid() || target.getEquipment() == null) {
            return false;
        }

        for (ItemStack itemStack : target.getEquipment().getArmorContents()) {
            if (itemStack == null) {
                continue;
            }

            if (ItemUtils.isArmor(itemStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * For every rank in Axe Mastery we add RankDamageMultiplier to get the total bonus damage from
     * Axe Mastery
     *
     * @param player The target player
     * @return The axe mastery bonus damage which will be added to their attack
     */
    public static double getAxeMasteryBonusDamage(Player player) {
        return RankUtils.getRank(player, SubSkillType.AXES_AXE_MASTERY)
                * Axes.axeMasteryRankDamageMultiplier;
    }
}
