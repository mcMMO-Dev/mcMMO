package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SuperAbilityType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;

public class AbilityCooldownTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private final BukkitMMOPlayer mcMMOPlayer;
    private final SuperAbilityType superAbilityType;

    public AbilityCooldownTask(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, SuperAbilityType superAbilityType) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.superAbilityType = superAbilityType;
    }

    @Override
    public void accept(Task task) {
        if (!mcMMOPlayer.getNative().isOnline() || mcMMOPlayer.getSuperAbilityInformed(superAbilityType)) {
            return;
        }

        mcMMOPlayer.setAbilityInformed(superAbilityType, true);

        pluginRef.getNotificationManager().sendPlayerInformation(mcMMOPlayer.getNative(), NotificationType.ABILITY_REFRESHED, pluginRef.getSkillTools().getSuperAbilityRefreshedLocaleKey(superAbilityType));
        //mcMMOPlayer.getPlayer().sendMessage(ability.getAbilityRefresh());
    }
}
