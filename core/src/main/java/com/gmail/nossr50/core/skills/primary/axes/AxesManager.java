package com.gmail.nossr50.core.skills.primary.axes;

import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.skills.PrimarySkillType;
import com.gmail.nossr50.core.skills.SubSkillType;
import com.gmail.nossr50.core.skills.SuperAbilityType;
import com.gmail.nossr50.core.skills.ToolType;
import com.gmail.nossr50.core.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AxesManager extends SkillManager {
    public AxesManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, PrimarySkillType.AXES);
    }

    public boolean canUseAxeMastery() {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_AXE_MASTERY))
            return false;

        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_AXE_MASTERY);
    }

    public boolean canCriticalHit(LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES);
    }

    public boolean canImpact(LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT) && Axes.hasArmor(target);
    }

    public boolean canGreaterImpact(LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_GREATER_IMPACT))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_GREATER_IMPACT) && !Axes.hasArmor(target);
    }

    public boolean canUseSkullSplitter(LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_SKULL_SPLITTER))
            return false;

        return target.isValid() && mcMMOPlayer.getAbilityMode(SuperAbilityType.SKULL_SPLITTER) && Permissions.skullSplitter(getPlayer());
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && Permissions.skullSplitter(getPlayer());
    }

    /**
     * Handle the effects of the Axe Mastery ability
     */
    public double axeMastery() {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.ALWAYS_FIRES, SubSkillType.AXES_AXE_MASTERY, getPlayer())) {
            return 0;
        }

        return Axes.getAxeMasteryBonusDamage(getPlayer());
    }

    /**
     * Handle the effects of the Critical Hit ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double criticalHit(LivingEntity target, double damage) {
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.AXES_CRITICAL_STRIKES, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        if (mcMMOPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CriticalHit");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CritStruck");
            }

            damage = (damage * Axes.criticalHitPVPModifier) - damage;
        }
        else {
            damage = (damage * Axes.criticalHitPVEModifier) - damage;
        }

        return damage;
    }

    /**
     * Handle the effects of the Impact ability
     *
     * @param target The {@link LivingEntity} being affected by Impact
     */
    public void impactCheck(LivingEntity target) {
        int durabilityDamage = getImpactDurabilityDamage();

        for (ItemStack armor : target.getEquipment().getArmorContents()) {
            if (armor != null && ItemUtils.isArmor(armor)) {
                if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_ARMOR_IMPACT, getPlayer())) {
                    SkillUtils.handleDurabilityChange(armor, durabilityDamage, Axes.impactMaxDurabilityModifier);
                }
            }
        }
    }

    public int getImpactDurabilityDamage() {
        return 1 + (getSkillLevel() / Axes.impactIncreaseLevel);
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(LivingEntity target) {
        //static chance (3rd param)
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_GREATER_IMPACT, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));

        if (mcMMOPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Proc");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Struck");
            }
        }

        return Axes.greaterImpactBonusDamage;
    }

    /**
     * Handle the effects of the Skull Splitter ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void skullSplitterCheck(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Axes.skullSplitterModifier, modifiers, skill);
    }
}
