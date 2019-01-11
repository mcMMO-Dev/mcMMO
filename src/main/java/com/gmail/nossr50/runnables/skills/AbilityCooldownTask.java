package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.SuperAbility;
import com.gmail.nossr50.listeners.InteractionManager;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;

public class AbilityCooldownTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private SuperAbility ability;

    public AbilityCooldownTask(McMMOPlayer mcMMOPlayer, SuperAbility ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (mcMMOPlayer.getAbilityInformed(ability)) {
            return;
        }

        mcMMOPlayer.setAbilityInformed(ability, true);

        InteractionManager.sendPlayerInformation(mcMMOPlayer.getPlayer(), NotificationType.ABILITY_REFRESHED, ability.getAbilityRefresh());
        //mcMMOPlayer.getPlayer().sendMessage(ability.getAbilityRefresh());
    }
}
