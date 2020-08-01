package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityToolType;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ToolLowerTask extends BukkitRunnable {
    private final McMMOPlayer mcMMOPlayer;
    private final AbilityToolType tool;

    public ToolLowerTask(McMMOPlayer mcMMOPlayer, AbilityToolType abilityToolType) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.tool = abilityToolType;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getSuperAbilityManager().isAbilityToolPrimed(tool)) {
            return;
        }

        mcMMOPlayer.getSuperAbilityManager().setAbilityToolPrime(tool, false);

        if (Config.getInstance().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(mcMMOPlayer.getPlayer(), NotificationType.TOOL, tool.getLowerToolLocaleKey());
        }
    }
}
