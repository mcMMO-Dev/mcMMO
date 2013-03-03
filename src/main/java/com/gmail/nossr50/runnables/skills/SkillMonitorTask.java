package com.gmail.nossr50.runnables.skills;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SkillMonitorTask implements Runnable {
    @Override
    public void run() {
        long curTime = System.currentTimeMillis();

        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            if (Misc.isNPCEntity(player)) {
                continue;
            }

            McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

            /*
             * MONITOR SKILLS & COOLDOWN
             */
            for (SkillType skill : SkillType.values()) {
                if (skill.getAbility() == null) {
                    continue;
                }

                if (skill.getToolType() != null) {
                    SkillUtils.monitorSkill(mcMMOPlayer, curTime, skill);
                }

                if (skill.getAbility().getCooldown() > 0) {
                    SkillUtils.watchCooldown(mcMMOPlayer, skill);
                }
            }
        }
    }
}
