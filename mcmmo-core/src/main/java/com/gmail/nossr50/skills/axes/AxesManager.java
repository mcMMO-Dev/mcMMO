package com.gmail.nossr50.skills.axes;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.datatypes.skills.behaviours.AxesBehaviour;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class AxesManager extends SkillManager {

    private final AxesBehaviour axesBehaviour;

    public AxesManager(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.AXES);
        this.axesBehaviour = pluginRef.getDynamicSettingsManager().getSkillBehaviourManager().getAxesBehaviour();
    }

    public boolean canUseAxeMastery() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_AXE_MASTERY))
            return false;

        return pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.AXES_AXE_MASTERY);
    }

    public boolean canCriticalHit(LivingEntity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES))
            return false;

        return target.isValid() && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.AXES_CRITICAL_STRIKES);
    }

    public boolean canImpact(LivingEntity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT))
            return false;

        return target.isValid() && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT) && axesBehaviour.hasArmor(target);
    }

    public boolean canGreaterImpact(LivingEntity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_GREATER_IMPACT))
            return false;

        return target.isValid() && pluginRef.getPermissionTools().isSubSkillEnabled(getPlayer(), SubSkillType.AXES_GREATER_IMPACT) && !axesBehaviour.hasArmor(target);
    }

    public boolean canUseSkullSplitter(LivingEntity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.AXES_SKULL_SPLITTER))
            return false;

        return target.isValid() && mcMMOPlayer.getSuperAbilityMode(SuperAbilityType.SKULL_SPLITTER) && pluginRef.getPermissionTools().skullSplitter(getPlayer());
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.AXE) && pluginRef.getPermissionTools().skullSplitter(getPlayer());
    }

    /**
     * Handle the effects of the Axe Mastery ability
     */
    public double axeMastery() {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.ALWAYS_FIRES, SubSkillType.AXES_AXE_MASTERY, getPlayer())) {
            return 0;
        }

        return axesBehaviour.getAxeMasteryBonusDamage(getPlayer());
    }

    /**
     * Handle the effects of the Critical Hit ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double criticalHit(LivingEntity target, double damage) {
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.AXES_CRITICAL_STRIKES, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        if (mcMMOPlayer.useChatNotifications()) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CriticalHit");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (pluginRef.getNotificationManager().doesPlayerUseNotifications(defender)) {
                pluginRef.getNotificationManager().sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.CritStruck");
            }

            damage = (damage * pluginRef.getConfigManager().getConfigAxes().getConfigAxesCriticalStrikes().getDamageProperty().getPVPModifier()) - damage;
        } else {
            damage = (damage * pluginRef.getConfigManager().getConfigAxes().getConfigAxesCriticalStrikes().getDamageProperty().getPVEModifier()) - damage;
        }

        return damage;
    }

    /**
     * Handle the effects of the Impact ability
     *
     * @param target The {@link LivingEntity} being affected by Impact
     */
    public void impactCheck(LivingEntity target) {
        double durabilityDamage = getImpactDurabilityDamage();

        for (ItemStack armor : target.getEquipment().getArmorContents()) {
            if (armor != null && pluginRef.getItemTools().isArmor(armor)) {
                if (pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_ARMOR_IMPACT, getPlayer())) {
                    pluginRef.getSkillTools().handleDurabilityChange(armor, durabilityDamage, 1);
                }
            }
        }
    }

    public double getImpactDurabilityDamage() {
        return pluginRef.getConfigManager().getConfigAxes().getConfigAxesImpact().getImpactDurabilityDamageModifier() * pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.AXES_ARMOR_IMPACT);
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(LivingEntity target) {
        //chance (3rd param)
        if (!pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_STATIC_CHANCE, SubSkillType.AXES_GREATER_IMPACT, getPlayer())) {
            return 0;
        }

        Player player = getPlayer();

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(pluginRef.getConfigManager().getConfigAxes().getGreaterImpactKnockBackModifier()));

        if (mcMMOPlayer.useChatNotifications()) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Proc");
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (pluginRef.getNotificationManager().doesPlayerUseNotifications(defender)) {
                pluginRef.getNotificationManager().sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Axes.Combat.GI.Struck");
            }
        }

        return pluginRef.getConfigManager().getConfigAxes().getConfigAxesGreaterImpact().getBonusDamage();
    }

    /**
     * Handle the effects of the Skull Splitter ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void skullSplitterCheck(LivingEntity target, double damage, Map<DamageModifier, Double> modifiers) {
        pluginRef.getCombatTools().applyAbilityAoE(getPlayer(), target, damage / pluginRef.getConfigManager().getConfigAxes().getConfigAxesSkullSplitter().getSkullSplitterDamageDivisor(), modifiers, skill);
    }
}
