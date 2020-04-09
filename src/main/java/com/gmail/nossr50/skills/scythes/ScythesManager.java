package com.gmail.nossr50.skills.scythes;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static com.gmail.nossr50.util.skills.CombatUtils.getTier;

public class ScythesManager extends SkillManager {
    public ScythesManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.SCYTHES);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.HOE) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseLifeSteal() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SCYTHE_LIFE_STEAL))
            return false;

        return mcMMOPlayer.getAbilityMode(SuperAbilityType.LIFE_STEAL);
    }


    /**
     * Handle the effects of the Life Steal ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void lifeSteal(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Scythes.lifeStealModifier, modifiers, skill);

        int numberOfTargetsHit = 1;
        int numberOfTargetsPossible = getTier(getPlayer().getInventory().getItemInMainHand());
        for (Entity entity : target.getNearbyEntities(2.5, 2.5, 2.5)) {
            if (numberOfTargetsHit < numberOfTargetsPossible) {
                numberOfTargetsHit++;
            }
        }

        //Steal Health
        getPlayer().setHealth(getPlayer().getHealth() + damage * Scythes.lifeStealModifier * numberOfTargetsHit);
    }
}
