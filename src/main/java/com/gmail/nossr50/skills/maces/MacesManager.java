package com.gmail.nossr50.skills.maces;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import java.util.Locale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacesManager extends SkillManager {
    private static @Nullable PotionEffectType slowEffectType;

    public MacesManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.MACES);
    }

    private static @Nullable PotionEffectType mockSpigotMatch(@NotNull String input) {
        // Replicates match() behaviour for older versions lacking this API
        final String filtered = input.toLowerCase(Locale.ROOT).replaceAll("\\s+", "_");
        final NamespacedKey namespacedKey = NamespacedKey.fromString(filtered);
        return (namespacedKey != null) ? Registry.EFFECT.get(namespacedKey) : null;
    }

    /**
     * Get the Crush damage bonus.
     *
     * @return the Crush damage bonus.
     */
    public double getCrushDamage() {
        if (!Permissions.canUseSubSkill(mmoPlayer.getPlayer(), SubSkillType.MACES_CRUSH)) {
            return 0;
        }

        int rank = RankUtils.getRank(getPlayer(), SubSkillType.MACES_CRUSH);

        if (rank > 0) {
            return (0.5D + (rank * 1.D));
        }

        return 0;
    }

    /**
     * Process Cripple attack.
     *
     * @param target The defending entity
     */
    public void processCripple(@NotNull LivingEntity target) {
        // Lazy initialized to avoid some backwards compatibility issues
        if (slowEffectType == null) {
            if (mockSpigotMatch("slowness") == null) {
                mcMMO.p.getLogger().severe("Unable to find the Slowness PotionEffectType, " +
                        "mcMMO will not function properly.");
                throw new IllegalStateException("Unable to find the Slowness PotionEffectType!");
            } else {
                slowEffectType = mockSpigotMatch("slowness");
            }
        }

        boolean isPlayerTarget = target instanceof Player;
        // Don't apply Cripple if the target is already Slowed
        if (slowEffectType == null || target.getPotionEffect(slowEffectType) != null) {
            return;
        }

        if (!Permissions.canUseSubSkill(mmoPlayer.getPlayer(), SubSkillType.MACES_CRIPPLE)) {
            return;
        }

        int crippleRank = RankUtils.getRank(getPlayer(), SubSkillType.MACES_CRIPPLE);
        double crippleOdds = (mcMMO.p.getAdvancedConfig().getCrippleChanceToApplyOnHit(crippleRank)
                * mmoPlayer.getAttackStrength());

        if (ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.MACES, mmoPlayer,
                crippleOdds)) {
            if (mmoPlayer.useChatNotifications()) {
                NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                        NotificationType.SUBSKILL_MESSAGE, "Maces.SubSkill.Cripple.Activated");
            }

            // Cripple is success, Cripple the target
            target.addPotionEffect(slowEffectType.createEffect(
                    getCrippleTickDuration(isPlayerTarget),
                    getCrippleStrength(isPlayerTarget)));
            ParticleEffectUtils.playCrippleEffect(target);
        }
    }

    public static int getCrippleTickDuration(boolean isPlayerTarget) {
        // TODO: Make configurable
        if (isPlayerTarget) {
            return 20;
        } else {
            return 30;
        }
    }

    public static int getCrippleStrength(boolean isPlayerTarget) {
        // TODO: Make configurable
        return isPlayerTarget ? 1 : 2;
    }
}
