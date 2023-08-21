package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.NotificationManager;

public class ToolLowerTask extends CancellableRunnable {
    private final McMMOPlayer mcMMOPlayer;
    private final ToolType tool;

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

        if (mcMMO.p.getGeneralConfig().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(mcMMOPlayer.getPlayer(), NotificationType.TOOL, tool.getLowerTool());
        }
    }
}
