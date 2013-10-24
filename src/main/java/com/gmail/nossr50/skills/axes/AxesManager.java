package com.gmail.nossr50.skills.axes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.axes.McMMOPlayerAxeMasteryEvent;
import com.gmail.nossr50.events.skills.axes.McMMOPlayerCriticalHitEvent;
import com.gmail.nossr50.events.skills.axes.McMMOPlayerGreaterImpactEvent;
import com.gmail.nossr50.events.skills.axes.McMMOPlayerImpactEvent;
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

    private boolean canUseAxeMastery(LivingEntity target) {
        return target.isValid() && Permissions.bonusDamage(getPlayer(), skill);
    }

    private boolean canCriticalHit(LivingEntity target) {
        return target.isValid() && Permissions.criticalStrikes(getPlayer()) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Axes.criticalHitMaxChance, Axes.criticalHitMaxBonusLevel);
    }

    private boolean canImpact(LivingEntity target) {
        return target.isValid() && Axes.hasArmor(target) && Permissions.armorImpact(getPlayer());
    }

    private boolean canUseGreaterImpact(LivingEntity target) {
        return target.isValid() && !Axes.hasArmor(target) && Permissions.greaterImpact(getPlayer()) && (Axes.greaterImpactChance > Misc.getRandom().nextInt(getActivationChance()));
    }

    /**
     * Handle the effects of the Axe Mastery ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double axeMastery(LivingEntity target) {
        if (!canUseAxeMastery(target)) {
            return 0;
        }

        McMMOPlayerAxeMasteryEvent event = new McMMOPlayerAxeMasteryEvent(getPlayer(), target, calculateAxeMasteryBonus());
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        return event.getDamage();
    }

    /**
     * Handle the effects of the Critical Hit ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public double criticalHit(LivingEntity target, double damage) {
        if (!canCriticalHit(target)) {
            return 0;
        }

        Player player = getPlayer();
        player.sendMessage(LocaleLoader.getString("Axes.Combat.CriticalHit"));

        boolean targetIsPlayer = target instanceof Player;

        if (targetIsPlayer) {
            ((Player) target).sendMessage(LocaleLoader.getString("Axes.Combat.CritStruck"));
        }

        McMMOPlayerCriticalHitEvent event = new McMMOPlayerCriticalHitEvent(player, target, calculateCriticalHitBonus(damage, targetIsPlayer));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        return event.getDamage();
    }

    /**
     * Handle the effects of the Impact ability
     *
     * @param target The {@link LivingEntity} being affected by Impact
     */
    public void impact(LivingEntity target) {
        if (!canImpact(target)) {
            return;
        }

        Player player = getPlayer();
        int durabilityDamage = 1 + (getSkillLevel() / Axes.impactIncreaseLevel);
        McMMOPlayerImpactEvent event;

        for (ItemStack armor : target.getEquipment().getArmorContents()) {
            if (ItemUtils.isArmor(armor) && Axes.impactChance > Misc.getRandom().nextInt(getActivationChance())) {
//<<<<<<< HEAD
                SkillUtils.handleDurabilityChange(armor, durabilityDamage, Axes.impactMaxDurabilityModifier);
//=======
//                event = new McMMOPlayerImpactEvent(player, armor, calculateImpactDurabilityDamage(durabilityDamage, armor));
//                mcMMO.p.getServer().getPluginManager().callEvent(event);
//
//                if (event.isCancelled()) {
//                    continue;
//                }
//
//                armor.setDurability((short) (event.getDurabilityDamage() + armor.getDurability()));
//>>>>>>> Axe events.
            }
        }
    }

    /**
     * Handle the effects of the Greater Impact ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     */
    public double greaterImpact(LivingEntity target) {
        if (!canUseGreaterImpact(target)) {
            return 0;
        }

        Player player = getPlayer();

        McMMOPlayerGreaterImpactEvent event = new McMMOPlayerGreaterImpactEvent(player, target, Axes.greaterImpactBonusDamage, player.getLocation().getDirection().normalize().multiply(Axes.greaterImpactKnockbackMultiplier));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return 0;
        }

        ParticleEffectUtils.playGreaterImpactEffect(target);
        target.setVelocity(event.getKnockbackVelocity());

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Proc"));
        }

        if (target instanceof Player) {
            Player defender = (Player) target;

            if (UserManager.getPlayer(defender).useChatNotifications()) {
                defender.sendMessage(LocaleLoader.getString("Axes.Combat.GI.Struck"));
            }
        }

        return event.getDamage();
    }

    /**
     * Handle the effects of the Skull Splitter ability
     *
     * @param target The {@link LivingEntity} being affected by the ability
     * @param damage The amount of damage initially dealt by the event
     */
    public void skullSplitter(LivingEntity target, double damage) {
        CombatUtils.applyAbilityAoE(getPlayer(), target, damage / Axes.skullSplitterModifier, skill);
    }

    private double calculateAxeMasteryBonus() {
        return Math.min(getSkillLevel() / (Axes.bonusDamageMaxBonusLevel / Axes.bonusDamageMaxBonus), Axes.bonusDamageMaxBonus);
    }

    private double calculateCriticalHitBonus(double damage, boolean isPlayer) {
        return (damage * (isPlayer ? Axes.criticalHitPVPModifier : Axes.criticalHitPVEModifier)) - damage;
    }
}
