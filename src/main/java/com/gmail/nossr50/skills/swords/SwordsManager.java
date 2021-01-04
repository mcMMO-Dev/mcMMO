package com.gmail.nossr50.skills.swords;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityToolType;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.runnables.skills.BleedTimerTask;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SwordsManager extends SkillManager {
    public SwordsManager(OnlineMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.SWORDS);
    }

    public boolean canActivateAbility() {
        return mmoPlayer.getSuperAbilityManager().isAbilityToolPrimed(AbilityToolType.SERRATED_STRIKES_TOOL) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseStab() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_STAB) && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_STAB);
    }

    public boolean canUseRupture() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_RUPTURE) && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_RUPTURE);
    }

    public boolean canUseCounterAttack(Entity target) {
        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.SWORDS_COUNTER_ATTACK))
            return false;

        return target instanceof LivingEntity && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK);
    }

    public boolean canUseSerratedStrike() {
        if(!RankUtils.hasUnlockedSubskill(mmoPlayer, SubSkillType.SWORDS_SERRATED_STRIKES))
            return false;

        return mmoPlayer.getSuperAbilityManager().getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void ruptureCheck(@NotNull LivingEntity target) throws IllegalStateException {
        if(BleedTimerTask.isBleedOperationAllowed()) {
            if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_RUPTURE, getPlayer())) {

                if (target instanceof Player) {
                    Player defender = (Player) target;

                    //Don't start or add to a bleed if they are blocking
                    if(defender.isBlocking())
                        return;

                    if (NotificationManager.doesPlayerUseNotifications(defender)) {
                        if(!BleedTimerTask.isBleeding(defender))
                            NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Started");
                    }
                }

                BleedTimerTask.add(target, getPlayer(), getRuptureBleedTicks(), RankUtils.getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE), getToolTier(getPlayer().getInventory().getItemInMainHand()));

                if (mmoPlayer.hasSkillChatNotifications()) {
                    NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding");
                }
            }
        }
    }

    public double getStabDamage()
    {
        int rank = RankUtils.getRank(getPlayer(), SubSkillType.SWORDS_STAB);

        if(rank > 0)
        {
            return (1.0D + (rank * 1.5));
        }

        return 0;
    }

    public int getToolTier(@NotNull ItemStack itemStack)
    {
        if(ItemUtils.isNetheriteTool(itemStack))
            return 5;
        if(ItemUtils.isDiamondTool(itemStack))
            return 4;
        else if(ItemUtils.isIronTool(itemStack) || ItemUtils.isGoldTool(itemStack))
            return 3;
        else if(ItemUtils.isStoneTool(itemStack))
            return 2;
        else
            return 1;
    }

    public int getRuptureBleedTicks()
    {
        int bleedTicks = 2 * RankUtils.getRank(mmoPlayer, SubSkillType.SWORDS_RUPTURE);

        if(bleedTicks > Swords.bleedMaxTicks)
            bleedTicks = Swords.bleedMaxTicks;

        return bleedTicks;
    }

    /**
     * Handle the effects of the Counter Attack ability
     *
     * @param attacker The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void counterAttackChecks(@NotNull LivingEntity attacker, double damage) {
        if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_COUNTER_ATTACK, getPlayer())) {
            CombatUtils.dealDamage(attacker, damage / Swords.counterAttackModifier, getPlayer());

            NotificationManager.sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Countered");

            if (attacker instanceof Player) {
                NotificationManager.sendPlayerInformation((Player)attacker, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Counter.Hit");
            }
        }
    }

    /**
     * Handle the effects of the Serrated Strikes ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void serratedStrikes(@NotNull LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Swords.serratedStrikesModifier, modifiers, skill);
    }
}
