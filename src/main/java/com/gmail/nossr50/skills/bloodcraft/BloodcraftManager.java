package com.gmail.nossr50.skills.bloodcraft;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.skills.RankUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class BloodcraftManager extends SkillManager {

    public BloodcraftManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.BLOODCRAFT);
    }

    public boolean canUseLifesteal() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.BLOODCRAFT_LIFESTEAL)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.BLOODCRAFT_LIFESTEAL);
    }

    public boolean canUseCrimsonSurge() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.BLOODCRAFT_CRIMSON_SURGE)
                && RankUtils.hasUnlockedSubskill(getPlayer(), SubSkillType.BLOODCRAFT_CRIMSON_SURGE);
    }

    public boolean canUseRuptureMastery() {
        return Permissions.isSubSkillEnabled(getPlayer(), SubSkillType.BLOODCRAFT_RUPTURE_MASTERY)
                && RankUtils.hasUnlockedSubskill(getPlayer(),
                SubSkillType.BLOODCRAFT_RUPTURE_MASTERY);
    }

    public boolean canActivateBerserkersRush() {
        return getPlayer().hasPermission("mcmmo.ability.bloodcraft.berserkers_rush");
    }

    /**
     * Heals the player for a percentage of damage dealt based on Lifesteal rank.
     */
    public void processLifesteal(@NotNull LivingEntity target, double damage) {
        if (!canUseLifesteal()) {
            return;
        }

        int rank = RankUtils.getRank(getPlayer(), SubSkillType.BLOODCRAFT_LIFESTEAL);
        if (rank <= 0) {
            return;
        }

        Player player = getPlayer();
        double healAmount = damage * (rank * Bloodcraft.LIFESTEAL_PERCENT_PER_RANK);
        double maxHealth = getMaxHealth(player);
        double newHealth = Math.min(player.getHealth() + healAmount, maxHealth);
        player.setHealth(newHealth);
    }

    /**
     * Returns bonus damage multiplier from Crimson Surge when HP is low.
     */
    public double getCrimsonSurgeBonusDamage(double baseDamage) {
        if (!canUseCrimsonSurge()) {
            return 0;
        }

        Player player = getPlayer();
        double maxHealth = getMaxHealth(player);

        if (player.getHealth() / maxHealth > Bloodcraft.CRIMSON_SURGE_HP_THRESHOLD) {
            return 0;
        }

        int rank = RankUtils.getRank(getPlayer(), SubSkillType.BLOODCRAFT_CRIMSON_SURGE);
        if (rank <= 0) {
            return 0;
        }

        return baseDamage * (rank * Bloodcraft.CRIMSON_SURGE_BONUS_PER_RANK);
    }

    /**
     * Returns additional rupture/bleed activation chance from Rupture Mastery.
     */
    public double getRuptureMasteryBonusChance() {
        if (!canUseRuptureMastery()) {
            return 0;
        }

        int rank = RankUtils.getRank(getPlayer(), SubSkillType.BLOODCRAFT_RUPTURE_MASTERY);
        return rank * Bloodcraft.RUPTURE_MASTERY_BONUS_PER_RANK;
    }

    /**
     * Activates Berserker's Rush: grants Speed II + Strength I for the duration.
     */
    public void activateBerserkersRush() {
        Player player = getPlayer();
        int durationTicks = Bloodcraft.BERSERKERS_RUSH_DURATION_TICKS;
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 1, false, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, durationTicks, 0, false, true, true));
    }

    public void applyBloodcraftXP(double damage, XPGainReason reason) {
        float xp = (float) damage * 3.0f;
        applyXpGain(xp, reason, XPGainSource.SELF);
    }

    private double getMaxHealth(@NotNull Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return attr != null ? attr.getValue() : 20.0;
    }
}
