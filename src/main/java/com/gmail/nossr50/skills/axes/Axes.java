package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.util.ItemUtils;

public class Axes {
    public static int bonusDamageMaxBonus      = AdvancedConfig.getInstance().getBonusDamageAxesBonusMax();
    public static int bonusDamageMaxBonusLevel = AdvancedConfig.getInstance().getBonusDamageAxesMaxBonusLevel();

    public static int    criticalHitMaxBonusLevel = AdvancedConfig.getInstance().getAxesCriticalMaxBonusLevel();
    public static double criticalHitMaxChance     = AdvancedConfig.getInstance().getAxesCriticalChance();
    public static double criticalHitPVPModifier   = AdvancedConfig.getInstance().getAxesCriticalPVPModifier();
    public static double criticalHitPVEModifier   = AdvancedConfig.getInstance().getAxesCriticalPVEModifier();

    public static int    impactIncreaseLevel         = AdvancedConfig.getInstance().getArmorImpactIncreaseLevel();
    public static double impactChance                = AdvancedConfig.getInstance().getImpactChance();
    public static double impactMaxDurabilityModifier = AdvancedConfig.getInstance().getArmorImpactMaxDurabilityDamage() / 100D;

    public static int    greaterImpactBonusDamage         = AdvancedConfig.getInstance().getGreaterImpactBonusDamage();
    public static double greaterImpactChance              = AdvancedConfig.getInstance().getGreaterImpactChance();
    public static double greaterImpactKnockbackMultiplier = AdvancedConfig.getInstance().getGreaterImpactModifier();

    public static int skullSplitterModifier = AdvancedConfig.getInstance().getSkullSplitterModifier();

    protected static boolean hasArmor(LivingEntity target) {
        for (ItemStack itemStack : target.getEquipment().getArmorContents()) {
            if (ItemUtils.isArmor(itemStack)) {
                return true;
            }
        }

        return false;
    }
}
