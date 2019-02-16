package com.gmail.nossr50.core.skills.primary.swords;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.mcmmo.entity.Player;
import com.gmail.nossr50.core.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.core.skills.*;
import com.gmail.nossr50.core.util.Permissions;
import com.gmail.nossr50.core.util.player.NotificationManager;
import com.gmail.nossr50.core.util.random.RandomChanceUtil;
import com.gmail.nossr50.core.util.skills.CombatUtils;
import com.gmail.nossr50.core.util.skills.RankUtils;
import com.gmail.nossr50.core.util.skills.SkillActivationType;

public class SwordsManager extends SkillManager {
    public SwordsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.SWORDS);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.SWORD) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseRupture() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_RUPTURE) && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_RUPTURE);
    }

    public boolean canUseCounterAttack(Entity target) {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK))
            return false;

        return target instanceof LivingEntity && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK);
    }

    public boolean canUseSerratedStrike() {
        if (!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_SERRATED_STRIKES))
            return false;

        return mcMMOPlayer.getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void ruptureCheck(LivingEntity target) {
        if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_RUPTURE, getPlayer())) {

            if (target instanceof Player) {
                Player defender = (Player) target;

                if (UserManager.getPlayer(defender).useChatNotifications()) {
                    if (!BleedTimerTask.isBleeding(defender))
                        NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Started");
                }
            }

            BleedTimerTask.add(target, getPlayer(), getRuptureBleedTicks(), RankUtils.getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE));

            if (mcMMOPlayer.useChatNotifications()) {
                NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding");
            }
        }
    }

    public int getRuptureBleedTicks() {
        int bleedTicks = 2 * RankUtils.getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE);

        if (bleedTicks > Swords.bleedMaxTicks)
            bleedTicks = Swords.bleedMaxTicks;

        return bleedTicks;
    }

    /**
     * Handle the effects of the Counter Attack ability
     *
     * @param attacker The {@link LivingEntity} being affected by the ability
     * @param damage   The amount of damage initially dealt by the event
     */
    public void counterAttackChecks(LivingEntity attacker, double damage) {
        if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_COUNTER_ATTACK, getPlayer())) {
            CombatUtils.dealDamage(attacker, damage / Swords.counterAttackModifier, getPlayer());

            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Countered");

            if (attacker instanceof Player) {
                NotificationManager.sendPlayerInformation((Player) attacker, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Counter.Hit");
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
        BleedTimerTask.add(target, getPlayer(), getRuptureBleedTicks(), RankUtils.getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE));
    }
}
