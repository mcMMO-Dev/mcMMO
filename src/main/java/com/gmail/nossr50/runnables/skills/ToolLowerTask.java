package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityToolType;
import com.gmail.nossr50.util.player.NotificationManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ToolLowerTask extends BukkitRunnable {
    private final McMMOPlayer mmoPlayer;
    private final AbilityToolType tool;

    public ToolLowerTask(McMMOPlayer mmoPlayer, AbilityToolType abilityToolType) {
        this.mmoPlayer = mmoPlayer;
        this.tool = abilityToolType;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getSuperAbilityManager().isAbilityToolPrimed(tool)) {
            return;
        }

        mmoPlayer.getSuperAbilityManager().setAbilityToolPrime(tool, false);

        if (Config.getInstance().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(), NotificationType.TOOL, tool.getLowerToolLocaleKey());
        }
    }
}
