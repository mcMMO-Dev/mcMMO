package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.RandomChanceUtil;
import com.gmail.nossr50.util.skills.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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

    public boolean canCriticalHit(@NotNull LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES);
    }

    public boolean canImpact(@NotNull LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT) && Axes.hasArmor(target);
    }

    public boolean canGreaterImpact(@NotNull LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_GREATER_IMPACT))
            return false;

        return target.isValid() && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.AXES_GREATER_IMPACT) && !Axes.hasArmor(target);
    }

    public boolean canUseSkullSplitter(@NotNull LivingEntity target) {
        if(!RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_SKULL_SPLITTER))
            return false;

        return target.isValid() && mmoPlayer.getAbilityMode(SuperAbilityType.SKULL_SPLITTER) && Permissions.skullSplitter(getPlayer());
    }

    public boolean canActivateAbility() {
        return mmoPlayer.getToolPreparationMode(ToolType.AXE) && Permissions.skullSplitter(getPlayer());
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

        if (mmoPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CriticalHit");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (NotificationManager.doesPlayerUseNotifications(defender)) {
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
    public void impactCheck(@NotNull LivingEntity target) {
        double durabilityDamage = getImpactDurabilityDamage();

        for (ItemStack armor : target.getEquipment().getArmorContents()) {
            if (armor != null && ItemUtils.isArmor(armor)) {
                if (RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_ARMOR_IMPACT, getPlayer())) {
                    SkillUtils.handleDurabilityChange(armor, durabilityDamage, 1);
                }
            }
        }
    }

    public double getImpactDurabilityDamage() {
        return AdvancedConfig.getInstance().getImpactDurabilityDamageMultiplier() * RankUtils.getRank(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT);
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(@NotNull LivingEntity target) {
        //static chance (3rd param)
        if (!RandomChanceUtil.isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_GREATER_IMPACT, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));

        if (mmoPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Proc");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (NotificationManager.doesPlayerUseNotifications(defender)) {
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
    public void skullSplitterCheck(@NotNull LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Axes.skullSplitterModifier, modifiers, skill);
    }
}
