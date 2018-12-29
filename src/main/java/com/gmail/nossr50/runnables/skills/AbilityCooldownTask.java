package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.skills.SuperAbility;
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
        mcMMOPlayer.getPlayer().sendMessage(ability.getAbilityRefresh());
    }
}
