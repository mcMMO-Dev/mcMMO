package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Axes {

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
     *
     * @param player The target player
     * @return The axe mastery bonus damage which will be added to their attack
     */
    public static double getAxeMasteryBonusDamage(Player player) {
        return RankUtils.getRank(player, SubSkillType.AXES_AXE_MASTERY) * Axes.axeMasteryRankDamageMultiplier;
    }
}
