package com.gmail.nossr50.skills.spears;

import static com.gmail.nossr50.util.random.ProbabilityUtil.isStaticSkillRNGSuccessful;
import static com.gmail.nossr50.util.skills.RankUtils.getRank;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpearsManager extends SkillManager {
    private static @Nullable PotionEffectType swiftnessEffectType;
    public SpearsManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.SPEARS);
    }

    private static @Nullable PotionEffectType mockSpigotMatch(@NotNull String input) {
        // Replicates match() behaviour for older versions lacking this API
        final String filtered = input.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        final NamespacedKey namespacedKey = NamespacedKey.fromString(filtered);
        return (namespacedKey != null) ? Registry.EFFECT.get(namespacedKey) : null;
    }

    /**
     * Process Momentum activation.
     */
    public void potentiallyApplyMomentum() {
        // Lazy initialized to avoid some backwards compatibility issues
        if (swiftnessEffectType == null) {
            if (mockSpigotMatch("speed") == null) {
                mcMMO.p.getLogger().severe("Unable to find the Speed PotionEffectType, " +
                        "mcMMO will not function properly.");
                throw new IllegalStateException("Unable to find the Speed PotionEffectType!");
            } else {
                swiftnessEffectType = mockSpigotMatch("speed");
            }
        }

        if (!canMomentumBeApplied()) {
            return;
        }

        int momentumRank = getRank(getPlayer(), SubSkillType.SPEARS_MOMENTUM);
        // Chance to activate on hit is influence by the CD
        double momentumOdds = (mcMMO.p.getAdvancedConfig().getMomentumChanceToApplyOnHit(momentumRank)
                * Math.min(mmoPlayer.getAttackStrength(), 1.0D));

        if (isStaticSkillRNGSuccessful(PrimarySkillType.SPEARS, mmoPlayer, momentumOdds)) {
            if (mmoPlayer.useChatNotifications()) {
                NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                        NotificationType.SUBSKILL_MESSAGE, "Spears.SubSkill.Momentum.Activated");
            }

            // Momentum is success, Momentum the target
            getPlayer().addPotionEffect(swiftnessEffectType.createEffect(
                    getMomentumTickDuration(momentumRank),
                    getMomentumStrength()));
            // TODO: Consider adding an effect here
            // ParticleEffectUtils.playMomentumEffect(target);
        }
    }

    public static int getMomentumTickDuration(int momentumRank) {
        return 20 * (momentumRank * 2);
    }

    public static int getMomentumStrength() {
        return 2;
    }

    private boolean canMomentumBeApplied() {
        // TODO: Potentially it should overwrite the effect if we are providing a stronger one
        if (swiftnessEffectType == null) {
            return false;
        }
        final PotionEffect currentlyAppliedPotion = getPlayer()
                .getPotionEffect(swiftnessEffectType);

        if (currentlyAppliedPotion != null) {
            if (isCurrentPotionEffectStronger(currentlyAppliedPotion)) {
                return false;
            }
        }

        if (!Permissions.canUseSubSkill(mmoPlayer.getPlayer(), SubSkillType.SPEARS_MOMENTUM)) {
            return false;
        }

        return true;
    }

    private boolean isCurrentPotionEffectStronger(@NotNull PotionEffect potionEffect) {
        if (potionEffect.getAmplifier() > getMomentumStrength()) {
            return true;
        }

        if (potionEffect.getDuration() > getMomentumTickDuration(getRank(getPlayer(),
                SubSkillType.SPEARS_MOMENTUM))) {
            return true;
        }

        return false;
    }

    public double getSpearMasteryBonusDamage() {
        return mcMMO.p.getAdvancedConfig().getSpearMasteryRankDamageMultiplier()
                * getRank(getPlayer(), SubSkillType.SPEARS_SPEAR_MASTERY);
    }

}
