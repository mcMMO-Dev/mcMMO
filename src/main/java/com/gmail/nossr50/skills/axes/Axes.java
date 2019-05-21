package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Axes {
    public static double axeMasteryRankDamageMultiplier = AdvancedConfig.getInstance().getAxeMasteryRankDamageMultiplier();

    public static double criticalHitPVPModifier   = AdvancedConfig.getInstance().getCriticalStrikesPVPModifier();
    public static double criticalHitPVEModifier   = AdvancedConfig.getInstance().getCriticalStrikesPVEModifier();

    public static double impactChance                = AdvancedConfig.getInstance().getImpactChance();

    public static double greaterImpactBonusDamage         = AdvancedConfig.getInstance().getGreaterImpactBonusDamage();
    public static double greaterImpactChance              = AdvancedConfig.getInstance().getGreaterImpactChance();
    public static double greaterImpactKnockbackMultiplier = AdvancedConfig.getInstance().getGreaterImpactModifier();

    public static double skullSplitterModifier = AdvancedConfig.getInstance().getSkullSplitterModifier();

    protected static boolean hasArmor(LivingEntity target) {
        for (ItemStack itemStack : target.getEquipment().getArmorContents()) {
            if (itemStack != null && ItemUtils.isArmor(itemStack)) {
                return true;
            }
        }

        return false;
    }

    /**
     * For every rank in Axe Mastery we add RankDamageMultiplier to get the total bonus damage from Axe Mastery
     * @param player The target player
     * @return The axe mastery bonus damage which will be added to their attack
     */
    public static double getAxeMasteryBonusDamage(Player player)
    {
        return RankUtils.getRank(player, SubSkillType.AXES_AXE_MASTERY) * Axes.axeMasteryRankDamageMultiplier;
    }
}
