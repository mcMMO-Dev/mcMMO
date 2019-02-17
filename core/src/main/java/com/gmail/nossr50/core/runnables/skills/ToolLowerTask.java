package com.gmail.nossr50.core.runnables.skills;

import com.gmail.nossr50.core.config.MainConfig;
import com.gmail.nossr50.core.datatypes.interactions.NotificationType;
import com.gmail.nossr50.core.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.core.skills.ToolType;
import com.gmail.nossr50.core.util.player.NotificationManager;

public class ToolLowerTask extends BukkitRunnable {
    private McMMOPlayer mcMMOPlayer;
    private ToolType tool;

    public ToolLowerTask(McMMOPlayer mcMMOPlayer, ToolType tool) {
        this.mcMMOPlayer = mcMMOPlayer;
        this.tool = tool;
    }

    @Override
    public void run() {
        if (!mcMMOPlayer.getToolPreparationMode(tool)) {
            return;
        }

        mcMMOPlayer.setToolPreparationMode(tool, false);

        if (MainConfig.getInstance().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(mcMMOPlayer.getPlayer(), NotificationType.TOOL, tool.getLowerTool());
        }
    }
}
