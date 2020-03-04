package com.gmail.nossr50.runnables.skills;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.mcmmo.api.platform.scheduler.Task;

import java.util.function.Consumer;


public class SkillUnlockNotificationTask implements Consumer<Task> {
    private final mcMMO pluginRef;
    private final BukkitMMOPlayer mcMMOPlayer;
    private final SubSkillType subSkillType;

    /**
     * Notify a player about a newly unlocked subskill
     *
     * @param mcMMOPlayer  target player
     * @param subSkillType the subskill that they just unlocked
     */
    public SkillUnlockNotificationTask(mcMMO pluginRef, BukkitMMOPlayer mcMMOPlayer, SubSkillType subSkillType) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.subSkillType = subSkillType;
    }

    @Override
    public void accept(Task task) {
        //mcMMOPlayer.getPlayer().sendTitle(subSkillType.getLocaleName(), "Rank "+rank, 7, 20, 7);
        pluginRef.getNotificationManager().sendPlayerUnlockNotification(mcMMOPlayer, subSkillType);
    }
}
