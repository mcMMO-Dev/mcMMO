package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SwordsManager extends SkillManager {
    public SwordsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SWORDS);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.SWORD) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseBleed() {
        return Permissions.bleed(getPlayer());
    }

    public boolean canUseCounterAttack(Entity target) {
        return target instanceof LivingEntity && Permissions.counterAttack(getPlayer());
    }

    public boolean canUseSerratedStrike() {
        return mcMMOPlayer.getAbilityMode(AbilityType.SERRATED_STRIKES) && Permissions.serratedStrikes(getPlayer());
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void bleedCheck(LivingEntity target) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Swords.bleedMaxChance, Swords.bleedMaxBonusLevel)) {

            if (getSkillLevel() >= Swords.bleedMaxBonusLevel) {
                BleedTimerTask.add(target, Swords.bleedMaxTicks);
            }
            else {
                BleedTimerTask.add(target, Swords.bleedBaseTicks);
            }

            if (mcMMOPlayer.useChatNotifications()) {
                getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding"));
            }

            if (target instanceof Player) {
                Player defender = (Player) target;

                if (UserManager.getPlayer(defender).useChatNotifications()) {
                    defender.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Started"));
                }
            }
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        if (SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Swords.counterAttackMaxChance, Swords.counterAttackMaxBonusLevel)) {
            CombatUtils.dealDamage(attacker, damage / Swords.counterAttackModifier);

            getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

            if (attacker instanceof Player) {
                ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
            }
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Swords.serratedStrikesModifier, skill);
        BleedTimerTask.add(target, Swords.serratedStrikesBleedTicks);
    }
}
