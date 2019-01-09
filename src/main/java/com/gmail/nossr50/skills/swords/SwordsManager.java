package com.gmail.nossr50.skills.swords;

import java.util.Map;

import com.gmail.nossr50.datatypes.skills.PrimarySkill;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbility;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
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
        super(mcMMOPlayer, PrimarySkill.SWORDS);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.SWORD) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseBleed() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_BLEED);
    }

    public boolean canUseCounterAttack(Entity target) {
        return target instanceof LivingEntity && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK);
    }

    public boolean canUseSerratedStrike() {
        return mcMMOPlayer.getAbilityMode(SuperAbility.SERRATED_STRIKES) && Permissions.serratedStrikes(getPlayer());
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void bleedCheck(LivingEntity target) {
        if (SkillUtils.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_BLEED, getPlayer(), this.skill, getSkillLevel(), activationChance)) {

            if (getSkillLevel() >= AdvancedConfig.getInstance().getMaxBonusLevel(SubSkillType.SWORDS_BLEED)) {
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

    /**
     * Handle the effects of the Counter Attack ability
     *
     * @param attacker The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void counterAttackChecks(LivingEntity attacker, double damage) {
        if (Swords.counterAttackRequiresBlock && !getPlayer().isBlocking()) {
            return;
        }

        if (SkillUtils.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_COUNTER_ATTACK, getPlayer(), this.skill, getSkillLevel(), activationChance)) {
            CombatUtils.dealDamage(attacker, damage / Swords.counterAttackModifier, getPlayer());

            getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

            if (attacker instanceof Player) {
                ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
            }
        }
    }

    /**
     * Handle the effects of the Serrated Strikes ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void serratedStrikes(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Swords.serratedStrikesModifier, modifiers, skill);
        BleedTimerTask.add(target, Swords.serratedStrikesBleedTicks);
    }
}
