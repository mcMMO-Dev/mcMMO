package com.gmail.nossr50.runnables.skills;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.AbilityType;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillUtils;

public class SkillMonitorTask extends BukkitRunnable {
    @Override
    public void run() {
        long curTime = System.currentTimeMillis();

        for (McMMOPlayer mcMMOPlayer : UserManager.getPlayers().values()) {
            /*
             * MONITOR SKILLS
             */
            for (SkillType skill : SkillType.values()) {
                if (skill.getTool() != null && skill.getAbility() != null) {
                    SkillUtils.monitorSkill(mcMMOPlayer, curTime, skill);
                }
            }

            /*
             * COOLDOWN MONITORING
             */
            for (AbilityType ability : AbilityType.values()) {
                if (ability.getCooldown() > 0) {
                    SkillUtils.watchCooldown(mcMMOPlayer, ability);
                }
            }
        }
    }
}
