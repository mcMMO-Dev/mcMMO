package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.ToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;

public class ToolLowerTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private final BukkitMMOPlayer mcMMOPlayer;
    private final ToolType tool;

    public ToolLowerTask(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, ToolType tool) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.tool = tool;
    }

    @Override
    public void accept(Task task) {
        if (!mcMMOPlayer.getToolPreparationMode(tool)) {
            return;
        }

        mcMMOPlayer.setToolPreparationMode(tool, false);

        if (pluginRef.getConfigManager().getConfigNotifications().isSuperAbilityToolMessage()) {
            pluginRef.getNotificationManager().sendPlayerInformation(mcMMOPlayer.getNative(), NotificationType.TOOL, tool.getLowerTool());
        }
    }
}
