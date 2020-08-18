package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilityCooldownTask extends BukkitRunnable {
    private final McMMOPlayer mmoPlayer;
    private final SuperAbilityType ability;

    public AbilityCooldownTask(McMMOPlayer mmoPlayer, SuperAbilityType ability) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getPlayer().isOnline() || mmoPlayer.getSuperAbilityManager().getAbilityInformed(ability)) {
            return;
        }

        mmoPlayer.getSuperAbilityManager().setAbilityInformed(ability, true);

        NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(), NotificationType.ABILITY_REFRESHED, ability.getAbilityRefresh());
        //mmoPlayer.getPlayer().sendMessage(ability.getAbilityRefresh());
    }
}
