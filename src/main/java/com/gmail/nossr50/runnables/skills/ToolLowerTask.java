package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.skills.AbilityToolType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.player.NotificationManager;
import com.neetgames.mcmmo.player.OnlineMMOPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ToolLowerTask extends BukkitRunnable {
    private final OnlineMMOPlayer mmoPlayer;
    private final AbilityToolType tool;

    public ToolLowerTask(OnlineMMOPlayer mmoPlayer, AbilityToolType abilityToolType) {
        this.mmoPlayer = mmoPlayer;
        this.tool = abilityToolType;
    }

    @Override
    public void run() {
        if (!mmoPlayer.getSuperAbilityManager().isAbilityToolPrimed(tool)) {
            return;
        }

        mmoPlayer.getSuperAbilityManager().setAbilityToolPrime(tool, false);

        if (mcMMO.p.getGeneralConfig().getAbilityMessagesEnabled()) {
            NotificationManager.sendPlayerInformation(Misc.adaptPlayer(mmoPlayer), NotificationType.TOOL, tool.getLowerToolLocaleKey());
        }
    }
}
