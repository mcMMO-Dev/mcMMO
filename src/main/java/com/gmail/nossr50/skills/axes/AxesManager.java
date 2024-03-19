package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
        if (ProbabilityUtil.isNonRNGSkillActivationSuccessful(SubSkillType.AXES_AXE_MASTERY, getPlayer())) {
            return Axes.getAxeMasteryBonusDamage(getPlayer());
        }

        return 0;
    }

    /**
     * Handle the effects of the Critical Hit ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double criticalHit(LivingEntity target, double damage) {
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.AXES_CRITICAL_STRIKES, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        if (mmoPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CriticalHit");
        }

        if (target instanceof Player defender) {

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
                if (ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.AXES_ARMOR_IMPACT, getPlayer())) {
                    SkillUtils.handleArmorDurabilityChange(armor, durabilityDamage, 1);
                }
            }
        }
    }

    public double getImpactDurabilityDamage() {
        return mcMMO.p.getAdvancedConfig().getImpactDurabilityDamageMultiplier() * RankUtils.getRank(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT);
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(@NotNull LivingEntity target) {
        //static chance (3rd param)
        if (!ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.AXES_GREATER_IMPACT, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));

        if (mmoPlayer.useChatNotifications()) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Proc");
        }

        if (target instanceof Player defender) {

            if (NotificationManager.doesPlayerUseNotifications(defender)) {
                NotificationManager.sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Struck");
            }
        }

        return Axes.greaterImpactBonusDamage;
    }

    /**
     * Handle the effects of the Skull Splitter ability
     *  @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void skullSplitterCheck(@NotNull LivingEntity target, double damage) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Axes.skullSplitterModifier, skill);
    }
}
