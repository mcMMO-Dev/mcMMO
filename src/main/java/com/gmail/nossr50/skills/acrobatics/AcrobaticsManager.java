package com.gmail.nossr50.skills.acrobatics;

import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.ParticleEffectUtils;

public class AcrobaticsManager extends SkillManager {
    public AcrobaticsManager (McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.ACROBATICS);
    }

    /**
     * Check for dodge damage reduction.
     *
     * @param event The event to check
     */
    public int dodgeCheck(int damage) {
        int modifiedDamage = Acrobatics.calculateModifiedDodgeDamage(damage, Acrobatics.dodgeDamageModifier);
        Player player = getPlayer();

        if (!Acrobatics.isFatal(player, modifiedDamage) && SkillTools.activationSuccessful(player, skill, Acrobatics.dodgeMaxChance, Acrobatics.dodgeMaxBonusLevel)) {
            ParticleEffectUtils.playDodgeEffect(player);

            PlayerProfile playerProfile = getProfile();

            if (playerProfile.useChatNotifications()) {
                player.sendMessage(LocaleLoader.getString("Acrobatics.Combat.Proc"));
            }

            // Why do we check respawn cooldown here?
            if (System.currentTimeMillis() >= playerProfile.getRespawnATS() + Misc.PLAYER_RESPAWN_COOLDOWN_SECONDS) {
                applyXpGain(damage * Acrobatics.dodgeXpModifier);
            }

            return modifiedDamage;
        }

        return damage;
    }
}
