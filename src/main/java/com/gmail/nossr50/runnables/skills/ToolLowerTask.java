package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.CancellableRunnable;
import com.gmail.nossr50.util.player.NotificationManager;

public class ToolLowerTask extends CancellableRunnable {
    private final McMMOPlayer mmoPlayer;
    private final ToolType tool;

    public ToolLowerTask(McMMOPlayer mmoPlayer, ToolType tool) {
        this.mmoPlayer = mmoPlayer;
        this.tool = tool;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getToolPreparationMode(tool)) {
            return;
        }

        mmoPlayer.setToolPreparationMode(tool, false);

        if (mcMMO.p.getGeneralConfig().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(mmoPlayer.getPlayer(), NotificationType.TOOL,
                    tool.getLowerTool());
        }
    }
}
