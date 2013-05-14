package com.gmail.nossr50.runnables.skills;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.HiddenConfig;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityDeactivateEvent;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.skills.ParticleEffectUtils;
import com.gmail.nossr50.util.skills.SkillUtils;

public class AbilityDisableTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private AbilityType ability;

    public AbilityDisableTask(McMMOPlayer mcMMOPlayer, AbilityType ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getAbilityMode(ability)) {
            return;
        }

        Player player = mcMMOPlayer.getPlayer();

        switch (ability) {
            case SUPER_BREAKER:
            case GIGA_DRILL_BREAKER:
                SkillUtils.handleAbilitySpeedDecrease(player);
                // Fallthrough

            case BERSERK:
                if (HiddenConfig.getInstance().resendChunksAfterBlockAbility()) {
                    Misc.resendChunkRadiusAt(player, 1);
                }
                // Fallthrough

            default:
                break;
        }

        McMMOPlayerAbilityDeactivateEvent event = new McMMOPlayerAbilityDeactivateEvent(player, SkillType.byAbility(ability));
        mcMMO.p.getServer().getPluginManager().callEvent(event);

        mcMMOPlayer.setAbilityMode(ability, false);
        mcMMOPlayer.setAbilityInformed(ability, false);

        ParticleEffectUtils.playAbilityDisabledEffect(player);

        if (mcMMOPlayer.useChatNotifications()) {
            player.sendMessage(ability.getAbilityOff());
        }

        SkillUtils.sendSkillMessage(player, ability.getAbilityPlayerOff(player));
        new AbilityCooldownTask(mcMMOPlayer, ability).runTaskLaterAsynchronously(mcMMO.p, ability.getCooldown() * 20);
    }
}
