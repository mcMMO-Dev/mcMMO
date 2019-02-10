package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityCooldownTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private SuperAbilityType ability;

    public AbilityCooldownTask(McMMOPlayer mcMMOPlayer, SuperAbilityType ability) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (mcMMOPlayer.getAbilityInformed(ability)) {
            return;
        }

        mcMMOPlayer.setAbilityInformed(ability, true);

        NotificationManager.sendPlayerInformation(mcMMOPlayer.getPlayer(), NotificationType.ABILITY_REFRESHED, ability.getAbilityRefresh());
        //mcMMOPlayer.getPlayer().sendMessage(ability.getAbilityRefresh());
    }
}
