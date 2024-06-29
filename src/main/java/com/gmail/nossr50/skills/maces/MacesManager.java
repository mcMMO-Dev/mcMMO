package com.gmail.nossr50.skills.maces;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class MacesManager extends SkillManager {
    public MacesManager(McMMOPlayer mmoPlayer) {
        super(mmoPlayer, PrimarySkillType.MACES);
    }

    /**
     * Get the Crush damage bonus.
     *
     * @return the Crush damage bonus.
     */
    public double getCrushDamage() {
        if (!Permissions.canUseSubSkill(mmoPlayer.getPlayer(), SubSkillType.MACES_CRUSH))
            return 0;

        int rank = RankUtils.getRank(getPlayer(), SubSkillType.MACES_CRUSH);

        if (rank > 0) {
            return (1.0D + (rank * 0.5D));
        }

        return 0;
    }

    /**
     * Process Cripple attack.
     *
     * @param target The defending entity
     */
    public void processCripple(@NotNull LivingEntity target) {
        // Don't apply Cripple if the target is already Slowed
        if (target.getPotionEffect(PotionEffectType.SLOWNESS) != null) {
            return;
        }

        if (!Permissions.canUseSubSkill(mmoPlayer.getPlayer(), SubSkillType.MACES_CRIPPLE)) {
            return;
        }

        int crippleRank = RankUtils.getRank(getPlayer(), SubSkillType.MACES_CRIPPLE);
        double crippleOdds = (mcMMO.p.getAdvancedConfig().getCrippleChanceToApplyOnHit(crippleRank)
                * mmoPlayer.getAttackStrength());

        if (ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.MACES, mmoPlayer, crippleOdds)) {
            // Cripple is success, Cripple the target
            target.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(getCrippleTickDuration(), 1));
            // TODO: Play some kind of Smash effect / sound
            SoundManager.sendCategorizedSound(getPlayer(), target.getLocation(), SoundType.CRIPPLE, SoundCategory.PLAYERS);
        }
    }

    public int getCrippleTickDuration() {
        // TODO: Make configurable
        return 20 * 5;
    }
}
