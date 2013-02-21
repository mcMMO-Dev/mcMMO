package com.gmail.nossr50.skills.runnables;

import org.bukkit.entity.Player;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.skills.utilities.AbilityType;
import com.gmail.nossr50.skills.utilities.SkillTools;
import com.gmail.nossr50.skills.utilities.SkillType;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Users;

public class SkillMonitor implements Runnable {
    @Override
    public void run() {
        long curTime = System.currentTimeMillis();

        for (Player player : mcMMO.p.getServer().getOnlinePlayers()) {
            if (Misc.isNPCEntity(player)) {
                continue;
            }

            PlayerProfile profile = Users.getPlayer(player).getProfile();

            /*
             * MONITOR SKILLS
             */
            for (SkillType skill : SkillType.values()) {
                if (skill.getTool() != null && skill.getAbility() != null) {
                    SkillTools.monitorSkill(player, profile, curTime, skill);
                }
            }

            /*
             * COOLDOWN MONITORING
             */
            for (AbilityType ability : AbilityType.values()) {
                if (ability.getCooldown() > 0) {
                    SkillTools.watchCooldown(player, profile, ability);
                }
            }
        }
    }
}
