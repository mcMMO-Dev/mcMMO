package com.gmail.nossr50.datatypes.skills.behaviours;

import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * These behaviour classes are a band-aid fix for a larger problem
 * Until the new skill system for mcMMO is finished/implemented, there is no good place to store the hardcoded behaviours for each skill
 * These behaviour classes server this purpose, they act as a bad solution to a bad problem
 * These classes will be removed when the new skill system is in place
 */
@Deprecated
public class AxesBehaviour {

    private final mcMMO pluginRef;

    public AxesBehaviour(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
    }

    public boolean hasArmor(LivingEntity target) {
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
    public double getAxeMasteryBonusDamage(Player player) {
        return RankUtils.getRank(player, SubSkillType.AXES_AXE_MASTERY) * pluginRef.getConfigManager().getConfigAxes().getAxeMasteryMultiplier();
    }
}
