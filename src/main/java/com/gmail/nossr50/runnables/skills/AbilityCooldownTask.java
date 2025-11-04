package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.NotificationManager;

public class AbilityCooldownTask extends CancellableRunnable {
    private final McMMOPlayer mmoPlayer;
    private final SuperAbilityType ability;

    public AbilityCooldownTask(McMMOPlayer mmoPlayer, SuperAbilityType ability) {
        this.mmoPlayer = mmoPlayer;
        this.ability = ability;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getPlayer().isOnline() || mmoPlayer.getAbilityInformed(ability)) {
            return;
        }

        mmoPlayer.setAbilityInformed(ability, true); // TODO: ?? What does this do again?
        NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(),
                NotificationType.ABILITY_REFRESHED, ability.getAbilityRefresh());
    }
}
