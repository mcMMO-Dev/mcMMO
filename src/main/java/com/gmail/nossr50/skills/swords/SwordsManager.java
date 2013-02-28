package com.gmail.nossr50.skills.swords;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.nossr50.datatypes.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.skills.SkillManager;
import com.gmail.nossr50.skills.runnables.BleedTimer;
import com.gmail.nossr50.skills.utilities.CombatTools;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Users;

public class SwordsManager extends SkillManager {
    public SwordsManager(McMMOPlayer mcMMOPlayer) {
        super(mcMMOPlayer, SkillType.SWORDS);
    }

    /**
     * Check for Bleed effect.
     *
     * @param target The defending entity
     */
    public void bleedCheck(LivingEntity target) {
        Player player = getPlayer();

        if (SkillTools.activationSuccessful(player, skill, Swords.bleedMaxChance, Swords.bleedMaxBonusLevel)) {

            if (getSkillLevel() >= Swords.bleedMaxBonusLevel) {
                BleedTimer.add(target, Swords.bleedMaxTicks);
            }
            else {
                BleedTimer.add(target, Swords.bleedBaseTicks);
            }

            if (getProfile().useChatNotifications()) {
                player.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding"));
            }

            if (target instanceof Player) {
                Player defender = (Player) target;

                if (Users.getPlayer(defender).getProfile().useChatNotifications()) {
                    defender.sendMessage(LocaleLoader.getString("Swords.Combat.Bleeding.Started"));
                }
            }
        }
    }

    public void counterAttackChecks(LivingEntity attacker, int damage) {
        if (SkillTools.activationSuccessful(getPlayer(), skill, Swords.counterAttackMaxChance, Swords.counterAttackMaxBonusLevel)) {
            CombatTools.dealDamage(attacker, damage / Swords.counterAttackModifier);

            getPlayer().sendMessage(LocaleLoader.getString("Swords.Combat.Countered"));

            if (attacker instanceof Player) {
                ((Player) attacker).sendMessage(LocaleLoader.getString("Swords.Combat.Counter.Hit"));
            }
        }
    }

    public void serratedStrikes(LivingEntity target, int damage) {
        CombatTools.applyAbilityAoE(getPlayer(), target, damage / Swords.serratedStrikesModifier, skill);
        BleedTimer.add(target, Swords.serratedStrikesBleedTicks);
    }
}
