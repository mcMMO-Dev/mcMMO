package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.CombatUtils;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AcrobaticsManager extends SkillManager {
    public AcrobaticsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ACROBATICS);
    }

    public boolean canRoll() {
        Player player = getPlayer();

        return (player.getItemInHand().getType() != Material.ENDER_PEARL) && !(Acrobatics.afkLevelingDisabled && player.isInsideVehicle()) && Permissions.roll(player);
    }

    public boolean canDodge(Entity damager) {
        if (Permissions.dodge(getPlayer())) {
            if (damager instanceof LightningStrike && Acrobatics.dodgeLightningDisabled) {
                return false;
            }

            return CombatUtils.shouldProcessSkill(damager, skill);
        }

        return false;
    }

    /**
     * Handle the damage reduction and XP gain from the Dodge ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public int dodgeCheck(int damage) {
        int modifiedDamage = Acrobatics.calculateModifiedDodgeDamage(damage, Acrobatics.dodgeDamageModifier);
        Player player = getPlayer();

        if (!isFatal(modifiedDamage) && SkillUtils.activationSuccessful(getSkillLevel(), getActivationChance(), Acrobatics.dodgeMaxChance, Acrobatics.dodgeMaxBonusLevel)) {
            ParticleEffectUtils.playDodgeEffect(player);

            if (mcMMOPlayer.useChatNotifications()) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
            }

            // Why do we check respawn cooldown here?
            if (System.currentTimeMillis() >= mcMMOPlayer.getRespawnATS() + Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS) {
                applyXpGain(damage * Acrobatics.dodgeXpModifier);
            }

            return modifiedDamage;
        }

        return damage;
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    public int rollCheck(int damage) {
        Player player = getPlayer();

        if (player.isSneaking() && Permissions.gracefulRoll(player)) {
            return gracefulRollCheck(damage);
        }

        int modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.rollThreshold);

        if (!isFatal(modifiedDamage) && isSuccessfulRoll(Acrobatics.rollMaxChance, Acrobatics.rollMaxBonusLevel, 1)) {
            player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));
            applyXpGain(damage * Acrobatics.rollXpModifier);

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(damage * Acrobatics.fallXpModifier);
        }

        return damage;
    }

    /**
     * Handle the damage reduction and XP gain from the Graceful Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private int gracefulRollCheck(int damage) {
        int modifiedDamage = Acrobatics.calculateModifiedRollDamage(damage, Acrobatics.gracefulRollThreshold);

        if (!isFatal(modifiedDamage) && isSuccessfulRoll(Acrobatics.gracefulRollMaxChance, Acrobatics.gracefulRollMaxBonusLevel, Acrobatics.gracefulRollSuccessModifier)) {
            getPlayer().sendMessage(LocaleLoader.getString("Acrobatics.Ability.Proc"));
            applyXpGain(damage * Acrobatics.rollXpModifier);

            return modifiedDamage;
        }
        else if (!isFatal(damage)) {
            applyXpGain(damage * Acrobatics.fallXpModifier);
        }

        return damage;
    }

    private boolean isSuccessfulRoll(double maxChance, int maxLevel, int successModifier) {
        return ((maxChance / maxLevel) * Math.min(getSkillLevel(), maxLevel) * successModifier) > Misc.getRandom().nextInt(activationChance);
    }

    private boolean isFatal(int damage) {
        return getPlayer().getHealth() - damage < 1;
    }
}
