package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AxesManager extends SkillManager {
    public AxesManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.AXES);
    }

    public boolean canUseAxeMastery() {
        return Permissions.bonusDamage(getPlayer(), skill);
    }

    public boolean canCriticalHit(LivingEntity target) {
        return target.isValid() && Permissions.criticalStrikes(getPlayer());
    }

    public boolean canImpact(LivingEntity target) {
        return target.isValid() && Permissions.armorImpact(getPlayer()) && Axes.hasArmor(target);
    }

    public boolean canGreaterImpact(LivingEntity target) {
        return target.isValid() && Permissions.greaterImpact(getPlayer()) && !Axes.hasArmor(target);
    }

    public boolean canUseSkullSplitter(LivingEntity target) {
        return target.isValid() && mcMMOPlayer.getAbilityMode(AbilityType.SKULL_SPLITTER) && Permissions.skullSplitter(getPlayer());
    }

    /**
     * Handle the effects of the Axe Mastery ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double axeMastery(LivingEntity target) {
        double axeBonus = Math.min(getSkillLevel() / (Axes.bonusDamageMaxBonusLevel / Axes.bonusDamageMaxBonus), Axes.bonusDamageMaxBonus);

        return CombatUtils.callFakeDamageEvent(getPlayer(), target, axeBonus);
    }

    /**
     * Handle the effects of the Critical Hit ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double criticalHit(LivingEntity target, double damage) {
        if (!SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Axes.criticalHitMaxChance, Axes.criticalHitMaxBonusLevel)) {
            return 0;
        }

        Player player = getPlayer();

        player.sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));

        if (target instanceof Player) {
            ((Player) target).sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));

            damage = (damage * Axes.criticalHitPVPModifier) - damage;
        }
        else {
            damage = (damage * Axes.criticalHitPVEModifier) - damage;
        }

        return CombatUtils.callFakeDamageEvent(player, target, damage);
    }

    /**
     * Handle the effects of the Impact ability
     *
     * @param target The {@link LivingEntity} being affected by Impact
     */
    public void impactCheck(LivingEntity target) {
        int durabilityDamage = 1 + (getSkillLevel() / Axes.impactIncreaseLevel);

        for (ItemStack armor : target.getEquipment().getArmorContents()) {
            if (ItemUtils.isArmor(armor) && Axes.impactChance > Misc.getRandom().nextInt(getActivationChance())) {
                SkillUtils.handleDurabilityChange(armor, durabilityDamage, Axes.impactMaxDurabilityModifier);
            }
        }
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(LivingEntity target) {
        if (!(Axes.greaterImpactChance > Misc.getRandom().nextInt(getActivationChance()))) {
            return 0;
        }

        Player player = getPlayer();

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                defender.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Struck"));
            }
        }

        return CombatUtils.callFakeDamageEvent(player, target, Axes.greaterImpactBonusDamage);
    }

    /**
     * Handle the effects of the Skull Splitter ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void skullSplitterCheck(LivingEntity target, double damage) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Axes.skullSplitterModifier, skill);
    }
}
