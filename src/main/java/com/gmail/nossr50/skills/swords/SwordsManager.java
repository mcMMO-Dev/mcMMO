package com.gmail.nossr50.skills.swords;

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
import com.gmail.nossr50.util.skills.SkillActivationType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SwordsManager extends SkillManager {
    public SwordsManager(mcMMO pluginRef, McMMOPlayer mcMMOPlayer) {
        super(pluginRef, mcMMOPlayer, PrimarySkillType.SWORDS);
    }

    public boolean canActivateAbility() {
        return mcMMOPlayer.getToolPreparationMode(ToolType.SWORD) && Permissions.serratedStrikes(getPlayer());
    }

    public boolean canUseStab() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_STAB) && pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_STAB);
    }

    public boolean canUseRupture() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_RUPTURE) && pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_RUPTURE);
    }

    public boolean canUseCounterAttack(Entity target) {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK))
            return false;

        return target instanceof LivingEntity && Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.SWORDS_COUNTER_ATTACK);
    }

    public boolean canUseSerratedStrike() {
        if (!pluginRef.getRankTools().hasUnlockedSubskill(getPlayer(), SubSkillType.SWORDS_SERRATED_STRIKES))
            return false;

        return mcMMOPlayer.getAbilityMode(SuperAbilityType.SERRATED_STRIKES);
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void ruptureCheck(LivingEntity target) {
        if (pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_RUPTURE, getPlayer())) {

            if (target instanceof Player) {
                Player defender = (Player) target;

                //Don't start or add to a bleed if they are blocking
                if (defender.isBlocking())
                    return;

                if (pluginRef.getNotificationManager().doesPlayerUseNotifications(defender)) {
                    if (!pluginRef.getBleedTimerTask().isBleeding(defender))
                        pluginRef.getNotificationManager().sendPlayerInformation(defender, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding.Started");
                }
            }

            pluginRef.getBleedTimerTask().add(target, getPlayer(), getRuptureBleedTicks(), pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE), getToolTier(getPlayer().getInventory().getItemInMainHand()));

            if (mcMMOPlayer.useChatNotifications()) {
                pluginRef.getNotificationManager().sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Bleeding");
            }
        }
    }

    public double getStabDamage() {
        int rank = pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.SWORDS_STAB);

        if (rank > 0) {
            double stabDamage = 1.0D + (rank * 1.5);
            return stabDamage;
        }

        return 0;
    }

    public int getToolTier(ItemStack itemStack) {
        if (ItemUtils.isDiamondTool(itemStack))
            return 4;
        else if (ItemUtils.isIronTool(itemStack) || ItemUtils.isGoldTool(itemStack))
            return 3;
        else if (ItemUtils.isStoneTool(itemStack))
            return 2;
        else
            return 1;
    }

    public int getRuptureBleedTicks() {
        int bleedTicks = pluginRef.getConfigManager().getConfigSwords().getRuptureBaseTicks() * pluginRef.getRankTools().getRank(getPlayer(), SubSkillType.SWORDS_RUPTURE);

        /*if (bleedTicks > AdvancedConfig.getInstance().getRuptureMaxTicks())
            bleedTicks = AdvancedConfig.getInstance().getRuptureMaxTicks();*/

        return bleedTicks;
    }

    /**
     * Handle the effects of the Counter Attack ability
     *
     * @param attacker The {@link LivingEntity} being affected by the ability
     * @param damage   The amount of damage initially dealt by the event
     */
    public void counterAttackChecks(LivingEntity attacker, double damage) {
        if (pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.SWORDS_COUNTER_ATTACK, getPlayer())) {
            pluginRef.getCombatTools().dealDamage(attacker, damage / pluginRef.getConfigManager().getConfigSwords().getCounterAttackDamageModifier(), getPlayer());

            pluginRef.getNotificationManager().sendPlayerInformation(getPlayer(), NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Countered");

            if (attacker instanceof Player) {
                pluginRef.getNotificationManager().sendPlayerInformation((Player) attacker, NotificationType.SUBSKILL_MESSAGE, "Swords.Combat.Counter.Hit");
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
        pluginRef.getCombatTools().applyAbilityAoE(getPlayer(), target, damage / pluginRef.getConfigManager().getConfigSwords().getSerratedStrikesDamageModifier(), modifiers, skill);
    }
}
